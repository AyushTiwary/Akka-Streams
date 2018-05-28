package akka.streams

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.stream.scaladsl._

import scala.concurrent.Future

object Example8 extends App {

  implicit val system = ActorSystem("Example8")
  implicit val materializer = ActorMaterializer()
  implicit val ec = system.dispatcher //ExecutionContext

  val source = Source(1 to 10)
  val sink = Sink.fold[Int, Int](0)(_ + _)

  //method1
  val sum: Future[Int] = source.runWith(sink)

  //method2
/*  val runnable: RunnableGraph[Future[Int]] = source.toMat(sink)(Keep.right)

  val sum: Future[Int] = runnable.run()*/

  sum.map(println)

  sum.onComplete(_ ⇒ system.terminate())
  system.whenTerminated.onComplete(_ => println("actor-system is terminated"))
}
