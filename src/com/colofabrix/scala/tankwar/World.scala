package com.colofabrix.scala.tankwar

import java.io.{File, PrintWriter}

import com.colofabrix.scala.tankwar.geometry._

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
  val arena: OrtoRectangle = OrtoRectangle( Vector2D.fromXY(0, 0), Vector2D.fromXY(1000, 1000) ),
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
  def tanks = _tanks
  private var _tanks: ListBuffer[Tank] = ListBuffer.fill(tanks_count)(new Tank(this))

  _tanks foreach { t => println(t.brain_net) }

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

  val writer = new PrintWriter(new File("""out/tank.csv"""))

  /**
   * Moves the world one step forward
   */
  def step() {
    _time += 1

    println( s"Time ${_time}: ${_tanks.length} tanks and ${_bullets.length} bullets")

    // Moving all tanks forward
    _tanks foreach { t =>
      writer.write(t.record + "\n")
      print(t.record)

      t.stepForward()
    }

    // Moving all bullets forward
    _bullets foreach { b =>
      val text = s"${b.id};${_time};${b.position.x};${b.position.y};;${b.speed.x};${b.speed.y}\n".replace(".", ",")
      writer.write(text + "\n")
      print( text )

      b.stepForward()

      // Check if the bullet is still in the arena
      if( !arena.overlaps(b.position) )
        _bullets -= b

      // Collision management
      _tanks foreach { t => if( b.touches(t) && b.tank != t  ) this.hit(t, b) }
    }
}

  /**
   * A tank requests to shot a bullet
   *
   * @param tank The tank that requested to shot
   */
  def shot(tank: Tank): Unit = {
    println( s"Tank ${tank.id} has shot")
    _bullets += new Bullet(this, tank, bullet_speed)
  }

  /**
   * A tank its hit by a bullet
   *
   * @param tank The tank hit by the bullet
   * @param bullet The bullet that hits the tank
   */
  def hit(tank: Tank, bullet: Bullet): Unit = {
    println( s"Tank ${tank.id} has been hit by ${bullet.id}")

    // Inform the hit tank
    tank.on_isHit(bullet)
    // Inform the bullet that hits
    bullet.on_hits(tank)
    // Inform the tank that shot the bullet
    bullet.tank.on_hits(bullet, tank)

    // Remove the bullet and the hit tank
    _bullets -= bullet
    _tanks -= tank
  }
}
