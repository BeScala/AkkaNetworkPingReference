organization := "org.bescala"

name := "concurrentProgramming"

version := "1.0.0"

scalaVersion := Version.scala

resolvers += "Typesafe Repository" at "http://repo.typesafe.com/typesafe/releases/"

resolvers += "Sonatype snapshots"  at "http://oss.sonatype.org/content/repositories/snapshots/"

resolvers += "Apache Repository" at "https://repository.apache.org/content/repositories/orgapachespark-1012/"

resolvers += "MVN Repo" at "http://mvnrepository.com/artifact"

libraryDependencies ++= Dependencies.ccpiscala

scalacOptions ++= List(
  "-unchecked",
  "-deprecation",
  "-language:_",
  "-encoding", "UTF-8"
)

initialCommands :=
  """
    |
  """.stripMargin

