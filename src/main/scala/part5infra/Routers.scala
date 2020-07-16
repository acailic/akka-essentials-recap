package part5infra

import akka.actor.{Actor, ActorLogging, ActorSystem, Props, Terminated}
import akka.io.Udp.SO
import akka.routing._
import com.typesafe.config.ConfigFactory

object Routers extends App {


  // method 1 -- manual router
  class Master extends Actor {
    //step one create routes
    // 5 actor routes based off slaves actor
    private val slaves = for (i <- 1 to 5) yield {
      val slave = context.actorOf(Props[Slave], s"[$i] slave ")
      context.watch(slave)
      ActorRefRoutee(slave)
      // slave // TODO
    }
    //step two define router
    private val router = Router(RoundRobinRoutingLogic(), slaves)

    //step 3 route three route message

    override def receive: Receive = {
      case message =>
        router.route(message, sender())

      //handle the termination/lifecycle
      case Terminated(ref) =>
        router.removeRoutee(ref)
        val newSlave = context.actorOf(Props[Slave])
        router.addRoutee(newSlave)
    }
  }

  class Slave extends Actor with ActorLogging {

    override def receive: Receive = {
      case message => log.info(message.toString)
    }

  }

  val system = ActorSystem("ActorDemo", ConfigFactory.load().getConfig("routersDemo"))
  val master = system.actorOf(Props[Master])
  /*

    for(i <- 1 to 10){
      master !  s"[$i] Hi"
    }
  */

  // method 2.1 programatic

  val poolMaster = system.actorOf(RoundRobinPool(5).props(Props[Slave]), "simplePoolMaster")
  for (i <- 1 to 10) {
    poolMaster ! s"[$i] Hi"
  }


  // method 2.2 from config routing

  val poolMaster2 = system.actorOf(FromConfig.props(Props[Slave]), "poolMaster2")

   // method 2.3  routers wiht actor created elsewhere
  //GROUP

  val slaveList = (1 to 5).map(i => system.actorOf(Props[Slave], s"slave_$i")).toList
  // need their path
  val slavePaths = slaveList.map(slaveRef => slaveRef.path.toString)

  //3.1 in config
  val groupMaster = system.actorOf(RoundRobinGroup(slavePaths).props())
/*
  for(i <- 1 to 10){
    groupMaster !  s"[$i] Hi"
  }
*/

  val groupMaster2 = system.actorOf(FromConfig.props(), "groupMaster2")
  for (i <- 1 to 10) {
    groupMaster2 ! s"[$i] Hello from the world"
  }



  /**
    * Special messages
    */
  groupMaster2 ! Broadcast("hello, everyone")

  // PoisonPill and Kill are NOT routed
  // AddRoutee, Remove, Get handled only by the routing actor
}
