package test.chameleon

import org.scalatest._
import java.nio.ByteBuffer
import boopickle.Default._
import chameleon._
import chameleon.boopickle._

class ChameleonSpec extends AsyncFreeSpec with MustMatchers {
  "works" in {
    val serializer = implicitly[Serializer[String, ByteBuffer]]
    val deserializer = implicitly[Deserializer[String, ByteBuffer]]

    val input = "hi du ei!"
    val result = deserializer.deserialize(serializer.serialize(input))

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
}
