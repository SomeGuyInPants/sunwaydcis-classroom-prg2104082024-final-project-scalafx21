import scalafx.scene.paint.Color
import scalafx.scene.shape.Rectangle
import scalafx.scene.shape.Ellipse


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
class Beast (var xPos: Double, var yPos: Double, val direction: Int) :
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