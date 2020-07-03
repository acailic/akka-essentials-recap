package part2actors

import akka.actor.{Actor, ActorRef, ActorSystem, Props}
import part2actors.ChangingActorBehavior.Mom.{Food, MomStart}

object ChangingActorBehavior extends App {

  object FussyKid {

    case object KidAccept

    case object KidReject

    val Happy = "Happy"
    val Sad = "Sad"

  }

  class FussyKid extends Actor {

    import FussyKid._
    import Mom._

    // state of kid
    var state = Happy

    override def receive: Receive = {
      case Food(vegetable) => state = Sad
      case Food(chocolate) => state = Happy
      case Ask(message) =>
        if (state == Happy) sender() ! KidAccept
        else sender() ! KidReject

    }

  }

  object Mom {

    case class Food(name: String)
    case class MomStart(kidRef: ActorRef)

    case class Ask(message: String) // message as do you want watter
    val vegetable = "veggies"
    val chocolate = "chocolate"
  }

  class Mom extends Actor {
    import FussyKid._
    import Mom._
    override def receive: Receive = {
      case  MomStart(kidRef)=>
      //our interaction
        kidRef ! Food(vegetable)
        kidRef ! Ask("do you want to play?")
      case KidAccept=>println("my kidd is happy")
      case KidReject=>println("my kidd is sad")

    }
  }


  val system = ActorSystem("changingActorBehavior")
  val fussyKid = system.actorOf(Props[FussyKid])
  val mom = system.actorOf(Props[Mom])


  mom ! MomStart(fussyKid)

}