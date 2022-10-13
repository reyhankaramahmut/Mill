package de.htwg.se.mill

import scala.io.StdIn.readLine
import de.htwg.se.mill.model.Player
import de.htwg.se.mill.model.Game
import de.htwg.se.mill.model.Board
import de.htwg.se.mill.aview.TUI
import de.htwg.se.mill.model.GameState
@main def main: Unit = {
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

  var game = Game(Board.withSize().get, Vector(player1, player2))
  var input = ""
  var currentPlayer = player1
  while (input != "q") {
    input = readLine(
      s"${game.board}\n${currentPlayer}'s turn(${game.state.representation}): "
    )
    game = tui.processInput(currentPlayer, input, game)
    if (game.state == GameState.Won) {
      println(
        s"Congratulations! $currentPlayer has won the game!\nStarting new game."
      )
      game = Game(Board.withSize(game.board.size).get, Vector(player1, player2))
    }
    if (game.state != GameState.Removing) {
      currentPlayer = if currentPlayer == player1 then player2 else player1
    }
  }
}
