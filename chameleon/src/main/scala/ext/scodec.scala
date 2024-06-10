package chameleon.ext

import chameleon._
import _root_.scodec.bits.BitVector
import _root_.scodec.Codec

import java.nio.ByteBuffer

object scodec {
  case class DeserializeException(msg: String) extends Exception(msg)

  implicit def scodecSerializerDeserializerByteBuffer[T : Codec]: SerializerDeserializer[T, ByteBuffer] = new Serializer[T, ByteBuffer] with Deserializer[T, ByteBuffer] {
    override def serialize(arg: T): ByteBuffer = Codec[T].encode(arg).require.toByteBuffer
    override def deserialize(arg: ByteBuffer): Either[Throwable, T] =
      Codec[T].decode(BitVector(arg)).toEither match {
        case Right(result) => Right(result.value)
        case Left(err) => Left(DeserializeException(err.message))
      }
  }

  implicit def scodecSerializerDeserializerByteArray[T : Codec]: SerializerDeserializer[T, Array[Byte]] = new Serializer[T, Array[Byte]] with Deserializer[T, Array[Byte]] {
    override def serialize(arg: T): Array[Byte] = Codec[T].encode(arg).require.toByteArray
    override def deserialize(arg: Array[Byte]): Either[Throwable, T] =
      Codec[T].decode(BitVector(arg)).toEither match {
        case Right(result) => Right(result.value)
        case Left(err) => Left(DeserializeException(err.message))
      }
  }
}
