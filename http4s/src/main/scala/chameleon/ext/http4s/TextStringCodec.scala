package chameleon.ext.http4s

import cats.effect.Concurrent
import cats.implicits._
import chameleon.{Deserializer, Serializer}
import org.http4s.{DecodeResult, EntityDecoder, EntityEncoder, MalformedMessageBodyFailure, Media}

object TextStringCodec {
  def textDecoderOf[F[_] : Concurrent, A](implicit deserializer: Deserializer[A, String]): EntityDecoder[F, A] =
    EntityDecoder.text[F].flatMapR { text =>
      DecodeResult[F, A](Concurrent[F].pure(
        deserializer.deserialize(text).leftMap(error => MalformedMessageBodyFailure("Invalid Text", Some(error)))
      ))
    }

  def textEncoderOf[F[_], A](implicit serializer: Serializer[A, String]): EntityEncoder[F, A] =
    EntityEncoder.stringEncoder[F].contramap(serializer.serialize)

  case class text[A](value: A)

  def textAs[A] = new TextAsPartialApplied[A]
  class TextAsPartialApplied[A] {
    def apply[F[_]: Concurrent](media: Media[F])(implicit deserializer: Deserializer[A, String]): F[A] = media.as[A](implicitly, textDecoderOf[F, A])
  }

  implicit def textEntityDecoderText[F[_]: Concurrent, A](implicit deserializer: Deserializer[A, String]): EntityDecoder[F, text[A]] =
    textDecoderOf[F, A].map(text(_))

  implicit def textEntityEncoderText[F[_], A](implicit serializer: Serializer[A, String]): EntityEncoder[F, text[A]] =
    textEncoderOf[F, A].contramap(_.value)

  object auto {
    implicit def textEntityDecoder[F[_] : Concurrent, A](implicit deserializer: Deserializer[A, String]): EntityDecoder[F, A] = textDecoderOf[F, A]

    implicit def textEntityEncoder[F[_], A](implicit serializer: Serializer[A, String]): EntityEncoder[F, A] = textEncoderOf[F, A]
  }
}
