import scalafx.scene.media.AudioClip
import scalafx.scene.paint.Color
import scalafx.scene.shape.{Rectangle,Ellipse,Shape}
import scalafx.scene.text.{Font, Text}
import java.nio.file.Paths
import scala.collection.mutable

trait Attacks :
  val ellipse : Shape
  def update() : Unit

class Boss(val initialX : Double, val initialY: Double) extends Hit :
  val health : Int = 50
  val damage : Int = 1

  val rectangle = new Rectangle():
    width = 25
    height = 55
    fill = Color.Yellow
    x = initialX
    y = initialY

  var lastAttack: Long = 0L
  val attackCooldown: Long = 3000
  var direction : Int = 2 // +ve = right, -ve = left
  var attackPerformed : Boolean = false // to track the activation of attacks

  // a mutable set to hold each attack
  var bossAttacks : mutable.Buffer[Attacks] = mutable.Buffer()

  def attack1(): Unit = // reusing auto attack for one of the attacks
    if !attackPerformed then
        val attackDirection = if direction > 0 then 1 else -1
        val newAttack = new AutoAttack(rectangle.x() + rectangle.width() / 2, rectangle.y() + rectangle.height() / 2, attackDirection)
        bossAttacks += newAttack
        attackPerformed = true

  def attBeast() : Unit =
    if !attackPerformed then
      val attackDirection = if direction > 0 then 1 else -1
      val newAttack = new Beast(rectangle.x() + rectangle.width() / 2, rectangle.y() + rectangle.height() / 2, attackDirection)
      bossAttacks += newAttack
      attackPerformed = true


  // to update the attacks in the loop
  def updateAtt() : Unit =
    bossAttacks.foreach(_.update())
    bossAttacks = bossAttacks.filter :
       case attack: AutoAttack =>
         attack.shape.asInstanceOf[Rectangle].x >= 0 && attack.shape.asInstanceOf[Rectangle].x <= 800


  def resetAttack(): Unit =
    attackPerformed = false // resets to allow other attacks to occur