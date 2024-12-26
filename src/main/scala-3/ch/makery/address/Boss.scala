package ch.makery.address


import scalafx.scene.paint.Color
import scalafx.scene.shape.{Ellipse, Rectangle, Shape}
import scala.collection.mutable


class Boss(val initialX : Double, val initialY: Double, player:Player) extends Hit :
  var health : Double = 100
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
  var holyLancePerformed : Boolean = false

  val stageMidPoint: Double = 400 // Half of the stage size

  def checkDirection(player: Player): Int =
    if player.rectangle.x() > rectangle.x() then
      1
    else
      -1

  var waiting: Boolean = false
  var waitEndTime: Long = 0L

  def startWait(duration: Long): Unit =
    waiting = true
    waitEndTime = System.currentTimeMillis() + duration

  def updateWait(): Unit =
    if waiting && System.currentTimeMillis() >= waitEndTime then
      waiting = false
      returnCheck() // Start returning after wait is complete

  var returning : Boolean = false
  var targetPos : Double = 700

  def returnCheck() : Unit =
    returning = true

  def updateCheck(): Unit =
    if waiting then
      updateWait()
    else if returning then
      val mvmSpeed = 20
      if math.abs(rectangle.x() - targetPos) > mvmSpeed then
        val returnDirection = if rectangle.x() > targetPos then -1 else 1
        rectangle.x = rectangle.x() + returnDirection * mvmSpeed
      else
        rectangle.x = targetPos
        returning = false

  // a mutable set to hold each attack
  var bossAttacks: mutable.Buffer[Any] = mutable.Buffer()

  def playerHitBoss(player: Player, hitDelay: Long): Unit =
    if player.showAttack.visible.value && hitCollision(this , player.showAttack) then
      if hitDelay - hitCooldown > 300 then
        println("Hit") // to show that it's really connecting
        hitCooldown = hitDelay
        this.health -= 1
        player.hit.x = this.rectangle.x() + this.rectangle.width() / 2
        player.hit.y = this.rectangle.y()
        player.hit.visible = true

    if player.hit.visible.value && hitDelay - hitCooldown > 200 then
      player.hit.visible = false


  //ranged attack
  def demonFang(player:Player): Unit = // reusing auto attack of the dummy
    if !demonFangPerformed then
        val attackDirection = checkDirection(player)
        val newAttack = new AutoAttack(rectangle.x() + rectangle.width() / 2, rectangle.y() + rectangle.height() / 2, attackDirection)
        bossAttacks += newAttack
        demonFangPerformed = true


  var prevBeast : Long = 0L
  val beastCooldown: Long = 10000L

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
        startWait(2500)

  // to control attack state
  var attacking: Boolean = false
  var dashing: Boolean = false
  var lastDashTime: Long = 0L
  var targetReached : Boolean = false
  val dashCooldown: Long = 15000L // Cooldown period in milliseconds, restarts every 40 seconds (e.g., 5000ms = 5 seconds)


  def dashToPlayer(player: Player): Unit =
    val currentTime = System.currentTimeMillis()
    val mvmSpeed = 20
    val stopDistance = 50 // Define the distance at which the boss should stop
    val direction = if player.rectangle.x() > rectangle.x() then 1 else -1

    // Start dashing sequence if not already dashing or attacking
    if !dashing && !attacking && !targetReached then
      dashing = true

    // Move towards the player's position if dashing
    if dashing && !attacking then
      if math.abs(player.rectangle.x() - rectangle.x()) > stopDistance then
        rectangle.x = rectangle.x() + direction * mvmSpeed

      // Check if close enough to stop moving and initiate the attack
      if math.abs(player.rectangle.x() - rectangle.x()) <= stopDistance then
        attacking = true // Set the attacking flag to true
        dashing = false // Stop dashing once in range
        targetReached = true // Indicate that the target has been reached

    // Perform the attack if within range
    if attacking then
      dragonSwarm(player) // Perform the attack

    // Reset after the full attack sequence is performed
    if dragonSwarmPerformed then
      attacking = false
      dragonSwarmPerformed = false // Reset for the next dash
      dashing = false // Ensure dashing is reset
      targetReached = true // Ensure target reached is true after attacking

    updateCheck()

  var castStartTime : Long = 0L
  var castStart : Boolean = false
  var castTime : Long = 5000L

  def startCasting(player:Player) : Unit =
    if !castStart then
      castStart = true
      castStartTime = System.currentTimeMillis()

    if castStart then
      val currentTime = System.currentTimeMillis()
      if currentTime - castStartTime >= castTime then
        castStart = false
        holyLance(player)

  def holyLance(player:Player) : Unit =
    if !holyLancePerformed then
      val attackDirection = checkDirection(player)
      val newAttack = new HolyLanceAttack(rectangle.x() + rectangle.width() / 2, rectangle.y() + rectangle.height() / 2, attackDirection, player)
      bossAttacks += newAttack
      holyLancePerformed = true


  def updateAtt() : Unit =
    bossAttacks.foreach:
      case attack : AutoAttack => attack.update()
      case beast : BeastAttack  => beast.update()
      case dragonSwarmAttack: DragonSwarmAttack => dragonSwarmAttack.update()
      case holyLance: HolyLanceAttack => holyLance.update()
    bossAttacks = bossAttacks.filter:
      case attack : AutoAttack => attack.xPos >= 0 && attack.xPos <= 800
      case beast : BeastAttack => beast.shape.visible.value
      case dragonSwarmAttack: DragonSwarmAttack => dragonSwarmAttack.shape.visible.value
      case holyLance: HolyLanceAttack => holyLance.spears.nonEmpty


  //currently for testing only
  def resetAttack(): Unit =
    println("Resetting attacks")

    // Reset performed flags for all attacks
    demonFangPerformed = false
    dragonSwarmPerformed = false
    holyLancePerformed = false
    dashing = false
    attacking = false
    targetReached = false
    waiting = false

    // Reset cooldown timers and other state variables

    //prevDS = 0L
    dragonSwarmHitCount = 0
    // For testing: Force all attacks to trigger immediately
    //demonFang() // This will add Demon Fang to `bossAttacks`
    //Beast() // Forces Beast to appear without needing proximity conditions
    //dragonSwarm() // Assuming dragonSwarm() is uncommented or needs similar behavior

  var phaseStartTime : Long =0
  var currentPhase : Int = 0
  // Method to manage attack phases and timings
  def managePhases(): Unit =
    val currentTime = System.currentTimeMillis()

    // Transition between phases based on time
    currentPhase match
      case 0 =>
        // Initial phase, just waiting to start attacks
        if currentTime - phaseStartTime > 2000 then
          currentPhase = 1
          phaseStartTime = currentTime

      case 1 =>
        // Perform first type of attack
        if currentTime - lastAttack > 1000 then
          demonFang(player)
          lastAttack = currentTime
        if currentTime - phaseStartTime > 5000 then
          currentPhase = 2
          phaseStartTime = currentTime

      case 2 =>
        // Perform 2nd type of attack
        if currentTime - lastAttack > 2000 then
          dashToPlayer(player)

        if currentTime - phaseStartTime > 10000 then
          currentPhase = 3
          phaseStartTime = currentTime
          lastAttack = currentTime

      case 3 =>
        // Perform 3rd type of attack (casting spell)
        if currentTime - lastAttack > 2500 then
          startCasting(player)
          lastAttack = currentTime
        if currentTime - phaseStartTime > 12000 then
          currentPhase = 0  // Loop back to initial phase
          resetAttack()
          phaseStartTime = currentTime

