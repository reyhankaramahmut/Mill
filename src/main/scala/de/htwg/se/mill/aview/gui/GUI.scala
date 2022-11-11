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
  override def update(message: Option[String], e: Event): Unit = e match
    case Event.QUIT => Platform.exit()
    case Event.PLAY => start()

  override def start(): Unit = {
    stage = new JFXApp3.PrimaryStage {
      title = "Mill"
      scene = new Scene(800, 600) {
        resizable = false
        root = {
          new StackPane {
            background = new Background(
              Array(
                new BackgroundImage(
                  new Image("bg.jpg"),
                  BackgroundRepeat.NoRepeat,
                  BackgroundRepeat.NoRepeat,
                  new BackgroundPosition(
                    Side.Left,
                    0,
                    true,
                    Side.Bottom,
                    0,
                    true
                  ),
                  new BackgroundSize(
                    BackgroundSize.Auto,
                    BackgroundSize.Auto,
                    true,
                    true,
                    false,
                    true
                  )
                )
              )
            )
            children =
              (0 until controller.gameState.get.game.board.size).map(n =>
                new Ring(
                  controller.gameState.get.game.board.fields.filter(f =>
                    f.ring == n
                  ),
                  controller.gameState.get.game.board.size,
                  onAction,
                  n
                )
              )
          }
        }
      }
    }
  }
}
