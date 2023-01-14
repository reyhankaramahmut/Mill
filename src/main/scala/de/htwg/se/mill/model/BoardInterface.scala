package de.htwg.se.mill.model

trait BoardInterface {
  val fields: List[FieldInterface]
  val size: Int
  def fieldsDump: String
  def getField(x: Int, y: Int, ring: Int): Option[FieldInterface]
}
