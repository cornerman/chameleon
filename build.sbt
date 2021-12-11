inThisBuild(Seq(
  organization := "com.github.cornerman",

  scalaVersion := "2.12.15",

  crossScalaVersions := Seq("2.12.15", "2.13.7", "3.1.0"),

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
  libraryDependencies ++= (CrossVersion.partialVersion(scalaVersion.value) match {
    case Some((3, _)) => Seq.empty
    case _ => Seq(compilerPlugin("org.typelevel" % "kind-projector" % "0.13.2" cross CrossVersion.full))
  }),

  scalacOptions --= Seq("-Xfatal-warnings")
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
      Deps.upickle.value % Optional ::

      Deps.scalaTest.value % Test ::
      Nil,

    libraryDependencies ++= (CrossVersion.partialVersion(scalaVersion.value) match {
      case Some((3, _)) => Seq(
        Deps.scodec.core2.value % Optional,
        Deps.scodec.bits.value  % Optional,
      )
      case _ => Seq(
        Deps.scodec.core.value % Optional,
        Deps.scodec.bits.value % Optional,
      )
    })
  ).jsSettings(
    scalacOptions ++= (CrossVersion.partialVersion(scalaVersion.value) match {
      case Some((3, _)) => Seq.empty //TODO?
      case _ =>
        val githubRepo    = "cornerman/chameleon"
        val local         = baseDirectory.value.toURI
        val subProjectDir = baseDirectory.value.getName
        val remote        = s"https://raw.githubusercontent.com/${githubRepo}/${git.gitHeadCommit.value.get}"
        Seq(s"-P:scalajs:mapSourceURI:$local->$remote/${subProjectDir}/")
    })
  )

lazy val chameleonJS = chameleon.js
lazy val chameleonJVM = chameleon.jvm
