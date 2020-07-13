package part5infra

import akka.actor.{Actor, ActorLogging, ActorSystem, Cancellable, Props}

object TimersSchedulers extends App {

  class SimpleActor extends Actor with ActorLogging {
    override def receive: Receive = {
      case message => log.info(message.toString)
    }


  }

  val system = new ActorSystem("SchedulersTimeDemo")
  val simpleActor = system.actorOf(Props[SimpleActor])

  system.log.info("Schedulers reminder to simple actor")

  import system.dispatcher

  system.scheduler.scheduleOnce(1 second)
  {
    simpleActor ! "reminder"
  }

  val routine ! Cancellable = system.scheduler.schedule(1 second, 2 second){
    simpleActor ! "heartbeat"
  }
}
