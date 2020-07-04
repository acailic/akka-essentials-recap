package part2actors

import akka.actor.AbstractActor.Receive
import akka.actor.{Actor, ActorLogging, ActorSystem, Props}
import akka.event.Logging

object ActorLoggingDemo extends App {

  class SimpleActorWithExplicitLogger extends Actor {
    ///// 1. Example explicit logging

    val logger = Logging(context.system,this)

    override def receive: Receive = {
      case message=> logger.info(message.toString)// LOG IT

    }
  }

  val system = ActorSystem("demoLoggingActor")
  val simpleLogging = system.actorOf(Props[SimpleActorWithExplicitLogger],"simpleLogging")

  simpleLogging ! " Sending a simple message.  "

  // 2. ActorLogging

  class  ActorWithLogging extends Actor with ActorLogging {
     override def receive: Receive ={
       case (a,b) => log.info("Two things: {} and {}",a,b)
       case message=> log.info(message.toString)
    }

  }

  val simpleActor = system.actorOf(Props[ActorWithLogging])

  simpleActor ! " Sending a simple message extended by trait  "
  simpleActor ! (2,3)



}
