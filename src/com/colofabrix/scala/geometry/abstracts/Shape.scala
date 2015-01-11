package com.colofabrix.scala.geometry.abstracts

import com.colofabrix.scala.geometry.Vector2D

/**
 * Represents a graphical shape
 *
 * TODO: Unit test this class
 */
trait Shape {
  /**
   * Determines if a point is inside or on the boundary the shape
   *
   * @param p The point to be checked
   * @return True if the point is inside the shape
   */
  def overlaps( p: Vector2D ): Boolean

  /**
   * Determines if a shape is inside or on the boundary this shape
   *
   * @param that The point to be checked
   * @return True if the point is inside the shape
   */
  def overlaps( that: Shape ): Boolean
}
