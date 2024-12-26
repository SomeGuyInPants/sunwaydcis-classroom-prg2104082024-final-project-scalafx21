package ch.makery.address.view

import ch.makery.address.testRoom
import ch.makery.address.mainFight
import javafx.event.ActionEvent
import javafx.fxml.FXML
import scalafx.scene.control.Button
import scalafx.stage.Stage
import scalafx.Includes.*
import scalafx.scene.image.Image

import scala.util.{Failure, Success}

@FXML
class homepageController :

  @FXML private var startButton: Button = _
  @FXML private var exitButton: Button = _

  def mainGame(event:ActionEvent) : Unit =
    val gameStage = new Stage:
      title = "Main Game"
      icons += new Image(getClass.getResource("/images/sword.png").toExternalForm)
      scene = mainFight.createMainScene()
    gameStage.show()

  def trainingRoom(event: ActionEvent): Unit =
    // Open the main game window
    val gameStage = new Stage:
      title = "Training Room"
      icons += new Image(getClass.getResource("/images/sword.png").toExternalForm)
      scene = testRoom.startTestRoom()
    gameStage.show()
  
  def exitGame(event: ActionEvent): Unit =
    // Exit the application
    sys.exit(0)
