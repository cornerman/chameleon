package chameleon.ext

import chameleon._
import io.circe._, io.circe.parser._, io.circe.syntax._

object circe {
  implicit def circeSerializer[T : Encoder]: Serializer[T, String] = new Serializer[T, String] {
    override def serialize(arg: T): String = arg.asJson.noSpaces
  }
  implicit def circeDeserializer[T : Decoder]: Deserializer[T, String] = new Deserializer[T, String] {
    override def deserialize(arg: String): Either[Throwable, T] = decode[T](arg)
  }

  implicit def jsonSerializer[T : Encoder]: Serializer[T, Json] = new Serializer[T, Json] {
    override def serialize(arg: T): Json = arg.asJson
  }
  implicit def jsonDeserializer[T : Decoder]: Deserializer[T, Json] = new Deserializer[T, Json] {
    override def deserialize(arg: Json): Either[Throwable, T] = arg.as[T]
  }
}
