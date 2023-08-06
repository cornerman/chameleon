package chameleon.ext

import chameleon._
import zio.json._
import zio.json.ast.Json

object zioJson {

  final case class DeserializeException(msg: String) extends Throwable(msg)

  implicit def zioJsonToString[T](implicit C: JsonCodec[T]): SerializerDeserializer[T, String] = new Serializer[T, String] with Deserializer[T, String] {
    override def serialize(arg: T): String                      = C.encoder.encodeJson(arg, None).toString
    override def deserialize(arg: String): Either[Throwable, T] = C.decoder.decodeJson(arg).left.map(DeserializeException.apply)
  }

  implicit def zioJsonToAST[T](implicit C: JsonCodec[T]): SerializerDeserializer[T, Json] = new Serializer[T, Json] with Deserializer[T, Json] {
    override def serialize(arg: T): Json                      = C.encoder.toJsonAST(arg).toOption.get
    override def deserialize(arg: Json): Either[Throwable, T] = C.decoder.fromJsonAST(arg).left.map(DeserializeException.apply)
  }
}
