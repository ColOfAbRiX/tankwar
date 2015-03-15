package com.colofabrix.scala.tankwar

import com.colofabrix.scala.geometry._
import com.colofabrix.scala.geometry.shapes.Box
import com.colofabrix.scala.neuralnetwork.builders.abstracts.DataReader
import com.vogon101.java.Renderer

import scala.collection.mutable.ListBuffer

/**
 * Where the simulation is run and world's data held
 *
 * Created by Fabrizio on 04/01/2015.
 *
 * @param arena The arena where the tanks play
 * @param max_tank_speed The maximum tank speed
 * @param max_bullet_speed The proper speed of a bullet
 * @param _tanks The tanks present in the world
 */
class World(
  val arena: Box = Box( Vector2D.new_xy(0, 0), Vector2D.new_xy(5000, 5000) ),
  val max_tank_speed: Double = 20,
  val max_bullet_speed: Double = 15,
  val max_sight: Double = 500,
  val max_rounds: Int = 5000,
  private val _tanks: List[Tank] = List() )
{
  require( arena.width > 0 && arena.height > 0, "The arena must not be a point" )
  require( max_tank_speed > 0, "Speed must be positive" )

  /**
   * List of tanks present in the world
   */
  val tanks: ListBuffer[Tank] = _tanks.to

  /**
   * List of bullets running through the world
   */
  def bullets = _bullets
  private var _bullets: ListBuffer[Bullet] = ListBuffer()

  /**
   * Global execution time
   * @return The number of steps taken from the beginning
   */
  def time = _time
  private var _time: Long = 0

  /**
   * Sequence of all rounds in the world
   */
  val rounds = 1 to Math.abs(max_rounds)

  val renderer = new Renderer(this);

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
   * Moves the world one step forward.
   *
   * It first moves tanks and thus manages tanks firing. It then
   * handles bullets and collision between bullets and tanks
   */
  def step(): Unit = {
    _time += 1

    // Moving all tanks forward
    tanks.filter( !_.isDead ).par.foreach { t =>
      t.stepForward()

      // Arena boundary check
      check_limit(
        () => arena.overlaps(t.position),
        () => t.on_hitsWalls(),
        () => tanks -= t
      )

      // Speed limit check
      check_limit(
        () => t.speed.x <= max_tank_speed || t.speed.y <= max_tank_speed,
        () => t.on_maxSpeedReached(),
        () => tanks -= t
      )

      tanks.filter( that => !that.isDead && !(t == that) ).foreach { that ⇒
        val lineOfSightP0 = t.position
        val lineOfSightP1 = lineOfSightP0 + Vector2D.new_rt(max_sight, t.rotation.t)

        if(that.boundary.overlaps(lineOfSightP0, lineOfSightP1)) {
          // TODO: Implement the Tank's sight
          t.on_tankOnSight(that, that.position - t.position)
        }
      }

    }

    // Moving all bullets forward
    bullets foreach { b =>
      b.stepForward()

      // Arena boundary check
      check_limit(
        () => arena.overlaps(b.position),
        () => b.on_hitsWalls(),
        () => bullets -= b
      )
    }

    // Bullet/Tank collision management
    bullets.par.foreach { b =>
      // TODO: space partitioning for collision detection
      tanks.filter( !_.isDead ).par foreach { t =>
        if( b.touches(t) && b.tank != t ) this.on_tankHit(t, b)
      }
    }

    if( tanks.count(!_.isDead) > 1 )
      renderer.update();
  }

  /**
   * Creates and add a new Tank to the world
   *
   * @return The newly created Tank
   */
  def createAndAddDefaultTank(reader: DataReader): Tank = {
    val chromosome = new TankChromosome(
      Seq(), Seq(), Tank.defaultSight, Tank.defaultMass, Tank.defaultRange, Tank.defaultActivationFunction, Tank.defaultBrainBuilder )

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
  }

  /**
   * A tank requests to shot a bullet
   *
   * @param tank The tank that requested to shot
   */
  def on_tankShot(tank: Tank) {
    try {
      _bullets += new Bullet(this, tank, max_bullet_speed)
    }
    catch {
      case _: Exception ⇒
    }
  }

  /**
   * A tank its hit by a bullet
   *
   * @param tank The tank hit by the bullet
   * @param bullet The bullet that hits the tank
   */
  def on_tankHit(tank: Tank, bullet: Bullet) {
    // Inform the hit tank
    tank.on_isHit(bullet)
    // Inform the bullet that hits
    bullet.on_hits(tank)
    // Inform the tank that shot the bullet
    bullet.tank.on_hits(bullet, tank)

    // Remove the bullet and the hit tank
    try {
      _bullets -= bullet
    }
    catch {
      case _: Exception ⇒
    }
  }
}
