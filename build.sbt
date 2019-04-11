scalaVersion := "2.11.12"

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
