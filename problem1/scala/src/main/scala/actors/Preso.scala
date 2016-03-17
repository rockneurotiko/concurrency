package actors

import scala.concurrent.duration._
import scala.util.Random
import akka.actor._
import akka.util._


class Preso(master: Boolean, presos: Int) extends Actor {
  import context.dispatcher
  import Preso.Messages._

  override def preStart() {
    if (master) {
      context become masterCount(0) // I count!
    } else {
      context become notMaster(false) // I just turn on once
    }
  }

  def receive: Receive = {
    case _ =>
  }

  def notMaster(saw: Boolean): Receive = {
    case YouAreIn(true) => context.parent ! Guardia.Messages.Answer(true, false) // just keep going
    case YouAreIn(false) => {
      if (!saw) {
        println("Turn on! First time")
        context become notMaster(true)
        context.parent ! Guardia.Messages.Answer(true, false) // turn off
      } else {
        context.parent ! Guardia.Messages.Answer(false, false) // whatever
      }
    }
  }

  def masterCount(n: Int): Receive = {
    case YouAreIn(false) => context.parent ! Guardia.Messages.Answer(false, false) // just keep going
    case YouAreIn(true) => {
      val newVistos = n + 1
      if (newVistos >= (presos - 1)) {
        println("We all are in!")
        context.parent ! Guardia.Messages.Answer(false, true) // We are done!
      } else {
        println(s"We are not yet: ${newVistos}/${presos - 1}")
        context become masterCount(newVistos)
        context.parent ! Guardia.Messages.Answer(false, false)
      }
    }
  }
}

object Preso {
  sealed trait Messages
  object Messages {
    case class YouAreIn(state: Boolean) extends Messages
  }
  def props(master: Boolean, n: Int): Props = Props(new Preso(master, n))
}
