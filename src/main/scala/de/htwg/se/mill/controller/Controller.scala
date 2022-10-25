package de.htwg.se.mill.controller

import de.htwg.se.mill.model.Game
import de.htwg.se.mill.model.Board
import de.htwg.se.mill.model.Field
import de.htwg.se.mill.model.Player
import scala.util.{Try, Success, Failure}
import de.htwg.se.mill.util.Observable
import de.htwg.se.mill.model.WinStrategy
import de.htwg.se.mill.model.GameState
import de.htwg.se.mill.model.GameEvent
import de.htwg.se.mill.model.{
  SettingState,
  RemovingState,
  MovingState,
  FlyingState
}

class Controller(private val board: Board) extends Observable {
  val twoPlayers = new Array[Player](2)
  val winStrategy = WinStrategy.classicStrategy
  var gameState: Option[GameState] = None

  def addFirstPlayer(playerName: String, playerColor: String = "🔴") = {
    twoPlayers(0) = Player(playerName, playerColor)
  }
  def addSecondPlayer(playerName: String, playerColor: String = "🔵") = {
    twoPlayers(1) = Player(playerName, playerColor)
  }
  def newGame = {
    gameState = Some(
      SettingState(
        Game(
          Board.withSize(board.size).get,
          twoPlayers,
          twoPlayers(0)
        )
      )
    )
    notifyObservers(None)
  }

  def setPiece(to: Field): Option[Throwable] = onTurn(
    gameState.get.handle(
      GameEvent.OnSetting,
      (to, None)
    )
  )
  def movePiece(from: Field, to: Field): Option[Throwable] = onTurn(
    gameState.get.handle(
      GameEvent.OnMoving,
      (from, Some(to))
    )
  )
  def removePiece(field: Field): Option[Throwable] = onTurn(
    gameState.get.handle(
      GameEvent.OnRemoving,
      (field, None)
    )
  )
  def currentGameState = gameState.get match {
    case FlyingState(game: Game)   => "Flying Pieces"
    case MovingState(game: Game)   => "Moving Pieces"
    case SettingState(game: Game)  => "Setting Pieces"
    case RemovingState(game: Game) => "Removing Pieces"
  }

  def isSetting = gameState.get.isInstanceOf[SettingState]
  def isRemoving = gameState.get.isInstanceOf[RemovingState]

  def isMovingOrFlying =
    gameState.get.isInstanceOf[MovingState] || gameState.get
      .isInstanceOf[FlyingState]

  private def onTurn(turn: Try[GameState]): Option[Throwable] =
    turn match {
      case Success(state: GameState) => {
        var currentGame: Option[Game] = None
        state match {
          case RemovingState(game: Game) => {
            gameState = Some(state)
            currentGame = Some(game)
          }
          case SettingState(game: Game) => {
            gameState = Some(
              SettingState(
                game.copy(currentPlayer =
                  twoPlayers.find(p => !p.equals(game.currentPlayer)).get
                )
              )
            )
            currentGame = Some(game)
          }
          case FlyingState(game: Game) => {
            gameState = Some(
              FlyingState(
                game.copy(currentPlayer =
                  twoPlayers.find(p => !p.equals(game.currentPlayer)).get
                )
              )
            )
            currentGame = Some(game)
          }
          case MovingState(game: Game) => {
            gameState = Some(
              MovingState(
                game.copy(currentPlayer =
                  twoPlayers.find(p => !p.equals(game.currentPlayer)).get
                )
              )
            )
            currentGame = Some(game)
          }
        }

        if (winStrategy(currentGame.get)) {
          notifyObservers(
            Some(
              s"Congratulations! ${currentGame.get.currentPlayer} has won the game!\nStarting new game."
            )
          )
          newGame
        } else {
          notifyObservers(None)
        }
        return None
      }
      case Failure(error) => {
        notifyObservers(Some(error.getMessage()))
        return Some(error)
      }
    }
}
