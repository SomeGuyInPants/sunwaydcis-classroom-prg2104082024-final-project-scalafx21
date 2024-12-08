import scalafx.application.JFXApp3
import scalafx.application.JFXApp3.PrimaryStage
import scalafx.scene.Scene
import scalafx.scene.input.KeyCode
import scalafx.scene.paint.Color
import scalafx.scene.shape.Rectangle
import scalafx.Includes.jfxKeyEvent2sfx
import scalafx.animation.AnimationTimer
import scalafx.Includes.jfxScene2sfx
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


// initialize player
class Player(val initialX : Double, val initialY: Double) extends Hit:

  var Health : Double = 10.0
  //temporary/perma? show player character
  val rectangle = new Rectangle():
    width = 25
    height = 55
    fill = Color.Blue
    x = initialX
    y = initialY

  //temporary, show attack
  val showAttack = new Rectangle():
    width = 60
    height = 15
    fill = Color.Black
    x = x() + rectangle.width()
    y = y() + rectangle.height() / 2
    visible = false

  // display health
  var showHealth = new Rectangle():
    width = (Health/10) * 100
    height = 30
    fill = Color.Green
    x = 50
    y = 50
  val healthText = new Text():
    text = "Health"
    x = 55
    y = 35
    font = new Font(24)

  val hit = new Text():
    text = "Hit!"
    font = new Font(24)
    x = 0
    y = 0

  var faceLeft : Boolean = false
  def moveLeft () : Unit =
    rectangle.x = rectangle.x() - 5
    faceLeft = true
    if showAttack.visible.value then
      showAttack.x = rectangle.x() + rectangle.width()
      showAttack.y = rectangle.y() + rectangle.height() / 2


  def moveRight () : Unit =
    rectangle.x = rectangle.x() + 5
    faceLeft = false
    if showAttack.visible.value then
      showAttack.x = rectangle.x() + rectangle.width()
      showAttack.y = rectangle.y() + rectangle.height() / 2

  var jumpHeight : Double = 0
  var jumping : Boolean = false

  def jump () : Unit =
    if !jumping then
      jumpHeight = -12
      jumping = true

  def jumpUpdate() : Unit =
    rectangle.y = rectangle.y() + jumpHeight
    jumpHeight += 0.5
    if rectangle.y() >= initialY then
      rectangle.y = initialY
      jumpHeight = 0
      jumping = false

  var attackOn: Boolean = false
  var attackTime: Long = 0L

  def attack(): Unit =
    if !attackOn then
      attackOn = true
      attackTime = System.currentTimeMillis()
      if faceLeft then
        showAttack.x = rectangle.x() - showAttack.width()
      else
        showAttack.x = rectangle.x() + rectangle.width()

      showAttack.y = rectangle.y() + rectangle.height() / 2
      showAttack.visible = true


  def attackUpdate(): Unit =
    if attackOn then
      if faceLeft then
        showAttack.x = rectangle.x() - showAttack.width()
      else
        showAttack.x = rectangle.x() + rectangle.width()
      showAttack.y = rectangle.y() + rectangle.height()/2

      if System.currentTimeMillis() - attackTime > 300 then
        showAttack.visible = false
        attackOn = false

  def healthBar () : Unit=
    showHealth.width = (Health/10) * 500
    // only for test range
    if Health == 0 then
      Health = 10

  //TESTING PURPOSES
  lazy private val skidSound = new AudioClip(
    Paths.get("src/main/resources/audio/Metal pipe falling sound effect but its more violent.mp3").toUri.toString)

  def checkHitCollision (dummy : Dummy , hitDelay: Long) : Unit =
    // to test if the damage collision works
    if showAttack.visible.value && hitCollision(dummy, showAttack) then
      if hitDelay - hitCooldown > 300 then
        println("Hit") //to show that its really connecting
        hitCooldown = hitDelay

        hit.x = dummy.rectangle.x() + dummy.rectangle.width()/2
        hit.y = dummy.rectangle.y()
        hit.visible = true

    if hit.visible.value && hitDelay - hitCooldown > 200 then
      hit.visible = false

    if walkCollision(dummy) then
      if hitDelay - hitCooldown > 500 then
        println("Dummy hit player") //to show that its really ocnnecting
        Health  -= dummy.Damage
        skidSound.play()
        hitCooldown = hitDelay


class AutoAttack(var xPos: Double, var yPos: Double, val direction: Int):
  val shape = new Rectangle:
    width = 55
    height = 25
    fill = Color.Black
    x = xPos
    y = yPos
  val speed: Double = 5.0

  def update(): Unit =
    xPos += direction * speed
    shape.x = xPos


// initialize TestDummy
class Dummy(val initialX : Double, val initialY: Double) extends Hit :
  val Damage : Int = 1
  val Health : Int = 999

  val rectangle = new Rectangle():
    width = 25
    height = 55
    fill = Color.Red
    x = initialX
    y = initialY

  var direction : Int = 2 // +ve = right, -ve = left
  var range : Int = 50
  var originalPos = initialX

  def movement() : Unit =
    rectangle.x = rectangle.x() + direction
    if rectangle.x() > originalPos + range then
      direction = -2
    else if rectangle.x() < originalPos - range then
      direction = 2

  var attackPellets : mutable.Buffer[AutoAttack] = mutable.Buffer()
  var lastAttack : Long = 0L
  var attSpeed : Double = 5.0
  var attackRange : Int = 300
  val attackInterval: Long = 1000


  def autoAttack(): Unit =
    val currentTime = System.currentTimeMillis()
    if currentTime - lastAttack > attackInterval then
      val attackDirection = if direction > 0 then 1 else -1
      val newAttack = new AutoAttack(rectangle.x() + rectangle.width() / 2, rectangle.y() + rectangle.height() / 2, attackDirection)
      attackPellets += newAttack
      lastAttack = currentTime

  def updateAA () : Unit =
    attackPellets.foreach(_.update())
    attackPellets = attackPellets.filter(attack => attack.xPos >= 0 && attack.xPos<=800)

object SimpleGame extends JFXApp3:
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
      player.checkHitCollision(dummy, hitDelay)

      dummy.autoAttack()
      dummy.updateAA()

      player.healthBar()
      dummy.movement()

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

