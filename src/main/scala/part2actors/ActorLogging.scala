package part2actors

import akka.actor.{Actor, ActorSystem, Props}
import akka.event.Logging

object ActorLogging extends App {

  class SimpleActorWithExplicitLogger extends Actor {
    val logger = Logging(context.system,this)

    override def receive: Receive = {
      case message=> logger.info(message.toString)// LOG IT

    }
  }

  val system = ActorSystem("demoLoggingActor")
  val simpleLogging = system.actorOf(Props[SimpleActorWithExplicitLogger],"simpleLogging")

  simpleLogging ! " Sending a simple message.  "
}
