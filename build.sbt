lazy val simple = project.in(file("."))
  .settings(Settings.common: _*)
  .settings(Settings.publish: _*)
  .settings(
    libraryDependencies += Library.scalaTest
  )
  .enablePlugins(AutomateHeaderPlugin, ScalafmtPlugin)