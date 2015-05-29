package com.colofabrix.scala.tankwar

import com.colofabrix.scala.geometry.shapes.Box
import com.colofabrix.scala.gfx.Controls.InputManager
import com.colofabrix.scala.gfx.Renderer
import com.colofabrix.scala.math.Vector2D
import com.colofabrix.scala.neuralnetwork.old.builders.abstracts.DataReader

import scala.collection.mutable.{ArrayBuffer, ListBuffer}
import scala.util.Random

/**
 * The world of the simulation
 *
 * This object represents the world where Tanks fight and provides a simulation for it. It also enforces rules
 * and limits that the participants must follow
 *
 * @param arena The arena where the tanks play
 * @param max_tank_speed The maximum speed allowed for a tank per step
 * @param max_tank_rotation The maximum angular speed of a tank per step
 * @param max_bullet_speed The maximum proper speed of a bullet per step
 * @param bullet_life The maximum number of steps a bullet can live
 * @param max_sight Maximum allowed sight for a tank
 * @param max_rounds Maximum number of rounds for a generation
 * @param dead_time Percentage of the time when a tank can be dead
 * @param _tanks The tanks present in the world
 */
class World(
  val arena: Box = Box( Vector2D.new_xy(0, 0), Vector2D.new_xy(1280, 800) ),
  val max_tank_speed: Double = 8,
  val max_tank_rotation: Double = Math.PI / 8.0,
  val max_bullet_speed: Double = 8,
  val bullet_life: Int = 15,
  val max_sight: Double = 62831.853071795864,   // Area for a total radius of 100
  val max_rounds: Int = 1000,
  val dead_time: Double = 0.1,
  private val _tanks: List[Tank] = List() )
{
  require( arena.width > 0 && arena.height > 0, "The arena must not be a point" )
  require( max_tank_rotation > 0, "The maximum angular speed must be positive" )
  require( max_tank_speed > 0, "The maximum tank speed must be positive" )
  require( max_bullet_speed > 0, "The maximum bullet speed must be positive" )
  require( bullet_life > 0, "The maximum bullet lifespan must be positive" )
  require( max_sight > 0, "The sight value must be positive" )
  require( max_rounds > 0, "The number of rounds must be positive" )
  require( dead_time >= 0 && dead_time <= 1.0, "The dead time percentage must be between 0 and 1" )


  /**
   * List of tanks present in the world
   */
  val tanks: ListBuffer[Tank] = _tanks.to

  /**
   * Penalty applied to each tank by the rules of the world
   */
  val tanksPenalty = ArrayBuffer.fill(tanks.length)(0.0)

  /**
   * List of bullets running through the world
   */
  def bullets = _bullets
  private var _bullets: ListBuffer[Bullet] = ListBuffer()


  /**
   * Global execution time
   *
   * @return The number of steps taken from the beginning
   */
  def time = _time
  private var _time: Long = 0


  /**
   * Sequence of all rounds in the world
   */
  val rounds = 1 to max_rounds


  // NOTE: Freddie's integration of graphic. Temporary
  private val renderer = new Renderer(this, "TankWar")
  private val _inputManager = new InputManager
  def inputManager = _inputManager


  /**
   * Check if a limit is respected. If not it first notifies an entity and
   * if the check is still not respected it takes a different action
   *
   * @param check What to check. If true no actions will be taken
   * @param notify Function to call to notify an entity the first time check is found to be equals to false
   * @param action The action to take if check equals false for a second time
   */
  private def check_limit(check: () => Boolean, notify: () => Unit, action: () => Unit) {
    if( !check() ) {
      notify()
      if( !check() ) action()
    }
  }


  /**
   * Counters for the statistics of the world
   */
  def counters = _counters.toMap
  private val _counters = collection.mutable.HashMap(
    "hits" -> 0,
    "shots" -> 0,
    "bannedForPosition" -> 0,
    "bannedForSpeed" -> 0,
    "bannedForSight" -> 0,
    "seenTanks" -> 0,
    "seenBullets" -> 0
  )


  /**
   * Moves the world one step forward.
   *
   * It first moves tanks and thus manages tanks firing. It then
   * handles bullets and collision between bullets and tanks
   */
  def step(): Unit = {
    _time += 1

    // Handling bullets
    bullets foreach { b =>
      b.stepForward()

      // Arena boundary check
      check_limit(
        () => arena.overlaps(b.position),
        () => b.on_hitsWalls(),
        () => {
          bullets -= b

        }
      )

      // Check the lifespan of a bullet
      if( b.life >= bullet_life ) {
        bullets -= b
      }
    }

    // Handling tanks
    tanks.filter( !_.isDead ).par.foreach { t =>
      t.stepForward()

      // Arena boundary check
      check_limit(
        () => arena.overlaps(t.position),
        () => t.on_hitsWalls(),
        () => { tanks -= t; incCounter("bannedForPosition") }
      )

      // Speed limit check
      check_limit(
        () => t.speed.x <= max_tank_speed || t.speed.y <= max_tank_speed,
        () => t.on_maxSpeedReached(),
        () => { tanks -= t; incCounter("bannedForSpeed") }
      )

      // Maximum sight boundary
      check_limit(
        () => t.targetsSight.area + t.threatsSight.area <= max_sight,
        () => t.on_sightExceedingMax(),
        () => { tanks -= t; incCounter("bannedForSight") }
      )

      // Tank/Tank sight (when a tank crosses the vision area of the current tank)
      tanks.filter( that => !that.isDead && !(t == that) ).par.foreach { that =>
        // If a tank overlaps a Tank's sight then I inform the Tank
        if( t.targetsSight.overlaps(that.boundary) ) {
          t.on_tankOnSight(that)
          incCounter("seenTanks")
        }
      }

      // Tank/Bullet sight (when a bullet crosses the vision area of the current tank)
      bullets.par.foreach { bullet =>
        // If a bullet overlaps a Tank's sight (and it's not one of the bullets fired by the Tank itself) then I inform the Tank
        if( t.threatsSight.overlaps(bullet.boundary) && bullet.tank != t && !bullet.tank.isDead ) {
          t.on_bulletOnSight(bullet)
          incCounter("seenBullets")
        }

        // TODO: space partitioning for collision detection
        if( bullet.touches(t) && bullet.tank != t ) this.on_tankHit(t, bullet)
      }
    }

    // Check which tank must be called to be respawned
    tanks.filter( _.isDead ).par.foreach { t =>
      if( t.surviveTime + max_rounds * dead_time < _time )
        t.on_respawn()
    }

    // Update the graphic
    if( renderer != null && tanks.count(!_.isDead) > 1 ) {
      renderer.update()
      inputManager.update()
    }
  }


    /**
   * Creates and add a new Tank to the world
   *
   * @return The newly created Tank
   */
  def createAndAddDefaultTank(reader: DataReader): Tank = {
    val chromosome = new TankChromosome(
      Seq(), Seq(), 2.0 * Math.PI * new Random().nextDouble(), Tank.defaultSightRatio, Tank.defaultRange, Tank.defaultActivationFunction, Tank.defaultBrainBuilder )

    createAndAddTank(chromosome, reader)
  }


  /**
   * Creates and add a new Tank to the world
   *
   * @param chromosome The chromosome defining the Tank
   * @param reader
   * @return
   */
  def createAndAddTank(chromosome: TankChromosome, reader: DataReader = null): Tank = {
    val tank = Tank(this, chromosome, reader)
    tanks += tank
    tank
  }


  def resetWorld(tankList: List[Tank]): Unit = {
    // Reset time
    _time = 0

    // Reinitialize the Tanks
    tanks.clear()
    tanks ++= tankList

    // Clear all bullets
    _bullets.clear()

    // Reset dei counter
    _counters.foreach{ case (k, v) => resCounter(k) }
  }


  /**
   * A tank requests to shot a bullet
   *
   * @param tank The tank that requested to shot
   */
  def on_tankShot(tank: Tank) {
    try {
      _bullets += new Bullet(this, tank, max_bullet_speed)
      incCounter("shots")
    }
    catch {
      case _: Exception =>
    }
  }


  /**
   * A tank its hit by a bullet
   *
   * @param tank The tank hit by the bullet
   * @param bullet The bullet that hits the tank
   */
  def on_tankHit(tank: Tank, bullet: Bullet) {
    // Prevent a dead tank to kill another tank
    if( bullet.tank.isDead ) return

    // Inform the bullet that hits
    bullet.on_hits(tank)
    // Inform the hit tank
    tank.on_isHit(bullet)
    // Inform the tank that shot the bullet
    bullet.tank.on_hits(bullet, tank)

    // FIXME: In the past here an exception appeared, multiple times
    _bullets -= bullet

    incCounter("hits")
  }

  // TODO: Create a proper data type for this
  private def incCounter(counter: String): Unit = {
    _counters += (counter -> (_counters(counter) + 1))
  }

  private def resCounter(counter: String): Unit = {
    _counters += (counter -> 0)
  }
}
