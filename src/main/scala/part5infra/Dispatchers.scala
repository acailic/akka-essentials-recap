package part5infra

import akka.actor.{Actor, ActorLogging, ActorSystem, Props}

import scala.concurrent.{ExecutionContext, Future}
import scala.util.Random

object Dispatchers extends App {

  class Counter extends Actor with ActorLogging {
    var count = 0

    override def receive: Receive = {
      case message =>
        count += 1
        log.info(s"[$count] $message")
    }
  }

  val system = ActorSystem("DispatchersDemo" )//, ConfigFactory.load().getConfig("dispatchersDemo"))

  val simpleCounterActor = system.actorOf(Props[Counter].withDispatcher("my-dispatcher"))

  //method 1 -- programmatic in code
  val actors = for (i <- 1 to 10) yield system.actorOf(Props[Counter].withDispatcher("my-dispatcher"),s"counter_$i")
  val r = new Random()
  for (i <-1 to 1000){
    actors(r.nextInt(10)) ! i
  }


  // method 2  - from config

  val rtjvmActor = system.actorOf(Props[Counter],"rtjvm")
  //behavior the same

  /*
   Dispatchers implements  execution context trait
   */

  class DBActor extends Actor with ActorLogging {
    // #1 solution
    implicit  val executionContext: ExecutionContext  = context.dispatcher
    // #2 router

    override def receive: Receive = {
      case message => Future {  //bad practice
        // wait on resource
        Thread.sleep(5000)
        log.info(s"Success: $message")
      }
    }
  }

  val dbActor = system.actorOf(Props[DBActor])
  //dbActor ! "opa opa opa"

  val nonBlockingActor = system.actorOf(Props[Counter])
  for ( i <- 1 to 1000){
      val message = s"important message $i"
      dbActor ! message
      nonBlockingActor ! message
  }
}
