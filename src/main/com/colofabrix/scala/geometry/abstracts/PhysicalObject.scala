package com.colofabrix.scala.geometry.abstracts

import com.colofabrix.scala.math.Vector2D
import com.colofabrix.scala.simulation.World

/**
 * Represents a physical objects in the physical space with some physical details
 *
 * Created by Fabrizio on 04/01/2015.
 */
trait PhysicalObject {
  /**
   * Reference to the world
   */
  def world: World

  /**
   * Physical boundary of the PhysicalObject.
   */
  def objectShape: Shape

  /**
   * Position of the center of the PhysicalObject
   *
   * @return The point on the world where is the center of the PhysicalObject
   */
  final def position: Vector2D = _position
  protected var _position: Vector2D = Vector2D.new_xy(0, 0)

  /**
   * Speed of the object relative to the arena
   *
   * @return The current step speed
   */
  final def speed: Vector2D = _speed
  protected var _speed: Vector2D = Vector2D.new_xy(0, 0)

  /**
   * Rotation of the Tank's main axis
   *
   * @return A versor indicating the angle formed by the Tank's main axis and the X axis in radians
   */
  final def rotation: Vector2D = _rotation
  protected var _rotation: Vector2D = Vector2D.new_rt(1, 0)

  /**
   * Angular speed of the object relative
   *
   * @return The current step angular speed
   */
  final def angularSpeed: Double = _angularSpeed
  protected var _angularSpeed: Double = 0.0

  /**
   * Mass of the PhysicalObject
   *
   * @return The mass of the object
   */
  final def mass: Double = _mass
  protected var _mass: Double = 1.0

  /**
   * Moves the PhysicalObject one step into the future
   */
  def stepForward(): Unit

  /**
   * Determines if a point lies inside the Shape
   *
   * @param that The point to check
   * @return true if the point is inside or on the boundary of the shape
   */
  def overlaps(that: Vector2D): Boolean = objectShape.overlaps(that)

  /**
   * Determines if a shape touches this one
   *
   * @param that The shape to check
   * @return true if the two shapes overlap
   */
  def touches(that: PhysicalObject): Boolean = this.objectShape.overlaps(that.objectShape)

  /**
   * Random PhysicalObject identifier
   */
  val id = this.getClass.toString.replace("class ", "").replace("com.colofabrix.scala.tankwar.", "") + "@" + java.util.UUID.randomUUID.toString.substring(0, 5)

  /**
   * Record identifying the step of the PhysicalObject
   *
   * @return A string in the format of a CSV
   */
  def record = s"$id,${world.time},${position.x},${position.y},${speed.x},${speed.y}"

  /**
   * Callback function used to signal the {PhysicalObject} that it has hit a wall (or it has gone beyond it)
   */
  def on_hitsWalls(): Unit

  /**
   * Callback function used to signal the {PhysicalObject} that is moving faster than the maximum allowed speed
   */
  def on_maxSpeedReached(maxSpeed: Double): Unit

  /**
   * Callback function used to signal the {PhysicalObject} that is revolving faster than the maximum allowed angular speed
   */
  def on_maxAngularSpeedReached(maxAngularSpeed: Double): Unit

  /**
   * Callback function used to signal the {PhysicalObject} that it will be respawned in the next step
   */
  def on_respawn(): Unit
}
