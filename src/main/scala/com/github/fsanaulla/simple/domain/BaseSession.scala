package com.github.fsanaulla.simple.domain

import java.net.HttpCookie

import com.github.fsanaulla.simple.Requester

import scala.collection.mutable

trait BaseSession {
  def headers: Map[String, String]

  def cookies: mutable.Map[String, HttpCookie]
  def readTimeout: Int
  def connectTimeout: Int
  def auth: RequestAuth
  def proxy: (String, Int)
  def maxRedirects: Int
  def persistCookies: Boolean
  def verifySslCerts: Boolean
  def autoDecompress: Boolean
  def compress: Compress

  lazy val get     = Requester("GET", this)
  lazy val post    = Requester("POST", this)
  lazy val put     = Requester("PUT", this)
  lazy val delete  = Requester("DELETE", this)
  lazy val head    = Requester("HEAD", this)
  lazy val options = Requester("OPTIONS", this)
  // unofficial
  lazy val patch = Requester("PATCH", this)
}

object BaseSession {

  val defaultHeaders: Map[String, String] = Map(
    "User-Agent"      -> "requests-scala",
    "Accept-Encoding" -> "gzip, deflate",
    "Connection"      -> "keep-alive",
    "Accept"          -> "*/*"
  )
}
