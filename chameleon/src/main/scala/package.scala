package object chameleon {
  type SerializerDeserializer[T, PickleType] = Serializer[T, PickleType] with Deserializer[T, PickleType]

  implicit class RichSerializerDeserializer[Type, PickleType](private val sd: SerializerDeserializer[Type, PickleType]) extends AnyVal {
    def imap[T](f: Type => T)(g: T => Type): SerializerDeserializer[T, PickleType] = SerializerDeserializer(sd.contramap(g), sd.map(f))
  }
}
