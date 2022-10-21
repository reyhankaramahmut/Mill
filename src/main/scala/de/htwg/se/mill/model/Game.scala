package de.htwg.se.mill.model

import scala.util.{Try, Success, Failure}
/*
  1             2             3
1 ⚫――――――――――――⚫――――――――――――⚫
  │   ⚫――――――――⚫――――――――⚫   │ 1
  │   │   ⚫――――⚫――――⚫   │ 2 │
  │   │   │            │ 3 │   │
2 ⚫――⚫――⚫          ⚫――⚫――⚫
  │   │   │            │   │   │
  │   │   ⚫――――⚫――――⚫   │   │
  │   ⚫――――――――⚫――――――――⚫   │
3 ⚫――――――――――――⚫――――――――――――⚫
 */
case class Game(
    board: Board,
    players: Array[Player],
    state: GameState = GameState.Setting
) {
  override def equals(game: Any): Boolean = game match {
    case g: Game =>
      g.board.equals(board) && g.players.equals(players) && g.state.equals(
        state
      )
    case _ => false
  }
  def isValidSet(field: Field): Boolean =
    field.x < board.size && field.x >= 0 && field.y < board.size
      && field.y >= 0 && field.ring < board.size && field.ring >= 0
      && board.fields
        .find(f => f.equals(field))
        .map(f => f.color == field.unsetFieldColor)
        .getOrElse(false)

  def isValidMove(from: Field, to: Field): Boolean = isValidSet(to) &&
    (Math.abs(from.x - to.x) == 1 ^ Math.abs(from.y - to.y) == 1
      ^ Math.abs(from.ring - to.ring) == 1)

  def isMill(to: Field, boardAfterTurn: Board): Boolean = {
    val possibleMillOnRow = boardAfterTurn.fields
      .count(field =>
        field.y == to.y && field.ring == to.ring && field.color == to.color
      ) == boardAfterTurn.size
    val possibleMillOnColumn = boardAfterTurn.fields
      .count(field =>
        field.x == to.x && field.ring == to.ring && field.color == to.color
      ) == boardAfterTurn.size
    val isMiddlePoint =
      to.x == Math.floor(boardAfterTurn.size / 2) || to.y == Math.floor(
        boardAfterTurn.size / 2
      )
    if (isMiddlePoint) {
      val possibleMillOnRing = boardAfterTurn.fields
        .count(field =>
          field.y == to.y && field.x == to.x && field.color == to.color
        ) == boardAfterTurn.size
      return possibleMillOnRow || possibleMillOnColumn || possibleMillOnRing
    }
    possibleMillOnColumn || possibleMillOnRow
  }
  def everyPlayerHasSetItsStones(players: Array[Player]): Boolean = players
    .count(player =>
      player.setStones == Math.pow(board.size, 2)
    ) == players.length
  def isSetting = state == GameState.Setting
  def isMoving = state == GameState.Moving
  def isFlying = state == GameState.Flying
  def isRemoving = state == GameState.Removing
  def isWon = state == GameState.Won
  def setPiece(player: Player, field: Field): Try[Game] = {
    if (!isSetting)
      return Failure(
        IllegalArgumentException(
          "The piece was not set. All pieces are already set."
        )
      )

    if (!isValidSet(field))
      return Failure(
        IllegalArgumentException(
          "The piece was not set. Please use a valid field that is not already in use."
        )
      )
    val newField = new Field(field, player.color)
    val playedTurnBoard = Board(
      board.fields.updated(
        board.fields.indexOf(field),
        newField
      ),
      board.size
    )
    val playedTurnPlayers = players.updated(
      players.indexOf(player),
      Player(player.name, player.color, player.setStones + 1)
    )
    val playedTurnState =
      if (isMill(newField, playedTurnBoard)) GameState.Removing
      else if (everyPlayerHasSetItsStones(playedTurnPlayers)) GameState.Moving
      else state
    Success(Game(playedTurnBoard, playedTurnPlayers, playedTurnState))
  }

  def movePiece(player: Player, from: Field, to: Field): Try[Game] = {
    if (!isMoving && !isFlying)
      return Failure(
        IllegalArgumentException(
          "The piece was not moved. Please provide a valid input for moving or flying a piece."
        )
      )

    if (from.color != player.color)
      return Failure(
        IllegalArgumentException(
          "The piece was not moved. You can only move your own pieces."
        )
      )

    if (isMoving) {
      if (!isValidMove(from, to))
        return Failure(
          IllegalArgumentException(
            "The piece was not moved. Please use a valid field that is not already in use."
          )
        )
    } else {
      if (!isValidSet(to))
        return Failure(
          IllegalArgumentException(
            "The piece was not moved. Please use a valid field that is not already in use."
          )
        )
    }
    val newField = new Field(to, player.color)

    val playedTurnBoard = Board(
      board.fields
        .updated(
          board.fields.indexOf(from),
          new Field(from, newField.unsetFieldColor)
        )
        .updated(
          board.fields.indexOf(to),
          newField
        ),
      board.size
    )
    val playedTurnState =
      if (isMill(newField, playedTurnBoard)) GameState.Removing
      else state
    Success(Game(playedTurnBoard, players, playedTurnState))
  }

  def removePiece(player: Player, field: Field): Try[Game] = {
    if (!isRemoving)
      return Failure(
        IllegalArgumentException(
          "The piece was not removed. Please provide a valid input for removing a piece."
        )
      )

    if (field.color == player.color)
      return Failure(
        IllegalArgumentException(
          "The piece was not removed. You cannot remove your own pieces."
        )
      )

    if (field.color == field.unsetFieldColor)
      return Failure(
        IllegalArgumentException(
          "The piece was not removed. You cannot remove unset fields."
        )
      )

    if (isMill(field, board))
      return Failure(
        IllegalArgumentException(
          "The piece was not removed. You cannot remove pieces on a mill."
        )
      )

    val playedTurnBoard = Board(
      board.fields
        .updated(
          board.fields.indexOf(field),
          new Field(field, field.unsetFieldColor)
        ),
      board.size
    )
    var playedTurnState = GameState.Setting
    val otherPlayersPieces = playedTurnBoard.fields.count(field =>
      field.color == players.find(p => !p.equals(player)).get.color
    )
    if (everyPlayerHasSetItsStones(players)) {
      playedTurnState = GameState.Moving
      if (otherPlayersPieces < board.size) {
        playedTurnState = GameState.Won
      } else if (otherPlayersPieces == board.size) {
        playedTurnState = GameState.Flying
      }
    }
    Success(Game(playedTurnBoard, players, playedTurnState))
  }
}
