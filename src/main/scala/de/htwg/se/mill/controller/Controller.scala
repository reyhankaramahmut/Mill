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
  private val twoPlayers = new Array[Player](2)
  private val winStrategy = WinStrategy.classicStrategy
  private var previousTurn: Option[Try[GameState]] = None

  private var undoCommand = new UndoCommand()
  var gameState: Option[GameState] = None

  def undo = undoCommand.undoStep
  def redo = undoCommand.redoStep

  def addFirstPlayer(playerName: String, playerColor: String = "🔴") = {
    twoPlayers(0) = Player(playerName, playerColor)
  }
  def addSecondPlayer(playerName: String, playerColor: String = "🔵") = {
    twoPlayers(1) = Player(playerName, playerColor)
  }
  def newGame = {
    // delete command history
    undoCommand = new UndoCommand()
    gameState = Some(
      SettingState(
        Game(
          board,
          twoPlayers,
          twoPlayers(0)
        )
      )
    )
    previousTurn = Some(Success(gameState.get))
    notifyObservers(None)
  }

  private def createSnapshot: Snapshot = new Snapshot(this, previousTurn)

  // Memento
  private class Snapshot(
      val controller: Controller,
      val previousTurn: Option[Try[GameState]]
  ) {
    def restore: Option[Throwable] =
      controller.previousTurn = previousTurn
      previousTurn.get match {
        case Success(state: GameState) => {
          state match {
            case RemovingState(game: Game) => {
              controller.gameState = Some(state)
            }
            case SettingState(game: Game) => {
              controller.gameState = Some(state)
            }
            case FlyingState(game: Game) => {
              controller.gameState = Some(state)
            }
            case MovingState(game: Game) => {
              controller.gameState = Some(state)
            }
          }
          controller.notifyObservers(None)
          return None
        }
        case Failure(error) => {
          controller.notifyObservers(Some(error.getMessage()))
          return Some(error)
        }
      }
  }

  // Command
  private class UndoCommand {
    private var undoStack: List[Snapshot] = Nil
    private var redoStack: List[Snapshot] = Nil
    def backup(snapshot: Snapshot): Unit = {
      undoStack = snapshot :: undoStack
    }
    def undoStep: Option[Throwable] =
      undoStack match {
        case Nil => None
        case head :: stack => {
          redoStack = createSnapshot :: redoStack
          undoStack = stack
          head.restore
        }
      }
    def redoStep: Option[Throwable] =
      redoStack match {
        case Nil => None
        case head :: stack => {
          undoStack = createSnapshot :: undoStack
          redoStack = stack
          head.restore
        }
      }
  }

  def setPiece(to: Field): Option[Throwable] = {
    undoCommand.backup(createSnapshot)
    doTurn(
      gameState.get.handle(
        GameEvent.OnSetting,
        (to, None)
      )
    )
  }

  def movePiece(from: Field, to: Field): Option[Throwable] = {
    undoCommand.backup(createSnapshot)
    doTurn(
      gameState.get.handle(
        GameEvent.OnMoving,
        (from, Some(to))
      )
    )
  }

  def removePiece(field: Field): Option[Throwable] = {
    undoCommand.backup(createSnapshot)
    doTurn(
      gameState.get.handle(
        GameEvent.OnRemoving,
        (field, None)
      )
    )
  }
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

  private def doTurn(turn: Try[GameState]): Option[Throwable] = {
    previousTurn = Some(turn)
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
}
