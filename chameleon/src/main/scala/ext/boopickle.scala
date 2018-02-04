package chameleon.ext

import chameleon._
import _root_.boopickle.Default._

import java.nio.ByteBuffer
import scala.util.{Failure, Success, Try}

object boopickle {
  implicit def boopickleSerializerDeserializer[T : Pickler]: SerializerDeserializer[T, ByteBuffer] = new Serializer[T, ByteBuffer] with Deserializer[T, ByteBuffer] {
    override def serialize(arg: T): ByteBuffer = Pickle.intoBytes(arg)
    override def deserialize(arg: ByteBuffer): Either[Throwable, T] =
      Try(Unpickle[T].fromBytes(arg)) match {
        case Success(arg) => Right(arg)
        case Failure(t) => Left(t)
      }
  }
}
