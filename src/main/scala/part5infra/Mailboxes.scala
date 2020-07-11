package part5infra

import akka.actor.{Actor, ActorLogging, ActorSystem, PoisonPill, Props}
import akka.dispatch.{ControlMessage, PriorityGenerator, UnboundedPriorityMailbox}
import com.typesafe.config.{Config, ConfigFactory}

object Mailboxes extends App {

  val system = ActorSystem("MailboxDemo", ConfigFactory.load().getConfig("mailboxesDemo"))

  class SimpleActor extends Actor with ActorLogging {
    override def receive: Receive = {
      case message => log.info(message.toString)
    }

  }

  /*
    Interesting case #1 - custom priority mailbox
    P0 -> most important
    P1
    P2
    P3
   */

  // step 1 mailbox definition
  class SupportTicketPriorityMailbox(settings: ActorSystem.Settings, config: Config)
    extends UnboundedPriorityMailbox(
      PriorityGenerator {
        case message: String if message.startsWith("[P0]") => 0
        case message: String if message.startsWith("[P1]") => 1
        case message: String if message.startsWith("[P2]") => 2
        case message: String if message.startsWith("[P3]") => 3
        case _ => 4
      }
    )

    // step 2 mailbox definition into config
    // step 3 attach  the dispatcher to actor

    val supportTicketLogger = system.actorOf(Props[SimpleActor].withDispatcher("support-ticket-dispatcher"))
    supportTicketLogger ! PoisonPill
    //Thread.sleep(1000)
    supportTicketLogger ! "[P3] this thing would be nice to have"
    supportTicketLogger ! "[P0] solve it now"
    supportTicketLogger ! "[P1] do this when you have time"

    // after which time can i send another message and be prioritized
    // dont know when is thread is allocated and will handle and its not possible to configure
    //

  /*
    case 2 control-aware mailbox
    no limit aware mailbox
  */

  //step 1
  // mark important messages as control messages
  case object ManagementTicket extends ControlMessage

  //step 2 -  configure who gets the mailbox
  //  ma the actor attache the mailbox
  val controlAwareActor = system.actorOf(Props[SimpleActor].withMailbox("control-mailbox"))

/*  controlAwareActor ! "[P0] this needs to be solved now"
  controlAwareActor ! "[P1] this will be solved now"
  controlAwareActor ! ManagementTicket*/

  //method #2 - using deployment config
  val altControlAwareActor = system.actorOf(Props[SimpleActor],"altControlAwareActor")

  controlAwareActor ! "[P0] this needs to be solved now"
  controlAwareActor ! "[P1] this will be solved now"
  controlAwareActor ! ManagementTicket

}

