package de.htwg.se.mill.model

import de.htwg.se.mill.model.Player
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

class PlayerSpec extends AnyWordSpec with Matchers {
  "A Player" when {
    "new" should {
      val player = Player("Melanie", "🔴")
      "have a name" in {
        player.name should be("Melanie")
      }
      "have a color" in {
        player.color should be("🔴")
      }
      "have a nice String representation" in {
        player.toString should be("Melanie 🔴")
      }
    }
  }
}
