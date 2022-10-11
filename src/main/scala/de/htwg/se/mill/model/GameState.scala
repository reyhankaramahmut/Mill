package de.htwg.se.mill.model

import de.htwg.se.mill.model.Board
import de.htwg.se.mill.model.Player
import de.htwg.se.mill.model.Field

/*
      a             b             c
    1 ⚫――――――――――――⚫――――――――――――⚫
      │   ⚫――――――――⚫――――――――⚫   │ 1
      │   │   ⚫――――⚫――――⚫   │ 2   │
      │   │   │            │ 3  │   │
    2 ⚫――⚫――⚫          ⚫――⚫――⚫
      │   │   │            │   │   │
      │   │   ⚫――――⚫――――⚫   │   │
      │   ⚫――――――――⚫――――――――⚫   │
    3 ⚫――――――――――――⚫――――――――――――⚫
 */
// chess input notation: a11 a21, a11, a11 c33
enum GameState {
  case Setting, Moving, Removing, Flying, Finish
}
final case class Game(
    board: Board,
    players: Vector[Player],
    state: GameState = GameState.Setting
) {
  def isValidSet(field: Field): Boolean =
    field.x < board.size && field.x >= 0 && field.y < board.size
      && field.y >= 0 && field.ring < board.size && field.ring >= 0
      && board.fields
        .find(f => f.equals(field))
        .get
        .color == UnsetFieldColor

  def isValidMove(from: Field, to: Field): Boolean = isValidSet(to) &&
    Math.abs(from.x - to.x) == 1 ^ Math.abs(from.y - to.y) == 1
    ^ Math.abs(from.ring - to.ring) == 1

  // TODO: Ausgehend von to, eckpunkte entscheiden, dimensionen fixieren und alle auf dem weg
  // liegenden steine nach farbe zaehlen
  def isMill(to: Field, board: Board): Boolean = true

  @throws(classOf[IllegalArgumentException])
  def setPiece(player: Player, field: Field): Game = {
    if (state != GameState.Setting) {
      throw new IllegalArgumentException(
        "The piece was not set. All pieces are already set."
      )
    }
    if (!isValidSet(field))
      throw new IllegalArgumentException(
        "The piece was not set. Please use a valid field that is not already in use."
      )
    val playedTurnBoard = Board(
      board.fields.updated(
        board.fields.indexOf(field),
        new Field(field, player.color)
      ),
      board.size
    )
    val playedTurnState =
      if (
        playedTurnBoard.fields
          .count(field => field.color != UnsetFieldColor) < Math
          .pow(playedTurnBoard.size, 2)
          .toInt * 2
      ) state
      else GameState.Moving
    Game(playedTurnBoard, players, playedTurnState)
  }
  def movePiece(player: Player, from: Field, to: Field): Game = {
    if (state != GameState.Moving && state != GameState.Flying) {
      throw new IllegalArgumentException(
        "The piece was not moved. Please provide a valid input for moving or flying a piece."
      )
    }
    if (state == GameState.Moving) {
      if (!isValidMove(from, to))
        throw new IllegalArgumentException(
          "The piece was not moved. Please use a valid field that is not already in use."
        )
    } else {
      if (!isValidSet(to))
        throw new IllegalArgumentException(
          "The piece was not moved. Please use a valid field that is not already in use."
        )
    }
    val playedTurnBoard = Board(
      board.fields
        .updated(
          board.fields.indexOf(from),
          new Field(from, UnsetFieldColor)
        )
        .updated(
          board.fields.indexOf(to),
          new Field(to, player.color)
        ),
      board.size
    )
    val playedTurnState =
      if (
        playedTurnBoard.fields
          .count(field => field.color != UnsetFieldColor) < Math
          .pow(playedTurnBoard.size, 2)
          .toInt * 2
      ) state
      else GameState.Moving
    Game(board, players, state)
  }
  def removePiece(player: Player, field: Field): Game = {
    if (state != GameState.Removing) {
      throw new IllegalArgumentException(
        "The piece was not removed. Please provide a valid input for removing a piece."
      )
    }
    Game(board, players, state)
  }
}
