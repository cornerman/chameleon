inThisBuild(Seq(
  organization := "com.github.cornerman",

  scalaVersion := "2.12.18",

  crossScalaVersions := Seq("2.12.18", "2.13.12", "3.2.2"),

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

lazy val chameleon = crossProject(JSPlatform, JVMPlatform)
  .crossType(CrossType.Pure)
  .settings(commonSettings)
  .settings(
    libraryDependencies ++=
      Deps.cats.value % Optional ::
      Deps.scalapb.value % Optional ::
      Deps.boopickle.value % Optional ::
      Deps.circe.core.value % Optional ::
      Deps.circe.parser.value % Optional ::
      Deps.upickle.value % Optional ::
      Deps.jsoniter.value % Optional ::
      Deps.zioJson.value % Optional ::

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
  )
