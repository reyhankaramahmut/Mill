package de.htwg.se.mill.controller

import de.htwg.se.mill.model.Game
import de.htwg.se.mill.model.Board
import de.htwg.se.mill.model.Field
import de.htwg.se.mill.model.Player
import scala.util.{Try, Success, Failure}
import de.htwg.se.mill.util.Observable

final case class Controller(private val board: Board) extends Observable {
  val twoPlayers = new Array[Player](2)
  var currentGame: Option[Game] = None
  var currentPlayer: Option[Player] = None

  def addFirstPlayer(playerName: String, playerColor: String = "🔴") = {
    twoPlayers(0) = Player(playerName, playerColor)
  }
  def addSecondPlayer(playerName: String, playerColor: String = "🔵") = {
    twoPlayers(1) = Player(playerName, playerColor)
  }
  def newGame = {
    currentGame = Some(
      Game(
        Board.withSize(board.size).get,
        twoPlayers
      )
    )
    currentPlayer = Some(twoPlayers(0))
    notifyObservers(None)
  }

  def setPiece(field: Field): Option[Throwable] = onTurn(
    currentGame.get.setPiece(currentPlayer.get, field)
  )
  def movePiece(from: Field, to: Field): Option[Throwable] = onTurn(
    currentGame.get.movePiece(currentPlayer.get, from, to)
  )
  def removePiece(field: Field): Option[Throwable] = onTurn(
    currentGame.get.removePiece(currentPlayer.get, field)
  )

  private def onTurn(turn: Try[Game]): Option[Throwable] =
    turn match {
      case Success(game: Game) => {
        currentGame = Some(game)
        if (!game.isRemoving) {
          currentPlayer = Some(
            twoPlayers.find(p => !p.equals(currentPlayer.get)).get
          )
        }
        notifyObservers(None)
        return None
      }
      case Failure(error) => {
        notifyObservers(Some(error.getMessage()))
        return Some(error)
      }
    }
}
