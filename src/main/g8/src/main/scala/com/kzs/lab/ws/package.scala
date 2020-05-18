package com.kzs.lab

import akka.http.scaladsl.server.Route
import com.kzs.lab.ws.Dependencies.ExtServices
import doobie.util.transactor.Transactor.Aux
import zio.{Has, Layer, RIO, Task, URIO, ZIO}

package object ws {

  type IOTransactor = Aux[Task, Unit]

  implicit class ZioHttpRoute[R <: Has[_]](zioRoute: URIO[R, Route]) {
    def toAkkaRoute[E](implicit layer: Layer[E, R]) = {
      val routeWithDependencies: ZIO[Any, E, Route] = zioRoute.provideLayer(layer)
      zio.Runtime.default.unsafeRun(routeWithDependencies)
    }
  }

}
