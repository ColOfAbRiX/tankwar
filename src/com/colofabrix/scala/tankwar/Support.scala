package com.colofabrix.scala.tankwar

/**
 * A generic Cartesian Point
 *
 * @param x X Coordinate
 * @param y Y Coordinate
 */
case class Point(x: Double, y: Double) {
  /**
   * Gets the polar coordinates of this point
   */
  val polar: (Double, Double) = {
    val rho = Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2))
    val theta = Math.atan2(x, y)
    ( rho, theta )
  }

  /**
   * Gets one of the cartesian components of the point position
   *
   * @param i 0: The x coordinate, 1: The y coordinate
   * @return The specified coordinate
   */
  def apply(i: Int): Double = {
    if( i == 0 ) x
    else if( i ==1 ) y
    else throw new IllegalArgumentException
  }

  /**
   * Apply a transformation to the point
   *
   * @param t A function that transform a coordinate of the point
   * @return A new point which is a transformation of the current one
   */
  def transform( t: Double => Double ) = Point( t(this.x), t(this.y) )

  /**
   * Apply a transformation to the point
   *
   * @param t A function that transform a coordinate of the point
   * @return A new point which is a transformation of the current one
   */
  def transform( t: (Double, Int) => Double ) = Point( t(this.x, 0), t(this.y, 1) )

  /**
   * Map a point through another one
   *
   * @param that The point to use as a map
   * @return A new point which is a point-to-point multiplication with `that`
   */
  def ->( that: Point ) = Point( this.x * that.x, this.y * that.y )

  def +(that: Double) = Point(this.x + that, this.y + that)
  def +(that: Point) = Point(this.x + that.x, this.y + that.y)

  def -(that: Double) = Point(this.x - that, this.y - that)
  def -(that: Point) = Point(this.x - that.x, this.y - that.y)

  // TODO: Unit test all this code
  def *(alpha: Double) = Point(this.x * alpha, this.y * alpha)
  def x(that: Point): Double = this.x * that.x + this.y * that.y
  def ^(that: Point) = Point(this.x * that.x + this.x * that.y, this.y * that.x + this.y * that.y)

  def /(alpha: Double) = Point(this.x / alpha, this.y / alpha)

  def <(that: Point) = this.polar._1 < that.polar._1
  def <(distance: Double) = this.polar._1 < distance

  def <=(that: Point) = this.polar._1 <= that.polar._1
  def <=(distance: Double) = this.polar._1 <= distance

  def >=(that: Point) = this.polar._1 >= that.polar._1
  def >=(distance: Double) = this.polar._1 >= distance

  def >(that: Point) = this.polar._1 > that.polar._1
  def >(distance: Double) = this.polar._1 > distance
}

object Point {
  /**
   * Creates a point using polar coordinates
   *
   * @param theta Length of the vector
   * @param rho Angle formed by the vector with the X axis
   * @return A new point represented by the specified coordinates
   */
  def fromPolar( theta: Double, rho: Double ) = Point(
    theta * Math.cos( rho ),
    theta * Math.sin( rho )
  )
}

/**
 * Represents a graphical shape
 */
trait Shape {
  /**
   * Determines if a point is inside the shape
   *
   * @param p The point to be checked
   * @return True if the point is inside the shape
   */
  def isInside( p: Point ): Boolean

  /**
   * Trip a point position from its current to one which is inside the shape
   *
   * @param p The point to move
   * @return A new point which is the nearest possible to the old one and inside the shape
   */
  def trimInside( p: Point ): Point
}

/**
 * Rectangle shape
 *
 * @param bottomLeft Rectangle left-bottom-most point
 * @param topRight Rectangle right-top point
 */
case class Rectangle(bottomLeft: Point, topRight: Point) extends Shape {
  require( bottomLeft.x < topRight.x && bottomLeft.y < topRight.y )

  override def isInside( p: Point ) = {
    p.x >= bottomLeft.x && p.x <= topRight.x &&
    p.y >= bottomLeft.y && p.y <= topRight.y
  }

  override def trimInside(p: Point): Point = {
    p transform { (x, i) => Math.max(Math.min(x, this.topRight(i)), this.bottomLeft(i)) }
  }

  val width = topRight.x - bottomLeft.x

  val height = topRight.y - bottomLeft.y
}

/**
 * Circle shape
 *
 * @param center Center of the circle
 * @param radius Radius of the circle. Must be non-negative
 */
case class Circle(center: Point, radius: Double) extends Shape {
  require( radius > 0 )

  // TODO: Unit test this code
  override def isInside(p: Point): Boolean =  p - center <= radius

  // TODO: Unit test this code
  override def trimInside(p: Point): Point =
    this.center + Point.fromPolar( this.radius, p.polar._2 )
}