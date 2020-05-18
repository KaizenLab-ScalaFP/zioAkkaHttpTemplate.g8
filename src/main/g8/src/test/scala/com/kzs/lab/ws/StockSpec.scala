package com.kzs.lab.ws

import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.testkit.ScalatestRouteTest
import com.kzs.lab.ws.Dependencies.{ExtServices, StockDAO}
import com.kzs.lab.ws.model.{Stock, StockDBAccessError, StockError, StockNotFound}
import org.scalatest.Matchers
import org.scalatest.wordspec.AnyWordSpec
import zio.clock.Clock
import zio.{IO, ULayer, ZLayer}

class StockSpec extends AnyWordSpec with Matchers with ScalatestRouteTest {

  val stockDAOTest: ULayer[StockDAO] = ZLayer.succeed {
    new StockDAO.Service {
      //you could also use a mocking framework here
      override def currentStock(stockId: Int): IO[StockError, Stock] = {
        stockId match {
          case 1 => IO.succeed(Stock(1, 10))
          case 2 => IO.succeed(Stock(2, 15))
          case 3 => IO.succeed(Stock(3, 0))
          case 99 => IO.fromEither(Left(StockDBAccessError(new Exception("BOOM!"))))
          case _ => IO.fromEither(Left(StockNotFound))
        }
      }
    }

  }

  implicit val externalServicesTest: ULayer[ExtServices] = stockDAOTest ++ Clock.live
  val akkaHttpService = new AkkaHttpService()

    "return 200 and current stock" in {
      Get("/stock/1") ~> akkaHttpService.routes ~> check {
        status shouldEqual StatusCodes.OK
        responseAs[String] shouldEqual """{"id":1,"value":10}"""
      }
    }

  "return 404 with unknown stock" in {
    Get("/stock/100") ~> akkaHttpService.routes ~> check {
      status shouldEqual StatusCodes.NotFound
    }
  }

    // ...

}
