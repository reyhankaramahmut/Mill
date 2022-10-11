package de.htwg.se.mill.model

case class Player(name: String, color: String) {
  override def toString: String = s"$name $color"
}
