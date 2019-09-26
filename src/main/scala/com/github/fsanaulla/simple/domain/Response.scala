package com.github.fsanaulla.simple.domain

import java.net.HttpCookie
import java.nio.charset.Charset

import scala.collection.JavaConverters._

/**
  * Represents a HTTP response
  *
  * @param url the URL that the original request was made to
  * @param statusCode the status code of the response
  * @param statusMessage the status message of the response
  * @param headers the raw headers the server sent back with the response
  * @param data the response body; may contain HTML, JSON, or binary or textual data
  * @param history the response of any redirects that were performed before
  *                arriving at the current response
  */
final case class Response(
    url: String,
    statusCode: Int,
    statusMessage: String,
    headers: Map[String, Seq[String]],
    data: ResponseBlob,
    history: Option[Response]) {

  /**
    * Decodes the byte contents of this response as a String using the default charset
    */
  def text() = new String(data.bytes)

  /**
    * Decodes the byte contents of this response as a String using the given charset
    */
  def text(cs: Charset) = new String(data.bytes, cs)

  /**
    * Returns the byte contents of this response
    */
  def contents: Array[Byte] = data.bytes

  /**
    * Returns the cookies set by this response, and by any redirects that lead up to it
    */
  val cookies: Map[String, HttpCookie] = history.toSeq.flatMap(_.cookies).toMap ++ headers
    .get("set-cookie")
    .iterator
    .flatten
    .flatMap(java.net.HttpCookie.parse(_).asScala)
    .map(x => x.getName -> x)
    .toMap

  def contentType: Option[String] = headers.get("content-type").flatMap(_.headOption)

  def contentLength: Option[String] = headers.get("content-length").flatMap(_.headOption)

  def location: Option[String] = headers.get("location").flatMap(_.headOption)

  def is2xx: Boolean = statusCode.toString.head == '2'
  def is3xx: Boolean = statusCode.toString.head == '3'
  def is4xx: Boolean = statusCode.toString.head == '4'
  def is5xx: Boolean = statusCode.toString.head == '5'
}
