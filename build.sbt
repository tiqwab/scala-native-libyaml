import ReleaseTransformations._

organization := "com.tiqwab"
name := "scala-native-libyaml"

scalaVersion := "2.11.12"

publishMavenStyle := true
publishTo := sonatypePublishTo.value

pomExtra := (
  <url>https://github.com/tiqwab/scala-native-libyaml</url>
  <licenses>
    <license>
      <name>MIT license</name>
      <url>http://www.opensource.org/licenses/mit-license.php</url>
    </license>
  </licenses>
  <scm>
    <url>https://github.com/tiqwab/scala-native-libyaml</url>
    <connection>scm:git:https//github.com/tiqwab/scala-native-libyaml.git</connection>
  </scm>
  <developers>
    <developer>
      <id>tiqwab></id>
      <name>Naohisa Murakami</name>
      <url>https://github.com/tiqwab</url>
    </developer>
  </developers>
)

releaseProcess := Seq[ReleaseStep](
  checkSnapshotDependencies,
  inquireVersions,
  runClean,
  runTest,
  setReleaseVersion,
  commitReleaseVersion,
  tagRelease,
  releaseStepCommandAndRemaining("publishSigned"),
  setNextVersion,
  commitNextVersion,
  releaseStepCommand("sonatypeRelease"),
  pushChanges
)

// Set to false or remove if you want to show stubs as linking errors
nativeLinkStubs := true

enablePlugins(ScalaNativePlugin)

scalacOptions ++= Seq(
  "-feature" // Emit warning and location for usages of features that should be imported explicitly.
  , "-deprecation" // Emit warning and location for usages of deprecated APIs.
  , "-unchecked" // Enable additional warnings where generated code depends on assumptions.
  , "-Xlint"
  , "-encoding" // Specify encoding of source files
  , "UTF-8"
)

libraryDependencies ++= Seq(
  "io.monix" %%% "minitest" % "2.3.2"  % Test
)

testFrameworks += new TestFramework("minitest.runner.Framework")
