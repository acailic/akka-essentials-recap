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
      case Food(VEGETABLE) => context.become(sadReceive, false)// change my receive handler
        // context.become(sadReceive, true)--erases old  or context.become(sadReceive, sad)-- saves old like a stack
        //stack.push(sadReceive) it will call top most
      case Food(CHOCOLATE) =>
      case Ask(_)=> sender() ! KidAccept
    }

    def sadReceive: Receive = {
      case Food(VEGETABLE) => context.become(sadReceive, false)
      case Food(CHOCOLATE) => context.unbecome() // unbecome is like pop out from stack
      // change my receive handler context.become(happyReceive)
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
        kidRef ! Food(VEGETABLE)
        kidRef ! Food(CHOCOLATE)
        kidRef ! Food(CHOCOLATE)
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