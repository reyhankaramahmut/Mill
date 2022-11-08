val scala3Version = "3.1.3"
val scalafxVersion = "18.0.1-R28"

ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := scala3Version

Compile / mainClass := Some("de.htwg.se.mill.Mill")
mainClass in (Compile, packageBin) := Some("de.htwg.se.mill.Mill")

name := "Mill"
scalacOptions ++= Seq(
  "-unchecked",
  "-deprecation",
  "-encoding",
  "utf8",
  "-feature"
)
libraryDependencies ++= Seq(
  "org.scalactic" %% "scalactic" % "3.2.13",
  "org.scalatest" %% "scalatest" % "3.2.13" % "test",
  "org.scalafx" %% "scalafx" % scalafxVersion
)

jacocoReportSettings := JacocoReportSettings(
  "Jacoco Coverage Report",
  None,
  JacocoThresholds(),
  Seq(JacocoReportFormats.ScalaHTML, JacocoReportFormats.XML),
  "utf-8"
)
jacocoExcludes := Seq(
  "de.htwg.se.mill.Mill*",
  "de.htwg.se.mill.util*"
)
//fork := true
