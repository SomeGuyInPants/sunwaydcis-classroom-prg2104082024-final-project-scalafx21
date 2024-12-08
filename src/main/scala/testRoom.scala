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


trait Hit :
  val rectangle : Rectangle
  var hitCooldown : Long = 0L

  def hitCollision(other : Hit, attacking : Rectangle): Boolean =
    val attack =  attacking.boundsInParent()
    val receiver = other.rectangle.boundsInParent()
    attack.intersects(receiver)
  def walkCollision (other: Hit) : Boolean =
    val player = rectangle.boundsInParent()
    val enemy = other.rectangle.boundsInParent()
    player.intersects(enemy)
  def dummyAACollision (other : Hit, attacking : AutoAttack) : Boolean =
    val attack = attacking.shape.boundsInParent()
    val receiver = other.rectangle.boundsInParent()
    attack.intersects(receiver)


object testRoom extends JFXApp3:
  override def start(): Unit =
    val keyInput: mutable.Set[KeyCode] = mutable.Set()
    val player = new Player(100,455)
    val dummy = new Dummy(500,455)

    stage = new JFXApp3.PrimaryStage:
      title = "Simple Game"
      scene = new Scene(800, 800):
        content = Seq (player.rectangle,player.showAttack,player.showHealth,player.healthText,player.hit,dummy.rectangle) ++  dummy.attackPellets.map(_.shape)

        // remember to include reference
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
      player.checkHitCollision(dummy, hitDelay, dummy.attackPellets)

      dummy.autoAttack()
      dummy.updateAA()

      player.healthBar()
      //dummy.movement()

      stage.scene().content = Seq(player.rectangle, player.showAttack, player.showHealth, player.healthText, player.hit, dummy.rectangle) ++ dummy.attackPellets.map(_.shape)
      /*
      // to test if the damage collision works
      if player.showAttack.visible.value && player.hitCollision(dummy, player.showAttack) then
        if hitDelay - player.hitCooldown > 300 then
          println("Hit")
          player.hitCooldown = hitDelay
      if player.walkCollision(dummy) then
        if hitDelay - player.hitCooldown > 500 then
          println("Dummy hit player")
          player.hitCooldown = hitDelay
       */
    }
    timer.start()
