package com.kzs.lab.ws

import com.kzs.lab.ws.dao.StockDAOLive
import com.kzs.lab.ws.model.{Stock, StockError}
import doobie.util.transactor.Transactor
import zio._
import zio.clock.Clock
import zio.interop.catz._

object Dependencies {

  type ExtServices = StockDAO with Clock
  type StockDAO = Has[StockDAO.Service]

  object StockDAO {

    trait Service {
      def currentStock(stockId: Int): IO[StockError, Stock]
    }

    val live: ULayer[StockDAO] =
      ZLayer.succeed {
        val xa = Transactor.fromDriverManager[Task](
          "org.h2.Driver",
          "jdbc:h2:file:./localdb;INIT=RUNSCRIPT FROM 'src/main/resources/sql/create.sql'"
          , "sa", ""
        )
        new StockDAOLive(xa)
      }
  }

  val extServicesLive: ULayer[ExtServices] = StockDAO.live ++ Clock.live

}