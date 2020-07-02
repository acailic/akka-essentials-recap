package part2actors

import akka.actor.{Actor, ActorSystem, Props}

object ActorsIntro extends App {

  //part1- actor system
  // heavy weight data structure that controls number of threads under  the hood
  // allocates running actors
  val actorSystem = ActorSystem("firstActorSystem")
  println(actorSystem.name)

  //part2-create actors
  // like humans talking to each other
  // actors are unique identified
  // messages are asynchronous
  //each actor may respond diff
  //actors are encapsulated


  // word counter actor
  class WordCountActor extends Actor {
    var totalWords = 0
    //internal data
    def receive: PartialFunction[Any, Unit] = {
      case message: String =>
        println(s"[wordCounter]-- i have received a message: $message")
        totalWords += message.split(" ").length
      case msg => println(s"[] not understand ${msg.toString}")
    }
  }

  //part3 - instantiate our actor
  // create by invoking actor
  // creates a reference of actor - exposed by akka
  val wordCounter = actorSystem.actorOf(Props[WordCountActor], "wordCounter")
  val anotherWordCounter = actorSystem.actorOf(Props[WordCountActor], "anotherWordCounter")

  ///part4
  // communicate with actor
  // postfix, like a method, wordCounter.!("i am learning akka we will if is it coo")
  wordCounter ! "i am learning akka we will if is it cool"
  anotherWordCounter ! "i am learning akka we will if is it cool"

  //displays asynchronous messages

  // new WordActor -- not possible
  // only actorOf
  // encapsulated

  // instantiated class of actor ??? -- by using props with argument


  class Person(str: String) extends Actor{
    override def receive: Receive = {
      case "hi"=> println(s"Hi my name is $str")
      case _ =>
    }
  }

  //discouraged
  val person = actorSystem.actorOf(Props(new Person("bob")))
  person ! "hi"

  //advantage: factory method of props method
  object  Person {
    def props(name: String) = Props(new Person(name))
  }
  //best practice
  val coa = actorSystem.actorOf(Person.props("bob"))


}
