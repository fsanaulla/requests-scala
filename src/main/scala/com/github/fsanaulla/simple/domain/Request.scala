package com.github.fsanaulla.simple.domain

import java.net.HttpCookie

import com.github.fsanaulla.simple.Requester

/**
  * The equivalent of configuring a [[Requester.apply]] or [[Requester.stream]]
  * call, but without invoking it. Useful if you want to further customize it
  * and make the call later via the overloads of `apply`/`stream` that take a
  * [[Request]].
  */
final case class Request(
    url: String,
    auth: RequestAuth = null,
    params: Iterable[(String, String)] = Nil,
    headers: Iterable[(String, String)] = Nil,
    readTimeout: Int = 0,
    connectTimeout: Int = 0,
    proxy: (String, Int) = null,
    cookies: Map[String, HttpCookie] = Map(),
    cookieValues: Map[String, String] = Map(),
    maxRedirects: Int = 5,
    verifySslCerts: Boolean = true,
    autoDecompress: Boolean = true,
    compress: Compress = Compress.None,
    keepAlive: Boolean = true)
