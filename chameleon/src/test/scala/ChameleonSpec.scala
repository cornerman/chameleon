package test.chameleon

import org.scalatest._
import java.nio.ByteBuffer
import boopickle.DefaultBasic._
import chameleon._
import chameleon.boopickle._

class ChameleonSpec extends AsyncFreeSpec with MustMatchers {
  case class Test(x: Int, y: Boolean, z: String)

  "works" in {
    val serializer = implicitly[Serializer[String, ByteBuffer]]
    val deserializer = implicitly[Deserializer[String, ByteBuffer]]

    val input = "hi du ei!"
    val result = deserializer.deserialize(serializer.serialize(input))

    result mustEqual Right(input)
  }

  "works with case class native" in {
    import chameleon.boopickle.implicits._
    val serializer = implicitly[Serializer[Test, ByteBuffer]]
    val deserializer = implicitly[Deserializer[Test, ByteBuffer]]

    val input = Test(1, true, "zirkus")
    val s = serializer.serialize(input)
    println("native s " + s.position() + " , " + s.limit() + " , " + s.remaining())
    val result = deserializer.deserialize(s)

    result mustEqual Right(input)
  }

  "works with case class boopickle" in {
    import _root_.boopickle.Default._
    val serializer = implicitly[Serializer[Test, ByteBuffer]]
    val deserializer = implicitly[Deserializer[Test, ByteBuffer]]

    val input = Test(1, true, "zirkus")
    val s = serializer.serialize(input)
    println("boop s " + s.position() + " , " + s.limit() + " , " + s.remaining())
    val result = deserializer.deserialize(s)

    result mustEqual Right(input)
  }

  "transform" in {
    val serializer = implicitly[Serializer[String, ByteBuffer]]
    val deserializer = implicitly[Deserializer[String, ByteBuffer]]

    val intSerializer = serializer.contramap[Int](_.toString)
    val intDeserialier = deserializer.map[Int](_.toInt)

    val input = 10
    val result = intDeserialier.deserialize(intSerializer.serialize(input))

    result mustEqual Right(input)
  }

  "transform with flatMap" in {
    val serializer = implicitly[Serializer[String, ByteBuffer]]
    val deserializer = implicitly[Deserializer[String, ByteBuffer]]

    val intSerializer = serializer.contramap[Int](_.toString)
    val intDeserialier = deserializer.flatMap[Int](s => util.Try(s.toInt).toEither)

    val input = 10
    val result = intDeserialier.deserialize(intSerializer.serialize(input))

    result mustEqual Right(input)
  }
}
