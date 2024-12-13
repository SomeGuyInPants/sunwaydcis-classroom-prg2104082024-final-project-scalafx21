import scalafx.scene.media.AudioClip
import scalafx.scene.paint.Color
import scalafx.scene.shape.{Rectangle,Ellipse,Shape}
import scalafx.scene.text.{Font, Text}
import java.nio.file.Paths
import scala.collection.mutable


class Boss(val initialX : Double, val initialY: Double) extends Hit :
  var health : Double = 50
  val damage : Double = 1


  val rectangle = new Rectangle():
    width = 25
    height = 55
    fill = Color.Yellow
    x = initialX
    y = initialY

  var lastAttack: Long = 0L
  // to track the activation of attacks
  var demonFangPerformed : Boolean = false 
  var beastPerformed : Boolean = false
  var dragonSwarmPerformed : Boolean = false

  val stageMidPoint: Double = 400 // Half of the stage size

  def checkDirection : Int =
    if rectangle.x() + rectangle.width() / 2 > stageMidPoint then
      -1
    else
      1
  // a mutable set to hold each attack
  var bossAttacks: mutable.Buffer[Any] = mutable.Buffer()


  //ranged attack
  def demonFang(): Unit = // reusing auto attack of the dummy
    if !demonFangPerformed then
        val attackDirection = checkDirection
        val newAttack = new AutoAttack(rectangle.x() + rectangle.width() / 2, rectangle.y() + rectangle.height() / 2, checkDirection)
        bossAttacks += newAttack
        demonFangPerformed = true


  var prevBeast : Long = 0L
  val beastCooldown: Long = 20000L
  def Beast() : Unit =
    val cooldownTimer = System.currentTimeMillis()
    if !beastPerformed && cooldownTimer - prevBeast > beastCooldown then
      val attackDirection = checkDirection
      val newAttack = new BeastAttack(rectangle.x() + rectangle.width() / 2, rectangle.y() + rectangle.height() / 2, checkDirection)
      bossAttacks += newAttack
      beastPerformed = true
      prevBeast = cooldownTimer


  var stickTime: Long = 0L
  val proximityThreshold : Double = 300.0
  val proximityDuration : Long = 6000

  // method to check when Beast should activate
  def checkDistance(player:Player) : Unit =
    val distanceBetween = math.abs(player.rectangle.x() - rectangle.x())
    if distanceBetween < proximityThreshold then
      if stickTime == 0L then
        stickTime = System.currentTimeMillis()
      else if System.currentTimeMillis() - stickTime > proximityDuration then
        Beast()
        beastPerformed = false
    else
      stickTime = 0L
  // to update the attacks in the loop
  
  def DragonSwarm() : Unit =
    if !dragonSwarmPerformed then 
      val attackDirection = checkDirection
      val newAttack = new AutoAttack(rectangle.x() + rectangle.width() / 2, rectangle.y() + rectangle.height() / 2, checkDirection)
      bossAttacks += newAttack
      dragonSwarmPerformed = true
  
  def updateAtt() : Unit =
    bossAttacks.foreach:
      case attack : AutoAttack => attack.update()
      case beast : BeastAttack  => beast.update()
    bossAttacks = bossAttacks.filter:
      case attack : AutoAttack => attack.xPos >= 0 && attack.xPos <= 800
      case beast : BeastAttack => beast.shape.visible.value

  def resetAttack(): Unit =
    demonFangPerformed = false
    beastPerformed = false // resets to allow other attacks to occur