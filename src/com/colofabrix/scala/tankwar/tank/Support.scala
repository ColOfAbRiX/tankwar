package com.colofabrix.scala.tankwar.tank

/**
 * A generic Cartesian Point
 *
 * @param x X Coordinate
 * @param y Y Coordinate
 */
case class Point(x: Double, y: Double)

trait Shape {
  def isInside( p: Point ): Boolean
}

case class Rectangle(p1: Point, p2: Point) extends Shape {
  override def isInside( p: Point ) = false
}

case class Circle(center: Point, radius: Double) extends Shape {
  require( radius > 0 )

  override def isInside(p: Point): Boolean = false
}