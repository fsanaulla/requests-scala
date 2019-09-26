package com.github.fsanaulla.simple.domain

import java.io.FileInputStream
import java.util.UUID

import com.github.fsanaulla.simple.{Requester, Util}

/**
  * Represents the different things you can upload in the body of a HTTP
  * request. By default allows form-encoded key-value pairs, arrays of bytes,
  * strings, files, and inputstreams. These types can be passed directly to
  * the `data` parameter of [[Requester.apply]] and will be wrapped automatically
  * by the implicit constructors.
  */
trait RequestBlob {
  def headers: Seq[(String, String)] = Nil
  def inMemory: Boolean
  def write(out: java.io.OutputStream): Unit
}

object RequestBlob {

  trait SizedBlob extends RequestBlob {
    override def headers: Seq[(String, String)] = Seq(
      "Content-Length" -> length.toString
    )
    def length: Long
  }

  object EmptyRequestBlob extends RequestBlob {
    def inMemory                               = true
    def write(out: java.io.OutputStream): Unit = ()
  }
  implicit def BytesRequestBlob(x: Array[Byte])          = SizedBlob.BytesRequestBlob(x)
  implicit def StringRequestBlob(x: String)              = SizedBlob.StringRequestBlob(x)
  implicit def FileRequestBlob(x: java.io.File)          = SizedBlob.FileRequestBlob(x)
  implicit def NioFileRequestBlob(x: java.nio.file.Path) = SizedBlob.NioFileRequestBlob(x)

  object SizedBlob {
    implicit class BytesRequestBlob(val x: Array[Byte]) extends SizedBlob {
      def inMemory = true
      override def headers = super.headers ++ Seq(
        "Content-Type" -> "application/octed-stream"
      )
      def length                           = x.length
      def write(out: java.io.OutputStream) = out.write(x)
    }
    implicit class StringRequestBlob(val x: String) extends SizedBlob {
      def inMemory = true
      override def headers = super.headers ++ Seq(
        "Content-Type" -> "text/plain"
      )
      val serialized                       = x.getBytes()
      def length                           = serialized.length
      def write(out: java.io.OutputStream) = out.write(serialized)
    }
    implicit class FileRequestBlob(val x: java.io.File) extends SizedBlob {
      def inMemory = false
      override def headers = super.headers ++ Seq(
        "Content-Type" -> "application/octed-stream"
      )
      def length                           = x.length()
      def write(out: java.io.OutputStream) = Util.transferTo(new FileInputStream(x), out)
    }
    implicit class NioFileRequestBlob(val x: java.nio.file.Path) extends SizedBlob {
      def inMemory = false
      override def headers = super.headers ++ Seq(
        "Content-Type" -> "application/octed-stream"
      )
      def length = java.nio.file.Files.size(x)

      def write(out: java.io.OutputStream) =
        Util.transferTo(java.nio.file.Files.newInputStream(x), out)
    }
  }

  implicit class InputStreamRequestBlob(val x: java.io.InputStream) extends RequestBlob {
    def inMemory = false
    override def headers = super.headers ++ Seq(
      "Content-Type" -> "application/octed-stream"
    )
    def write(out: java.io.OutputStream) = Util.transferTo(x, out)
  }
  implicit class FormEncodedRequestBlob(val x: Iterable[(String, String)]) extends SizedBlob {
    def inMemory   = true
    val serialized = Util.urlEncode(x).getBytes
    def length     = serialized.length
    override def headers = super.headers ++ Seq(
      "Content-Type" -> "application/x-www-form-urlencoded"
    )

    def write(out: java.io.OutputStream) = {
      out.write(serialized)
    }
  }

  implicit class MultipartFormRequestBlob(val parts: Iterable[MultiItem]) extends RequestBlob {
    def inMemory = true
    val boundary = UUID.randomUUID().toString
    val crlf     = "\r\n"
    val pref     = "--"

    val ContentDisposition = "Content-Disposition: form-data; name=\""
    val filenameSnippet    = "\"; filename=\""

    // encode params up front for the length calculation

    val partBytes = parts.map(
      p => (p.name.getBytes(), if (p.filename == null) Array[Byte]() else p.filename.getBytes(), p)
    )

    // we need to pre-calculate the Content-Length of this HttpRequest because most servers don't
    // support chunked transfer
    val totalBytesToSend: Long = {

      val partsLength = partBytes.map {
        case (name, filename, part) =>
          pref.length + boundary.length + crlf.length +
            ContentDisposition.length +
            name.length +
            (if (filename.nonEmpty) filenameSnippet.length + filename.length else 0) +
            "\"".length + crlf.length + crlf.length +
            part.data.length +
            crlf.length
      }
      val finaleBoundaryLength = (pref.length * 2) + boundary.length + crlf.length

      partsLength.sum + finaleBoundaryLength
    }

    override def headers = Seq(
      "Content-Type"   -> s"multipart/form-data; boundary=$boundary",
      "Content-Length" -> totalBytesToSend.toString
    )

    def write(out: java.io.OutputStream) = {
      def writeBytes(s: String): Unit = out.write(s.getBytes())

      partBytes.foreach {
        case (name, filename, part) =>
          writeBytes(pref + boundary + crlf)
          writeBytes(ContentDisposition)
          out.write(name)
          if (filename.nonEmpty) {
            writeBytes(filenameSnippet)
            out.write(filename)
          }
          writeBytes("\"" + crlf + crlf)
          part.data.write(out)
          writeBytes(crlf)
      }

      writeBytes(pref + boundary + pref + crlf)

      out.flush()
      out.close()
    }
  }

  case class MultiPart(items: MultiItem*) extends RequestBlob.MultipartFormRequestBlob(items)
  case class MultiItem(
      name: String,
      data: RequestBlob.SizedBlob,
      filename: String = null)

}
