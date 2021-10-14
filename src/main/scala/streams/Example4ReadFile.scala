package streams

import java.nio.file.Paths
import java.security.MessageDigest

import akka.actor.ActorSystem
import akka.stream.scaladsl._
import akka.util.ByteString

import scala.concurrent.ExecutionContext.Implicits._
import scala.concurrent.Future

object Example4ReadFile extends App {
  implicit val akka = ActorSystem()

  val lotstohash = Paths.get("lotstohash")

  val tabulatedSource =
    Source.fromIterator(() => (1 to 100000).toIterator)
      .map(_.toString)

  val fileSource =
    FileIO.fromPath(lotstohash)
      .via(Framing.delimiter(ByteString("\n"), 256, true))
      .map(_.utf8String)

//  Read from file and hash
  fileSource
    .mapAsync(2)(hash)
    .runWith(Sink.foreach(println))

  private def hash(value: String): Future[String] = Future {
    MessageDigest.getInstance("MD5")
      .digest(value.getBytes)
      .map("%02x".format(_))
      .mkString
  }
}
