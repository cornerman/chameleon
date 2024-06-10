package chameleon.ext.http4s

import cats.effect.Concurrent
import cats.implicits._
import chameleon.{Deserializer, Serializer}
import org.http4s.headers.`Content-Type`
import org.http4s.{DecodeResult, EntityDecoder, EntityEncoder, Headers, MalformedMessageBodyFailure, Media, MediaType}

object JsonByteArrayCodec {
  def jsonDecoderOf[F[_]: Concurrent, A](implicit deserializer: Deserializer[A, Array[Byte]]): EntityDecoder[F, A] =
    EntityDecoder.decodeBy(MediaType.application.json) { media =>
      EntityDecoder.collectBinary(media).flatMap { chunk =>
        DecodeResult[F, A](Concurrent[F].pure(
          deserializer.deserialize(chunk.toArray).leftMap(error => MalformedMessageBodyFailure("Invalid JSON", Some(error)))
        ))
      }
    }

  def jsonEncoderOf[F[_], A](implicit serializer: Serializer[A, Array[Byte]]): EntityEncoder[F, A] =
    EntityEncoder.encodeBy[F, A](Headers(`Content-Type`(MediaType.application.json))) {
      EntityEncoder.byteArrayEncoder[F].contramap(serializer.serialize).toEntity(_)
    }

  case class json[A](value: A)

  def jsonAs[A] = new JsonAsPartialApplied[A]
  class JsonAsPartialApplied[A] {
    def apply[F[_]: Concurrent](media: Media[F])(implicit deserializer: Deserializer[A, Array[Byte]]): F[A] = media.as[A](implicitly, jsonDecoderOf[F, A])
  }

  implicit def jsonEntityDecoderJson[F[_]: Concurrent, A](implicit deserializer: Deserializer[A, Array[Byte]]): EntityDecoder[F, json[A]] =
    jsonDecoderOf[F, A].map(json(_))

  implicit def jsonEntityEncoderJson[F[_], A](implicit serializer: Serializer[A, Array[Byte]]): EntityEncoder[F, json[A]] =
    jsonEncoderOf[F, A].contramap(_.value)

  object auto {
    implicit def jsonEntityDecoder[F[_] : Concurrent, A](implicit deserializer: Deserializer[A, Array[Byte]]): EntityDecoder[F, A] = jsonDecoderOf[F, A]

    implicit def jsonEntityEncoder[F[_], A](implicit serializer: Serializer[A, Array[Byte]]): EntityEncoder[F, A] = jsonEncoderOf[F, A]
  }
}
