val scala3Version = "3.1.3"
val scalafxVersion = "18.0.1-R28"

<<<<<<< HEAD
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
  "de.htwg.se.mill.util*",
  "de.htwg.se.mill.aview.gui*"
)
=======
lazy val root = project
  .in(file("."))
  .settings(
    name := "Mill",
    version := "0.1.0-SNAPSHOT",
    scalaVersion := scala3Version,
    javacOptions ++= Seq("-encoding", "UTF-8"),
    libraryDependencies ++= Seq(
      "org.scalactic" %% "scalactic" % "3.2.13",
      "org.scalatest" %% "scalatest" % "3.2.13" % "test"
    ),
    jacocoReportSettings := JacocoReportSettings(
      "Jacoco Coverage Report",
      None,
      JacocoThresholds(),
      Seq(JacocoReportFormats.ScalaHTML, JacocoReportFormats.XML),
      "utf-8"
    ),
    jacocoExcludes := Seq(
      "de.htwg.se.mill.Mill*",
      "de.htwg.se.mill.util*"
    ),
    jacocoCoverallsServiceName := "github-actions",
    jacocoCoverallsBranch := sys.env.get("CI_BRANCH"),
    jacocoCoverallsPullRequest := sys.env.get("GITHUB_EVENT_NAME"),
    jacocoCoverallsRepoToken := sys.env.get("COVERALLS_REPO_TOKEN")
  )
  .enablePlugins(JacocoCoverallsPlugin)
>>>>>>> d466e6f6c11ef69d0cb1599e38b43be7beb561c5
