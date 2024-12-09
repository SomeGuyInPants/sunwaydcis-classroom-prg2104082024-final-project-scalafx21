import scalafx.scene.paint.Color
import scalafx.scene.shape.Rectangle

import scala.collection.mutable

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

  // a mutable set to hold each attack
  var attackPellets : mutable.Buffer[AutoAttack] = mutable.Buffer()

  var lastAttack : Long = 0L
  var attSpeed : Double = 5.0
  var attackRange : Int = 300
  val attackInterval: Long = 1200

  // val currentTime = System.currentTimeMillis() just leave it like this if you want it to happen once, if it works, it works
  def autoAttack(): Unit =
    val currentTime = System.currentTimeMillis()
    if currentTime - lastAttack > attackInterval then
      val attackDirection = if direction > 0 then 1 else -1
      val newAttack = new AutoAttack(rectangle.x() + rectangle.width() / 2, rectangle.y() + rectangle.height() / 2, attackDirection)
      attackPellets += newAttack
      lastAttack = currentTime

  // to update the attacks in the loop
  def updateAA () : Unit =
    attackPellets.foreach(_.update())
    attackPellets = attackPellets.filter(attack => attack.xPos >= 0 && attack.xPos<=800)
