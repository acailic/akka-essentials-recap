package part2actors

import akka.actor.{Actor, ActorRef, ActorSystem, Props}

object ChildActors extends App {

  // Actors can create other actors
  object Parent {

    case class CreateChild(name: String)

    case class TellChild(message: String)

  }

  class Parent extends Actor {

    import Parent._

    var child: ActorRef = null


    override def receive: Receive = {
      case CreateChild(name) =>
        println(s"${self.path} creating child")
        //create a new actor here
        var childRef = context.actorOf(Props[Child], name)
        context.become(withChild(childRef)) //child = childRef
    }

    def withChild(childRef: ActorRef): Receive = {
      case TellChild(message) =>
        if (childRef != null) childRef forward message
    }
  }

  class Child extends Actor {
    override def receive: Receive = {
      case message => println(s"${self.path} : here is the message $message")
    }
  }


  import Parent._
  val system = ActorSystem("demoActorSystem")
  val parent = system.actorOf(Props[Parent],"parent")
  parent ! CreateChild("ivica")
  parent ! TellChild("desi sinovac")

}
