package chameleon.ext

import chameleon._
import com.github.plokhotnyuk.jsoniter_scala.core

import scala.util.{Try, Success, Failure}

//TODO with CodecMakerConfig
object jsoniter {
  implicit def jsoniterSerializerDeserializerString[T: core.JsonValueCodec]: SerializerDeserializer[T, String] = new Serializer[T, String] with Deserializer[T, String] {
    override def serialize(arg: T): String = core.writeToString(arg)

    override def deserialize(arg: String): Either[Throwable, T] =
      Try(core.readFromString[T](arg)) match {
        case Success(arg) => Right(arg)
        case Failure(t) => Left(t)
      }
  }

  implicit def jsoniterSerializerDeserializerByteArray[T: core.JsonValueCodec]: SerializerDeserializer[T, Array[Byte]] = new Serializer[T, Array[Byte]] with Deserializer[T, Array[Byte]] {
    override def serialize(arg: T): Array[Byte] = core.writeToArray(arg)

    override def deserialize(arg: Array[Byte]): Either[Throwable, T] =
      Try(core.readFromArray[T](arg)) match {
        case Success(arg) => Right(arg)
        case Failure(t) => Left(t)
      }
  }
}
