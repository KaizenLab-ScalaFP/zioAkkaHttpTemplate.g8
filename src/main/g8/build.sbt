val H2Version = "1.4.196"
val DoobieVersion = "0.9.0"
val CirceVersion = "0.13.0"
val ZioVersion = "1.0.1"
val AkkaHttpVersion = "10.1.12"

organization := "com.github.kzs"
name := "ZIOAkkaHTTPWebAppWorkshop"
version := "0.0.1-SNAPSHOT"
scalaVersion := "2.13.1"
mainClass in Compile := Some("com.kzs.lab.ws.MainApp")
libraryDependencies ++= Seq(
  "dev.zio" %% "zio" % ZioVersion,
  "com.typesafe.akka" %% "akka-http"   % AkkaHttpVersion,
  "com.typesafe.akka" %% "akka-stream" % "2.6.5",
  "de.heikoseeberger" %% "akka-http-circe" % "1.31.0",
  "dev.zio" %% "zio-interop-cats" % "2.1.4.0",
  "org.tpolecat" %% "doobie-core" % DoobieVersion,
  "io.circe" %% "circe-generic" % CirceVersion,
  "com.h2database" % "h2" % H2Version,
  "com.typesafe.akka" %% "akka-stream-testkit" % "2.6.5" % Test,
  "com.typesafe.akka" %% "akka-http-testkit" % "10.1.12" % Test,
  "org.scalatest" %% "scalatest" % "3.1.1" % Test,
)
