package akka.streams

import akka.actor.ActorSystem
import akka.stream._
import akka.stream.scaladsl._

import scala.concurrent.duration._

object Example5 extends App {

  implicit val system = ActorSystem("Example5")
  implicit val materializer = ActorMaterializer()
  implicit val ec = system.dispatcher

  val source = Source(1 to 10)

  val factorials = source.scan(BigInt(1))((acc, next) ⇒ acc * next)

val done =
  factorials
    .zipWith(Source(0 to 5))((num, idx) ⇒ s"$idx! = $num")
    .throttle(2, 4.second)
    .runForeach(println)

  done.onComplete(_ ⇒ system.terminate())

  system.whenTerminated.onComplete(_ => println("actor-system is terminated"))
}
