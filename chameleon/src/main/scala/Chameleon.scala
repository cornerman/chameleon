package chameleon

//TODO make cats.syntax import work?

trait Serializer[Type, PickleType] {
  def serialize(arg: Type): PickleType
  final def contramap[T](f: T => Type) = Serializer.contravariant[PickleType].contramap(this)(f)
}
object Serializer {
  implicit def contravariant[PickleType] = new cats.Contravariant[({ type S[T] = Serializer[T, PickleType]})#S] {
    def contramap[A,B](s: Serializer[A, PickleType])(f: B => A): Serializer[B, PickleType] = new Serializer[B, PickleType] {
      def serialize(arg: B): PickleType = s.serialize(f(arg))
    }
  }
}

trait Deserializer[Type, PickleType] {
  def deserialize(arg: PickleType): Either[Throwable, Type]
  final def map[T](f: Type => T) = Deserializer.functor[PickleType].map(this)(f)
}
object Deserializer {
  implicit def functor[PickleType] = new cats.Functor[({ type S[T] = Deserializer[T, PickleType]})#S] {
    def map[A,B](s: Deserializer[A, PickleType])(f: A => B): Deserializer[B, PickleType] = new Deserializer[B, PickleType] {
      def deserialize(arg: PickleType): Either[Throwable, B] = s.deserialize(arg).right.map(f)
    }
  }
}
