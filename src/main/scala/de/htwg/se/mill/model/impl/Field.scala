package de.htwg.se.mill.model

case class Field(val x: Int, val y: Int, val ring: Int, val color: String = "⚫")
    extends FieldInterface {
  override def toString: String = color
  override def equals(field: Any): Boolean = field match {
    case f: FieldInterface =>
      f.x.equals(x) && f.y.equals(y) && f.ring.equals(ring)
    case _ => false
  }
  def copyColor(color: String): FieldInterface = copy(color = color)
}
