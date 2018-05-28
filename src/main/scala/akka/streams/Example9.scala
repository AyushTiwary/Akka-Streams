package akka.streams

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.stream.scaladsl._

object Example9 extends App {

  implicit val system = ActorSystem("Example9")
  implicit val materializer = ActorMaterializer()
  implicit val ec = system.dispatcher //ExecutionContext

  val source1 = Source(1 to 3)
  val source2 = Source(1 to 3)

  val done = source1.runForeach(a => source2.runForeach(b => print(a * b + "  ")))

  done.onComplete(_ ⇒ system.terminate())
  system.whenTerminated.onComplete(_ => println("actor-system is terminated"))
}
