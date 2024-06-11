package chameleon.ext.http4s

import cats.effect.Concurrent
import cats.implicits._
import chameleon.{Deserializer, Serializer}
import org.http4s.{DecodeResult, EntityDecoder, EntityEncoder, MalformedMessageBodyFailure, Media}

object BinaryByteArrayCodec {
  def binaryDecoderOf[F[_]: Concurrent, A](implicit deserializer: Deserializer[A, Array[Byte]]): EntityDecoder[F, A] =
    EntityDecoder.byteArrayDecoder[F].flatMapR { bytes =>
      DecodeResult[F, A](Concurrent[F].pure(
        deserializer.deserialize(bytes).leftMap(error => MalformedMessageBodyFailure("Invalid Binary", Some(error)))
      ))
    }

  def binaryEncoderOf[F[_], A](implicit serializer: Serializer[A, Array[Byte]]): EntityEncoder[F, A] =
    EntityEncoder.byteArrayEncoder[F].contramap(serializer.serialize)

  case class binary[A](value: A)

  def binaryAs[A] = new BinaryAsPartialApplied[A]
  class BinaryAsPartialApplied[A] {
    def apply[F[_]: Concurrent](media: Media[F])(implicit deserializer: Deserializer[A, Array[Byte]]): F[A] = media.as[A](implicitly, binaryDecoderOf[F, A])
  }

  implicit def binaryEntityDecoderBinary[F[_]: Concurrent, A](implicit deserializer: Deserializer[A, Array[Byte]]): EntityDecoder[F, binary[A]] =
    binaryDecoderOf[F, A].map(binary(_))

  implicit def binaryEntityEncoderBinary[F[_], A](implicit serializer: Serializer[A, Array[Byte]]): EntityEncoder[F, binary[A]] =
    binaryEncoderOf[F, A].contramap(_.value)

  object auto {
    implicit def binaryEntityDecoder[F[_] : Concurrent, A](implicit deserializer: Deserializer[A, Array[Byte]]): EntityDecoder[F, A] = binaryDecoderOf[F, A]

    implicit def binaryEntityEncoder[F[_], A](implicit serializer: Serializer[A, Array[Byte]]): EntityEncoder[F, A] = binaryEncoderOf[F, A]
  }
}
