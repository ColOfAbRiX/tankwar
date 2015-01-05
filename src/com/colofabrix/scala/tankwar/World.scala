package com.colofabrix.scala.tankwar

import java.io.{File, PrintWriter}

import scala.collection.mutable.ListBuffer

/**
 * Where the simulation is run and world's data held
 *
 * Created by Fabrizio on 04/01/2015.
 *
 * @param arena The arena where the tanks play
 * @param max_speed The maximum tank speed
 * @param bullet_speed The proper speed of a bullet
 * @param tanks_count The number of tanks to create
 */
class World(
  val arena: Rectangle = Rectangle( Point(0, 0), Point(1000, 1000) ),
  val max_speed: Double = 20,
  val bullet_speed: Double = 15,
  val tanks_count: Int = 1 )
{
  require( arena.width > 0 && arena.height > 0, "The arena must not be a point" )
  require( max_speed > 0, "Speed must be positive" )
  require( tanks_count > 0, "The number of tanks must be positive" )

  /**
   * List of tanks present in the world
   */
  var tanks: ListBuffer[Tank] = ListBuffer.fill(tanks_count)(new Tank(this))

  /**
   * List of bullets running through the world
   */
  var bullets: ListBuffer[Bullet] = ListBuffer()

  /**
   * Global execution time
   * @return The number of steps taken from the beginning
   */
  def time = _time
  private var _time: Long = 0

  val writer = new PrintWriter(new File("""out/tank.csv"""))

  /**
   * Moves the world one step forward
   */
  def step() {
    _time += 1

    // Moving all tanks forward
    tanks foreach { t =>
      writer.write(
        s"T;${_time};${t.position.x};${t.position.y};${t.rotation};${t.speed.x};${t.speed.y};${t.isShooting}\n".replace(".", ",")
      )
      t.stepForward()
    }

    // Moving all bullets forward
    bullets foreach { b =>
      print()
      writer.write(
        s"B;${_time};${b.position.x};${b.position.y};;${b.speed.x};${b.speed.y}\n".replace(".", ",")
      )

      b.stepForward()

      // Check if the bullet is still in the arena
      if( !b.isInside(arena.topRight) ) bullets -= b

      // Collision management
      tanks foreach { t => if( b.isInside(t) ) this.hit(t, b) }
    }
}

  /**
   * A tank requests to shot a bullet
   *
   * @param tank The tank that requested to shot
   */
  def shot(tank: Tank): Unit = {
    // FIXME: Remove after testing
    if( bullets.length >= 1 ) return
    bullets += new Bullet(this, tank, 20.0)
  }

  /**
   * A tank its hit by a bullet
   *
   * @param tank The tank hit by the bullet
   * @param bullet The bullet that hits the tank
   */
  def hit(tank: Tank, bullet: Bullet): Unit = {
    bullets -= bullet
    tank.hit(bullet)
    tanks -= tank
  }
}
