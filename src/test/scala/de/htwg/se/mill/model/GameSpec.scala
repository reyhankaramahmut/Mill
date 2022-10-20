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
  "A game" when {
    val board = Board.withSize().get
    val melanie = Player("Melanie", "🔴")
    val reyhan = Player("Reyhan", "🔵")
    val players = Vector(melanie, reyhan)
    val game = Game(board, players)
    val firstField = Field(0, 0, 0)
    "is set a piece" should {
      "be not valid if its horizontal location is outside of the board" in {
        game.isValidSet(Field(3, 0, 0)) should be(false)
        game.isValidSet(Field(-1, 0, 0)) should be(false)
      }
      "be not valid if its vertical location is outside of the board" in {
        game.isValidSet(Field(0, 3, 0)) should be(false)
        game.isValidSet(Field(0, -1, 0)) should be(false)
      }

      "be not valid if its ring location is outside of the board" in {
        game.isValidSet(Field(0, 0, 3)) should be(false)
        game.isValidSet(Field(0, 0, -1)) should be(false)
      }
      "be not valid if its set on a field that is already in use" in {
        Game(
          Board(
            game.board.fields.updated(
              game.board.fields.indexOf(
                firstField
              ),
              new Field(firstField, reyhan.color)
            ),
            game.board.size
          ),
          players,
          game.state
        ).isValidSet(firstField) should be(false)
      }
      "be valid if its set on a correct field" in {
        Game(
          Board(
            game.board.fields.updated(
              game.board.fields.indexOf(
                firstField
              ),
              new Field(firstField, reyhan.color)
            ),
            game.board.size
          ),
          players,
          game.state
        ).isValidSet(Field(1, 0, 0)) should be(true)
      }
    }
  }
}
