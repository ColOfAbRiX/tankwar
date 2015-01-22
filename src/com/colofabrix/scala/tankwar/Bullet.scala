package com.colofabrix.scala.tankwar

import com.colofabrix.scala.geometry._
import com.colofabrix.scala.geometry.abstracts.{PhysicalObject, Shape}
import com.colofabrix.scala.geometry.shapes.Circle

/**
 * Represents a bullet shot by a Tank
 *
 * Created by Fabrizio on 02/01/2015.
 */
class Bullet( override val world: World, val tank: Tank, val proper_speed: Double ) extends  PhysicalObject {

  /**
   * Position of the center of the PhysicalObject
   *
   * @return The point on the world where is the center of the PhysicalObject
   */
  override var _position: Vector2D = tank.position

  /**
   * Speed of the object relative to the arena
   *
   * @return The current step speed
   */
  override var _speed = Vector2D.new_rt( proper_speed, tank.rotation ) + tank.speed

  /**
   * Physical boundary of the bullet.
   */
  override def boundaries: Shape = Circle(_position, 2)

  /**
   * Moves the bullet one step into the future
   */
  override def stepForward() {
    _position = _position + _speed
  }

  /**
   * Called when the bullet hits a tank with
   *
   * @param tank The tank that is hit
   */
  def on_hits(tank: Tank) {
  }

  override def toString = id

  /**
   * Mass of the PhysicalObject
   *
   * @return The mass of the object
   */
  override protected var _mass: Double = 1.0

  /**
   * When the bullet reached the walls of the arena nothing happens, so
   * the bullet flies outside and it is removed from the game
   */
  override def on_hitsWalls: Unit = {}

  /**
   * Called when the objects is moving faster than the allowed speed
   */
  override def on_maxSpeedReached: Unit = {}
}