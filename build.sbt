name := """PlayVisualizerC"""

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.11.7"

libraryDependencies ++= Seq(
  jdbc,
  cache,
  ws,
  "net.arnx" % "jsonic" % "1.3.10",
  "com.github.UnicoenProject" % "Junicoen" % "9128c5dab753b5993e629daf9c1b2230aba09b4e",
  "org.scalatestplus.play" %% "scalatestplus-play" % "1.5.1" % Test,
  "mysql" % "mysql-connector-java" % "5.1.36"
)

resolvers += "jitpack.io" at "https://jitpack.io"
resolvers += "scalaz-bintray" at "http://dl.bintray.com/scalaz/releases"

import com.typesafe.sbt.packager.Keys.scriptClasspath

scriptClasspath := {
  val originalClasspath = scriptClasspath.value
  val manifest = new java.util.jar.Manifest()
  manifest.getMainAttributes().putValue("Class-Path", originalClasspath.mkString(" "))
  val classpathJar = (target in Universal).value / "lib" / "classpath.jar"
  IO.jar(Seq.empty, classpathJar, manifest)
  Seq(classpathJar.getName)
}
mappings in Universal += (((target in Universal).value / "lib" / "classpath.jar") -> "lib/classpath.jar")

fork in run := true