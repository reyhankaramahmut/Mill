package de.htwg.se.muehle.model

case class Player(name: String, colorToken: String) {
  override def toString: String = s"$name $colorToken"
}
