import scalafx.application.JFXApp3
import scalafx.application.JFXApp3.PrimaryStage
import scalafx.scene.Scene
import scalafx.scene.paint.Color
import scalafx.scene.input.KeyCode
import scalafx.scene.shape.Rectangle
import scalafx.Includes.jfxKeyEvent2sfx
import scalafx.animation.AnimationTimer
import scalafx.scene.SceneIncludes.jfxScene2sfx
import scala.collection.mutable
import scalafx.scene.text.{Font, Text}



object mainFight extends JFXApp3:
  private var gameOver : Boolean = false
  private var timer : AnimationTimer = _


  override def start() : Unit =
    val keyInput: mutable.Set[KeyCode] = mutable.Set()
    val player = new Player(100,455)
    //player.Health = 0
    val boss = new Boss(700,455)

    val gameOverText = new Text():
      text = ""
      x = 300
      y = 400
      font = new Font(48)
      fill = Color.Red


    def gameOverScreen(): Unit =
      if player.Health == 0 then
        gameOver = true
        gameOverText.text = "Game Over"
        timer.stop()
      if boss.health == 0 then
        gameOver = true
        gameOverText.text = "Victory!"
        timer.stop()


    stage = new JFXApp3.PrimaryStage:
        title = "Main Game"
        scene = new Scene(800, 800):
          content = Seq (player.rectangle,player.showAttack,player.showHealth,player.healthText,player.hit, boss.rectangle, gameOverText) ++ boss.bossAttacks.map:
            case attack : AutoAttack => attack.shape
            case beast : BeastAttack => beast.shape

          onKeyPressed = (event) =>
            keyInput += event.code

          onKeyReleased = (event) =>
            keyInput -= event.code




    timer = AnimationTimer { _ => //remember to include reference
      if keyInput.contains(KeyCode.Left) then player.moveLeft()
      if keyInput.contains(KeyCode.Right) then player.moveRight()
      if keyInput.contains(KeyCode.Space) then player.jump()
      if keyInput.contains(KeyCode.Z) then player.attack()
      player.jumpUpdate()
      player.attackUpdate()
      player.healthBar()

      val hitDelay = System.currentTimeMillis()
      player.checkBossHit(boss, hitDelay, boss.bossAttacks)

      //boss.demonFang()
      boss.checkDistance(player) // used to check when Beast should activate

      boss.updateAtt()

      // Reset the boss's attack for testing purposes
      if keyInput.contains(KeyCode.R) then boss.resetAttack()

      if !gameOver then
        if player.Health == 0 || boss.health == 0 then
          gameOverScreen()
      /*
      if player.Health == 0 && !gameOver then
        gameOverScreen()
      */
      stage.scene().content = Seq(player.rectangle, player.showAttack, player.showHealth, player.healthText, player.hit, boss.rectangle, gameOverText) ++ boss.bossAttacks.map:
        case attack : AutoAttack => attack.shape
        case beast : BeastAttack => beast.shape

    }
    timer.start()

