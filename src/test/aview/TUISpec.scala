package de.htwg.se.mill.aview

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers
import de.htwg.se.mill.model.Player
import de.htwg.se.mill.model.Game
import de.htwg.se.mill.model.Board
import de.htwg.se.mill.model.GameState
import de.htwg.se.mill.model.Field
import de.htwg.se.mill.controller.Controller
import java.io.ByteArrayInputStream

class TUISpec extends AnyWordSpec with Matchers {
  "A new TUI" when {
    val melanie = Player("Melanie", "🔴")
    val reyhan = Player("Reyhan", "🔵")
    val controller = Controller(Board.withSize().get)
    controller.addFirstPlayer(melanie.name)
    controller.addSecondPlayer(reyhan.name)
    controller.newGame
    val firstField = Field(0, 0, 0)
    val game = controller.currentGame.get
    val tui = TUI(controller)

    "processing input" should {
      "quit the game on entering q" in {
        tui.onInput("q") should be(TUIStatusCode.QUIT)
      }
      "start a new game on entering n" in {
        controller.currentGame = Some(
          game.copy(
            state = GameState.Moving
          )
        )
        // TODO: test for board
        tui.onInput("n") should be(TUIStatusCode.NEW_GAME)
        controller.currentGame.get.equals(game) should be(true)
      }
      "fail if the game state is either Setting or Removing on entering an invalid command" in {
        controller.currentGame = Some(game.copy(state = GameState.Setting))
        tui.onInput("1") should be(
          TUIStatusCode.WRONG_SETTING_OR_REMOVING_COMMAND
        )
        tui.onInput("121 123") should be(
          TUIStatusCode.WRONG_SETTING_OR_REMOVING_COMMAND
        )
        controller.currentGame = Some(game.copy(state = GameState.Removing))
        tui.onInput("1") should be(
          TUIStatusCode.WRONG_SETTING_OR_REMOVING_COMMAND
        )
        tui.onInput("121 123") should be(
          TUIStatusCode.WRONG_SETTING_OR_REMOVING_COMMAND
        )
      }
      "fail if the game state is either Moving or Flying on entering an invalid command" in {
        controller.currentGame = Some(game.copy(state = GameState.Moving))
        tui.onInput("111") should be(
          TUIStatusCode.WRONG_MOVING_OR_FLYING_COMMAND
        )
        tui.onInput("11 123") should be(
          TUIStatusCode.WRONG_MOVING_OR_FLYING_COMMAND
        )
        controller.currentGame = Some(game.copy(state = GameState.Flying))
        tui.onInput("111") should be(
          TUIStatusCode.WRONG_MOVING_OR_FLYING_COMMAND
        )
        tui.onInput("11 123") should be(
          TUIStatusCode.WRONG_MOVING_OR_FLYING_COMMAND
        )
      }
      "fail if the field in the first command part could not be found" in {
        controller.currentGame = Some(game.copy(state = GameState.Setting))
        tui.onInput("221") should be(TUIStatusCode.WRONG_FIRST_FIELD)
      }
      "fail if the field in the second command part could not be found" in {
        controller.currentGame = Some(game.copy(state = GameState.Moving))
        tui.onInput("112 221") should be(TUIStatusCode.WRONG_SECOND_FIELD)
      }
      "fail setting a piece" in {
        controller.currentPlayer = Some(melanie)
        val currentBoard = controller.currentGame.get.board
        controller.currentGame = Some(
          game.copy(board =
            Board(
              currentBoard.fields.updated(
                currentBoard.fields.indexOf(firstField),
                firstField.copy(color = melanie.color)
              ),
              currentBoard.size
            )
          )
        )
        tui.onInput("111") should be(TUIStatusCode.SETTING_PIECE_FAILED)
      }
      "fail moving a piece" in {
        controller.currentPlayer = Some(melanie)
        val currentBoard = controller.currentGame.get.board
        controller.currentGame = Some(
          game.copy(
            board = Board(
              currentBoard.fields.updated(
                currentBoard.fields.indexOf(firstField),
                firstField.copy(color = reyhan.color)
              ),
              currentBoard.size
            ),
            state = GameState.Moving
          )
        )
        tui.onInput("111 211") should be(TUIStatusCode.MOVING_PIECE_FAILED)
      }
      "fail removing a piece" in {
        controller.currentPlayer = Some(melanie)
        val currentBoard = controller.currentGame.get.board
        controller.currentGame = Some(
          game.copy(
            board = Board(
              currentBoard.fields.updated(
                currentBoard.fields.indexOf(firstField),
                firstField.copy(color = melanie.color)
              ),
              currentBoard.size
            ),
            state = GameState.Removing
          )
        )
        tui.onInput("111") should be(TUIStatusCode.REMOVING_PIECE_FAILED)
      }
      "move a piece successfully" in {
        val currentBoard = controller.currentGame.get.board
        controller.currentGame = Some(
          game.copy(
            board = Board(
              currentBoard.fields.updated(
                currentBoard.fields.indexOf(firstField),
                firstField.copy(color = melanie.color)
              ),
              currentBoard.size
            ),
            state = GameState.Moving
          )
        )
        tui.onInput("111 211") should be(TUIStatusCode.SUCCESSFUL)
      }
      "set a piece successfully" in {
        controller.currentGame = Some(game.copy(state = GameState.Setting))
        tui.onInput("111") should be(TUIStatusCode.SUCCESSFUL)
      }
      "remove a piece successfully" in {
        val currentBoard = controller.currentGame.get.board
        controller.currentPlayer = Some(melanie)
        controller.currentGame = Some(
          game.copy(
            board = Board(
              currentBoard.fields.updated(
                currentBoard.fields.indexOf(firstField),
                firstField.copy(color = reyhan.color)
              ),
              currentBoard.size
            ),
            state = GameState.Removing
          )
        )
        tui.onInput("111") should be(TUIStatusCode.SUCCESSFUL)
      }
      "win" in {
        controller.currentPlayer = Some(melanie)
        controller.currentGame = Some(
          game.copy(
            board = Board(
              game.board.fields
                .updated(
                  game.board.fields.indexOf(firstField),
                  firstField.copy(color = reyhan.color)
                )
                .updated(
                  game.board.fields.indexOf(Field(1, 0, 2)),
                  Field(1, 0, 2).copy(color = reyhan.color)
                )
                .updated(
                  game.board.fields.indexOf(Field(2, 0, 0)),
                  Field(2, 0, 0).copy(color = reyhan.color)
                )
                .updated(
                  game.board.fields.indexOf(Field(0, 0, 1)),
                  Field(0, 0, 1).copy(color = melanie.color)
                )
                .updated(
                  game.board.fields.indexOf(Field(1, 0, 1)),
                  Field(1, 0, 1).copy(color = melanie.color)
                )
                .updated(
                  game.board.fields.indexOf(Field(2, 0, 1)),
                  Field(2, 0, 1).copy(color = melanie.color)
                ),
              game.board.size
            ),
            state = GameState.Removing,
            setStones = Math.pow(game.board.size, 2).toInt * game.players.length
          )
        )
        tui.onInput("111") should be(TUIStatusCode.SUCCESSFUL)
        controller.currentGame.get.equals(game) should be(true)
      }
      "run and quit successfully" in {
        val controller = Controller(Board.withSize().get)
        val tui = TUI(controller)
        Console.withIn(
          new ByteArrayInputStream(("melanie\nreyhan\n111\nq").getBytes())
        ) {
          tui.run
        }

      }
    }
  }
}
