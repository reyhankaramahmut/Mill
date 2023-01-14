package de.htwg.se.mill

import de.htwg.se.mill.model.Board
import de.htwg.se.mill.aview.TUI
import scala.util.{Success, Failure}
import de.htwg.se.mill.controller.Controller
import de.htwg.se.mill.aview.GUI
import scala.io.StdIn.readLine
import scalafx.application.Platform
import de.htwg.se.mill.model.BoardInterface

object Mill {
  def main(args: Array[String]): Unit = {
    val board = Board.withSize() match {
      case Success(board: BoardInterface) => board
      case Failure(exception)             => throw exception
    }
    val controller = Controller(board)

    val tui = TUI(controller)
    // tui.start

    val gui = GUI(controller)
    val guiThread = new Thread(() => {
      gui.main(Array.empty)
      System.exit(0)
    })
    guiThread.setDaemon(true)
    guiThread.start()

    tui.run
  }
}
