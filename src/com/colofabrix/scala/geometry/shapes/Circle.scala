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

  /**
   * Determines if a line touches in any way this shape
   *
   * @param p0 The first point that defines the line
   * @param p1 The second point that defines the line
   * @return True if the point is inside the shape
   */
  override def overlaps(p0: Vector2D, p1: Vector2D): Boolean = distance(p0, p1, center) <= radius

  /**
   * Compute the distance between a point and the circle
   *
   * @param p Point to check
   * @return A distance vector from the point to polygon and the edge or point from which the distance is calculated
   */
  override def distance(p: Vector2D): (Vector2D, Vector2D) = {
    val distanceFromCenter = p - center
    val radiusTowardsPoint = distanceFromCenter.v * radius

    val distance = distanceFromCenter - radiusTowardsPoint
    val touchPoint = center + radiusTowardsPoint

    (distance, touchPoint)
  }

  /**
   * Compute the distance between a line and the edges of the polygon
   *
   * @param p0 The first point that defines the line
   * @param p1 The second point that defines the line
   * @return A distance vector from the point to polygon and the edge or point from which the distance is calculated
   */
  override def distance(p0: Vector2D, p1: Vector2D): (Vector2D, Vector2D) = {
    val distanceToCenter = distance(p0, p1, center)
    distance(distanceToCenter)
  }

  /**
   * Moves a shape
   *
   * @param where The vector specifying how to move the shape
   * @return A new shape moved of `where`
   */
  override def move(where: Vector2D): Shape = {
    new Circle(center + where, radius)
  }

  /**
   * Find a containing box for the current shape.
   *
   * Implementation of the rotating caliper algorithm
   * Ref: http://geomalgorithms.com/a08-_containers.html
   *
   * @return A polygon that fully contains this shape
   */
  override lazy val container: Shape = this.clone.asInstanceOf
}
