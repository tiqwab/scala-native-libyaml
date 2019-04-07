lazy val clangOpt: Option[String] = sys.env.get("NATIVE_CLANG")
lazy val clangPPOpt: Option[String] = sys.env.get("NATIVE_CLANG_PP")

scalaVersion := "2.11.12"

// Set to false or remove if you want to show stubs as linking errors
nativeLinkStubs := true

// Give clang and clang++ command path from env if necessary
nativeClang := {
    clangOpt match {
        case None => nativeClang.value
        case Some(v) => new java.io.File(v)
    }
}
nativeClangPP := {
    clangPPOpt match {
        case None => nativeClangPP.value
        case Some(v) => new java.io.File(v)
    }
}

enablePlugins(ScalaNativePlugin)

libraryDependencies ++= Seq(
  "io.monix" %%% "minitest" % "2.3.2"  % Test
)

testFrameworks += new TestFramework("minitest.runner.Framework")
