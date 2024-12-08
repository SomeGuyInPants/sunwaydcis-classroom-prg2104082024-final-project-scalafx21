import scalafx.application.JFXApp3
import scalafx.application.JFXApp3.PrimaryStage
import scalafx.scene.Scene
import scalafx.scene.input.KeyCode
import scalafx.scene.shape.Rectangle
import scalafx.Includes.jfxKeyEvent2sfx
import scalafx.animation.AnimationTimer
import scalafx.scene.SceneIncludes.jfxScene2sfx
import scala.collection.mutable

object mainFight extends JFXApp3:
  override def start() : Unit =
    val keyInput: mutable.Set[KeyCode] = mutable.Set()
    val player = new Player(100,455)
    

    stage = new JFXApp3.PrimaryStage:
        title = "Main Game"
        scene = new Scene(800, 800):
          content = Seq (player.rectangle,player.showAttack,player.showHealth,player.healthText,player.hit)
          
          onKeyPressed = (event) =>
            keyInput += event.code
          onKeyReleased = (event) =>
            keyInput -= event.code
    
    
    
    val timer = AnimationTimer { _ => //remember to include reference
      if keyInput.contains(KeyCode.Left) then player.moveLeft()
      if keyInput.contains(KeyCode.Right) then player.moveRight()
      if keyInput.contains(KeyCode.Space) then player.jump()
      if keyInput.contains(KeyCode.Z) then player.attack()
      player.jumpUpdate()
      player.attackUpdate()

      val hitDelay = System.currentTimeMillis()




      player.healthBar()
      //dummy.movement()

      stage.scene().content = Seq(player.rectangle, player.showAttack, player.showHealth, player.healthText, player.hit)

    }
    timer.start()

