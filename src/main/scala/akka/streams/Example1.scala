package akka.streams

import akka.actor.ActorSystem
import akka.stream._
import akka.stream.scaladsl._

object Example1 extends App {

  implicit val system = ActorSystem("Example1")
  implicit val materializer = ActorMaterializer()
  implicit val ec = system.dispatcher //ExecutionContext

  val source = Source.unfold(0 -> 1) {
    case (a, _) if a > 10000000 => None
    case (a, b) => Some((b -> (a + b)) -> a)
  }

  val done = source.runForeach(i ⇒ print(i + "   "))(materializer)

  done.onComplete(_ ⇒ system.terminate())
  system.whenTerminated.onComplete(_ => println("actor-system is terminated"))

}
