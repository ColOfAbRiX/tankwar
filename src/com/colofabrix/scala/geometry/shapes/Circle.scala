package com.colofabrix.scala.geometry.shapes

import com.colofabrix.scala.geometry.Vector2D
import com.colofabrix.scala.geometry.abstracts.Shape

/**
 * Circle shape
 *
 * @param center Center of the circle
 * @param radius Radius of the circle. Must be non-negative
 */
case class Circle(center: Vector2D, radius: Double) extends Shape {
  // If the radius is 0... it's a point!
  require( radius > 0, "The circle must have a non-zero radius" )

  override def overlaps(p: Vector2D): Boolean = p - center <= radius

  /**
   * Determines if a shape touches this one
   *
   * Circle can't use the default STA implementation as it doesn't have vertices!
   * Instead it uses two different comparisons, one circle-circle, the other circle-shape
   *
   * @param that The shape to be checked
   * @return True if the shape touches the current shape
   */
  override def overlaps( that: Shape ): Boolean = {
    that match {

      case c: Circle =>
        // For circles is enough to check the distance from the centers
        center - c.center < radius + c.radius

      case p: Polygon =>
        p.distance(center)._1 <= radius
    }
  }
}
