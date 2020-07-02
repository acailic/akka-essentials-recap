package part2actors

import akka.actor.{Actor, ActorSystem, Props}

object ActorCapabilities extends App {

  class SimpleActor extends Actor {
    override def receive: Receive = {
      case message: String => println(s"[simple actor] i have received $message")
    }
  }

  val system = ActorSystem("ActorSystemDemo")
  val simpleActor = system.actorOf(Props[SimpleActor], "simpleActor")

  simpleActor ! "olla actor"

}
