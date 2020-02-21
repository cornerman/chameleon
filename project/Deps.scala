import sbt._
import Keys._
import org.portablescala.sbtplatformdeps.PlatformDepsPlugin.autoImport._

object Deps {
  // hack to expand %%% in settings, needs .value in build.sbt
  import Def.{setting => dep}

  val scalaTest = dep("org.scalatest" %%% "scalatest" % "3.1.1")
  val boopickle = dep("io.suzaku" %%% "boopickle" % "1.3.1")
  val cats = dep("org.typelevel" %%% "cats-core" % "2.1.0")
  val circe = new {
    private val version = "0.12.1"
    val core = dep("io.circe" %%% "circe-core" % version)
    val parser = dep("io.circe" %%% "circe-parser" % version)
  }
  val scodec = new {
    val core = dep("org.scodec" %%% "scodec-core" % "1.11.4")
    val bits = dep("org.scodec" %%% "scodec-bits" % "1.1.13")
  }
  val upickle = dep("com.lihaoyi" %%% "upickle" % "0.7.5")
}
