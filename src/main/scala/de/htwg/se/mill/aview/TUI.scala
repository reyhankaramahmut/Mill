package de.htwg.se.mill.aview
import de.htwg.se.mill.model.Game
import de.htwg.se.mill.model.Board
import de.htwg.se.mill.model.Field
import de.htwg.se.mill.model.Player
import de.htwg.se.mill.model.GameState

final class TUI {
  @throws(classOf[IllegalArgumentException])
  def processInput(player: Player, input: String, game: Game): Game = {
    input match {
      case "q" => game
      case "n" => Game(new Board, game.players)
      // chess input notation: (columnrowring)111 121, 111, 111 133
      case _ => {
        if (!input.matches("[1-9][1-9][1-9]( [1-9][1-9][1-9])?")) {
          throw new IllegalArgumentException(
            "Your command is wrong. Please check it again. Should be something like 111 121 or 111."
          )
        }
        val fields = input.split(" ").map(field => field.split(""))
        val field =
          Field(
            fields(0)(0).toInt - 1,
            fields(0)(1).toInt - 1,
            fields(0)(2).toInt - 1
          )
        // moving, flying
        if (fields.length > 1) {
          val to =
            Field(
              fields(1)(0).toInt - 1,
              fields(1)(1).toInt - 1,
              fields(1)(2).toInt - 1
            )
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
