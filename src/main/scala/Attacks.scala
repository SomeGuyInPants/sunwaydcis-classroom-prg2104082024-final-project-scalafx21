import scalafx.scene.paint.Color
import scalafx.scene.shape.Rectangle
import scalafx.scene.shape.Ellipse
import scalafx.scene.shape.{Path, MoveTo, LineTo}
import scala.collection.mutable


// general attack frame for dummy and boss
class AutoAttack(var xPos: Double, var yPos: Double, val direction: Int) :
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

// an attack that would only happen if the player is too close to the boss for too long
class BeastAttack (var xPos: Double, var yPos: Double, val direction: Int) :
  val shape = new Ellipse:
    centerX = xPos
    centerY = yPos
    fill = Color.LightCyan
    radiusX = 25
    radiusY = 55

  val speed: Double = 5.0
  val maxDistance: Double = speed * 8 // speed x travel time
  val initialX : Double = xPos
  
  def update(): Unit =
    if Math.abs(xPos - initialX) < maxDistance then
      xPos += direction * speed
      shape.centerX = xPos
    else
      shape.visible = false

class DragonSwarmAttack (var xPos: Double, var yPos: Double, val direction: Int) :
    val shape = new Ellipse:
      centerX = xPos
      centerY = yPos
      fill = Color.LightCyan
      radiusX = 25
      radiusY = 10

    val speed: Double = 5.0
    val maxDistance: Double = speed * 8 // speed x travel time
    val initialX: Double = xPos

    def update(): Unit =
      if Math.abs(xPos - initialX) < maxDistance then
        xPos += direction * speed
        shape.centerX = xPos
      else
        shape.visible = false
    
    // new
    def reset() : Unit =
      xPos = initialX
      shape.centerX = xPos
      shape.visible = true

class HolyLanceAttack(var xPos: Double, var yPos: Double, val direction: Int, player: Player):
  var spears = mutable.Buffer[LightSpears]()
  var spearIndex = 0
  var lastStrike: Long = 0L
  val strikeInterval: Long = 200L

  // Create and position spears above the player
  for i <- 0 until 5 do
    val angle = i * (2* math.Pi / 5)
    val playerCenterX = player.rectangle.x() + player.rectangle.width() / 2
    val playerCenterY = player.rectangle.y() - 200 // Position spears above the player
    val spearX = playerCenterX + math.cos(angle) * 100
    val spearY = playerCenterY + math.sin(angle) * 100
    val spear = new LightSpears(spearX, spearY)
    spear.toPosition(playerCenterX, playerCenterY) // Position above player
    spears += spear

  def update(): Unit =
    val currentTime = System.currentTimeMillis()
    if currentTime - lastStrike > strikeInterval && spearIndex < spears.length then
      val playerCenterX = player.rectangle.x() + player.rectangle.width() / 2
      val playerCenterY = player.rectangle.y() + player.rectangle.height()
      spears(spearIndex).strike(playerCenterX, playerCenterY)
      lastStrike = currentTime
      spearIndex += 1

    // Update spear positions
    spears.foreach(_.update())

    spears = spears.filter(!_.hasStruck)



class LightSpears(var xPos: Double, var yPos: Double):
  val shape = new Ellipse():
    centerX = xPos
    centerY = yPos
    fill = Color.Red
    radiusX = 5
    radiusY = 20

  var hasStruck: Boolean = false
  var abovePlayer: Boolean = false
  var targetX: Double = 0
  var targetY: Double = 0
  var hoverTime: Long = 0L
  val hoverDuration: Long = 5000L // Duration to hover above the player
  val floor : Double = 505

  def toPosition(targetX: Double, targetY: Double): Unit =
    this.targetX = targetX
    this.targetY = targetY 

    this.hoverTime = System.currentTimeMillis()

  def strike(targetX: Double, targetY: Double): Unit =
    this.targetX = targetX
    this.targetY = floor

    this.hoverTime = System.currentTimeMillis()
    abovePlayer = true

  def move(): Unit =
    if !hasStruck then
      val speed = 10
      // Calculate the direction towards the target
      val directionX = targetX - xPos
      val directionY = targetY - yPos
      val distance = math.sqrt(directionX * directionX + directionY * directionY)

      // Ensure we don't divide by zero
      if distance > speed then
        val unitX = directionX / distance
        val unitY = directionY / distance

        if distance > speed then
          xPos += unitX * speed
          yPos += unitY * speed
          shape.centerX = xPos
          shape.centerY = yPos
        else
          if !abovePlayer then
            // When the spear reaches the initial target Y position, hover for a while
            if System.currentTimeMillis() - hoverTime >= hoverDuration then
              abovePlayer = true
              // Set new target to strike down
              this.targetY = floor
          else
            println("Spear has struck")
            hasStruck = true

  def update(): Unit =
    move()

