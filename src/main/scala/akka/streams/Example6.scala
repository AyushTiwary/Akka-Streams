package akka.streams

import akka.NotUsed
import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.stream.scaladsl._

object Example6 extends App {

  implicit val system = ActorSystem("Example6")
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


  val authors: Source[String, NotUsed] =
    tweets
      .filter(_.hashTags.contains(akkaTag))
      .map(_.author.handle)

  println("Authors who tweet #akka ...!!!!!!!!")
  val done =
    authors.runWith(Sink.foreach(println))

  done.onComplete(_ ⇒ system.terminate())
  system.whenTerminated.onComplete(_ => println("actor-system is terminated...!!!!"))
}
