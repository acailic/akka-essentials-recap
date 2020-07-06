package part3testing

import akka.actor.{Actor, ActorSystem, Props}
import akka.testkit.{ImplicitSender, TestKit}
import org.scalatest.{BeforeAndAfterAll, WordSpecLike}

import scala.concurrent.duration._
import scala.util.Random

class BasicSpec extends TestKit(ActorSystem("BasicSpec"))
  with ImplicitSender
  with WordSpecLike
  with BeforeAndAfterAll {

  //setup
  override def afterAll(): Unit = {
    TestKit.shutdownActorSystem(system)
  }

  "The thing should be tested" should {
    "do this" in {
      //test scenario
    }
  }

  import BasicSpec._

  "a simple actor" should {
    " send back message " in {
      val echoActor = system.actorOf(Props[SimpleActor])
      val message = "hello test"
      echoActor ! message
      expectMsg(message) // akka.test.single-expect-default 3s
      //testActor is behind these messages
    }
  }

  "a black hole actor" should {
    " send back message " in {
      val blackHole = system.actorOf(Props[BlackHoleActor])
      val message = "hello test"
      blackHole ! message
      expectNoMessage(1 second)
    }
  }
  //message assertions

  "a lab test actor" should {
    val labTestActor = system.actorOf(Props[LabTestActor])
    " send back uppercase " in {

      labTestActor ! "i love akka"
      val reply = expectMsgType[String]
      assert(reply == "I LOVE AKKA")
    }

    "reply to greeting" in {
      labTestActor ! "greeting"
      expectMsgAnyOf("hi", "hello")
    }

    "reply to favorite tech" in {
      labTestActor ! "favoriteTech"
      expectMsgAllOf("Java", "Scala")
    }

    "reply with cool tech with fancy way " in {
      labTestActor ! "favoriteTech"
      expectMsgPF(){
        case "Java" => // usually used in different ways
        case "Scala" =>
      }
    }
  }
}

object BasicSpec {

  class SimpleActor extends Actor {
    override def receive: Receive = {
      case message => sender() ! message
    }
  }

  class BlackHoleActor extends Actor {
    override def receive: Receive = Actor.emptyBehavior
  }

  class LabTestActor extends Actor {
    val random = new Random()

    override def receive: Receive = {
      case "greeting" =>
        if (random.nextBoolean()) sender() ! "hi" else sender() ! "hello"
      case "favoriteTech" =>
        sender() ! "Java"
        sender() ! "Scala"
      case message: String => sender() ! message.toUpperCase()
    }
  }

}

