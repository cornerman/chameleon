package chameleon.ext

import chameleon._
import _root_.cats.{Contravariant, Functor, Invariant}

object cats {
  implicit def contravariantSerializer[PickleType]: Contravariant[Serializer[*, PickleType]] = new Contravariant[Serializer[*, PickleType]] {
    override def contramap[A,B](s: Serializer[A, PickleType])(f: B => A): Serializer[B, PickleType] = s.contramap(f)
  }
  implicit def functorDeserializer[PickleType]: Functor[Deserializer[*, PickleType]] = new Functor[Deserializer[*, PickleType]] {
    override def map[A,B](s: Deserializer[A, PickleType])(f: A => B): Deserializer[B, PickleType] = s.map(f)
  }
  implicit def invariantSerializerDeserializer[PickleType]: Invariant[SerializerDeserializer[*, PickleType]] = new Invariant[SerializerDeserializer[*, PickleType]] {
    override def imap[A,B](sd: SerializerDeserializer[A, PickleType])(f: A => B)(g: B => A): SerializerDeserializer[B, PickleType] = sd.imap(f)(g)
  }
}
