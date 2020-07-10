package part4faulttolerance

import akka.actor.SupervisorStrategy.{Escalate, Restart, Resume, Stop}
import akka.actor.{Actor, OneForOneStrategy, Props}
import akka.testkit.{ImplicitSender, TestKit}
import org.scalatest.{BeforeAndAfterAll, WordSpecLike}

class SupervisionSpec extends TestKit("SupervisionSpec")
  with ImplicitSender with WordSpecLike with BeforeAndAfterAll {
  override def afterAll(): Unit = {
    TestKit.shutdownActorSystem(system)
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


  class FuzzyWordCount extends Actor {
    var words = 0

    override def receive: Receive = {
      case "" => throw new NullPointerException("sentences empty")
      case sentence: String =>
        if (sentence.length > 20) throw new RuntimeException("sentence is big")
        else if (!Character.isUpperCase(sentence(0))) throw new IllegalArgumentException("Sentence must wait")
        else words += sentence.split(" ").length
      case _ => throw new Exception("can only receive strings")

    }
  }

}