package de.htwg.se.mill.aview

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers
import de.htwg.se.mill.model.Player
import de.htwg.se.mill.model.Game
import de.htwg.se.mill.model.Board
import de.htwg.se.mill.model.GameState
import de.htwg.se.mill.model.Field
import de.htwg.se.mill.controller.Controller

class TUISpec extends AnyWordSpec with Matchers {
  "A new TUI" when {

    val melanie = Player("Melanie", "🔴")
    val reyhan = Player("Reyhan", "🔵")
    val players = Array(melanie, reyhan)
    val board = Board.withSize().get
    val controller = Controller(board)
    val tui = TUI(controller)

    "processing input" should {
      "yield the game successfully on entering q" in {
        val processed = tui.processInput(melanie, "q", game)
        processed.isSuccess should be(true)
        processed.get.equals(game) should be(true)
      }
      "yield a new game succesfully if a board could be created on entering n" in {
        val processed =
          tui.processInput(melanie, "n", game.copy(state = GameState.Moving))
        processed.isSuccess should be(true)
        processed.get.equals(game) should be(true)
      }
      "fail if a board could not be created on entering n" in {
        val processed =
          tui.processInput(
            melanie,
            "n",
            game.copy(board = Board(game.board.fields, 4))
          )
        processed.isFailure should be(true)
      }
      "fail if the game state is either Setting or Removing on entering an invalid command" in {
        tui
          .processInput(
            melanie,
            "1",
            game.copy(state = GameState.Setting)
          )
          .isFailure should be(true)
        tui
          .processInput(
            melanie,
            "121 123",
            game.copy(state = GameState.Removing)
          )
          .isFailure should be(true)
      }
      "fail if the game state is either Moving or Flying on entering an invalid command" in {
        tui
          .processInput(
            melanie,
            "111",
            game.copy(state = GameState.Moving)
          )
          .isFailure should be(true)
        tui
          .processInput(
            melanie,
            "11 123",
            game.copy(state = GameState.Flying)
          )
          .isFailure should be(true)
      }
      "fail if the field in the first command could not be found" in {
        tui
          .processInput(
            melanie,
            "221",
            game.copy(state = GameState.Setting)
          )
          .isFailure should be(true)
      }
      "fail if the field in the second command could not be found" in {
        tui
          .processInput(
            melanie,
            "112 221",
            game.copy(state = GameState.Moving)
          )
          .isFailure should be(true)
      }
      "yield a game with a moved piece if there are two valid commands" in {
        tui
          .processInput(
            melanie,
            "111 211",
            game.copy(
              board = Board(
                fields = game.board.fields.updated(
                  game.board.fields.indexOf(Field(0, 0, 0)),
                  Field(0, 0, 0, melanie.color)
                ),
                game.board.size
              ),
              state = GameState.Moving
            )
          )
          .isSuccess should be(true)
      }
      "yield a game with a set piece if there is one command and the game state is Setting" in {
        tui
          .processInput(melanie, "111", game.copy(state = GameState.Setting))
          .isSuccess should be(true)
      }
      "yield a game with a removed piece if there is one command and the game state is Removing" in {
        tui
          .processInput(
            melanie,
            "111",
            game.copy(
              board = Board(
                fields = game.board.fields.updated(
                  game.board.fields.indexOf(Field(0, 0, 0)),
                  Field(0, 0, 0, reyhan.color)
                ),
                game.board.size
              ),
              state = GameState.Removing
            )
          )
          .isSuccess should be(true)
      }
    }
  }
}
