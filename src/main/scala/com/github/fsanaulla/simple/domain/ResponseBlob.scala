package com.github.fsanaulla.simple.domain

/**
  * Wraps the array of bytes returned in the body of a HTTP response
  */
class ResponseBlob(val bytes: Array[Byte]) {
  override def toString = s"ResponseBlob(${bytes.length} bytes)"
  def text              = new String(bytes)

  override def hashCode() = java.util.Arrays.hashCode(bytes)

  override def equals(obj: scala.Any) = obj match {
    case r: ResponseBlob => java.util.Arrays.equals(bytes, r.bytes)
    case _               => false
  }
}
