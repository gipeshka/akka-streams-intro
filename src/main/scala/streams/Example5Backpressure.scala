package streams

import java.nio.file.Paths
import java.security.MessageDigest

import akka.actor.ActorSystem
import akka.pattern.after
import akka.stream.alpakka.file.scaladsl.FileTailSource
import akka.stream.scaladsl._
import akka.util.ByteString

import scala.concurrent.ExecutionContext.Implicits._
import scala.concurrent.Future
import scala.concurrent.duration._
import scala.util.{Failure, Success}

object Example5Backpressure extends App {
  implicit val akka = ActorSystem()

  val lotstohash = Paths.get("lotstohash")

  val tabulatedSource =
    Source.fromIterator(() => (1 to 100000).toIterator)
      .map(_.toString)

  val fileTailSource =
    FileTailSource.lines(
      path = lotstohash,
      maxLineSize = 8192,
      pollingInterval = 250.millis
    )

//  Store to file
  tabulatedSource
    .mapAsyncUnordered(2)(slowHash)
    .map(_ + "\n")
    .map(ByteString(_))
    .runWith(FileIO.toPath(lotstohash))

//  Tail consumer
  Thread.sleep(1000)
  fileTailSource
    .runWith(Sink.foreach(hash => println(s"Reading from file $hash")))
    .onComplete {
      case Success(_) =>
        println("Done")
      case Failure(e) =>
        println(s"Failed due to ${e.getMessage}")
    }

  private def hash(value: String): Future[String] = Future {
    MessageDigest.getInstance("MD5")
      .digest(value.getBytes)
      .map("%02x".format(_))
      .mkString
  }

  private def slowHash(value: String): Future[String] = after(1.second)(hash(value))
}
