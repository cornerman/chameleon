import sbt._
import Keys._
import org.scalajs.sbtplugin.ScalaJSPlugin.autoImport._

object Deps {
  // hack to expand %%% in settings, needs .value in build.sbt
  import Def.{setting => dep}

  val scalaTest = dep("org.scalatest" %%% "scalatest" % "3.0.4")
  val boopickle = dep("io.suzaku" %%% "boopickle" % "1.2.6")
  val cats = dep("org.typelevel" %%% "cats-core" % "1.0.1")
  val shapeless = dep("com.chuusai" %%% "shapeless" % "2.3.3")
  val circe = new {
    private val version = "0.9.0"
    val core = dep("io.circe" %%% "circe-core" % version)
    val parser = dep("io.circe" %%% "circe-parser" % version)
    val generic = dep("io.circe" %%% "circe-generic" % version)
    val shapes = dep("io.circe" %%% "circe-shapes" % version)
  }
}
