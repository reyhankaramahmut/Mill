package de.htwg.se.mill.model

trait GameInterface {
  val board: BoardInterface
  val players: Array[PlayerInterface]
  val currentPlayer: PlayerInterface
  val setStones: Int
  override def equals(game: Any): Boolean
  def isValidSet(field: FieldInterface): Boolean
  def isValidMove(from: FieldInterface, to: FieldInterface): Boolean
  def isMill(to: FieldInterface): Boolean
  def everyPlayerHasSetItsStones: Boolean
  def copyStones(setStones: Int): GameInterface
  def copyBoard(board: BoardInterface): GameInterface
  def copyCurrentPlayer(currentPlayer: PlayerInterface): GameInterface
}
