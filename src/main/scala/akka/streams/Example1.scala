package akka.streams

import akka.actor.ActorSystem
import akka.stream._
import akka.stream.scaladsl._

object Example1 extends App {

  implicit val system = ActorSystem("Example1")
  implicit val materializer = ActorMaterializer()
  implicit val ec = system.dispatcher //ExecutionContext

  val source = Source(1 to 100)

  val done = source.runForeach(i ⇒ println(i))(materializer)

  done.onComplete(_ ⇒ system.terminate())
  system.whenTerminated.onComplete(_ => println("actor-system is terminated"))

}
