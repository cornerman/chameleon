package chameleon.ext

import chameleon._
import _root_.scalapb.{GeneratedMessage, GeneratedMessageCompanion, Message}
import java.nio.ByteBuffer
import scala.util.{Failure, Success}

object scalapb {
  implicit def scalapbSerializerDeserializer[T <: GeneratedMessage with Message[T]](implicit companion: GeneratedMessageCompanion[T]): SerializerDeserializer[T, ByteBuffer] = new Serializer[T, ByteBuffer] with Deserializer[T, ByteBuffer] {
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
}
