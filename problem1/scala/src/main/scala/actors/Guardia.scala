package actors

import scala.concurrent.duration._
import scala.util.Random
import akka.actor._
import akka.util._

class Guardia(n: Int) extends Actor {
  import Guardia.Messages._
  import context.dispatcher

  val rnd = new scala.util.Random
  var presos: List[ActorRef] = List()
  var howmany: Set[Int] = Set()

  def receive = {
    case Start => {
      presos = (1 to (n-1) map { _ => context.actorOf(Preso.props(false, n)) }).toList
      presos = context.actorOf(Preso.props(true, n)) :: presos // Create the master

      context become contextOffOn(false) // start off
      self ! Ask
    }
  }

  def awaitingResponse: Receive = {
    case Answer(newstate, true) => {
      if (howmany.size == presos.length) {
        println("You are free!")
        self ! End
      } else {
        println("You all die")
        self ! End
      }
    }
    case Answer(newstate, false) => {
      context become contextOffOn(newstate)
      self ! Ask
    }
    case End => context.system.shutdown
  }

  def contextOffOn(state: Boolean): Receive = {
    case Ask => {
      val next = rnd.nextInt(n)
      presos.lift(next) match {
        case Some(preso) => {
          howmany = howmany + next
          context become awaitingResponse
          preso ! Preso.Messages.YouAreIn(state)
        }

        case None => {
          println("WTF the random")
          self ! End
        }
      }
    }
    case End => context.system.shutdown
  }
}

object Guardia {
  sealed trait Messages
  object Messages {
    case object Start extends Messages
    case object End extends Messages
    case object Ask
    case class Answer(newstate: Boolean, answer: Boolean) extends Messages
  }
  def props(n: Int): Props = Props(new Guardia(n))
}
