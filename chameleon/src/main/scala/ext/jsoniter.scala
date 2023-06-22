package chameleon.ext

import chameleon._
import com.github.plokhotnyuk.jsoniter_scala.core

import scala.util.{Try, Success, Failure}

object jsoniter {
  implicit def jsoniterSerializerDeserializer2[T: core.JsonValueCodec]: SerializerDeserializer[T, String] = new Serializer[T, String] with Deserializer[T, String] {
    override def serialize(arg: T): String = core.writeToString(arg)

    override def deserialize(arg: String): Either[Throwable, T] =
      Try(core.readFromString[T](arg)) match {
        case Success(arg) => Right(arg)
        case Failure(t) => Left(t)
      }
  }
}
