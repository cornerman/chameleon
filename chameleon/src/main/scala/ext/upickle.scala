package chameleon.ext

import chameleon._
import _root_.upickle.Api
import _root_.upickle.default

import scala.util.{Try, Success, Failure}

trait upickle { api: Api =>
  implicit def upickleSerializer[T: api.Writer]: chameleon.Serializer[T, String]     = upickle.upickleSerializerOfApi(api)
  implicit def upickleDeserializer[T: api.Reader]: chameleon.Deserializer[T, String] = upickle.upickleDeserializerOfApi(api)
}

object upickle {
  def upickleSerializerOfApi[T](api: Api)(implicit writer: api.Writer[T]): Serializer[T, String] = new Serializer[T, String] {
    override def serialize(arg: T): String = api.write(arg)
  }
  def upickleDeserializerOfApi[T](api: Api)(implicit reader: api.Reader[T]): Deserializer[T, String] = new Deserializer[T, String] {
    override def deserialize(arg: String): Either[Throwable, T] =
      Try(api.read[T](arg)) match {
        case Success(arg) => Right(arg)
        case Failure(t) => Left(t)
      }
  }

  implicit def upickleSerializer[T: default.Writer]: Serializer[T, String] = upickleSerializerOfApi(default)
  implicit def upickleDeserializer[T : default.Reader]: Deserializer[T, String] = upickleDeserializerOfApi(default)
}