package com.colofabrix.scala.tankwar.geometry

/**
 * Represents a graphical shape
 *
 * TODO: Unit test this class
 */
trait Shape {
  /**
   * Vertexes of the shape, if any. The vertexes should be enumerated
   * clockwise starting from the bottom-left one.
   */
  val vertexes: Seq[Vector2D]

  /**
   * Determines if a point is inside the shape
   *
   * @param p The point to be checked
   * @return True if the point is inside the shape
   */
  def isInside( p: Vector2D ): Boolean

  /**
   * Determines a shape touches this one
   *
   * @param that The shape to be checked
   * @return True if the shape touches the current shape
   */
  def touches( that: Shape ): Boolean = {
    // If any of the vertexes of the two shapes is inside one shape, the shapes touche
    (that.vertexes forall { v => !this.isInside(v) }) || (this.vertexes forall { v => !that.isInside(v) })
  }

  /**
   * Implementation of the Separating Axes Theorem - DRAFT
   *
   * TODO: Complete the implementation and the unit testing
   *
   * @param that The shape to be checked
   * @return True if the shape touches the current shape
   */
  def touches2( that: Shape ): Boolean = {
    val vxThis = this.vertexes :+ this.vertexes.head

    !vxThis.sliding(2)
      .map(vertex => (vertex(1) - vertex(0)) n )
      .forall{ edge_normals =>
        val projected_this = this.vertexes.map(v => (v -> edge_normals).r)
        val projected_that = that.vertexes.map(v => (v -> edge_normals).r)

        val this_boundaries = Seq(projected_this.min, projected_this.max)
        val that_boundaries = Seq(projected_that.min, projected_that.max)

      false
      }
  }

  /**
   * Trip a point position from its current to one which is inside the shape
   *
   * @param p The point to move
   * @return A new point which is the nearest possible to the old one and inside the shape
   */
  def trimInside( p: Vector2D ): Vector2D
}

/**
 * Rectangle shape
 *
 * TODO: Unit test this class
 *
 * @param bottomLeft Rectangle left-bottom-most point
 * @param topRight Rectangle right-top point
 */
case class Rectangle(bottomLeft: Vector2D, topRight: Vector2D, rotation: Double = 0) extends Shape {
  require( bottomLeft.x < topRight.x && bottomLeft.y < topRight.y )

  override lazy val vertexes: Seq[Vector2D] = Seq(
    bottomLeft,
    Vector2D.fromXY(bottomLeft.x, topRight.y),
    topRight,
    Vector2D.fromXY(topRight.x, bottomLeft.y)
  )

  override def isInside( p: Vector2D ) = {
    p.x >= bottomLeft.x && p.x <= topRight.x &&
    p.y >= bottomLeft.y && p.y <= topRight.y
  }

  override def trimInside(p: Vector2D): Vector2D = {
    p transform { (x, i) => Math.max(Math.min(x, this.topRight(i)), this.bottomLeft(i)) }
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

  override val vertexes: Seq[Vector2D] = Seq()

  override def isInside(p: Vector2D): Boolean =  p - center <= radius

  override def trimInside(p: Vector2D): Vector2D = this.center + Vector2D.fromRT( this.radius, p.t )

  /**
   * Determines a shape touches this one
   *
   * TODO: Massively check this code!
   *
   * @param that The shape to be checked
   * @return True if the shape touches the current shape
   */
  override def touches( that: Shape ): Boolean = {
    that match {
      // For circles is enough to check the distance from the centers
      case c: Circle => this.center - c.center < this.radius + c.radius
      case s: Shape =>
        // Check all edges of the shape taking 2 vertices at the time
        (s.vertexes :+ s.vertexes.head).sliding(2) foreach { v =>
          // With reference to: https://stackoverflow.com/questions/1073336/circle-line-segment-collision-detection-algorithm
          val AB = v(1) - v(0)
          val AC = this.center - v(0)
          val AD = AC -> AB
          val CD = AC - AD

          if( CD <= this.radius ) return true
        }

        false
    }
  }
}