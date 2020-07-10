package part4faulttolerance

import akka.actor.SupervisorStrategy.{Escalate, Restart, Resume, Stop}
import akka.actor.{Actor, ActorRef, ActorSystem, OneForOneStrategy, Props, Terminated}
import akka.testkit.{ImplicitSender, TestKit}
import org.scalatest.{BeforeAndAfterAll, WordSpecLike}

class SupervisionSpec extends TestKit(ActorSystem("SupervisionSpec"))
  with ImplicitSender with WordSpecLike with BeforeAndAfterAll {
  override def afterAll(): Unit = {
    TestKit.shutdownActorSystem(system)
  }

  import SupervisionSpec._
  "A supervision " should {
    "a resume its child in case of minor error" in {
      val supervisor=system.actorOf(Props[Supervisor])
      supervisor ! Props[FuzzyWordCount]
      val child = expectMsgType[ActorRef]

      child ! "I love akka"
      child ! Report
      expectMsg(3)

      child ! "Aaaa laaaaove aaaasdadsa sdadasd sdadsad  "
      child ! Report
      expectMsg(3)

    }
  }

  "A supervision " should {
    "restart child in case of empty sentence " in {
      val supervisor=system.actorOf(Props[Supervisor])
      supervisor ! Props[FuzzyWordCount]
      val child = expectMsgType[ActorRef]

      child ! ""
      child ! Report
      expectMsg(0)

    }
  }

  "A supervision " should {
    "terminate child in case of error" in {
      val supervisor=system.actorOf(Props[Supervisor])
      supervisor ! Props[FuzzyWordCount]
      val child = expectMsgType[ActorRef]

      watch(child)
      child ! "akka is nice"
      val terminatedMessage= expectMsgType[Terminated]
      assert(terminatedMessage.actor==child)

    }
  }

  "A supervision " should {
    "escalate on exception" in {
      val supervisor=system.actorOf(Props[Supervisor])
      supervisor ! Props[FuzzyWordCount]
      val child = expectMsgType[ActorRef]

      watch(child)
      child ! "123123213"
      val terminatedMessage= expectMsgType[Terminated]
      assert(terminatedMessage.actor==child)

    }
  }

}


object SupervisionSpec {

  class Supervisor extends Actor {
    override val supervisorStrategy = OneForOneStrategy() {
      case _: NullPointerException => Restart
      case _: IllegalArgumentException => Stop
      case _: RuntimeException => Resume
      case _: Exception => Escalate
    }

    override def receive: Receive ={
      case props: Props =>
        val childRef = context.actorOf(props)
        sender() ! childRef

    }
  }

  case object Report
  class FuzzyWordCount extends Actor {
    var words = 0

    override def receive: Receive = {
      case Report=>  sender() ! words
      case "" => throw new NullPointerException("sentences empty")
      case sentence: String =>
        if (sentence.length > 20) throw new RuntimeException("sentence is big")
        else if (!Character.isUpperCase(sentence(0))) throw new IllegalArgumentException("Sentence must wait")
        else words += sentence.split(" ").length
      case _ => throw new Exception("can only receive strings")

    }
  }

}