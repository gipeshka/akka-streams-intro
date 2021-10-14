package streams

import java.nio.file.Paths

import akka.actor.ActorSystem
import akka.pattern.after
import akka.stream.scaladsl._
import java.security.MessageDigest

import akka.stream.alpakka.file.scaladsl.FileTailSource
import akka.util.ByteString

import scala.concurrent.ExecutionContext.Implicits._
import scala.concurrent.Future
import scala.concurrent.duration._
import scala.util.{Failure, Success}

object Main extends App {
  implicit val akka = ActorSystem()

  val lotstohash = Paths.get("lotstohash")

  val graph =
    Source(List.tabulate(10)(identity))
      .map(_ * 10)
      .toMat(Sink.fold(0)(_ + _))(Keep.right)

//  graph.run()
//    .onComplete {
//      case Success(result) =>
//        println(result)
//      case Failure(e) =>
//        println(s"Failed due to ${e.getMessage}")
//    }

  val badTabulatedSource =
    Source(List.tabulate(10000000)(identity))
      .map(_.toString)

  val tabulatedSource =
    Source.fromIterator(() => (1 to 100000).toIterator)
      .map(_.toString)

  // file source
  val fileSource =
    FileIO.fromPath(lotstohash)
      .via(Framing.delimiter(ByteString("\n"), 256, true))
      .map(_.utf8String)

  val fileTailSource =
    FileTailSource.lines(
      path = lotstohash,
      maxLineSize = 8192,
      pollingInterval = 250.millis
    )

//  Read from file and hash
//  tabulatedSource
//    .mapAsync(2)(slowHash)
//    .runWith(Sink.foreach(println))

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
