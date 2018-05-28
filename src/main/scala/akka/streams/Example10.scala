package akka.streams

import akka.NotUsed
import akka.actor.ActorSystem
import akka.stream.scaladsl._
import akka.stream.{ActorMaterializer, ClosedShape}

object Example10 extends App {

  implicit val system = ActorSystem("Example10")
  implicit val materializer = ActorMaterializer()
  implicit val ec = system.dispatcher

  val graph = RunnableGraph.fromGraph(GraphDSL.create() { implicit builder: GraphDSL.Builder[NotUsed] =>
    import GraphDSL.Implicits._
    val in = Source(1 to 10)
    val out = Sink.foreach(println)

    val bcast = builder.add(Broadcast[Int](2))
    val merge = builder.add(Merge[Int](2))

    val f1, f2, f3, f4, f5 = Flow[Int].map(_ + 10)

    in ~> f1 ~> bcast ~> f2 ~> merge ~> f3 ~> out
    bcast ~> f4 ~> f5 ~> merge
    ClosedShape
  })
  graph.run()

  Thread.sleep(2000)
  system.terminate()
  system.whenTerminated.onComplete(_ => println("actor-system is terminated...!!!!"))
}
