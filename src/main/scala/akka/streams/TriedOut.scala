package akka.streams

import akka.NotUsed
import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.stream.scaladsl._

object TriedOut extends App {

  implicit val system = ActorSystem("actor-system")
  implicit val materializer = ActorMaterializer()
  implicit val ec = system.dispatcher //ExecutionContext

  // Explicitly creating and wiring up a Source, Sink and Flow
  val runnable = Source(1 to 6).via(Flow[Int].map(_ * 2)).to(Sink.foreach(println(_)))

  // Starting from a Sink
  val sink: Sink[Int, NotUsed] = Flow[Int].map(_ * 2).to(Sink.foreach(println(_)))
  Source(1 to 6).to(sink)

  // Broadcast to a sink inline
  val otherSink: Sink[Int, NotUsed] = Flow[Int].alsoTo(Sink.foreach(println(_))).to(Sink.ignore)
  Source(1 to 6).to(otherSink)

  Source(List(1, 2, 3))
    .map(_ + 1).async
    .map(_ * 2)
    .to(Sink.foreach(println(_)))

}