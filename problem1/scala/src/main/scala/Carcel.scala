import actors.Guardia
import actors.Guardia.Messages._

import akka.actor.ActorSystem
import akka.actor.Props

object Carcel {
  def main(args: Array[String]): Unit = {
    val system = ActorSystem("Carcel")


    val helloActor = system.actorOf(Guardia.props(1000), name = "Guardia")
    helloActor ! Start

    // val initialActor = classOf[Guardia].getName
    // akka.Main.main(Array(initialActor))
  }
}
