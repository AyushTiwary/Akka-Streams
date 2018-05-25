package akka.streams

import java.nio.file.Paths

import akka.actor.ActorSystem
import akka.stream._
import akka.stream.scaladsl._
import akka.util.ByteString

import scala.concurrent.Future

object Example2 extends App {

  implicit val system = ActorSystem("Example2")
  implicit val materializer = ActorMaterializer()
  implicit val ec = system.dispatcher

  val source = Source(1 to 10)

  val factorials = source.scan(BigInt(1))((acc, next) ⇒ acc * next)

  val result: Future[IOResult] =
    factorials
      .map(num ⇒ ByteString(s"$num\n"))
      .runWith(FileIO.toPath(Paths.get("factorials.txt")))

  result.onComplete(_ ⇒ system.terminate())

  system.whenTerminated.onComplete(_ => println("actor-system is terminated"))
}
