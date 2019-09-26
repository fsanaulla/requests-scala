import com.typesafe.sbt.SbtPgp.autoImportImpl.{pgpPassphrase, pgpPublicRing, pgpSecretRing, useGpg}
import de.heikoseeberger.sbtheader.HeaderPlugin.autoImport.headerLicense
import de.heikoseeberger.sbtheader.License
import sbt.Keys.{publishArtifact, _}
import sbt.librarymanagement.LibraryManagementSyntax
import sbt.{Developer, Opts, ScmInfo, file, url}

/** Basic sbt settings */
object Settings extends LibraryManagementSyntax {

  private val mitUrl = "https://opensource.org/licenses/MIT"

  private object Owner {
    val id     = "fsanaulla"
    val name   = "Faiaz Sanaulla"
    val email  = "fayaz.sanaulla@gmail.com"
    val github = "https://github.com/fsanaulla"
  }

  val common = Seq(
    scalaVersion := "2.13.1",
    organization := "com.github.fsanaulla",
    scalacOptions ++= Scalac.options(scalaVersion.value),
    crossScalaVersions := Seq("2.11.8", "2.12.8", scalaVersion.value),
    homepage := Some(url("https://github.com/fsanaulla/simple")),
    licenses += "MIT" -> url(mitUrl),
    developers += Developer(
      id = Owner.id,
      name = Owner.name,
      email = Owner.email,
      url = url(Owner.github)
    )
  )

  val publish = Seq(
    useGpg := false,
    scmInfo := Some(
      ScmInfo(
        url("https://github.com/fsanaulla/simple"),
        "scm:git@github.com:fsanaulla/simple.git"
      )
    ),
    pomIncludeRepository := (_ => false),
    publishTo := Some(
      if (isSnapshot.value)
        Opts.resolver.sonatypeSnapshots
      else
        Opts.resolver.sonatypeStaging
    ),
    publishArtifact in Test := false,
    pgpPublicRing := file("pubring.asc"),
    pgpSecretRing := file("secring.asc"),
    pgpPassphrase := sys.env.get("PGP_PASSPHRASE").map(_.toCharArray)
  )

  val header = headerLicense := Some(License.MIT("2019", Owner.name))
}
