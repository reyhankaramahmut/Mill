package de.htwg.se.mill.aview

import de.htwg.se.mill.util.Observer
import de.htwg.se.mill.controller.Controller
import de.htwg.se.mill.util.Event
import scalafx.application.JFXApp3
import scalafx.scene.Scene
import javafx.scene.paint.Color
import scalafx.Includes.*
import scalafx.scene.layout.StackPane
import de.htwg.se.mill.aview.gui.Ring
import scalafx.application.Platform
import scalafx.scene.layout.Background
import scalafx.scene.layout.BackgroundImage
import scalafx.scene.image.Image
import scalafx.scene.layout.BackgroundRepeat
import scalafx.scene.layout.BackgroundPosition
import scalafx.geometry.Side
import scalafx.scene.layout.BackgroundSize
import de.htwg.se.mill.model.Field
import de.htwg.se.mill.aview.gui.Board
import de.htwg.se.mill.aview.gui.MessageBox
import scalafx.scene.control.Alert
import scalafx.scene.control.Alert.AlertType
import scalafx.scene.text.Font

class GUI(val controller: Controller) extends JFXApp3 with Observer {
  controller.add(this)

  def onAction: (field: Field) => Unit = (field: Field) => {
    if (controller.isSetting) {
      controller.setPiece(field)
    } else if (controller.isRemoving) {
      controller.removePiece(field)
    } else {
      if (controller.fromField.isDefined) {
        controller.movePiece(controller.fromField.get, field)
        controller.fromField = None
      } else {
        controller.fromField = Some(field)
      }
    }
  }
  override def update(message: Option[String], e: Event): Unit = {
    if (message.isDefined) {
      Platform.runLater {
        new Alert(AlertType.Warning) {
          initOwner(stage)
          headerText = message.get
        }.showAndWait()
      }
    }
    e match
      case Event.QUIT => Platform.exit()
      case Event.PLAY => start()
  }

  override def start(): Unit = {
    stage = new JFXApp3.PrimaryStage {
      title = "Mill"
      scene = new Scene(800, 600) {
        resizable = false
        root = Board(controller, onAction)
      }
    }
  }
}
