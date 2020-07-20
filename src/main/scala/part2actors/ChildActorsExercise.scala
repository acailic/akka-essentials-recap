package part2actors

import akka.actor.Actor

object ChildActorsExercise extends App  {

  // distributed word counting

  object WordCounterMaster {
    case class Initialize(nChildren: Int)

  }


  class WordCounterMaster extends Actor {

    override def receive: Receive = {

    }


  }
}
