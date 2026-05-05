name := "scalus-cape-submissions"
version := "0.1.0"
scalaVersion := "3.3.7"

val scalusVersion = "0.17.0"

// Scalus dependencies
libraryDependencies ++= Seq(
  "org.scalus" %% "scalus" % scalusVersion
)

// Scalus compiler plugin (artifactId pinned to compiler version since 0.17.0)
addCompilerPlugin("org.scalus" % "scalus-plugin_3.3.7" % scalusVersion)

// Source directories
Compile / scalaSource := baseDirectory.value / "src"
