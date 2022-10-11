package de.htwg.se.mill.model

import de.htwg.se.mill.model.Field
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

class FieldSpec extends AnyWordSpec with Matchers {
  "A Field" when {
    "new" should {
      val field = Field(0, 0, 0, "🔴")
      "have a x" in {
        field.x should be(0)
      }
      "have a y" in {
        field.y should be(0)
      }
      "have a ring" in {
        field.ring should be(0)
      }
      "have a player color" in {
        field.color should be("🔴")
      }
      "have a nice String representation" in {
        field.toString should be("🔴")
      }
    }
  }
}
