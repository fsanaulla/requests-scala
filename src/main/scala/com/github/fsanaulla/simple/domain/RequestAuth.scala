package com.github.fsanaulla.simple.domain

/**
 * Different ways you can authorize a HTTP request; by default, HTTP Basic
 * auth and Proxy auth are supported
 */
trait RequestAuth {
  def header: Option[String]
}

object RequestAuth {

  object Empty extends RequestAuth {
    def header: None.type = None
  }
  implicit def implicitBasic(x: (String, String)) = new Basic(x._1, x._2)

  case class Basic(username: String, password: String) extends RequestAuth {

    def header =
      Some(
        "Basic " + java.util.Base64.getEncoder
          .encodeToString((username + ":" + password).getBytes())
      )
  }
  case class Proxy(username: String, password: String) extends RequestAuth {

    def header =
      Some(
        "Proxy-Authorization " + java.util.Base64.getEncoder
          .encodeToString((username + ":" + password).getBytes())
      )
  }
  case class Bearer(token: String) extends RequestAuth {
    def header = Some(s"Bearer $token")
  }
}
