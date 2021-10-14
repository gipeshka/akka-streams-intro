lazy val root = project
  .in(file("."))
  .settings(
    libraryDependencies ++= Seq(
      "com.typesafe.akka" %% "akka-stream" % "2.6.16",
      "com.lightbend.akka" %% "akka-stream-alpakka-file" % "3.0.3"
    )
  )

commands ++= List(
  Command.command("simple") { state =>
    "runMain streams.Example1Simple" :: state
  },
  Command.command("badSource") { state =>
    "runMain streams.Example2BadSource" :: state
  },
  Command.command("goodSource") { state =>
    "runMain streams.Example2GoodSource" :: state
  },
  Command.command("fillFile") { state =>
    "runMain streams.Example3FillFile" :: state
  },
  Command.command("readFile") { state =>
    "runMain streams.Example4ReadFile" :: state
  },
  Command.command("backpressure") { state =>
    "runMain streams.Example5Backpressure" :: state
  }
)
