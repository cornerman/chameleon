package chameleon.ext

import chameleon._
import _root_.upickle.Api
import _root_.upickle.default

import scala.util.{Try, Success, Failure}

trait upickle { api: Api =>
  implicit def upickleSerializerString[T: api.Writer]: chameleon.Serializer[T, String]     = upickle.upickleSerializerStringOfApi(api)
  implicit def upickleDeserializerString[T: api.Reader]: chameleon.Deserializer[T, String] = upickle.upickleDeserializerStringOfApi(api)

  implicit def upickleSerializerByteArray[T: api.Writer]: chameleon.Serializer[T, Array[Byte]]     = upickle.upickleSerializerByteArrayOfApi(api)
  implicit def upickleDeserializerByteArray[T: api.Reader]: chameleon.Deserializer[T, Array[Byte]] = upickle.upickleDeserializerByteArrayOfApi(api)
}

object upickle {
  def upickleSerializerStringOfApi[T](api: Api)(implicit writer: api.Writer[T]): Serializer[T, String] = new Serializer[T, String] {
    override def serialize(arg: T): String = api.write(arg)
  }
  def upickleDeserializerStringOfApi[T](api: Api)(implicit reader: api.Reader[T]): Deserializer[T, String] = new Deserializer[T, String] {
    override def deserialize(arg: String): Either[Throwable, T] =
      Try(api.read[T](arg)) match {
        case Success(arg) => Right(arg)
        case Failure(t) => Left(t)
      }
  }

  def upickleSerializerByteArrayOfApi[T](api: Api)(implicit writer: api.Writer[T]): Serializer[T, Array[Byte]] = new Serializer[T, Array[Byte]] {
    override def serialize(arg: T): Array[Byte] = api.writeBinary(arg)
  }
  def upickleDeserializerByteArrayOfApi[T](api: Api)(implicit reader: api.Reader[T]): Deserializer[T, Array[Byte]] = new Deserializer[T, Array[Byte]] {
    override def deserialize(arg: Array[Byte]): Either[Throwable, T] =
      Try(api.readBinary[T](arg)) match {
        case Success(arg) => Right(arg)
        case Failure(t) => Left(t)
      }
  }

  implicit def upickleSerializerString[T: default.Writer]: Serializer[T, String] = upickleSerializerStringOfApi(default)
  implicit def upickleDeserializerString[T : default.Reader]: Deserializer[T, String] = upickleDeserializerStringOfApi(default)

  implicit def upickleSerializerByteArray[T: default.Writer]: Serializer[T, Array[Byte]] = upickleSerializerByteArrayOfApi(default)
  implicit def upickleDeserializerByteArray[T : default.Reader]: Deserializer[T, Array[Byte]] = upickleDeserializerByteArrayOfApi(default)
}