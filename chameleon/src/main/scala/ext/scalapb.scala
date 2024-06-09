package chameleon.ext

import chameleon._
import _root_.scalapb.{GeneratedMessage, GeneratedMessageCompanion}
import java.nio.ByteBuffer
import scala.util.{Failure, Success}

object scalapb {
  implicit def scalapbSerializerDeserializerByteBuffer[T <: GeneratedMessage](implicit companion: GeneratedMessageCompanion[T]): SerializerDeserializer[T, ByteBuffer] = new Serializer[T, ByteBuffer] with Deserializer[T, ByteBuffer] {
    override def serialize(arg: T): ByteBuffer = ByteBuffer.wrap(companion.toByteArray(arg))
    override def deserialize(arg: ByteBuffer): Either[Throwable, T] = {
      val bytes = new Array[Byte](arg.remaining)
      arg.get(bytes);
      companion.validate(bytes) match {
        case Success(arg) => Right(arg)
        case Failure(t) => Left(t)
      }
    }
  }

  implicit def scalapbSerializerDeserializerByteArray[T <: GeneratedMessage](implicit companion: GeneratedMessageCompanion[T]): SerializerDeserializer[T, Array[Byte]] = new Serializer[T, Array[Byte]] with Deserializer[T, Array[Byte]] {
    override def serialize(arg: T): Array[Byte] = companion.toByteArray(arg)
    override def deserialize(bytes: Array[Byte]): Either[Throwable, T] =
      companion.validate(bytes) match {
        case Success(arg) => Right(arg)
        case Failure(t) => Left(t)
      }
  }
}
