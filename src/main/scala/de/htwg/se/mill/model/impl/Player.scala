package de.htwg.se.mill.model

import de.htwg.se.mill.model.PlayerInterface

case class Player(val name: String, val color: String) extends PlayerInterface {
  override def toString: String = s"$name $color"
  override def equals(player: Any): Boolean = player match {
    case p: PlayerInterface =>
      p.color.equals(color) && p.name.equals(name)
    case _ => false
  }
}
