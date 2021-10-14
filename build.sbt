lazy val root = project
  .in(file("."))
  .settings(
    Compile / run / mainClass := Some("streams.Main"),
    Compile / packageBin / mainClass := Some("streams.Main"),
    libraryDependencies ++= Seq(
      "com.typesafe.akka" %% "akka-stream" % "2.6.16",
      "com.lightbend.akka" %% "akka-stream-alpakka-file" % "3.0.3"
    )
  )
