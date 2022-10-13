package de.htwg.se.mill.model

case class Field(x: Int, y: Int, ring: Int, color: String = "⚫") {
  def this(field: Field, color: String) =
    this(field.x, field.y, field.ring, color)

  override def toString: String = color
  override def equals(field: Any): Boolean = field match {
    case f: Field =>
      f.x.equals(x) && f.y.equals(y) && f.ring.equals(ring)
    case _ => false
  }
  def unsetFieldColor = "⚫"
}
