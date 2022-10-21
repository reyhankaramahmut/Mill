package de.htwg.se.mill

import de.htwg.se.mill.model.Board
import de.htwg.se.mill.aview.TUI
import scala.util.{Success, Failure}
import de.htwg.se.mill.controller.Controller

object Mill {
  def main(args: Array[String]): Unit = {
    val board = Board.withSize() match {
      case Success(board: Board) => board
      case Failure(exception)    => throw exception
    }
    val controller = Controller(board)
    val tui = TUI(controller)
    tui.run
  }
}
