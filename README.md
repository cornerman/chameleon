<p align="center">
    <img src="https://user-images.githubusercontent.com/34600369/41201195-3fc423fe-6cab-11e8-9956-a300ab40e2e7.png" alt="Chameleon" width="500">
</p>

# chameleon
[![Build Status](https://travis-ci.org/cornerman/chameleon.svg?branch=master)](https://travis-ci.org/cornerman/chameleon)

Typeclasses for serialization

Currently supports:
* [circe](https://github.com/circe/circe)
* [upickle](https://github.com/lihaoyi/upickle)
* [boopickle](https://github.com/suzaku-io/boopickle)
* [scodec](https://github.com/scodec/scodec)

Get latest release:
```scala
libraryDependencies += "com.github.cornerman" %%% "chameleon" % "0.3.3"
```

Or get development snapshots via jitpack:
```scala
resolvers += "jitpack" at "https://jitpack.io"
libraryDependencies += "com.github.cornerman.chameleon" %%% "chameleon" % "master-SNAPSHOT"
```

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
