package com.github.fsanaulla.simple.domain

final case class StreamHeaders(
    url: String,
    statusCode: Int,
    statusMessage: String,
    headers: Map[String, Seq[String]],
    history: Option[Response])
