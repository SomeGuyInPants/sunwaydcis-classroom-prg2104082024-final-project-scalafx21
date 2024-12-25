package ch.makery.address

import javafx.fxml.FXMLLoader
import scalafx.application.JFXApp3
import scalafx.application.JFXApp3.PrimaryStage
import scalafx.scene.Scene
import scalafx.Includes.*
import scalafx.scene as sfxs
import javafx.scene as jfxs
import scalafx.stage.Stage
import scalafx.stage.Modality
import scalafx.scene.image.Image

object homepage extends JFXApp3:

  var roots: Option[scalafx.scene.layout.BorderPane] = None

  override def start(): Unit =

    // transform path of RootLayout.fxml to URI for resource location.
    val rootResource = getClass.getResource("/ch/makery/address/view/RootLayout.fxml")
    // initialize the loader object.
    val loader = new FXMLLoader(rootResource)
    // load root layout from fxml file.
    loader.load()
    // retrieve the root component BorderPane from the FXML
    // refer to slides on scala option monad
    roots = Option(loader.getRoot[jfxs.layout.BorderPane])


    stage = new PrimaryStage():
      title = "AddressApp"
      //icons += new Image(getClass.getResource("/images/funkybob.png").toExternalForm)
      scene = new Scene():
        root = roots.get
    showHomepage()
  def showHomepage(): Unit =
    val resource = getClass.getResource("view/homepage.fxml")
    val loader = new FXMLLoader(resource)
    loader.load()
    val roots = loader.getRoot[jfxs.layout.AnchorPane]
    this.roots.get.center = roots