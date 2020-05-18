package com.kzs.lab.ws.dao

import com.kzs.lab.ws.model.{Stock, StockDBAccessError, StockError, StockNotFound}
import com.kzs.lab.ws.{Dependencies, IOTransactor}
import doobie.implicits._
import zio.IO
import zio.interop.catz._

class StockDAOLive(val xa: IOTransactor) extends Dependencies.StockDAO.Service {

  override def currentStock(stockId: Int): IO[StockError, Stock] = {
    val stockDatabaseResult = sql"""
      SELECT * FROM stock where id=$stockId
     """.query[Stock].option

    stockDatabaseResult.transact(xa).mapError(StockDBAccessError)
    .flatMap{
      case Some(stock) => IO.succeed(stock)
      case None => IO.fail(StockNotFound)
    }
  }
}
