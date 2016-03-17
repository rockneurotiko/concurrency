name := """scala-carcel-concurrency"""

version := "1.0"

scalaVersion := "2.11.8"

libraryDependencies ++= Seq(
  // Change this to another test framework if you prefer
  "org.scalatest" %% "scalatest" % "2.1.6" % "test",
  // Akka
  "com.typesafe.akka" %% "akka-actor" % "2.4.2"
)
