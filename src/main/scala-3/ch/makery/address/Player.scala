package ch.makery.address

import scalafx.scene.media.AudioClip
import scalafx.scene.paint.Color
import scalafx.scene.shape.Rectangle
import scalafx.scene.text.{Font, Text}
import java.nio.file.Paths
import scala.collection.mutable


// initialize player
class Player(val initialX : Double, val initialY: Double) extends Hit:
  var Health : Double = 10.0
  var Damage : Double = 1

  //a simple player model
  val rectangle = new Rectangle():
    width = 25
    height = 55
    fill = Color.Blue
    x = initialX
    y = initialY

  // to show the attack
  val showAttack = new Rectangle():
    width = 45
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

  // Boolean to check which direction the player is facing whenever they move
  var faceLeft : Boolean = false
  def moveLeft () : Unit =
    rectangle.x = rectangle.x() - 5
    faceLeft = true

  def moveRight () : Unit =
    rectangle.x = rectangle.x() + 5
    faceLeft = false

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

  //checks the direction the player is facing and the timing of the attack appearing
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

  // Unused
  lazy private val skidSound = new AudioClip(
    Paths.get("src/main/resources/audio/Metal pipe falling sound effect but its more violent.mp3").toUri.toString)
  
  // for hitCollision for test Room
  def checkHitCollision (dummy : Dummy , hitDelay: Long, attackPellets: mutable.Buffer[AutoAttack]) : Unit =
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
        //skidSound.play()
        hitCooldown = hitDelay
    
    attackPellets.foreach { pellet =>
      if dummyAACollision(this,pellet) then
        if hitDelay - hitCooldown> 500 then
          println("Player hit by dummy AA")
          Health -= 1
          //skidSound.play()
          hitCooldown = hitDelay
    }
  
  // main fight scene
  def checkBossHit(boss:Boss, hitDelay: Long, bossAttacks: mutable.Buffer[Any]): Unit =

    if walkCollision(boss) then
      if hitDelay - hitCooldown > 500 then
        println("Player Hit by Boss") //to show that its really ocnnecting
        Health -= boss.damage
        //skidSound.play()
        hitCooldown = hitDelay

    boss.bossAttacks.foreach {
      case pellet: AutoAttack =>
        if dummyAACollision(this, pellet) then
          if hitDelay - hitCooldown > 500 then
            println("Player hit by Demon Fang")
            Health -= Damage
            //skidSound.play()
            hitCooldown = hitDelay
      case beast : BeastAttack =>
        if bossBeastCollision(this, beast) then
          if hitDelay - hitCooldown > 500 then
            println("Player hit by Beast")
            Health -= boss.damage
            //skidSound.play()
            hitCooldown = hitDelay
            val checkDirection = if boss.rectangle.x() < rectangle.x() then
              1 else
              -1
            rectangle.x = rectangle.x() + checkDirection * 550 // to calculate how far the player is pushed from the original position
      
      case dragonSwarm: DragonSwarmAttack =>
        if bossDSCollision(this,dragonSwarm) then
          println(s"Dragon Swarm bounds") // Debug log println(s"Player bounds: $receiver"
          if hitDelay - hitCooldown > 500 then
            println("Player hit by Dragon Swarm")
            Health -= boss.damage
            //skidSound.play()
            hitCooldown = hitDelay
    
            val checkDirection = if boss.rectangle.x() < rectangle.x() then
              1 else
              -1

      case holyLance : HolyLanceAttack =>
        if bossHLCollision(this,holyLance) then 
          if hitDelay - hitCooldown > 500 then
            println("Player hit by Holy Lance")
            Health -= boss.damage
            hitCooldown = hitDelay
          
      case _ => // do nothing
    }

