package test.harals

import org.scalatest._

class HaralsSpec extends AsyncFreeSpec with MustMatchers {
  "true" in {
    true mustEqual true
  }
}
