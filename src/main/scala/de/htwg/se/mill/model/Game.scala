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
    currentPlayer: Player,
    setStones: Int = 0
) {
  override def equals(game: Any): Boolean = game match {
    case g: Game =>
      g.board.equals(board) && g.players.sameElements(players)
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

  def isMill(to: Field): Boolean = {
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
}
