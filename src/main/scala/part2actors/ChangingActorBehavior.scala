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
      case Food(VEGETABLE) => state = Sad
      case Food(CHOCOLATE) => state = Happy
      case Ask(message) =>
        if (state == Happy) sender() ! KidAccept
        else sender() ! KidReject

    }

  }

  class StatelessFuzzyKid extends Actor {

    import FussyKid._
    import Mom._

    override def receive: Receive = happyReceive

    def happyReceive: Receive = {
      case Food(VEGETABLE) => context.become(sadReceive)// change my receive handler
      case Food(CHOCOLATE) =>
      case Ask(_)=> sender() ! KidAccept
    }

    def sadReceive: Receive = {
      case Food(VEGETABLE) =>
      case Food(CHOCOLATE) => context.become(happyReceive)// change my receive handler
      case Ask(_)=> sender() ! KidReject
    }
  }


  object Mom {

    case class Food(name: String)

    case class MomStart(kidRef: ActorRef)

    case class Ask(message: String) // message as do you want watter
    val VEGETABLE = "veggies"
    val CHOCOLATE = "chocolate" // ??


  }

  class Mom extends Actor {

    import FussyKid._
    import Mom._

    override def receive: Receive = {
      case MomStart(kidRef) =>
        //our interaction
        kidRef ! Food(VEGETABLE)
        kidRef ! Ask("do you want to play?")
      case KidAccept => println("my kidd is happy")
      case KidReject => println("my kidd is sad")

    }
  }


  val system = ActorSystem("changingActorBehavior")
  val fussyKid = system.actorOf(Props[FussyKid])
  val statelessFussyKid = system.actorOf(Props[StatelessFuzzyKid])
  val mom = system.actorOf(Props[Mom])


  mom ! MomStart(fussyKid)
  mom ! MomStart(statelessFussyKid) // same like before
  /// issue different kind of behavior on different state
  // it should be no var in actor state -- bad idea


}