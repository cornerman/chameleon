// shadow sbt-scalajs' crossProject and CrossType from Scala.js 0.6.x
import sbtcrossproject.CrossPlugin.autoImport.{crossProject, CrossType}

organization in Global := "com.github.cornerman"
version in Global := "0.3.1-SNAPSHOT"

lazy val commonSettings = Seq(
  scalaVersion := "2.12.10",
  crossScalaVersions := Seq("2.12.10", "2.13.6"),
  publishTo := sonatypePublishTo.value,

  scalacOptions ++=
    "-encoding" :: "UTF-8" ::
    "-unchecked" ::
    "-deprecation" ::
    "-explaintypes" ::
    "-feature" ::
    "-language:_" ::
    "-Xfuture" ::
    "-Xlint" ::
    "-Ywarn-value-discard" ::
    "-Ywarn-extra-implicit" ::
    "-Ywarn-unused" ::
    Nil,

  scalacOptions ++= {
    CrossVersion.partialVersion(scalaVersion.value) match {
      case Some((2, 12)) =>
        "-Ywarn-nullary-override" ::
        "-Ywarn-nullary-unit" ::
        "-Ywarn-infer-any" ::
        "-Yno-adapted-args" ::
        "-Ypartial-unification" ::
        Nil
      case _ =>
        Nil
    }
  },

  addCompilerPlugin("org.typelevel" %% "kind-projector" % "0.10.3")
)

enablePlugins(ScalaJSPlugin)

lazy val root = (project in file("."))
  .aggregate(chameleonJS, chameleonJVM)
  .settings(commonSettings)
  .settings(
    skip in publish := true
  )

lazy val chameleon = crossProject(JSPlatform, JVMPlatform)
  .crossType(CrossType.Pure)
  .settings(commonSettings)
  .settings(
    libraryDependencies ++=
      Deps.cats.value % Optional ::
      Deps.boopickle.value % Optional ::
      Deps.circe.core.value % Optional ::
      Deps.circe.parser.value % Optional ::
      Deps.scodec.core.value % Optional ::
      Deps.scodec.bits.value % Optional ::
      Deps.upickle.value % Optional ::

      Deps.scalaTest.value % Test ::
      Nil
  )

lazy val chameleonJS = chameleon.js
lazy val chameleonJVM = chameleon.jvm


pomExtra in Global := {
  <url>https://github.com/cornerman/chameleon</url>
  <licenses>
    <license>
      <name>The MIT License (MIT)</name>
      <url>http://opensource.org/licenses/MIT</url>
    </license>
  </licenses>
  <scm>
    <url>https://github.com/cornerman/chameleon</url>
    <connection>scm:git:git@github.com:cornerman/chameleon.git</connection>
  </scm>
  <developers>
    <developer>
      <id>jkaroff</id>
      <name>Johannes Karoff</name>
      <url>https://github.com/cornerman</url>
    </developer>
  </developers>
}
