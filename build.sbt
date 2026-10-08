name := "scalus-cape-submissions"
version := "0.1.0"
scalaVersion := "3.3.8"

val scalusVersion = "1.3.0"

// Scalus dependencies
libraryDependencies ++= Seq(
  "org.scalus" %% "scalus" % scalusVersion
)

// Scalus compiler plugin (published per full Scala version, e.g. scalus-plugin_3.3.8)
addCompilerPlugin("org.scalus" % "scalus-plugin" % scalusVersion cross CrossVersion.full)

// Source directories
Compile / scalaSource := baseDirectory.value / "src"
