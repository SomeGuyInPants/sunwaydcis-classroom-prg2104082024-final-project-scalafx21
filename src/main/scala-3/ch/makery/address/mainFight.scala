package ch.makery.address

import ch.makery.address.*
import scalafx.Includes.jfxKeyEvent2sfx
import scalafx.animation.AnimationTimer
import scalafx.scene.{Node, Scene}
import scalafx.scene.input.KeyCode
import scalafx.scene.paint.Color
import scalafx.scene.text.{Font, Text}

import scala.collection.mutable



object mainFight:

  def createMainScene(): Scene =
    val keyInput: mutable.Set[KeyCode] = mutable.Set()
    val player = new Player(100, 455)
    val boss = new Boss(700, 455, player)
    val gameOverText = new Text():
      text = ""
      x = 300
      y = 400
      font = new Font(48)
      fill = Color.Red

    new Scene(800, 800):
      content = Seq(player.rectangle, player.showAttack, player.showHealth, player.healthText, player.hit, boss.rectangle, gameOverText) ++ boss.bossAttacks.flatMap:
        case attack: AutoAttack => Seq(attack.shape: Node)
        case beast: BeastAttack => Seq(beast.shape: Node)
        case dragonSwarm: DragonSwarmAttack => Seq(dragonSwarm.shape: Node)
        case holyLance: HolyLanceAttack => holyLance.spears.map(_.shape: Node)

      onKeyPressed = (event) => keyInput += event.code

      onKeyReleased = (event) => keyInput -= event.code

      val timer : AnimationTimer = AnimationTimer { _ => // https://www.youtube.com/watch?v=JtuSLFrfaFs (referenece)
        if keyInput.contains(KeyCode.Left) then player.moveLeft()
        if keyInput.contains(KeyCode.Right) then player.moveRight()
        if keyInput.contains(KeyCode.Space) then player.jump()
        if keyInput.contains(KeyCode.Z) then player.attack()
        player.jumpUpdate()
        player.attackUpdate()
        player.healthBar()

        val hitDelay = System.currentTimeMillis()
        boss.playerHitBoss(player, hitDelay)
        player.checkBossHit(boss, hitDelay, boss.bossAttacks)

        boss.checkDistance(player)
        boss.managePhases()
        boss.updateAtt()

        if player.Health == 0 || boss.health == 0 then
          gameOverText.text = if player.Health == 0 then "Game Over" else "Victory!"
          gameOverText.visible = true
          timer.stop()


        content = Seq(
          player.rectangle,
          player.showAttack,
          player.showHealth,
          player.healthText,
          player.hit,
          boss.rectangle,
          gameOverText
        ) ++ boss.bossAttacks.flatMap:
          case attack: AutoAttack => Seq(attack.shape: Node)
          case beast: BeastAttack => Seq(beast.shape: Node)
          case dragonSwarm: DragonSwarmAttack => Seq(dragonSwarm.shape: Node)
          case holyLance: HolyLanceAttack => holyLance.spears.map(_.shape: Node)
      }
        timer.start()



/*
object mainFight extends JFXApp3:
  private var gameOver : Boolean = false
  private var timer : AnimationTimer = _


  override def start() : Unit =
    val keyInput: mutable.Set[KeyCode] = mutable.Set()
    val player = new Player(100,455)
    //player.Health = 0
    val boss = new Boss(700,455,player)

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
          content = Seq (player.rectangle,player.showAttack,player.showHealth,player.healthText,player.hit, boss.rectangle, gameOverText) ++ boss.bossAttacks.flatMap: //flat map to ensure they are correctly formed
            case attack : AutoAttack => Seq(attack.shape : Node) //initializes the shape with Node to avoid type mismatch
            case beast : BeastAttack => Seq(beast.shape : Node)
            case dragonSwarm : DragonSwarmAttack => Seq(dragonSwarm.shape : Node)
            case holyLance : HolyLanceAttack => holyLance.spears.map(_.shape : Node)


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
      boss.dragonSwarm(player)
      //boss.dashToPlayer(player)
      //boss.holyLance(player)
      //boss.startCasting(player)
      //boss.castHolyLance(player)
      //boss.managePhases()
      boss.updateAtt()


      // Reset the boss's attack for testing purposes
      if keyInput.contains(KeyCode.R) then boss.resetAttack()

      if !gameOver then
        if player.Health == 0 || boss.health == 0 then
          gameOverScreen()

      stage.scene().content = Seq(
        player.rectangle,
        player.showAttack,
        player.showHealth,
        player.healthText,
        player.hit,
        boss.rectangle,
        gameOverText
      ) ++ boss.bossAttacks.flatMap:
        case attack: AutoAttack => Seq(attack.shape: Node)
        case beast: BeastAttack => Seq(beast.shape: Node)
        case dragonSwarm: DragonSwarmAttack => Seq(dragonSwarm.shape: Node)
        case holyLance: HolyLanceAttack => holyLance.spears.map(_.shape : Node)

    }
    timer.start()

*/