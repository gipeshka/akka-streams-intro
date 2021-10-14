package streams

import akka.actor.ActorSystem
import akka.stream.scaladsl._

import scala.concurrent.ExecutionContext.Implicits._
import scala.util.{Failure, Success}

object Example1Simple extends App {
  implicit val akka = ActorSystem()

  val graph =
    Source(List.tabulate(10)(identity))
      .map(_ * 10)
      .toMat(Sink.fold(0)(_ + _))(Keep.right)

  graph.run()
    .onComplete {
      case Success(result) =>
        println(s"Result: $result")
      case Failure(e) =>
        println(s"Failed due to ${e.getMessage}")
    }
}
