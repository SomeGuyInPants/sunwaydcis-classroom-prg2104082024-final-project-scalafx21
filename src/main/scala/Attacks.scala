import scalafx.scene.paint.Color
import scalafx.scene.shape.Rectangle

class AutoAttack(var xPos: Double, var yPos: Double, val direction: Int):
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

class Attack2 (var xPos: Double, var yPos: Double, val direction: Int):
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