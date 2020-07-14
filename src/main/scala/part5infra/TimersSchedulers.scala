package part5infra

import akka.actor.{Actor, ActorLogging, ActorSystem, Cancellable, Props, Timers}

import scala.concurrent.duration._

object TimersSchedulers extends App {

  class SimpleActor extends Actor with ActorLogging {
    override def receive: Receive = {
      case message => log.info(message.toString)
    }
  }

  val system = ActorSystem("SchedulersTimeDemo")
  val simpleActor = system.actorOf(Props[SimpleActor])

  system.log.info("Schedulers reminder to simple actor")

  import system.dispatcher
  // imports context implicitly
/*
     system.scheduler.scheduleOnce(1 second) {
      simpleActor ! "reminder"
    }

    // heartbeat
    val routine: Cancellable = system.scheduler.schedule(1 second, 2 second) {
      simpleActor ! "heartbeat"
    }

    system.scheduler.scheduleOnce(5 seconds) {
      routine.cancel()
    }
*/

  /// not use ref in sched
  /// schedulers are not for months messages

  /*
  implement self closing actor
  1-if actor receives one message, respond with another with one sec
  2-if window expires, actor will stop
  3-if send another mess, time windows is reset
   */

  class SelfClosingActor extends Actor with ActorLogging {
    var schedule = createTimeoutWindow()

    def createTimeoutWindow(): Cancellable = {
      context.system.scheduler.scheduleOnce(1 second) {
        self ! "timeout"
      }
    }


    override def receive: Receive = {
      case "timeout" =>
        log.info("Stopping myself")
        context.stop(self)
      case message =>
        log.info(s"Received $message, staying alive")
        schedule.cancel()
        schedule = createTimeoutWindow()
    }
  }

/*  val selfClosingActor = system.actorOf(Props[SelfClosingActor])
  system.scheduler.scheduleOnce(250 millis) {
    selfClosingActor ! "ping"
  }

  system.scheduler.scheduleOnce(2 seconds) {
    system.log.info("sending pong message")
    selfClosingActor ! "pong" //will never reach
  }*/

  case object TimerKey
  case object Start
  case object Stop
  case object Reminder
  class TimerBasedHeartbeatActor extends Actor with ActorLogging with Timers{
    timers.startSingleTimer(TimerKey, Start, 500 millis )

    override def receive: Receive = {
      case Start =>
        log.info("Bootstrapping")
        timers.startPeriodicTimer(TimerKey, Reminder, 1 second)
      case Reminder=>
        log.info("im alive")
      case Stop=>
        log.warning("stop")
        timers.cancel(TimerKey)
        context.stop(self)
    }


  }

  val timerBasedHeartbeatActor = system.actorOf(Props[TimerBasedHeartbeatActor], "timerActor")
  system.scheduler.scheduleOnce(5 seconds){
    timerBasedHeartbeatActor ! Stop
  }


}
