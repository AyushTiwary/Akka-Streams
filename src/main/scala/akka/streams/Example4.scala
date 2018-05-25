package akka.streams

import java.nio.file.Paths

import akka.actor.ActorSystem
import akka.stream.scaladsl.{FileIO, Flow, Keep, Sink, Source}
import akka.stream.{ActorMaterializer, IOResult}
import akka.util.ByteString

import scala.concurrent.Future

object Example4 extends App {

  implicit val system = ActorSystem("Example4")
  implicit val materializer = ActorMaterializer()
  implicit val ec = system.dispatcher

  val source = Source(1 to 10)
  val factorials = source.scan(BigInt(1))((acc, next) ⇒ acc * next)

  def lineSink(filename: String): Sink[String, Future[IOResult]] =
    Flow[String]
      .map(s ⇒ ByteString(s + "\n"))
      .toMat(FileIO.toPath(Paths.get(filename)))(Keep.right)

  val result = factorials.map(_.toString).runWith(lineSink("factorial2.txt"))

  result.onComplete(_ ⇒ system.terminate())

  system.whenTerminated.onComplete(_ => println("actor-system is terminated"))
}
