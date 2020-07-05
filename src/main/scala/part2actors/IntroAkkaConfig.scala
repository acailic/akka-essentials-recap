package part2actors

import akka.actor.{Actor, ActorLogging, ActorSystem, Props}
import com.typesafe.config.ConfigFactory

object IntroAkkaConfig extends App {

  class SimpleActorLogging extends Actor with ActorLogging{
    override def receive: Receive = {
      case message => log.info(message.toString)
    }
  }

  /*
  Inline configuration
   */

  val configString =
    """
      | akka {
      |   logLevel="DEBUG"
      | }
    """.stripMargin

  val config = ConfigFactory.parseString(configString)
  val system = ActorSystem("ConfigurationDemo", ConfigFactory.load(config))
  val actor = system.actorOf(Props[SimpleActorLogging])

  actor ! "A message to remember"



  val defaultConfigFileSystem = ActorSystem("DefaultConfigurationDemo")
  val defaultConfigActor =  defaultConfigFileSystem.actorOf(Props[SimpleActorLogging])
  defaultConfigActor ! " remember me "


}
