package com.colofabrix.scala.tankwar.geometry

import com.colofabrix.scala.tankwar.geometry.abstracts.Shape


/**
 * Rectangle shape
 *
 * TODO: Unit test this class
 *
 * @param bottomLeft Rectangle left-bottom-most point
 * @param topRight Rectangle right-top point
 */
case class OrtoRectangle(bottomLeft: Vector2D, topRight: Vector2D, rotation: Double = 0) extends Shape {
  require( bottomLeft.x < topRight.x && bottomLeft.y < topRight.y )

  override lazy val vertices: Seq[Vector2D] = Seq(
    bottomLeft,
    Vector2D.fromXY(bottomLeft.x, topRight.y),
    topRight,
    Vector2D.fromXY(topRight.x, bottomLeft.y)
  )

  override def overlaps( p: Vector2D ) = {
    p.x >= bottomLeft.x && p.x <= topRight.x &&
    p.y >= bottomLeft.y && p.y <= topRight.y
  }

  override def trimInside(p: Vector2D): Vector2D = {
    p := { (x, i) => Math.max(Math.min(x, this.topRight(i)), this.bottomLeft(i)) }
  }

  /**
   * Width of the rectangle
   */
  val width = topRight.x - bottomLeft.x

  /**
   * Height of the rectangle
   */
  val height = topRight.y - bottomLeft.y
}

/*
case class Polygon(vertices: Seq[Vector2D]) extends Shape {
  /**
   * Determines if a point is inside or on the boundary the shape
   *
   * @param p The point to be checked
   * @return True if the point is inside the shape
   */
  override def overlaps(p: Vector2D): Boolean = ???

  /**
   * Trip a point position from its current to one which is inside the shape
   *
   * @param p The point to move
   * @return A new point which is the nearest possible to the old one and inside the shape
   */
  override def trimInside(p: Vector2D): Vector2D = ???
}
*/

/**
 * Circle shape
 *
 * TODO: Unit test this class
 *
 * @param center Center of the circle
 * @param radius Radius of the circle. Must be non-negative
 */
case class Circle(center: Vector2D, radius: Double) extends Shape {
  require( radius > 0 )

  override lazy val vertices: Seq[Vector2D] = Seq()

  override def overlaps(p: Vector2D): Boolean = p - center <= radius

  override def trimInside(p: Vector2D): Vector2D = {
    if (this overlaps p) p
    else (p - center).v * radius + center
  }


  /**
   * Determines a shape touches this one
   *
   * Circle can't use the default STA implementation as it doesn't have vertices!
   * Instead it uses two different comparisons, one circle-circle, the other circle-shape
   *
   * @param that The shape to be checked
   * @return True if the shape touches the current shape
   */
  override def overlaps( that: Shape ): Boolean = {
    that match {
      // For circles is enough to check the distance from the centers
      case c: Circle =>
        center - c.center < radius + c.radius
      case s: Shape =>
        // Check all edges of the shape taking 2 vertices at the time
          (s.vertices :+ s.vertices.head).sliding(2) foreach { v =>
            // With reference to: https://stackoverflow.com/questions/1073336/circle-line-segment-collision-detection-algorithm
            val AB = v(1) - v(0)
            val AC = center - v(0)
            val AD = AC -> AB
            val CD = AC - AD

            if (CD <= radius) return true
        }
        false
    }
  }

}