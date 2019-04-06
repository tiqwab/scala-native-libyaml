scalaVersion := "2.11.12"

// Set to false or remove if you want to show stubs as linking errors
nativeLinkStubs := true

enablePlugins(ScalaNativePlugin)

libraryDependencies ++= Seq(
  "io.monix" %%% "minitest" % "2.3.2"  % Test
)

testFrameworks += new TestFramework("minitest.runner.Framework")
