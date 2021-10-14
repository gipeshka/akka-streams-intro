package streams

import akka.actor.ActorSystem
import akka.stream.scaladsl._

import scala.concurrent.ExecutionContext.Implicits._
import scala.util.{Failure, Success}

object Example2BadSource extends App {
  implicit val akka = ActorSystem()

  val badTabulatedSource =
    Source(List.tabulate(10000000)(identity))
      .map(_.toString)

  badTabulatedSource
    .map(_.length)
    .runWith(Sink.fold(0)(_ + _))
    .onComplete {
      case Success(result) =>
        println(s"Result: $result")
      case Failure(e) =>
        println(s"Failed due to ${e.getMessage}")
    }
}
