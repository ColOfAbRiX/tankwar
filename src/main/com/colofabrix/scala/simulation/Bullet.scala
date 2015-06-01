package com.colofabrix.scala.simulation

import com.colofabrix.scala.geometry.abstracts.{PhysicalObject, Shape}
import com.colofabrix.scala.geometry.shapes.Circle
import com.colofabrix.scala.math.Vector2D

/**
 * Represents a bullet shot by a Tank
 *
 * Created by Fabrizio on 02/01/2015.
 */
class Bullet( override val world: World, val tank: Tank, val proper_speed: Double ) extends  PhysicalObject {

  private var _life = 0

  /**
   * Life of the bullet
   *
   * @return The number of ticks the bullet has been alive for
   */
  def life = _life

  /**
   * Position of the center of the PhysicalObject
   *
   * @return The point on the world where is the center of the PhysicalObject
   */
   _position = tank.position

  /**
   * Speed of the object relative to the arena
   *
   * Bullets have a proper speed summed to the one of the Tank and always less than the maximum allowed speed
   *
   * @return The current speed of a bullet
   */
   _speed = Vector2D.new_rt( proper_speed, tank.rotation.t ) + tank.speed

  /**
   * Physical boundary of the bullet.
   */
  override def objectShape: Shape = Circle(_position, 2)

  /**
   * Moves the bullet one step into the future.
   */
  override def stepForward() {
    _life += 1
    _position = _position + _speed
  }

  override def toString = id

  /**
   * Callback function used to signal the {PhysicalObject} that it has hit a wall (or it has gone beyond it)
   *
   * If a bullet hits a wall nothing is done, as we want the {World} to remove it, as if it simply flew away.
   */
  override def on_hitsWalls( ): Unit = {}

  /**
   * Callback function used to signal the {PhysicalObject} that it will be respawned in the next step
   *
   * Bullets are never respawned, so the implementation is empty
   */
  override def on_respawn( ): Unit = {}

  /**
   * Callback function used to signal the {PhysicalObject} that is revolving faster than the maximum allowed angular speed
   *
   * Bullets don't rotate, their angle is fixed, so the implementation is empty
   */
  override def on_maxAngularSpeedReached( maxAngularSpeed: Double ): Unit = {}

  /**
   * Callback function used to signal the {PhysicalObject} that is moving faster than the maximum allowed speed
   *
   * Bullets speed is fixed when they are fired, there's no way they can exceed the maximum speed. If, by error the do, it's
   * of to let the world remove them. Empty implementation
   */
  override def on_maxSpeedReached( maxSpeed: Double ): Unit = {}
}