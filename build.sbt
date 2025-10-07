name := "scalus-cape-submissions"
version := "0.1.0"
scalaVersion := "3.3.6"

// Scalus dependencies
libraryDependencies ++= Seq(
  "org.scalus" %% "scalus" % "0.12.0"
)

// Scalus compiler plugin
addCompilerPlugin("org.scalus" %% "scalus-plugin" % "0.12.0")

// Source directories
Compile / scalaSource := baseDirectory.value / "src"
