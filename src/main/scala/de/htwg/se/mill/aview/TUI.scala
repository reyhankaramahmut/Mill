package de.htwg.se.mill.aview
import de.htwg.se.mill.model.Game
import de.htwg.se.mill.model.Board
import de.htwg.se.mill.model.Field
import de.htwg.se.mill.model.Player
import de.htwg.se.mill.model.GameState
import scala.util.{Try, Success, Failure}

final class TUI {
  def processInput(
      player: Player,
      input: String,
      game: Game
  ): Try[Game] = {
    input match {
      case "q" => Success(game)
      case "n" =>
        Board.withSize(game.board.size) match {
          case Success(board: Board) => Success(Game(board, game.players))
          case Failure(exception)    => Failure(exception)
        }
      // input notation: (columnrowring) 111 121, 111, 111 133
      case _ => {
        val commandPattern = s"[1-${game.board.size}]{3}"
        if (
          (game.state == GameState.Setting || game.state == GameState.Removing) && !input
            .matches(commandPattern)
        ) {
          return Failure(
            IllegalArgumentException(
              "Your command is wrong. Please check it again. " +
                "Should be something like 111 for removing or setting pieces."
            )
          )
        }
        if (
          (game.state == GameState.Moving || game.state == GameState.Flying) && !input
            .matches(s"$commandPattern $commandPattern")
        ) {
          return Failure(
            IllegalArgumentException(
              "Your command is wrong. Please check it again. " +
                "Should be something like 111 121 for moving pieces."
            )
          )
        }

        val fields = input.split(" ").map(field => field.split(""))
        var field = Field(0, 0, 0)
        game.board.fields
          .find(f =>
            f.equals(
              Field(
                fields(0)(0).toInt - 1,
                fields(0)(1).toInt - 1,
                fields(0)(2).toInt - 1
              )
            )
          ) match {
          case Some(f: Field) => { field = f }
          case None =>
            return Failure(
              IllegalArgumentException(
                "Your command is wrong. Please check it again. The field position provided is invalid."
              )
            )
        }
        // moving, flying
        if (fields.length > 1) {
          var to = Field(0, 0, 0)
          game.board.fields
            .find(f =>
              f.equals(
                Field(
                  fields(1)(0).toInt - 1,
                  fields(1)(1).toInt - 1,
                  fields(1)(2).toInt - 1
                )
              )
            ) match {
            case Some(f: Field) => { to = f }
            case None =>
              return Failure(
                IllegalArgumentException(
                  "Your command is wrong.  Please check it again.The field position where the piece should be moved to is invalid."
                )
              )
          }
          game.movePiece(player, field, to)
        }
        // setting, removing
        else {
          if (game.state == GameState.Setting) game.setPiece(player, field)
          else game.removePiece(player, field)
        }
      }
    }
  }
}
