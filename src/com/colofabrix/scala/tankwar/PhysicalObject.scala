package com.colofabrix.scala.tankwar

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
  val boundary: Shape

  /**
   * Position of the center of the PhysicalObject
   *
   * @return The point on the world where is the center of the PhysicalObject
   */
  final def position: Point = _position
  protected var _position: Point

  final def speed: Point = _speed
  protected var _speed: Point

  def stepForward()

  def isInside(that: Point): Boolean = boundary.isInside(that)

  // TODO: Unit test this function
  def isInside(that: PhysicalObject): Boolean = {
    if( that.eq(this) ) return false

    val distance = that._position - this._position

    (0.0 to (distance.polar._1 , 1.0)).forall { x =>
      val check_point = this._position + Point.fromPolar( x, distance.polar._2 )
      this.isInside(check_point) || that.isInside(check_point)
    }
  }
}
