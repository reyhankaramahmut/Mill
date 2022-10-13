package de.htwg.se.mill.model

enum GameState(value: String) {
  def representation = value
  case Setting extends GameState("Setting pieces")
  case Moving extends GameState("Moving pieces")
  case Removing extends GameState("Removing piece")
  case Flying extends GameState("Flying pieces")
  case Won extends GameState("Won")
}
