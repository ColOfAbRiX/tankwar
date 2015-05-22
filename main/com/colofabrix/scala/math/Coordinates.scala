package com.colofabrix.scala.math

import com.colofabrix.scala.geometry.abstracts.Coordinates


/**
 * Cartesian Coordinate representation
 *
 * @param x Distance on the X-Axis
 * @param y Distance on the Y-Axis
*/
case class CartesianCoord (x: Double, y: Double) extends Coordinates

object CartesianCoord {
  import com.colofabrix.scala.math.CoordinatesImplicits._

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
 * @param t Length of the vector, modulus. Must not be negative
 * @param r Rotation relative to the X-Axis, in radians. It's always non-negative or converted in a non-negative angle
 */
final class PolarCoord private(val r: Double, val t: Double) extends Coordinates {
  require(r >= 0)

  override def equals(other: Any) = other match {
    case pc: PolarCoord => this.r == pc.r && this.t == pc.t
    case cc: CartesianCoord => PolarCoord(cc) == this
    case _ => false
  }

  override def hashCode: Int = 41 + this.r.hashCode + 41 * this.t.hashCode
}

object PolarCoord {
  import com.colofabrix.scala.math.CoordinatesImplicits._

  /**
   * Polar Coordinate representation
   *
   * @param c A reference in polar coordinates
   * @return An equivalent point in cartesian coordinates
   */
  def apply(c: CartesianCoord): PolarCoord = c

  /**
   * Polar Coordinate representation
   *
   * @param t Length of the vector, modulus. Must not be negative
   * @param r Rotation relative to the X-Axis, in radians. It's always non-negative or converted in a non-negative angle
   * @return An equivalent point in cartesian coordinates
   */
  def apply(r: Double, t: Double): PolarCoord =
    new PolarCoord(r, {
      if( t >= 0) t % (2 * Math.PI)
      else trimAngles(t)
    })

  /**
   * Trim an angle making sure that the resulting angle is always non-negative and less than 2 * PI
   *
   * @param t The angle to trim
   * @return An equivalent angle that is always non-negative and less than 2 * PI
   */
  def trimAngles( t: Double ): Double = t % (-2 * Math.PI) + 2 * Math.PI
}

/**
 * Implicit conversions to work with coordinates
 */
object CoordinatesImplicits {
  import java.lang.Math._

  implicit def Double2Polar( t: (Double, Double) ): PolarCoord = PolarCoord(t._1, t._2)

  implicit def Polar2Cartesian( p: PolarCoord ): CartesianCoord =
    CartesianCoord(
      p.r * cos( p.t ),
      p.r * sin( p.t )
    )

  implicit def Double2Cartesian( t: (Double, Double) ): CartesianCoord = CartesianCoord(t._1, t._2)

  implicit def Cartesian2Polar( c: CartesianCoord ): PolarCoord =
    PolarCoord(
      sqrt(pow(c.x, 2) + pow(c.y, 2)),
      PolarCoord.trimAngles(atan2(c.y, c.x))
    )
}