package playground

import akka.actor.ActorSystem

object Playground extends App {

  var actorSystem = ActorSystem("HelloAkka")
  println(actorSystem.name)
}
