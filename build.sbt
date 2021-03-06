name := "Boilerplate"

organization := "nl.tvogels"

version:= "2.0-SNAPSHOT"

scalaVersion := "2.10.4"

cancelable in Global := true
fork in run := true

mainClass in assembly := Some("ch.ethz.dalab.web2text.Main")

assemblyMergeStrategy in assembly := {
  case PathList("javax", "xml", xs @ _*)         => MergeStrategy.first
  case PathList("org", "apache", "logging", xs @ _*) => MergeStrategy.first
  case PathList("org", "apache", "commons", xs @ _*) => MergeStrategy.first
  case PathList("org", "apache", xs @ _*) => MergeStrategy.discard
  case PathList("cleaneval", xs @ _*) => MergeStrategy.discard
  case PathList("com", "typesafe", xs @ _*)         => MergeStrategy.first
  case PathList("com", "thoughtworks", xs @ _*)         => MergeStrategy.first
  case PathList("com", xs @ _*)         => MergeStrategy.discard
  case PathList("parquet", xs @ _*)         => MergeStrategy.discard
  case PathList("tachyon", xs @ _*)         => MergeStrategy.discard
  case PathList("tachyon", xs @ _*)         => MergeStrategy.discard
  case PathList("org", "spark-project", xs @ _*)         => MergeStrategy.discard
  case PathList("org", "spark_project", xs @ _*)         => MergeStrategy.discard
  case PathList("org", "jboss", xs @ _*)         => MergeStrategy.discard
  case x =>
    val oldStrategy = (assemblyMergeStrategy in assembly).value
    oldStrategy(x)
}

libraryDependencies += "org.scalatest" % "scalatest_2.10" % "2.2.4" % "test"

libraryDependencies += "org.apache.spark" %% "spark-core" % "1.4.1"

libraryDependencies += "org.apache.spark" %% "spark-mllib" % "1.4.1"

libraryDependencies += "org.mongodb" %% "casbah" % "3.1.0"

libraryDependencies += "io.spray" %% "spray-json" % "1.3.5"