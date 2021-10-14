package streams

import akka.actor.ActorSystem
import akka.stream.scaladsl._

import scala.concurrent.ExecutionContext.Implicits._
import scala.util.{Failure, Success}

object Example2GoodSource extends App {
  implicit val akka = ActorSystem()

  val tabulatedSource =
    Source.fromIterator(() => (1 to 10000000).toIterator)
      .map(_.toString)

  tabulatedSource
    .map(_.length)
    .runWith(Sink.fold(0)(_ + _))
    .onComplete {
      case Success(result) =>
        println(s"Result: $result")
      case Failure(e) =>
        println(s"Failed due to ${e.getMessage}")
    }
}
