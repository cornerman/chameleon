package chameleon

import _root_.upickle.default._

import scala.util.{Try, Success, Failure}

object Upickle {
  implicit def upickleSerializer[T: Writer]: Serializer[T, String] = new Serializer[T, String] {
    override def serialize(arg: T): String = write(arg)
  }
  implicit def upickleDeserializer[T : Reader]: Deserializer[T, String] = new Deserializer[T, String] {
    override def deserialize(arg: String): Either[Throwable, T] =
      Try(read[T](arg)) match {
        case Success(arg) => Right(arg)
        case Failure(t) => Left(t)
      }
  }
}
