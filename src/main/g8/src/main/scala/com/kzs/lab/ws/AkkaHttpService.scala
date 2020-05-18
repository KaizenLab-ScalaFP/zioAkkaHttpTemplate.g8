package com.kzs.lab.ws

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.{HttpResponse, StatusCodes}
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import com.kzs.lab.ws.Dependencies.{ExtServices, StockDAO}
import com.kzs.lab.ws.model.{EmptyStock, Stock, StockError, StockNotFound}
import de.heikoseeberger.akkahttpcirce.ErrorAccumulatingCirceSupport
import zio.{IO, ULayer, URIO, ZIO}
import io.circe.generic.auto._
import io.circe.Json
import scala.io.StdIn


object MainApp {

  private final case class Foo(bar: String)

  def main(args: Array[String]): Unit = {

    implicit val layer: ULayer[ExtServices] = Dependencies.extServicesLive
    implicit val system = ActorSystem()

    val akkaHttpService = new AkkaHttpService()
    Http().bindAndHandle(akkaHttpService.routes, "127.0.0.1", 8000)

    StdIn.readLine("Hit ENTER to exit")
    system.terminate()
  }

}

class AkkaHttpService(implicit val serviceLayer: ULayer[ExtServices]) extends ErrorAccumulatingCirceSupport {

  val stockDao: URIO[StockDAO, StockDAO.Service] = ZIO.access[StockDAO](_.get)

  val getStockRoute =
    pathPrefix("stock") {
      path(IntNumber) { stockId =>
        val stockDbResult: ZIO[ExtServices, StockError, Stock] = for {
          dao <- stockDao
          stock <- dao.currentStock(stockId)
          result <- IO.fromEither(Stock.validate(stock))
        } yield result
        stockOrErrorResponse(stockDbResult).toAkkaRoute
      }
    }

  val routes = concat(getStockRoute /*, ...*/)

  def stockOrErrorResponse(stockResponse: ZIO[ExtServices, StockError, Stock]): URIO[ExtServices, Route] = {
    stockResponse.fold({

      case EmptyStock => complete(Json.fromString("Stock is empty"))

      case StockNotFound => complete(HttpResponse(StatusCodes.NotFound))

      case stockError => failWith(stockError)
    },
      //success case
      stock => complete(stock))
  }
}
