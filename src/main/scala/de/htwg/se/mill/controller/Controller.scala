package de.htwg.se.mill.controller

import de.htwg.se.mill.model.Game
import de.htwg.se.mill.model.Board
import de.htwg.se.mill.model.Field
import de.htwg.se.mill.model.Player
import scala.util.{Try, Success, Failure}
import de.htwg.se.mill.util.Observable

final case class Controller(board: Board) extends Observable {
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
        board,
        twoPlayers
          .updated(0, twoPlayers(0).copy(setStones = 0))
          .updated(1, twoPlayers(1).copy(setStones = 0))
      )
    )
    currentPlayer = Some(twoPlayers(0))
    notifyObservers(None)
  }
  private def nextTurn(game: Game) = {
    currentGame = Some(game)
    if (!game.isRemoving) {
      currentPlayer = Some(
        twoPlayers.find(p => !p.equals(currentPlayer.get)).get
      )
    }
    notifyObservers(None)
  }
  def setPiece(field: Field) = {
    currentGame.get.setPiece(currentPlayer.get, field) match {
      case Success(game: Game) => nextTurn(game)
      case Failure(error)      => notifyObservers(Some(error))
    }
  }
  def movePiece(from: Field, to: Field) = {
    currentGame.get.movePiece(currentPlayer.get, from, to) match {
      case Success(game: Game) => nextTurn(game)
      case Failure(error)      => notifyObservers(Some(error))
    }
  }
  def removePiece(field: Field) = {
    currentGame.get.removePiece(currentPlayer.get, field) match {
      case Success(game: Game) => nextTurn(game)
      case Failure(error)      => notifyObservers(Some(error))
    }
  }
}
