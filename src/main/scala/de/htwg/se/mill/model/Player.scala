package de.htwg.se.mill.model

case class Player(name: String, colorToken: String) {
  override def toString: String = s"$name $colorToken"
}
