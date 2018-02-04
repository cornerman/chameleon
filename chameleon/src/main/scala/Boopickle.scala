package chameleon

import _root_.boopickle.Default._
import java.nio.ByteBuffer

import scala.util.{Failure, Success, Try}

package object boopickle {
  implicit def boopickleSerializer[T : Pickler]: Serializer[T, ByteBuffer] = new Serializer[T, ByteBuffer] {
    override def serialize(arg: T): ByteBuffer = Pickle.intoBytes(arg)
  }
  implicit def boopickleDeserializer[T : Pickler]: Deserializer[T, ByteBuffer] = new Deserializer[T, ByteBuffer] {
    override def deserialize(arg: ByteBuffer): Either[Throwable, T] =
      Try(Unpickle[T].fromBytes(arg)) match {
        case Success(arg) => Right(arg)
        case Failure(t) => Left(t)
      }
  }

  val implicits = new Implicits[ByteBuffer]
}
