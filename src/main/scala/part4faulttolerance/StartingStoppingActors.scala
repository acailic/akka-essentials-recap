package part4faulttolerance

import akka.actor.{Actor, ActorLogging, ActorRef, ActorSystem, Props}

object StartingStoppingActors extends App {

  val system = ActorSystem("DemoStartingStoppingActors")

  object Parent{
    case class StartChild(name: String)
    case class StopChild(name: String)
    case object Stop
  }

  class Parent extends Actor with ActorLogging  {
    import Parent._

    override def receive: Receive = withChildren(Map())

    def withChildren(children: Map [String,ActorRef]):Receive = {
      case StartChild(name) =>
        log.info(s"Starting child $name")
        context.become(withChildren(children+(name->context.actorOf(Props[Child],name))))
      case StopChild(name) =>
        log.info(s"Stopping child $name")
        val childOption= children.get(name)
        childOption.foreach(childRef=> context.stop(childRef))
    }
  }

  class Child extends Actor with ActorLogging {
    override def receive: Receive = {
      case message => log.info(message.toString)
    }
  }


  import Parent._
  val parent = system.actorOf(Props[Parent],"parent")
  parent ! StartChild("child1")
  val child = system.actorSelection("/user/parent/child1")
  child ! "Hi kid"

}
