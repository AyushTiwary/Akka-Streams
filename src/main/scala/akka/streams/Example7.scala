package akka.streams

import java.nio.file.Paths

import akka.NotUsed
import akka.actor.ActorSystem
import akka.stream.scaladsl._
import akka.stream.{ActorMaterializer, ClosedShape}
import akka.util.ByteString

/**
  * Broadcasting a stream
  * */
//Todo(ayush) read out more about it
object Example7 extends App {

  implicit val system = ActorSystem("Example7")
  implicit val materializer = ActorMaterializer()
  implicit val ec = system.dispatcher

  final case class Author(handle: String)

  final case class HashTag(name: String)

  final case class Tweet(author: Author, timestamp: Long, body: String) {
    def hashTags: Set[HashTag] = body.split(" ").collect {
      case t if t.startsWith("#") ⇒ HashTag(t.replaceAll("[^#\\w]", ""))
    }.toSet
  }

  val akkaTag = HashTag("#akka")

  val writeAuthors: Sink[Author, NotUsed] =
    Flow[Author]
    .map(s ⇒ ByteString(s + "\n"))
    .toMat(FileIO.toPath(Paths.get("target/results/author7.txt")))(Keep.left)

  val writeHashTags: Sink[HashTag, NotUsed] = Flow[HashTag]
    .map(s ⇒ ByteString(s + "\n"))
    .toMat(FileIO.toPath(Paths.get("target/results/hashtag7.txt")))(Keep.left)

  val runnableGraph = RunnableGraph.fromGraph(GraphDSL.create() { implicit b =>
    import akka.stream.scaladsl.GraphDSL.Implicits._

    val tweets: Source[Tweet, NotUsed] = Source(
      Tweet(Author("rolandkuhn"), System.currentTimeMillis, "#akka rocks!") ::
        Tweet(Author("patriknw"), System.currentTimeMillis, "#akka !") ::
        Tweet(Author("bantonsson"), System.currentTimeMillis, "#akka !") ::
        Tweet(Author("drewhk"), System.currentTimeMillis, "#akka !") ::
        Tweet(Author("ktosopl"), System.currentTimeMillis, "#akka on the rocks!") ::
        Tweet(Author("mmartynas"), System.currentTimeMillis, "wow #akka !") ::
        Tweet(Author("akkateam"), System.currentTimeMillis, "#akka rocks!") ::
        Tweet(Author("bananaman"), System.currentTimeMillis, "#bananas rock!") ::
        Tweet(Author("appleman"), System.currentTimeMillis, "#apples rock!") ::
        Tweet(Author("drama"), System.currentTimeMillis, "we compared #apples to #oranges!") ::
        Nil)

    val bcast = b.add(Broadcast[Tweet](2))
    tweets ~> bcast.in
    bcast.out(0) ~> Flow[Tweet].map(_.author) ~> writeAuthors
    bcast.out(1) ~> Flow[Tweet].mapConcat(_.hashTags.toList) ~> writeHashTags
    ClosedShape
  })
  runnableGraph.run()

  Thread.sleep(2000)
  system.terminate()
  system.whenTerminated.onComplete(_ => println("actor-system is terminated...!!!!"))
}
