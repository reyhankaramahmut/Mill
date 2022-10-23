package de.htwg.se.mill.aview

import scala.io.StdIn.readLine
import de.htwg.se.mill.util.Observer
import de.htwg.se.muill.controller.Controller

final case class TUI(controller: Controller) extends Observer {
  controller.add(this)
  def run = {
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
    controller.addFirstPlayer(readLine)
    println(
      "Now please enter the name of the second player to play."
    )
    controller.addSecondPlayer(readLine)
    controller.newGame
    inputLoop
  }
  override def update(error: Option[Throwable]) = {
    println(if (error.isDefined) error else controller.currentGame.get.board)
  }
  private def inputLoop: Unit = {
    val input = readLine(
      s"${controller.currentPlayer.get}'s turn(${controller.currentGame.get.state}): "
    )
    if (!onInput(input)) return
    inputLoop
  }

  def onInput(
      input: String
  ): Boolean = {
    val currentGame = controller.currentGame.get
    val currentPlayer = controller.currentPlayer.get
    input match {
      case "q" => false
      case "n" => {
        controller.newGame
        return true
      }
      // input notation: (columnrowring) e.g. 111 121 or 111
      case _ => {
        val commandPattern = s"[1-${currentGame.board.size}]{3}"

        if (
          (currentGame.isSetting || currentGame.isRemoving) && !input
            .matches(commandPattern)
        ) {
          update(
            Some(
              IllegalArgumentException(
                "Your command is wrong. Please check it again. " +
                  "Should be something like 111 for removing or setting pieces."
              )
            )
          )
          return true
        }
        if (
          (currentGame.isMoving || currentGame.isFlying) && !input
            .matches(s"$commandPattern $commandPattern")
        ) {
          update(
            Some(
              IllegalArgumentException(
                "Your command is wrong. Please check it again. " +
                  "Should be something like 111 121 for moving pieces."
              )
            )
          )
          return true
        }

        val fields = input.split(" ").map(field => field.split(""))
        val field = currentGame.board.getField(
          fields(0)(0).toInt - 1,
          fields(0)(1).toInt - 1,
          fields(0)(2).toInt - 1
        )
        if (field.isEmpty) {
          update(
            Some(
              IllegalArgumentException(
                "Your command is wrong. Please check it again. " +
                  "The field position provided is invalid."
              )
            )
          )
          return true
        }
        if (fields.length > 1) {
          val to = currentGame.board.getField(
            fields(1)(0).toInt - 1,
            fields(1)(1).toInt - 1,
            fields(1)(2).toInt - 1
          )
          if (to.isEmpty) {
            update(
              Some(
                IllegalArgumentException(
                  "Your command is wrong. Please check it again. " +
                    "The field position where the piece should be moved to is invalid."
                )
              )
            )
            return true
          }
          controller.movePiece(field.get, to.get)
        } else {
          if (currentGame.isSetting)
            controller.setPiece(field.get)
          else controller.removePiece(field.get)
        }
        if (currentGame.isWon) {
          println(
            s"Congratulations! $currentPlayer has won the game!\nStarting new game."
          )
          controller.newGame
        }
        return true
      }
    }
  }
}
