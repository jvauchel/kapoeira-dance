lazy val root = (project in file("."))
  .enablePlugins(GitVersioning)
  .settings(
    organizationName := "jvauchel",
    organization := "io.github.jvauchel",
    name := "burger-factory",
    scalaVersion := "2.13.10",
    // src
    libraryDependencies ++= Seq(
      "ch.qos.logback" % "logback-classic" % "1.4.4",
      "org.apache.kafka" %% "kafka-streams-scala" % "3.2.0",
      "com.typesafe" % "config" % "1.4.2",
      "com.typesafe.scala-logging" %% "scala-logging" % "3.9.5"
    ),
    // test
    libraryDependencies ++= Seq(
      "org.scalatest" %% "scalatest" % "3.2.12",
      "org.scalacheck" %% "scalacheck" % "1.16.0",
      "org.apache.kafka" % "kafka-streams-test-utils" % "3.2.0"
    ).map(_ % Test)
  )

Test / parallelExecution := false

// assembly config
assembly / assemblyJarName := "burger-factory.jar"
assembly / assemblyMergeStrategy := {
  case "module-info.class" => MergeStrategy.discard
  case x if x.endsWith("/module-info.class") => MergeStrategy.discard
  case "application.conf" =>  new sbtassembly.MergeStrategy {
    val name = "reverseConcat"
    def apply(tempDir: File, path: String, files: Seq[File]): Either[String, Seq[(File, String)]] =
      MergeStrategy.concat(tempDir, path, files.reverse)
  }
  case x =>
    val oldStrategy = (assembly / assemblyMergeStrategy).value
    oldStrategy(x)

}
