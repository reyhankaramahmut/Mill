package de.htwg.se.mill

import scala.io.StdIn.readLine
import de.htwg.se.mill.model.Player
import de.htwg.se.mill.model.Game
import de.htwg.se.mill.model.Board
import de.htwg.se.mill.aview.TUI
import de.htwg.se.mill.model.GameState
import scala.util.{Success, Failure}
import util.control.Breaks._

object Muehle {
  def main(args: Array[String]): Unit = {
    println("""
Welcome to Muehle a strategy board game.
To set or remove a piece please use a command like 123
where 1 stands for the first column, 2 stands for the second row
and 3 stands for the third ring.
To move a piece please use a command like 111 112 where the first
part of the command 111 indicates the piece field before moving
and the part of the command 112 indicates the piece field after moving.
You can exit the game by pressing q key or start a new game by pressing n key.

Before starting please enter the name of the first player.""")
    val player1 = Player(readLine(), "🔴")
    println("Now please enter the name of the second player to play.")
    val player2 = Player(readLine(), "🔵")
    val tui = new TUI
    var game = Board.withSize() match {
      case Success(board: Board) => Game(board, Vector(player1, player2))
      case Failure(exception)    => throw exception
    }
    var input = ""
    var currentPlayer = player1
    while (input != "q") {
      input = readLine(
        s"${game.board}\n${currentPlayer}'s turn(${game.state.representation}): "
      )
      breakable {
        game = tui.processInput(currentPlayer, input, game) match {
          case Success(game: Game) => game
          case Failure(exception) => {
            println(exception)
            break
          }
        }
        if (game.state == GameState.Won) {
          println(
            s"Congratulations! $currentPlayer has won the game!\nStarting new game."
          )
          game = Board.withSize(game.board.size) match {
            case Success(board: Board) => Game(board, Vector(player1, player2))
            case Failure(exception)    => throw exception
          }
        }
        if (game.state != GameState.Removing) {
          currentPlayer = if currentPlayer == player1 then player2 else player1
        }
      }
    }
  }
}
