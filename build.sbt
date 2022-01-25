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
          file("src/main/io/control.scala"),
          file("src/main/io/SourceLocation.scala"),
          file("src/main/io/SourcePath.scala"),
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
          Seq(file("src/main/io/package.scala")) ++
            (file("src/main/io/threadcso") ** "*.scala").get,
        target := file("target/modules/lib"),
    )
