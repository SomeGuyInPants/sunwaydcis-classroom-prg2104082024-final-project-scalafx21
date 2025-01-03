package ch.makery.address

import ch.makery.address.testRoom
import scalafx.Includes.jfxKeyEvent2sfx
import scalafx.animation.AnimationTimer
import scalafx.application.JFXApp3
import scalafx.application.JFXApp3.PrimaryStage
import scalafx.scene.Scene
import scalafx.scene.SceneIncludes.jfxScene2sfx
import scalafx.scene.input.KeyCode
import scalafx.scene.shape.{Rectangle, Ellipse, Shape}
import scalafx.scene.paint.Color
import scalafx.stage
import scala.collection.mutable


trait Hit[T<:Shape]:
  val rectangle: Rectangle
  var hitCooldown: Long = 0L
  
  // Method to calculcate the size of the shapes
  def checkIntersection(shape: Shape): (Double, Double, Double, Double) =
    shape match
      case rect: Rectangle =>
        (rect.x.value, rect.y.value, rect.width.value, rect.height.value)
      case ellipse: Ellipse =>
        (ellipse.centerX.value - ellipse.radiusX.value,
          ellipse.centerY.value - ellipse.radiusY.value,
          2 * ellipse.radiusX.value,
          2 * ellipse.radiusY.value)
      case _ =>
        throw new IllegalArgumentException("Unsupported shape type")
  
  // Grabs two shapes and checks for intersection
  def intersects(bounds1: (Double, Double, Double, Double), bounds2: (Double, Double, Double, Double)): Boolean =
    val (x1, y1, width1, height1) = bounds1
    val (x2, y2, width2, height2) = bounds2
    x1 < x2 + width2 && x1 + width1 > x2 && y1 < y2 + height2 && y1 + height1 > y2
  
  // Each takes the shape of their respective things and uses intereects() to check for intersection
  def hitCollision(other: Hit[_ <: Shape], attacking: Rectangle): Boolean =
    val attack = checkIntersection(attacking)
    val receiver = checkIntersection(other.rectangle)
    intersects(attack, receiver)

  def walkCollision(other: Hit[_ <: Shape]): Boolean =
    val player = checkIntersection(rectangle)
    val enemy = checkIntersection(other.rectangle)
    intersects(player, enemy)

  def dummyAACollision(other: Hit[_ <: Shape], attacking: AutoAttack): Boolean =
    val attack = checkIntersection(attacking.shape)
    val receiver = checkIntersection(other.rectangle)
    intersects(attack, receiver)

  def bossBeastCollision(other: Hit[_ <: Shape], attacking: BeastAttack): Boolean =
    val attack = checkIntersection(attacking.shape)
    val receiver = checkIntersection(other.rectangle)
    println(s"Beast attack bounds: $attack") // debug
    println(s"Player bounds: $receiver") // debug
    intersects(attack, receiver)

  def bossDSCollision(other: Hit[_ <: Shape], attacking: DragonSwarmAttack): Boolean =
    val attack = checkIntersection(attacking.shape)
    val receiver = checkIntersection(other.rectangle)
    println(s"Dragon Swarm bounds: $attack") // debug
    println(s"Player bounds: $receiver") // debug
    intersects(attack, receiver)

  def bossHLCollision(other: Hit[_ <: Shape], attacking: HolyLanceAttack): Boolean =
    val receiver = checkIntersection(other.rectangle)
    attacking.spears.exists { spear =>
      val attack = checkIntersection(spear.shape)
      println(s"Holy Lance bounds: $attack") // debug
      println(s"Player bounds: $receiver") // debug
      intersects(attack, receiver)
    }

object testRoom:

  def startTestRoom(): Scene =
    val keyInput: mutable.Set[KeyCode] = mutable.Set() // Holds the key inputs by the player 
    val player = new Player(100,455)
    val dummy = new Dummy(500,455)

    val floor = new Rectangle():
      width = 800
      height = 10
      fill = Color.Brown
      x = 0
      y = 510
    new Scene(800, 800):
        content = Seq (floor,player.rectangle,player.showAttack,player.showHealth,player.healthText,player.hit,dummy.rectangle) ++  dummy.attackPellets.map(_.shape)

        // https://rockthejvm.com/articles/make-a-snake-game-with-scala-in-10-minutes
        onKeyPressed = (event) =>
          keyInput += event.code
        onKeyReleased = (event) =>
          keyInput -= event.code

        val timer : AnimationTimer = AnimationTimer { _ => //https://www.youtube.com/watch?v=JtuSLFrfaFs
          // https://rockthejvm.com/articles/make-a-snake-game-with-scala-in-10-minutes
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
          dummy.movement()
    
          content = Seq(floor,player.rectangle, player.showAttack, player.showHealth, player.healthText, player.hit, dummy.rectangle) ++ dummy.attackPellets.map(_.shape)
    
          // only for test range
          if player.Health == 0 then
            player.Health = 10
    
        } 
          timer.start()

/*
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
      dummy.movement()

      stage.scene().content = Seq(player.rectangle, player.showAttack, player.showHealth, player.healthText, player.hit, dummy.rectangle) ++ dummy.attackPellets.map(_.shape)

      // only for test range
      if player.Health == 0 then
        player.Health = 10

    }
    timer.start()

*/