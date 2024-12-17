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

  def stayPut(duration:Long) : Unit =
    val move = System.currentTimeMillis() + duration
    while (System.currentTimeMillis() < move){
      // does nothing while waiting
      rectangle.x = rectangle.x()
    }
  def returnPosition () : Unit =
    val mvmSpeed = 20
    val targetPosition : Double = 700

    while (math.abs(rectangle.x()-targetPosition)>mvmSpeed){
      val returnDirection = if rectangle.x() > targetPosition then 1 else -1
      rectangle.x = rectangle.x() + returnDirection * mvmSpeed
    }

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
  var dragonSwarmHitCount : Int = 0 // new

  def dragonSwarm(player: Player): Unit =
    val currentTime = System.currentTimeMillis()
    if currentTime - prevDS > dsInterval then
      if dragonSwarmHitCount < 3 then
        val attackDirection = checkDirection(player)
        val newAttack = new DragonSwarmAttack(rectangle.x() + rectangle.width() / 2, rectangle.y() + rectangle.height() / 2, attackDirection)
        bossAttacks += newAttack
        prevDS = currentTime
        dragonSwarmHitCount += 1
      else
        dragonSwarmPerformed = true
        dragonSwarmHitCount = 0 // Reset hit count for the next sequence
        prevDS = currentTime
        stayPut(5000)

  // to control attack state
  var attacking: Boolean = false
  var dashing: Boolean = false
  var lastDashTime: Long = 0L
  val dashCooldown: Long = 40000L // Cooldown period in milliseconds, restarts every 40 seconds (e.g., 5000ms = 5 seconds)

  def dashToPlayer(player: Player): Unit =
    val currentTime = System.currentTimeMillis()
    val mvmSpeed = 20
    val stopDistance = 50 // Define the distance at which the boss should stop
    val direction = if player.rectangle.x() > rectangle.x() then 1 else -1

    // Check if the cooldown period has passed
    if currentTime - lastDashTime >= dashCooldown then
      // Start dashing sequence
      if !dashing && !attacking then
        dashing = true

    // Move towards the player's position if dashing
    if dashing && !attacking then
      if math.abs(player.rectangle.x() - rectangle.x()) > stopDistance then
        rectangle.x = rectangle.x() + direction * mvmSpeed

      // Check if close enough to stop moving and initiate the attack
      if math.abs(player.rectangle.x() - rectangle.x()) <= stopDistance then
        attacking = true // Set the attacking flag to true
        dashing = false // Stop dashing once in range

    // Perform the attack if within range
    if attacking then
      dragonSwarm(player) // Perform the attack

    // Reset after the full attack sequence is performed
    if dragonSwarmPerformed then
      attacking = false
      dragonSwarmPerformed = false // Reset for the next dash
      lastDashTime = currentTime // Update the last dash time
      dashing = false // Ensure dashing is reset


  // a function that sends the boss back to a position







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