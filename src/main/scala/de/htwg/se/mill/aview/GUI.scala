package de.htwg.se.mill.aview

import de.htwg.se.mill.util.Observer
import de.htwg.se.mill.controller.Controller
import de.htwg.se.mill.util.Event
import scalafx.application.JFXApp3
import scalafx.scene.Scene
import javafx.scene.paint.Color
import scalafx.scene.shape.Rectangle
import scalafx.Includes.*

class GUI(val controller: Controller) extends JFXApp3 with Observer {
  controller.add(this)
  override def update(message: Option[String], e: Event): Unit = e match
    case Event.QUIT => stopApp()
    case Event.PLAY => this.start()
  override def start(): Unit = {
    stage = new JFXApp3.PrimaryStage {
      title = "Mill"
      scene = new Scene(600, 450) {
        fill = Color.LIGHTGREEN
        content = new Rectangle {
          x = 25
          y = 40
          width = 100
          height = 100
          fill <== when(hover) choose Color.GREEN otherwise Color.RED
        }
      }
    }
  }
}
