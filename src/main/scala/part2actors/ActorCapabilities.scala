package part2actors

import akka.actor.{Actor, ActorRef, ActorSystem, Props}

object ActorCapabilities extends App {

  class SimpleActor extends Actor {
    override def receive: Receive = {
      case "hi!!!!"=> context.sender() ! "Hello there"  // replay message
      case message: String => println(s"[$self] i have received $message")
      case number: Int => println(s"[simple actor] i have received number $number")
      case SpecialMessage(contents)  => println(s"[simple actor] i have received something special $contents")
      case SayHiTo(ref)  => ref ! "hi!!!!"
      case SendMessageToYourself(contents)  =>
        self ! contents

    }
  }

  val system = ActorSystem("ActorSystemDemo")
  val simpleActor = system.actorOf(Props[SimpleActor], "simpleActor")

  simpleActor ! "olla actor"

  //messages can be of any type
  // akka retrieves a object and then invoke it
  // condition for sending:
  // 1) messages immutable
  // 2) messages serializable
  // in practise use case classes and case objects

  simpleActor ! 32
  case class SpecialMessage(contents: String)
  simpleActor ! SpecialMessage("special content")

  // each actor has information about themselves,
  // has context, has info about reference
  // this -> context.self

  // send messages to self
  case class SendMessageToYourself(content: String)
  simpleActor ! SendMessageToYourself("message to myself")

  // everything asynchronous


  // 3 - actors can replay
  val alice = system.actorOf(Props[SimpleActor], "alice")
  val bob = system.actorOf(Props[SimpleActor], "bob")

  case class SayHiTo(ref: ActorRef)
  alice ! SayHiTo(bob)


}
