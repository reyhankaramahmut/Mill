package de.htwg.se.mill.model
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

class GameStateSpec extends AnyWordSpec with Matchers {
  "A Game State" when {
    "new" should {
      val gameState = GameState.Setting
      "have a representation" in {
        gameState.representation should be("Setting pieces")
      }
    }
  }
}
