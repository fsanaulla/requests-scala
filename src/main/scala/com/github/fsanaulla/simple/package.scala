package com.github.fsanaulla

import java.net.HttpCookie

import com.github.fsanaulla.simple.domain.{BaseSession, Compress, RequestAuth}

import scala.collection.mutable

package object simple extends BaseSession {
  def cookies = mutable.Map.empty[String, HttpCookie]

  val headers: Map[String, String] = BaseSession.defaultHeaders

  def auth: RequestAuth.Empty.type = RequestAuth.Empty

  def proxy: Null = null

  def maxRedirects: Int = 5

  def persistCookies = false

  def readTimeout: Int = 10 * 1000

  def connectTimeout: Int = 10 * 1000

  def verifySslCerts: Boolean = true

  def autoDecompress: Boolean = true

  def compress: Compress = Compress.None
}
