package de.htwg.se.mill

import com.google.inject.AbstractModule
import de.htwg.se.mill.controller.ControllerInterface
import de.htwg.se.mill.controller.Controller
import de.htwg.se.mill.model.GameInterface
import de.htwg.se.mill.model.Game
import de.htwg.se.mill.model.Player
import de.htwg.se.mill.model.PlayerInterface
import de.htwg.se.mill.model.BoardInterface
import de.htwg.se.mill.model.FieldInterface
import de.htwg.se.mill.model.Field
import de.htwg.se.mill.model.Board

class MillModule extends AbstractModule {
  override def configure(): Unit = {
    bind(classOf[ControllerInterface]).to(classOf[Controller])
    bind(classOf[BoardInterface]).toInstance(Board.withSize(3).get)
  }
}
