package harals

trait Serializer[Type, PickleType] {
  def serialize(arg: Type): PickleType
}
trait Deserializer[Type, PickleType] {
  def deserialize(arg: PickleType): Either[Throwable, Type]
}
