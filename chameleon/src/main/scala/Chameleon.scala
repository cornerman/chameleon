package chameleon

trait Serializer[Type, PickleType] { self =>
  def serialize(arg: Type): PickleType

  final def contramap[T](f: T => Type) = new Serializer[T, PickleType] {
    def serialize(arg: T): PickleType = self.serialize(f(arg))
  }

  final def mapSerialize[T](f: PickleType => T) = new Serializer[Type, T] {
    def serialize(arg: Type): T = f(self.serialize(arg))
  }
}
object Serializer {
  def apply[Type, PickleType](implicit s: Serializer[Type, PickleType]): Serializer[Type, PickleType] = s

  implicit def IdentitySerializer[T]: Serializer[T, T] = new Serializer[T, T] {
  def serialize(arg: T): T = arg
  }
}

trait Deserializer[Type, PickleType] { self =>
  def deserialize(arg: PickleType): Either[Throwable, Type]

  final def map[T](f: Type => T) = new Deserializer[T, PickleType] {
    def deserialize(arg: PickleType): Either[Throwable, T] = self.deserialize(arg).map(f)
  }

  final def flatMap[T](f: Type => Either[Throwable, T]) = new Deserializer[T, PickleType] {
    def deserialize(arg: PickleType): Either[Throwable, T] = self.deserialize(arg).flatMap(f)
  }

  final def mapDeserialize[T](f: T => PickleType) = new Deserializer[Type, T] {
    def deserialize(arg: T): Either[Throwable, Type] = self.deserialize(f(arg))
  }

  final def flatmapDeserialize[T](f: T => Either[Throwable, PickleType]) = new Deserializer[Type, T] {
    def deserialize(arg: T): Either[Throwable, Type] = f(arg).flatMap(self.deserialize)
  }
}
object Deserializer {
  def apply[Type, PickleType](implicit d: Deserializer[Type, PickleType]): Deserializer[Type, PickleType] = d

  implicit def IdentityDeserializer[T]: Deserializer[T, T] = new Deserializer[T, T] {
    def deserialize(arg: T): Either[Throwable, T] = Right(arg)
  }
}

object SerializerDeserializer {
  def apply[Type, PickleType](implicit s: Serializer[Type, PickleType], d: Deserializer[Type, PickleType]) = new Serializer[Type, PickleType] with Deserializer[Type, PickleType] {
    def serialize(arg: Type): PickleType = s.serialize(arg)
    def deserialize(arg: PickleType): Either[Throwable, Type] = d.deserialize(arg)
  }
}
