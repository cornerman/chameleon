package test.chameleon

import org.scalatest._

class ChameleonSpec extends AsyncFreeSpec with MustMatchers {
  "true" in {
    true mustEqual true
  }
}
