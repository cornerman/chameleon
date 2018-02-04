package chameleon

trait Serializer[Type, PickleType] { self =>
  def serialize(arg: Type): PickleType

  final def contramap[T](f: T => Type) = new Serializer[T, PickleType] {
    def serialize(arg: T): PickleType = self.serialize(f(arg))
  }
}
object Serializer {
  def apply[Type, PickleType](implicit s: Serializer[Type, PickleType]): Serializer[Type, PickleType] = s

  implicit def identitySerializer[T]: Serializer[T, T] = new Serializer[T, T] {
    def serialize(arg: T): T = arg
  }
}

trait Deserializer[Type, PickleType] { self =>
  def deserialize(arg: PickleType): Either[Throwable, Type]

  final def map[T](f: Type => T) = new Deserializer[T, PickleType] {
    def deserialize(arg: PickleType): Either[Throwable, T] = self.deserialize(arg).right.map(f)
  }

  final def flatMap[T](f: Type => Either[Throwable, T]) = new Deserializer[T, PickleType] {
    def deserialize(arg: PickleType): Either[Throwable, T] = self.deserialize(arg).right.flatMap(f)
  }
}
object Deserializer {
  def apply[Type, PickleType](implicit d: Deserializer[Type, PickleType]): Deserializer[Type, PickleType] = d

  implicit def identityDeserializer[T]: Deserializer[T, T] = new Deserializer[T, T] {
    def deserialize(arg: T): Either[Throwable, T] = Right(arg)
  }
}

object SerializerDeserializer {
  def apply[Type, PickleType](implicit s: Serializer[Type, PickleType], d: Deserializer[Type, PickleType]) = new Serializer[Type, PickleType] with Deserializer[Type, PickleType] {
    def serialize(arg: Type): PickleType = s.serialize(arg)
    def deserialize(arg: PickleType): Either[Throwable, Type] = d.deserialize(arg)
  }
}
