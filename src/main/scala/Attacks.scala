import scalafx.scene.paint.Color
import scalafx.scene.shape.Rectangle
import scalafx.scene.shape.Ellipse

class AutoAttack(var xPos: Double, var yPos: Double, val direction: Int) extends Attacks:
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

class Beast (var xPos: Double, var yPos: Double, val direction: Int) extends Attacks:
  val shape = new Ellipse:
    centerX = xPos
    centerY = yPos
    fill = Color.LightCyan
    radiusX = 25
    radiusY = 15
  val speed: Double = 5.0

  def update(): Unit =
    xPos += direction * speed
    shape.centerX = xPos