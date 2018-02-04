package chameleon

import scodec.bits.BitVector
import scodec.Codec
import java.nio.ByteBuffer

object Scodec {
  case class DeserializeException(msg: String) extends Exception(msg)

  implicit def scodecSerializerDeserializer[T : Codec]: SerializerDeserializer[T, ByteBuffer] = new Serializer[T, ByteBuffer] with Deserializer[T, ByteBuffer] {
    override def serialize(arg: T): ByteBuffer = Codec[T].encode(arg).require.toByteBuffer
    override def deserialize(arg: ByteBuffer): Either[Throwable, T] =
      Codec[T].decode(BitVector(arg)).toEither match {
        case Right(result) => Right(result.value)
        case Left(err) => Left(DeserializeException(err.message))
      }
  }
}
