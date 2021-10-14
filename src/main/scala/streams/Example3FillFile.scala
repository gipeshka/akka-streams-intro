package streams

import java.nio.file.Paths
import java.security.MessageDigest

import akka.actor.ActorSystem
import akka.stream.scaladsl._
import akka.util.ByteString

import scala.concurrent.ExecutionContext.Implicits._
import scala.concurrent.Future
import scala.util.{Failure, Success}

object Example3FillFile extends App {
  implicit val akka = ActorSystem()

  val lotstohash = Paths.get("lotstohash")

  val tabulatedSource =
    Source.fromIterator(() => (1 to 100000).toIterator)
      .map(_.toString)

  tabulatedSource
    .mapAsyncUnordered(2)(hash)
    .map(_ + "\n")
    .map(ByteString(_))
    .runWith(FileIO.toPath(lotstohash))
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
}
