package part2actors

import akka.actor.{Actor, ActorRef, ActorSystem, Props}
import part2actors.ChildActors.CreditCard.{AttachToAccount, CheckStatus}

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
  val parent = system.actorOf(Props[Parent], "parent")
  parent ! CreateChild("ivica")
  parent ! TellChild("desi sinovac")

  //actor hierarchies
  // parent-> child-> grandchild
  //any level of hierarchies
  /*
   Guardian actors - top level actor
   /system = system guardian
   /user - user-level guardian     //demoActorSystem/user/parent/ivica : here is the message desi sinovac
   /root = the root guardian  - guard previous one
   */


  /**
    * Actor selection
    */
  var childrenSelection = system.actorSelection("/user/parent/child") // dead letters if no children
  childrenSelection ! "i found you"

  /** ************************************************************
    * NEVER PASS MUTABLE ACTOR STATE OR THIS REFERENCE TO CHILD ACTORS
    * ************************************************************ */

  object NaiveBankAccount {

    case class Deposit(amount: Int)

    case class Withdraw(amount: Int)

    case object InitializeAccount

  }

  class NaiveBankAccount extends Actor {

    import NaiveBankAccount._

    var amount = 0

    override def receive: Receive = {
      case InitializeAccount =>
        val creditCardRef = context.actorOf(Props[CreditCard],"card")
        creditCardRef ! AttachToAccount(this) /////!!!!!!!!!!!!!!
      case Deposit(funds) => deposit(funds)
      case Withdraw(funds) => withdraw(funds)

    }

    def deposit(funds: Int) {
      println(s"${self.path} depositing funds $funds on top of $amount ")
      amount += funds
    }

    def withdraw(funds: Int) = {
      println(s"${self.path} withdraw funds $funds from $amount ")
      amount -= funds
    }
  }

  object CreditCard {

     case class AttachToAccount(bankAccount: NaiveBankAccount) /// !!!!!!!!
    //case class AttachToAccount(bankAccountRef: ActorRef) /// every single interaction with actor through methods NO MESSAGES
    // NEVER CLOSE OVER THIS REFERENCE

    case object CheckStatus

  }

  class CreditCard extends Actor {
    override def receive: Receive = {
      case AttachToAccount(account) => context.become(attachedTo(account))
    }

    def attachedTo(account: NaiveBankAccount): Receive = {
      case CheckStatus =>
        println(s"${self.path} your message has been processed.")
        ///benign
        account.withdraw(1) // CHANGE OF ACCOUNT WITHOUT METHOD.
    }

  }

  import NaiveBankAccount._
  import CreditCard._

  val bankAccountRef = system.actorOf(Props[NaiveBankAccount], "account")
  bankAccountRef ! InitializeAccount
  bankAccountRef ! Deposit(500)

  Thread.sleep(1000)
  val ccSelection = system.actorSelection("/user/account/card")
  ccSelection ! CheckStatus


}
