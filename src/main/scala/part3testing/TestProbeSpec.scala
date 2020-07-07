package part3testing

import akka.actor.{Actor, ActorRef, ActorSystem, Props}
import akka.testkit.{ImplicitSender, TestKit, TestProbe}
import org.scalatest.{BeforeAndAfterAll, WordSpecLike}

class TestProbeSpec extends TestKit(ActorSystem("TestProbeSpec"))
  with ImplicitSender
  with WordSpecLike
  with BeforeAndAfterAll {

  override def afterAll(): Unit = {
    TestKit.shutdownActorSystem(system)
  }

  import TestProbeSpec._

  "A master actor" should {
    "register in slave" in {
      val master = system.actorOf(Props[Master])
      val slave = TestProbe("slave")

      master ! Register(slave.ref)
      expectMsg(RegistrationTrack)
    }
  }
  "Send work " should {
    "register in slave" in {
      val master = system.actorOf(Props[Master])
      val slave = TestProbe("slave")

      master ! Register(slave.ref)
      expectMsg(RegistrationTrack)
      val workLoadString = "protests in belgrade"
      master ! Work(workLoadString)

      //interation beetween master and slave
      slave.expectMsg(SlaveWork(workLoadString, testActor))

      slave.reply(WorkCompleted(3, testActor))

      expectMsg(Report(3))  // testActor receives Report
    }
  }
}


object TestProbeSpec {

  /// scenario
  /*
    word counting hierarchy master slave

    send some work to master
    -   master send to slave piece work to
    -   slave process to work and replies to master
    -   master aggregates the result
    -   master send total count  of the original requester

   */

  case class Register(slaveRef: ActorRef)

  case class SlaveWork(text: String, originalRequestor: ActorRef)

  case class WorkCompleted(count: Int, originalRequestor: ActorRef)

  case class Work(text: String)

  case class Report(number: Int)

  case object RegistrationTrack


  class Master extends Actor {
    override def receive: Receive = {
      case Register(slaveRef) =>
        sender ! RegistrationTrack
        context.become(onLine(slaveRef, 0))
      case _ => ///ignore

    }

    def onLine(slaveRef: ActorRef, totalWordCount: Int): Receive = {
      case Work(text) => slaveRef ! SlaveWork(text, sender())
      case WorkCompleted(count, originalRequestor) =>
        val newTotalWordCount = totalWordCount + count
        originalRequestor ! Report(newTotalWordCount)
        context.become(onLine(slaveRef, newTotalWordCount))
    }
  }


}