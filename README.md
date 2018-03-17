# chameleon
[![Build Status](https://travis-ci.org/cornerman/chameleon.svg?branch=master)](https://travis-ci.org/cornerman/chameleon)

Typeclasses for serialization

Supports:
* Circe
* Boopickle
* Upickle
* Scodec

# usage

Using for example Boopickle:

```scala
import chameleon._

// for example
import chameleon.ext.boopickle._
import java.nio.ByteBuffer
import boopickle.Default._

val serializer = Serializer[String, ByteBuffer]
val deserializer = Deserializer[String, ByteBuffer]

val input = "chameleon"
val serialized = serializer.serialize(input)
val deserialized = deserializer.deserialize(serialized)
```
