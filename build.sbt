lazy val simple = project.in(file("."))
  .settings(Settings.common: _*)
  .settings(Settings.publish: _*)
  .enablePlugins(AutomateHeaderPlugin, ScalafmtPlugin)