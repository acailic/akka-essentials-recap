package part5infra

import akka.actor.{Actor, ActorLogging, ActorSystem, Cancellable, Props}
import scala.concurrent.duration._

object TimersSchedulers extends App {

  class SimpleActor extends Actor with ActorLogging {
    override def receive: Receive = {
      case message => log.info(message.toString)
    }
  }

  val system =   ActorSystem("SchedulersTimeDemo")
  val simpleActor = system.actorOf(Props[SimpleActor])

  system.log.info("Schedulers reminder to simple actor")

  import system.dispatcher
  // imports context implicitly
  system.scheduler.scheduleOnce(1 second)
  {
    simpleActor ! "reminder"
  }

  // heartbeat
  val routine: Cancellable = system.scheduler.schedule(1 second, 2 second){
    simpleActor ! "heartbeat"
  }

}
