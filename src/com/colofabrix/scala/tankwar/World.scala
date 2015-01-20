package com.colofabrix.scala.tankwar

import java.io.{File, PrintWriter}

import com.colofabrix.scala.geometry._
import com.colofabrix.scala.geometry.shapes.OrtoRectangle
import com.colofabrix.scala.neuralnetwork.builders.abstracts.BehaviourBuilder

import scala.collection.mutable.ListBuffer

/**
 * Where the simulation is run and world's data held
 *
 * Created by Fabrizio on 04/01/2015.
 *
 * @param arena The arena where the tanks play
 * @param max_speed The maximum tank speed
 * @param bullet_speed The proper speed of a bullet
 * @param _tanks The tanks present in the world
 */
class World(
  val arena: OrtoRectangle = OrtoRectangle( Vector2D.fromXY(0, 0), Vector2D.fromXY(5000, 5000) ),
  val max_speed: Double = 20,
  val bullet_speed: Double = 15,
  val max_rounds: Int = 500,
  private val _tanks: List[Tank] = List() )
{
  require( arena.width > 0 && arena.height > 0, "The arena must not be a point" )
  require( max_speed > 0, "Speed must be positive" )

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

  // FIXME: Remove when done
  val writer = new PrintWriter(new File("""out/tank.csv"""))

  /**
   * Sequence of all rounds in the world
   */
  val rounds = 1 to Math.abs(max_rounds)

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
      if( !check() ) action
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

    println( s"Time ${_time}: ${tanks.count(!_.isDead)} alive tanks, ${tanks.count(_.isDead)} dead tanks and ${bullets.size} bullets flying" )

    // Moving all tanks forward
    tanks.filter( !_.isDead ).par foreach { t =>
      writer.write(t.record + "\n")
      //println(t.record)

      t.stepForward()

      // Arena boundary check
      check_limit(
        () => arena.overlaps(t.position),
        () => t.on_hitsWalls,
        () => tanks -= t
      )

      // Speed limit check
      check_limit(
        () => t.speed <= max_speed,
        () => t.on_maxSpeedReached,
        () => tanks -= t
      )

      // TODO: tank-tank collision avoidance
    }

    // Moving all bullets forward
    bullets.par foreach { b =>
      writer.write(b.record + "\n")
      //println( b.record )

      b.stepForward()

      // Arena boundary check
      check_limit(
        () => arena.overlaps(b.position),
        () => b.on_hitsWalls,
        () => bullets -= b
      )

      // Speed limit check
      check_limit(
        () => b.speed <= bullet_speed,
        () => b.on_maxSpeedReached,
        () => bullets -= b
      )

      // Bullet/Tank collision management
      // TODO: space partitioning for collision detection
      tanks.filter( !_.isDead ).par foreach { t =>
        if( b.touches(t) && b.tank != t ) this.hit(t, b)
      }
    }
  }

  /**
   * Creates and add a new Tank to the world
   *
   * @param builder The builder used to construct a Tank's brain
   * @return The newly created Tank
   */
  def createTank(builder: BehaviourBuilder): Tank = {
    val tank = new Tank(this, builder)
    tanks += tank
    tank
  }

  def resetWorld(tankList: List[Tank]): Unit = {
    // Reset time
    _time =0

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
  def shot(tank: Tank) {
    println( s"Tank ${tank.id} has shot")
    _bullets += new Bullet(this, tank, bullet_speed)
  }

  /**
   * A tank its hit by a bullet
   *
   * @param tank The tank hit by the bullet
   * @param bullet The bullet that hits the tank
   */
  def hit(tank: Tank, bullet: Bullet) {
    println( s"Tank ${tank.id} has been hit by ${bullet.id}")

    // Inform the hit tank
    tank.on_isHit(bullet)
    // Inform the bullet that hits
    bullet.on_hits(tank)
    // Inform the tank that shot the bullet
    bullet.tank.on_hits(bullet, tank)

    // Remove the bullet and the hit tank
    _bullets -= bullet
  }
}
