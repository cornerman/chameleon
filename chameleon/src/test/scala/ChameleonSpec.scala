package test.chameleon

import org.scalatest._
import java.nio.ByteBuffer
import boopickle.Default._
import chameleon._
import chameleon.Boopickle._
import chameleon.Cats._

class ChameleonSpec extends AsyncFreeSpec with MustMatchers {
  "works" in {
    val serializer = Serializer[String, ByteBuffer]
    val deserializer = Deserializer[String, ByteBuffer]

    val input = "hi du ei!"
    val result = deserializer.deserialize(serializer.serialize(input))

    result mustEqual Right(input)
  }

  "transform" in {
    val serializer = Serializer[String, ByteBuffer]
    val deserializer = Deserializer[String, ByteBuffer]

    val intSerializer = serializer.contramap[Int](_.toString)
    val intDeserialier = deserializer.map[Int](_.toInt)

    val input = 10
    val result = intDeserialier.deserialize(intSerializer.serialize(input))

    result mustEqual Right(input)
  }

  "transform with flatMap" in {
    val serializer = Serializer[String, ByteBuffer]
    val deserializer = Deserializer[String, ByteBuffer]

    val intSerializer = serializer.contramap[Int](_.toString)
    val intDeserialier = deserializer.flatMap[Int](s => util.Try(s.toInt).toEither)

    val input = 10
    val result = intDeserialier.deserialize(intSerializer.serialize(input))

    result mustEqual Right(input)
  }

  "transform with imap" in {
    val serdes = SerializerDeserializer[String, ByteBuffer]

    val intSerdes = serdes.imap[Int](_.toInt)(_.toString)

    val input = 10
    val result = intSerdes.deserialize(intSerdes.serialize(input))

    result mustEqual Right(input)
  }
}
