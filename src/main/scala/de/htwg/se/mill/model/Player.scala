package de.htwg.se.mill.model

case class Player(name: String, color: String) {
  override def toString: String = s"$name $color"
  override def equals(player: Any): Boolean = player match {
    case p: Player =>
      p.color.equals(color) && p.name.equals(name)
    case _ => false
  }
}
