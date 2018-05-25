package akka.streams



import akka.NotUsed
import akka.actor.ActorSystem
import akka.stream._
import akka.stream.scaladsl._

object Example1 extends App{

  implicit val system = ActorSystem("Example1")
  implicit val materializer = ActorMaterializer()

  val source: Source[Int, NotUsed] = Source(1 to 100)

  source.runForeach(i ⇒ println(i))(materializer)

}
