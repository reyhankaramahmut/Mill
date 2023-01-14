package de.htwg.se.mill.model

import scala.util.{Try, Success, Failure}
import de.htwg.se.mill.model.PlayerInterface
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
    val board: BoardInterface,
    val players: Array[PlayerInterface],
    val currentPlayer: PlayerInterface,
    val setStones: Int = 0
) extends GameInterface {
  override def equals(game: Any): Boolean = game match {
    case g: GameInterface =>
      g.board.equals(board) && g.players.sameElements(players)
    case _ => false
  }
  def isValidSet(field: FieldInterface): Boolean =
    field.x < board.size && field.x >= 0 && field.y < board.size
      && field.y >= 0 && field.ring < board.size && field.ring >= 0
      && board.fields
        .find(f => f.equals(field))
        .map(f => f.color == field.unsetFieldColor)
        .getOrElse(false)

  def isValidMove(from: FieldInterface, to: FieldInterface): Boolean =
    isValidSet(to) &&
      (Math.abs(from.x - to.x) == 1 ^ Math.abs(from.y - to.y) == 1
        ^ Math.abs(from.ring - to.ring) == 1)

  def isMill(to: FieldInterface): Boolean = {
    val possibleMillOnRow = board.fields
      .count(field =>
        field.y == to.y && field.ring == to.ring && field.color == currentPlayer.color
      ) == board.size
    val possibleMillOnColumn = board.fields
      .count(field =>
        field.x == to.x && field.ring == to.ring && field.color == currentPlayer.color
      ) == board.size
    val isMiddlePoint =
      to.x == Math.floor(board.size / 2) || to.y == Math.floor(
        board.size / 2
      )
    if (isMiddlePoint) {
      val possibleMillOnRing = board.fields
        .count(field =>
          field.y == to.y && field.x == to.x && field.color == currentPlayer.color
        ) == board.size
      return possibleMillOnRow || possibleMillOnColumn || possibleMillOnRing
    }
    possibleMillOnColumn || possibleMillOnRow
  }
  def everyPlayerHasSetItsStones =
    setStones == Math.pow(board.size, 2).toInt * players.length
  def copyStones(setStones: Int): GameInterface = copy(setStones = setStones)
  def copyBoard(board: BoardInterface): GameInterface = copy(board = board)
  def copyCurrentPlayer(currentPlayer: PlayerInterface): GameInterface =
    copy(currentPlayer = currentPlayer)
}
