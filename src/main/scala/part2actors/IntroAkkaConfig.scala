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
    1. Inline configuration
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

  /*
    2. config file
   */


  val defaultConfigFileSystem = ActorSystem("DefaultConfigurationDemo")
  val defaultConfigActor =  defaultConfigFileSystem.actorOf(Props[SimpleActorLogging])
  defaultConfigActor ! " remember me "


  /*
  3.  separate config in the same file
 */
  val specialConfig = ConfigFactory.load().getConfig("mySpecialConfig")
  val specialConfigFileSystem = ActorSystem("SpecialConfigurationDemo",specialConfig)
  val specialConfigActor = specialConfigFileSystem.actorOf(Props[SimpleActorLogging])
  specialConfigActor ! " remember special me  "


  /*
4.  separate config in the another file
*/
}
