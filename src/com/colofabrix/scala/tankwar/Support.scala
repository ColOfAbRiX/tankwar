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

  def apply(i: Int): Double = {
    if( i == 0 ) x
    else if( i ==1 ) y
    else throw new IllegalArgumentException
  }

  def transform( t: Double => Double ) = Point( t(this.x), t(this.y) )

  def transform( t: (Double, Int) => Double ) = Point( t(this.x, 0), t(this.y, 1) )

  def +(that: Point) = new Point(this.x + that.x, this.y + that.y)

  def -(that: Point) = new Point(this.x - that.x, this.y - that.y)

  def *(alpha: Double) = new Point(this.x * alpha, this.y * alpha)

  def /(alpha: Double) = new Point(this.x / alpha, this.y / alpha)

  def <(that: Point) = this.polar._1 < that.polar._1
  def <(distance: Double) = this.polar._1 < distance

  def <=(that: Point) = this.polar._1 <= that.polar._1
  def <=(distance: Double) = this.polar._1 <= distance

  def >=(that: Point) = this.polar._1 >= that.polar._1
  def >=(distance: Double) = this.polar._1 >= distance

  def >(that: Point) = this.polar._1 > that.polar._1
  def >(distance: Double) = this.polar._1 > distance
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
}

/**
 * Rectangle shape
 *
 * @param start Rectangle left-bottom-most point
 * @param end Rectangle right-top point
 */
case class Rectangle(start: Point, end: Point) extends Shape {
  require( start.x < end.x && start.y < end.y )

  override def isInside( p: Point ) = {
    p.x >= start.x && p.x <= end.x &&
    p.y >= start.y && p.y <= end.y
  }

  val width = end.x - start.x

  val height = end.y - start.y
}

/**
 * Circle shape
 *
 * @param center Center of the circle
 * @param radius Radius of the circle. Must be non-negative
 */
case class Circle(center: Point, radius: Double) extends Shape {
  require( radius > 0 )

  override def isInside(p: Point): Boolean =  p - center <= radius
}