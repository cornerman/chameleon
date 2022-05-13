import sbt._
import Keys._
import org.portablescala.sbtplatformdeps.PlatformDepsPlugin.autoImport._

object Deps {
  // hack to expand %%% in settings, needs .value in build.sbt
  import Def.{setting => dep}

  val scalaTest = dep("org.scalatest" %%% "scalatest" % "3.2.12")

  val boopickle = dep("io.suzaku" %%% "boopickle" % "1.4.0")

  val cats = dep("org.typelevel" %%% "cats-core" % "2.7.0")

  val scalapb = dep("com.thesamet.scalapb" %%% "scalapb-runtime" % "0.11.10")

  val circe = new {
    private val version = "0.14.1"
    val core = dep("io.circe" %%% "circe-core" % version)
    val parser = dep("io.circe" %%% "circe-parser" % version)
  }
  val scodec = new {
    val core = dep("org.scodec" %%% "scodec-core" % "1.11.9")
    val core2 = dep("org.scodec" %%% "scodec-core" % "2.1.0")
    val bits = dep("org.scodec" %%% "scodec-bits" % "1.1.31")
  }
  val upickle = dep("com.lihaoyi" %%% "upickle" % "2.0.0")
  val jsoniter = dep("com.github.plokhotnyuk.jsoniter-scala" %%% "jsoniter-scala-core" % "2.13.22")
}
