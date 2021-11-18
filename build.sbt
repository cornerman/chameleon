// shadow sbt-scalajs' crossProject and CrossType from Scala.js 0.6.x
import sbtcrossproject.CrossPlugin.autoImport.{crossProject, CrossType}

inThisBuild(Seq(
  organization := "com.github.cornerman",

  scalaVersion := "2.12.15",

  crossScalaVersions := Seq("2.12.15", "2.13.6"),

  licenses := Seq("MIT License" -> url("https://opensource.org/licenses/MIT")),

  homepage := Some(url("https://github.com/cornerman/chameleon")),

  scmInfo := Some(ScmInfo(
    url("https://github.com/cornerman/chameleon"),
    "scm:git:git@github.com:cornerman/chameleon.git",
    Some("scm:git:git@github.com:cornerman/chameleon.git"))
  ),

  pomExtra :=
    <developers>
      <developer>
        <id>jkaroff</id>
        <name>Johannes Karoff</name>
        <url>https://github.com/cornerman</url>
      </developer>
    </developers>
))

lazy val commonSettings = Seq(
  addCompilerPlugin("org.typelevel" % "kind-projector" % "0.13.2" cross CrossVersion.full)
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
  ).jsSettings(
      scalacOptions += {
        val githubRepo    = "cornerman/chameleon"
        val local         = baseDirectory.value.toURI
        val subProjectDir = baseDirectory.value.getName
        val remote        = s"https://raw.githubusercontent.com/${githubRepo}/${git.gitHeadCommit.value.get}"
        s"-P:scalajs:mapSourceURI:$local->$remote/${subProjectDir}/"
      },
  )

lazy val chameleonJS = chameleon.js
lazy val chameleonJVM = chameleon.jvm
