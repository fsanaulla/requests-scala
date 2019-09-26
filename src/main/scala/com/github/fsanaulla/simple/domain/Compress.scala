package com.github.fsanaulla.simple.domain

import java.io.OutputStream
import java.util.zip.{DeflaterOutputStream, GZIPOutputStream}

/**
  * Mechanisms for compressing the upload stream; supports Gzip and Deflate
  * by default
  */
trait Compress {
  def headers: Seq[(String, String)]
  def wrap(x: OutputStream): OutputStream
}

object Compress {

  object Gzip extends Compress {

    def headers: Seq[(String, String)] = Seq(
      "Content-Encoding" -> "gzip"
    )
    def wrap(x: OutputStream) = new GZIPOutputStream(x)
  }

  object Deflate extends Compress {

    def headers: Seq[(String, String)] = Seq(
      "Content-Encoding" -> "deflate"
    )
    def wrap(x: OutputStream) = new DeflaterOutputStream(x)
  }

  object None extends Compress {
    def headers: Nil.type                   = Nil
    def wrap(x: OutputStream): OutputStream = x
  }
}
