package chameleon.ext.http4s

import cats.effect.Concurrent
import cats.implicits._
import chameleon.{Deserializer, Serializer}
import org.http4s.headers.`Content-Type`
import org.http4s.{DecodeResult, EntityDecoder, EntityEncoder, Headers, MalformedMessageBodyFailure, Media, MediaType}

object MessagePackByteArrayCodec {
  // https://www.iana.org/assignments/media-types/application/vnd.msgpack
  val messagePackMediaType = MediaType.unsafeParse("application/vnd.msgpack")

  def messagePackDecoderOf[F[_]: Concurrent, A](implicit deserializer: Deserializer[A, Array[Byte]]): EntityDecoder[F, A] =
    EntityDecoder.decodeBy(messagePackMediaType) { media =>
      EntityDecoder.collectBinary(media).flatMap { chunk =>
        DecodeResult[F, A](Concurrent[F].pure(
          deserializer.deserialize(chunk.toArray).leftMap(error => MalformedMessageBodyFailure("Invalid MessagePack", Some(error)))
        ))
      }
    }

  def messagePackEncoderOf[F[_], A](implicit serializer: Serializer[A, Array[Byte]]): EntityEncoder[F, A] =
    EntityEncoder.encodeBy[F, A](Headers(`Content-Type`(messagePackMediaType))) {
      EntityEncoder.byteArrayEncoder[F].contramap(serializer.serialize).toEntity(_)
    }

  case class messagePack[A](value: A)

  def messagePackAs[A] = new MessagePackAsPartialApplied[A]
  class MessagePackAsPartialApplied[A] {
    def apply[F[_]: Concurrent](media: Media[F])(implicit deserializer: Deserializer[A, Array[Byte]]): F[A] = media.as[A](implicitly, messagePackDecoderOf[F, A])
  }

  implicit def messagePackEntityDecoderMessagePack[F[_]: Concurrent, A](implicit deserializer: Deserializer[A, Array[Byte]]): EntityDecoder[F, messagePack[A]] =
    messagePackDecoderOf[F, A].map(messagePack(_))

  implicit def messagePackEntityEncoderMessagePack[F[_], A](implicit serializer: Serializer[A, Array[Byte]]): EntityEncoder[F, messagePack[A]] =
    messagePackEncoderOf[F, A].contramap(_.value)

  object auto {
    implicit def messagePackEntityDecoder[F[_] : Concurrent, A](implicit deserializer: Deserializer[A, Array[Byte]]): EntityDecoder[F, A] = messagePackDecoderOf[F, A]

    implicit def messagePackEntityEncoder[F[_], A](implicit serializer: Serializer[A, Array[Byte]]): EntityEncoder[F, A] = messagePackEncoderOf[F, A]
  }
}
