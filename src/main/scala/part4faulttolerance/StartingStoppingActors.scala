package part4faulttolerance

import akka.actor.{Actor, ActorLogging, ActorRef, ActorSystem, PoisonPill, Props, Terminated}

object StartingStoppingActors extends App {

  val system = ActorSystem("DemoStartingStoppingActors")

  object Parent {

    case class StartChild(name: String)

    case class StopChild(name: String)

    case object Stop

  }

  class Parent extends Actor with ActorLogging {

    import Parent._

    override def receive: Receive = withChildren(Map())

    def withChildren(children: Map[String, ActorRef]): Receive = {
      case StartChild(name) =>
        log.info(s"Starting child $name")
        context.become(withChildren(children + (name -> context.actorOf(Props[Child], name))))
      case StopChild(name) =>
        log.info(s"Stopping child $name")
        val childOption = children.get(name)
        childOption.foreach(childRef => context.stop(childRef)) //its   asynchronous
      case Stop =>
        log.info("Stopping myself")
        context.stop(self) // waits until all children are stopped and then stop self
      case message => log.info(message.toString)
    }
  }

  class Child extends Actor with ActorLogging {
    override def receive: Receive = {
      case message => log.info(message.toString)
    }
  }

  /*
     1. method by context.stop
   */

  import Parent._

  val parent = system.actorOf(Props[Parent], "parent")
  parent ! StartChild("child1")
  val child = system.actorSelection("user/parent/child1")
  child ! "Hi kid"

  //parent ! StopChild("child1")
  //for (_<- 1 to 50) child ! " are you still therer ?    "

  parent ! StartChild("child2")
  val child2 = system.actorSelection("user/parent/child2")
  child2 ! "hi second child"
  parent ! Stop
  for (_ <- 1 to 10) parent ! " are you there ? "
  for (i <- 1 to 100) child ! s"[$i] second kid ? are you still alive ? "


  /*
   2. Method using special messages
   */
/*
  val loseActor = system.actorOf(Props[Child])
  loseActor ! " Hello lose actor"
  loseActor ! PoisonPill // special message to kill, there is Kill message
  loseActor ! "are you still there ?  "

  val abruptlyTerminatedActor = system.actorOf(Props[Child])
  abruptlyTerminatedActor ! " you are going to be terminated"
  abruptlyTerminatedActor ! Kill // more brutal than poison pill, ActorKilledException throw
  abruptlyTerminatedActor ! "are you still there " // not executed
*/

  /*
  Death watch

   */

  class Watcher extends Actor with ActorLogging {

    import Parent._

    override def receive: Receive = {
      case StartChild(name) =>
        val child = context.actorOf(Props[Child], name)
        log.info(s"started and watching child $name")
        context.watch(child) // registers as actor, gets exceptions and references
        // there is unwatched as well
      case Terminated(ref) =>
        log.info(s" the reference im watching  $ref has been stopped ")
    }
  }

  val watcher = system.actorOf(Props[Watcher], "watcher")
  watcher ! StartChild("childWatched")
  val watchedChild = system.actorSelection("/user/watcher/childWatched")

  Thread.sleep(500)
  watchedChild ! PoisonPill
}
