import sbt.Keys._
import ReleaseTransformations._
import sbtrelease.{Version, versionFormatError}

lazy val appVendor = "com.interelgroup"
lazy val appName = "core3-example-combined"

organization := appVendor
name := appName

scalaVersion in ThisBuild := "2.12.3"

lazy val defaultResolvers = Seq(
  "Sonatype OSS Snapshots" at "https://oss.sonatype.org/content/repositories/releases",
  "lightshed-maven" at "http://dl.bintray.com/content/lightshed/maven"
)

lazy val core3_example_dependencies = (project in file("./core3_example_dependencies"))
  .settings(
    resolvers ++= defaultResolvers,
    libraryDependencies ++= Seq(
      "com.interelgroup" %% "core3" % "2.2.0",
      "org.scalameta" %% "scalameta" % "1.8.0",
      "com.github.etaty" %% "rediscala" % "1.8.0" % Test,
      "org.scalatest" %% "scalatest" % "3.0.4" % Test
    ),
    macroSettings,
    logBuffered in Test := false,
    parallelExecution in Test := false
  )

lazy val macroSettings = Seq(
  addCompilerPlugin("org.scalameta" % "paradise" % "3.0.0-M10" cross CrossVersion.full),
  scalacOptions += "-Xplugin-require:macroparadise",
  scalacOptions in (Compile, console) := Seq()
)

lazy val core3_example_combined = (project in file("."))
  .settings(
    organization := appVendor,
    name := appName,
    resolvers ++= defaultResolvers,
    libraryDependencies ++= Seq(
      guice,
      "org.jline" % "jline" % "3.2.0",
      "com.github.scopt" %% "scopt" % "3.5.0",
      "com.github.etaty" %% "rediscala" % "1.8.0",
      "com.interelgroup" %% "core3" % "2.2.0",
      "org.webjars" % "jquery" % "3.0.0",
      "org.scalatest" %% "scalatest" % "3.0.4" % Test
    ),
    buildInfoKeys := Seq[BuildInfoKey](organization, name, version),
    buildInfoPackage := "core3_example_combined",
    buildInfoObject := "BuildInfo",
    logBuffered in Test := false,
    parallelExecution in Test := false
  )
  .dependsOn(core3_example_dependencies)
  .enablePlugins(PlayScala, BuildInfoPlugin)

//loads the Play project at sbt startup
onLoad in Global := (Command.process("project core3_example_combined", _: State)) compose (onLoad in Global).value
scalacOptions in ThisBuild ++= Seq("-unchecked", "-deprecation")

//Release Config
releaseVersion := {
  v =>
    Version(v).map {
      version =>
        val next = System.getProperty("release-version-bump", "bugfix") match {
          case "major" => version.withoutQualifier.bump(sbtrelease.Version.Bump.Major)
          case "minor" => version.withoutQualifier.bump(sbtrelease.Version.Bump.Minor)
          case "bugfix" => version.withoutQualifier
        }

        next.string
    }.getOrElse(versionFormatError)
}

releaseProcess := Seq[ReleaseStep](
  checkSnapshotDependencies,
  inquireVersions,
  setReleaseVersion,
  commitReleaseVersion,
  tagRelease,
  setNextVersion,
  commitNextVersion,
  pushChanges
)
