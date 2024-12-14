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

  def checkDirection(player: Player): Int =
    if player.rectangle.x() > rectangle.x() then
      1
    else
      -1

  // a mutable set to hold each attack
  var bossAttacks: mutable.Buffer[Any] = mutable.Buffer()

  //ranged attack
  def demonFang(player:Player): Unit = // reusing auto attack of the dummy
    if !demonFangPerformed then
        val attackDirection = checkDirection(player)
        val newAttack = new AutoAttack(rectangle.x() + rectangle.width() / 2, rectangle.y() + rectangle.height() / 2, attackDirection)
        bossAttacks += newAttack
        demonFangPerformed = true


  var prevBeast : Long = 0L
  val beastCooldown: Long = 20000L

  def Beast(player:Player) : Unit =
    val cooldownTimer = System.currentTimeMillis()
    if !beastPerformed && cooldownTimer - prevBeast > beastCooldown then
      val attackDirection = checkDirection(player)
      val newAttack = new BeastAttack(rectangle.x() + rectangle.width() / 2, rectangle.y() + rectangle.height() / 2, attackDirection)
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
        Beast(player)
        beastPerformed = false
    else
      stickTime = 0L

  // to update the attacks in the loop
  var prevDS : Long = 0L
  val dsInterval : Long = 600L
  val dsCooldown : Long = 500L
  var dragonSwarmHitCount : Int = 0 // new

  def dragonSwarm(player:Player) : Unit =
    val currentTime = System.currentTimeMillis()
    if !dragonSwarmPerformed && currentTime - prevDS > dsCooldown then
      if currentTime - prevDS > dsInterval then
        if dragonSwarmHitCount < 3 then
          val attackDirection = checkDirection(player)
          val newAttack = new DragonSwarmAttack(rectangle.x() + rectangle.width() / 2, rectangle.y() + rectangle.height() / 2, attackDirection)
          bossAttacks += newAttack
          prevDS = currentTime
          dragonSwarmHitCount += 1
        else
          dragonSwarmPerformed = true
          prevDS = currentTime

  // to control attack state
  var attacking : Boolean = false
  var returning : Boolean = false

   // create a dash feature for the boss before dragon swarm happens
  def dashToPlayer(player:Player) : Unit =
    val initialPos = rectangle.x()
    val mvmSpeed = 20
    val stop = 50 // the distance between boss and player
    val direction = if player.rectangle.x() > rectangle.x() then 1 else -1

    if !attacking then
      if math.abs(player.rectangle.x() - rectangle.x()) > stop then
        rectangle.x = rectangle.x() + direction * mvmSpeed

    // to check if its close enough to activate attack
    if math.abs(player.rectangle.x() - rectangle.x()) <= stop then
      attacking = true
      dragonSwarm(player)

    if attacking && !returning then
      returning = true

    if returning then
      if direction == 1 then
        rectangle.x = rectangle.x() + 700 * mvmSpeed 
      else
        rectangle.x = rectangle.x() - 700 * mvmSpeed
  end dashToPlayer





  /*
  // close ranged attack, boss will dash close to the player and do a 3-swing combo then dash towards the opposite end(?)
  def dragonSwarm() : Unit =
    if !dragonSwarmPerformed  then
      val attackDirection = checkDirection
      val newAttack = new DragonSwarmAttack(rectangle.x() + rectangle.width() / 2, rectangle.y() + rectangle.height() / 2, checkDirection)
      bossAttacks += newAttack
      dragonSwarmPerformed = true
*/

  def updateAtt() : Unit =
    bossAttacks.foreach:
      case attack : AutoAttack => attack.update()
      case beast : BeastAttack  => beast.update()
      case dragonSwarmAttack: DragonSwarmAttack => dragonSwarmAttack.update()
    bossAttacks = bossAttacks.filter:
      case attack : AutoAttack => attack.xPos >= 0 && attack.xPos <= 800
      case beast : BeastAttack => beast.shape.visible.value
      case dragonSwarmAttack: DragonSwarmAttack => dragonSwarmAttack.shape.visible.value

  def resetAttack(): Unit =
    println("Resetting attacks")

    // Reset performed flags for all attacks
    //demonFangPerformed = false
    beastPerformed = false
    dragonSwarmPerformed = false

    // Reset cooldown timers and other state variables
    prevBeast = 0L
    //prevDS = 0L
    stickTime = 0L // Resets proximity timer for Beast attack
    dragonSwarmHitCount = 0
    // For testing: Force all attacks to trigger immediately
    //demonFang() // This will add Demon Fang to `bossAttacks`
    //Beast() // Forces Beast to appear without needing proximity conditions
    //dragonSwarm() // Assuming dragonSwarm() is uncommented or needs similar behavior