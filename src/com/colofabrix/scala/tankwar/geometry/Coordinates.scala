package com.colofabrix.scala.tankwar.geometry

trait Coordinates

/**
 * Cartesian Coordinate representation
 *
 * @param x Distance on the X-Axis
 * @param y Distance on the Y-Axis
*/
case class CartesianCoord(x: Double, y: Double) extends Coordinates

object CartesianCoord {
  import com.colofabrix.scala.tankwar.geometry.CoordinatesImplicits._

  /**
   * Cartesian Coordinate representation
   *
   * @param p A reference in polar coordinates
   * @return An equivalent point in cartesian coordinates
   */
  def apply(p: PolarCoord): CartesianCoord = p
}

/**
 * Polar Coordinate representation
 *
 * @param t Length of the vector, modulus
 * @param r Rotation relative to the X-Axis, in radians
 */
case class PolarCoord(r: Double, t: Double) extends Coordinates

object PolarCoord {
  import com.colofabrix.scala.tankwar.geometry.CoordinatesImplicits._

  /**
   * Polar Coordinate representation
   *
   * @param c A reference in polar coordinates
   * @return An equivalent point in cartesian coordinates
   */
  def apply(c: CartesianCoord): PolarCoord = c
}

/**
 * Implicit conversions to work with coordinates
 */
object CoordinatesImplicits {
  import java.lang.Math._

  implicit def Double2Polar( t: (Double, Double) ): PolarCoord = new PolarCoord(t._1, t._2)

  implicit def Polar2Cartesian( p: PolarCoord ): CartesianCoord =
    new CartesianCoord(
      p.r * cos( p.t ),
      p.r * sin( p.t )
    )

  implicit def Double2Cartesian( t: (Double, Double) ): CartesianCoord = new CartesianCoord(t._1, t._2)

  implicit def Cartesian2Polar( c: CartesianCoord ): PolarCoord =
    new PolarCoord(
      sqrt(pow(c.x, 2) + pow(c.y, 2)),
      atan2(c.y, c.x)
    )
}