package part2actors

import akka.actor.Actor

class ChildActors extends  App {

  // Actors can create other actors
  object Parent{
    case class CreateChild(name: String)
    case class TellChild(message: String)
  }
  class Parent extends Actor {
    override def receive: Receive = ???
  }

  class Child extends Actor{
    override def receive: Receive = {
      case message => println(s"${self.path} : here is the message $message")
    }
  }

}
