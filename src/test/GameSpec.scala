package de.htwg.se.mill.model

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers

class GameSpec extends AnyWordSpec with Matchers {
  "A new Game" when {
    "created with a board and two players" should {
      val board = Board.withSize().get
      val players = Vector(Player("Melanie", "🔴"), Player("Reyhan", "🔵"))
      val game = Game(board, players)
      "have the board" in {
        game.board should be(board)
      }
      "have the players" in {
        game.players should be(players)
      }
      "have the game state Setting" in {
        game.state should be(GameState.Setting)
      }
    }
  }
}
