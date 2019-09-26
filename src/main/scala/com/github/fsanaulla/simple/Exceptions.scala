package com.github.fsanaulla.simple

final class TimeoutException(
    val url: String,
    val readTimeout: Int,
    val connectTimeout: Int)
  extends Exception(
    s"Request to $url timed out. (readTimeout: $readTimeout, connectTimout: $connectTimeout)"
  )

final class UnknownHostException(val url: String, val host: String)
  extends Exception(s"Unknown host $host in url $url")

final class InvalidCertException(val url: String, cause: Throwable)
  extends Exception(s"Unable to validate SSL certificates for $url", cause)
