ThisBuild / scalaVersion := "2.12.3"
ThisBuild / version := "1.2R5"

ThisBuild / scalacOptions ++= Seq(
  "-language:implicitConversions",
  "-language:postfixOps",
)

lazy val macros =
  project
    .settings(
        name := "cso-mirror-macros",
        libraryDependencies ++= Seq(
            "org.scala-lang" % "scala-reflect" % scalaVersion.value,
        ),
        Compile / sources := Seq(
          file("src/io/control.scala"),
          file("src/io/SourceLocation.scala"),
          file("src/io/SourcePath.scala"),
        ),
        target := file("target/modules/macros"),
    )

lazy val lib =
  project
    .dependsOn(macros)
    .settings(
        name := "sco-mirror-lib",
        scalacOptions ++= Seq(
            "-Xelide-below",
            "350"
        ),
        Compile / sources :=
          Seq(file("src/io/package.scala")) ++
            (file("src/io/threadcso") ** "*.scala").get,
        target := file("target/modules/lib"),
    )
