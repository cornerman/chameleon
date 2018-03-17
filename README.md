# chameleon
[![Build Status](https://travis-ci.org/cornerman/chameleon.svg?branch=master)](https://travis-ci.org/cornerman/chameleon)

Typeclasses for serialization

Currently supports:
* [circe](https://github.com/circe/circe)
* [upickle](https://github.com/lihaoyi/upickle)
* [boopickle](https://github.com/suzaku-io/boopickle)
* [scodec](https://github.com/scodec/scodec)

# usage

Using for example boopickle:

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

Have typeclasses for cats (`Contravariant`, `Functor`, `Invariant`):
```scala
import chameleon.ext.cats._
```
