lazy val commonSettings = Seq(
  organization := "com.github.cornerman",
  version      := "0.1.0-SNAPSHOT",

  scalaVersion := "2.12.4",
  crossScalaVersions := Seq("2.11.12", "2.12.4"),

  scalacOptions ++=
    "-encoding" :: "UTF-8" ::
    "-unchecked" ::
    "-deprecation" ::
    "-explaintypes" ::
    "-feature" ::
    "-language:_" ::
    "-Xfuture" ::
    "-Xlint" ::
    "-Ypartial-unification" ::
    "-Yno-adapted-args" ::
    "-Ywarn-infer-any" ::
    "-Ywarn-value-discard" ::
    "-Ywarn-nullary-override" ::
    "-Ywarn-nullary-unit" ::
    "-Ywarn-unused" ::
    Nil,

  scalacOptions ++= {
    CrossVersion.partialVersion(scalaVersion.value) match {
      case Some((2, 12)) =>
        "-Ywarn-extra-implicit" ::
        Nil
      case _ =>
        Nil
    }
  },

  addCompilerPlugin("org.spire-math" %% "kind-projector" % "0.9.4")
)

enablePlugins(ScalaJSPlugin)

lazy val root = (project in file("."))
  .aggregate(chameleonJS, chameleonJVM)
  .settings(commonSettings)

lazy val chameleon = crossProject.crossType(CrossType.Pure)
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
