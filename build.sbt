ThisBuild / scalaVersion := "2.12.3"
ThisBuild / version := "1.2R5-SNAPSHOT"

ThisBuild / scalacOptions ++= Seq(
  "-language:implicitConversions",
  "-language:postfixOps",
)

lazy val macros =
  project
    .in(file("modules/macros"))
    .settings(
        name := "cso-mirror-macros",
        libraryDependencies ++= Seq(
            "org.scala-lang" % "scala-reflect" % scalaVersion.value,
        ),
        target := file("target/modules/macros"),
    )

lazy val lib =
  project
    .in(file("modules/lib"))
    .dependsOn(macros)
    .settings(
        name := "cso-mirror-lib",
        scalacOptions ++= Seq(
            "-Xelide-below",
            "350"
        ),
        target := file("target/modules/lib"),
    )
