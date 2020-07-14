package part5infra

import akka.actor.{Actor, ActorLogging, ActorSystem, Props, Terminated}
import akka.routing.{ActorRefRoutee, RoundRobinRoutingLogic, Router}

object Routers extends App {


  // method 1 -- manual router
  class Master extends Actor {
    //step one create routes
    // 5 actor routes based off slaves actor
    private val slaves = for (i <- 1 to 5) yield {
      val slave = context.actorOf(Props[Slave],s"[$i] slave ")
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

  val system = ActorSystem("ActorDemo")
  val master = system.actorOf(Props[Master])

  for(i <- 1 to 10){
    master !  s"[$i] Hi"
  }


}
