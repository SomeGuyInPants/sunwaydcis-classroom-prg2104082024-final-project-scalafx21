import scalafx.application.JFXApp3
import scalafx.application.JFXApp3.PrimaryStage
import scalafx.scene.Scene
import scalafx.scene.input.KeyCode
import scalafx.scene.paint.Color
import scalafx.scene.shape.Rectangle
import scalafx.Includes.jfxKeyEvent2sfx
import scalafx.animation.AnimationTimer
import scalafx.scene.SceneIncludes.jfxScene2sfx
import scala.collection.mutable
import scalafx.scene.text.{Font, Text}
import scalafx.scene.media.AudioClip
import java.nio.file.Paths


object mainFight extends JFXApp3:
  override def start() : Unit =
    stage = new JFXApp3.PrimaryStage:
        title = "Main Game"
        scene = new Scene(800, 800)

  