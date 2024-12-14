import scalafx.scene.paint.Color
import scalafx.scene.shape.Rectangle
import scalafx.scene.shape.Ellipse
import scalafx.scene.shape.{Path, MoveTo, LineTo}


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
    var hitCount: Int = 0 //new 
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
      hitCount = 0