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

trait Deserializer[Type, PickleType] { self =>
  import Deserializer._

  def deserialize(arg: PickleType): Result[Type]
  final def map[T](f: Type => T) = Deserializer.functor[PickleType].map(this)(f)
  final def flatMap[T](f: Type => Result[T]) = new Deserializer[T, PickleType] {
    def deserialize(arg: PickleType): Result[T] = self.deserialize(arg).right.flatMap(f)
  }
}
object Deserializer {
  type Result[T] = Either[Throwable, T]

  implicit def functor[PickleType] = new cats.Functor[({ type S[T] = Deserializer[T, PickleType]})#S] {
    def map[A,B](s: Deserializer[A, PickleType])(f: A => B): Deserializer[B, PickleType] = new Deserializer[B, PickleType] {
      def deserialize(arg: PickleType): Result[B] = s.deserialize(arg).right.map(f)
    }
  }
}

trait PickleTypeJoiner[PickleType] {
  def join(p1: PickleType, p2: PickleType): PickleType
  def empty: PickleType
}

class Implicits[PickleType](joiner: PickleTypeJoiner[PickleType]) {
  import shapeless._

  type SerializerT[T] = Serializer[T, PickleType]
  type DeserializerT[T] = Deserializer[T, PickleType]

  implicit def hconsSerializer[H, T <: HList](implicit hs: SerializerT[H], ts: SerializerT[T]): SerializerT[H :: T] = new SerializerT[H :: T] {
    override def serialize(value: H :: T): PickleType = value match {
      case h :: t =>
        val sh = hs.serialize(h)
        val st = ts.serialize(t)
        joiner.join(sh, st)
    }
  }

  implicit def hnilSerializer: SerializerT[HNil] = new SerializerT[HNil] {
    override def serialize(value: HNil): PickleType = joiner.empty
  }

  implicit def genericSerializer[T, R](implicit gen: Generic.Aux[T, R], rs: SerializerT[R]): SerializerT[T] = new SerializerT[T] {
    override def serialize(from: T): PickleType = rs.serialize(gen.to(from))
  }

  implicit def hconsDeserializer[H, T <: HList](implicit hd: DeserializerT[H], td: DeserializerT[T]): DeserializerT[H :: T] = new DeserializerT[H :: T] {
    override def deserialize(from: PickleType): Deserializer.Result[H :: T] = for {
      head <- hd.deserialize(from)
      last <- td.deserialize(from)
    } yield head :: last
  }

  implicit def hnilDeserializer: DeserializerT[HNil] = new DeserializerT[HNil] {
    override def deserialize(from: PickleType): Deserializer.Result[HNil] = Right(HNil)
      // ld.deserialize(from) match {
      // case Right(Nil) => Right(HNil)
      // case Right(list) => Left(new Exception("Expected empty list in deserializer"))
      // case Left(err) => Left(err)
    // }
  }

  implicit def genericDeserializer[T, R](implicit gen: Generic.Aux[T, R], rd: DeserializerT[R]): DeserializerT[T] = new DeserializerT[T] {
    override def deserialize(from: PickleType): Deserializer.Result[T] = rd.deserialize(from).map(gen.from)
  }
}
