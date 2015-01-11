package com.colofabrix.scala.geometry.abstracts

import com.colofabrix.scala.geometry.Vector2D
import com.colofabrix.scala.tankwar.World

/**
 * Represents a physical objects with some physical details
 *
 * Created by Fabrizio on 04/01/2015.
 */
trait PhysicalObject {
  /**
   * Reference to the world
   */
  val world: World

  /**
   * Physical boundary of the PhysicalObject.
   */
  def boundaries: Shape

  /**
   * Position of the center of the PhysicalObject
   *
   * @return The point on the world where is the center of the PhysicalObject
   */
  final def position: Vector2D = _position
  protected var _position: Vector2D

  /**
   * Speed of the object relative to the arena
   *
   * @return
   */
  final def speed: Vector2D = _speed
  protected var _speed: Vector2D

  /**
   * Moves the PhysicalObject one step into the future
   */
  def stepForward()

  /**
   * Determines if a point lies inside the Shape
   *
   * @param that The point to check
   * @return true if the point is inside or on the boundary of the shape
   */
  def isInside(that: Vector2D): Boolean = boundaries.overlaps(that)

  /**
   * Determines if a shape touches this one
   *
   * TODO: Unit test this function
   *
   * @param that The shape to check
   * @return true if the two shapes overlap
   */
  def touches(that: PhysicalObject): Boolean = this.boundaries.overlaps(that.boundaries)

  /**
   * Random PhysicalObject identifier
   */
  val id = this.getClass.toString.replace("class ", "").replace("com.colofabrix.scala.tankwar.", "") + "@" + java.util.UUID.randomUUID.toString.substring(0, 5)

  /**
   * Record identifying the step of the PhysicalObject
   *
   * @return A string in the format of a CSV
   */
  def record = s"$id;${world.time};${position.x};${position.y};${speed.x};${speed.y}".replace(".", ",")
}
