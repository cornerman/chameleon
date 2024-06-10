<p align="center">
    <img src="https://user-images.githubusercontent.com/34600369/41201195-3fc423fe-6cab-11e8-9956-a300ab40e2e7.png" alt="Chameleon" width="500">
</p>

# chameleon

Typeclasses for serialization

Currently supports:
* [circe](https://github.com/circe/circe)
* [upickle](https://github.com/lihaoyi/upickle)
* [boopickle](https://github.com/suzaku-io/boopickle)
* [scodec](https://github.com/scodec/scodec)
* [jsoniter](https://github.com/plokhotnyuk/jsoniter-scala)
* [scalapb](https://github.com/scalapb/ScalaPB)
* [zio-json](https://github.com/zio/zio-json)

We build one artifact with an `Optional` dependency on each of the above serialization libraries.
This will not bloat your project. It only has an effect if you explicitly depend on the serialization library yourself.

Get latest release:
```scala
libraryDependencies += "com.github.cornerman" %%% "chameleon" % "0.4.0"
```

We additionally publish sonatype snapshots for every commit on master.

# Usage

Using for example boopickle:

```scala
import chameleon._

// boopickle-specific imports
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

## http4s

We have an extra package to integrate `http4s` with chameleon `Serializer` and `Deserializer`. Specifically to create `EntityEncoder` and `EntityDecoder`.

Usage:
```scala
libraryDependencies += "com.github.cornerman" %%% "chameleon-http4s" % "0.4.0"
```

To work with json inside your API:
```scala
import chameleon.ext.http4s.JsonStringCodec.*
import chameleon.ext.upickle.*

// serialize case class as json response
Ok(json(MyCaseClass("hallo")))

// deserialize json request into case class
jsonAs[MyCaseClass](someRequest)
```

You can also use the auto import, which provides implicit `EntityEncoder` / `EntityDecoder` for all types with `Serializer` / `Deserializer`. This should only be imported when exclusively working with json.
```scala
import chameleon.ext.http4s.JsonStringCodec.auto.*
import chameleon.ext.upickle.*

// serialize
Ok(MyCaseClass("hallo"))

// deserialize
someRequest.as[MyCaseClass]
```

# Motivation

Say, you want to write a library that needs serialization but abstracts over the type of serialization. Then you might end up with something like this:

```scala
trait Library[PickleType] {
    def readAndDo() = {
        val pickled: PickleType = ???
        ???
    }

    def writeAndDo() = {
        val thing: Thing = ???
        ???
    }
}
```

But how can you deserialize the `pickled` value and how do you serialize a `thing`? You then need to let the user provide an implementation for their serialization of `PickleType`.

With chameleon, you can use existing typeclasses `Serializer` and `Deserializer` which are generic on the pickled type:

```scala
import chameleon._

trait Library[PickleType] {
    def readAndDo(implicit d: Deserializer[Thing, PickleType]) = {
        val pickled: PickleType = ???
        d.deserialize(pickled) match {
            case Right(thing: Thing) => ???
            case Left(err: Throwable) => ???
        }
    }

    def writeAndDo(implicit s: Serializer[Thing, PickleType]) = {
        val thing: Thing = ???
        val pickled: PickleType = s.serialize(thing)
        ???
    }
}
```

Users of this library can now decide what kind of serialization they want to use and rely on existing implementation for some serializers. If you want to use this library with, e.g., JSON using circe, you can do:
```scala
import io.circe._, io.circe.syntax._, io.circe.generic.auto._
import chameleon.ext.circe._

val lib: Library[String] = ???
lib.readAndDo()
lib.writeAndDo()
```

# Support additional Serializers

If your favorite serialization library is not supported yet, you can easily add it (see [existing implementations](https://github.com/cornerman/chameleon/tree/master/chameleon/src/main/scala/ext)). You need to define implicit `Serializer` and `Deserializer` instances for that library. Then, please add a PR for it.
