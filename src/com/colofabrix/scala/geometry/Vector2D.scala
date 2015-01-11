package com.colofabrix.scala.geometry


/**
 * A generic Cartesian Vector
 *
 * @param cartesian The ending point of a origin centered vector in cartesian coordinates
 */
case class Vector2D( cartesian: CartesianCoord ) {
  import java.lang.Math._

import com.colofabrix.scala.geometry.Vector2DImplicits._

  /**
   * @param polar The ending point of a origin centered vector in polar coordinates
   */
  def this(polar: PolarCoord) = this(CartesianCoord(polar))

  /**
   * Polar representation of this vectors
   */
  val polar = PolarCoord(cartesian)

  /**
   * Distance on the X-Axis
   */
  val x: Double = cartesian.x

  /**
   * Distance on the Y-Axis
   */
  val y: Double = cartesian.y

  /**
   * Length of the vector, modulus
   */
  val t: Double = polar.t

  /**
   * Rotation relative to the X-Axis, in radians
   */
  val r: Double = polar.r

  /**
   * Gets one of the cartesian components of the point position
   *
   * @param i 0: x coordinate, 1: y coordinate, 2: Theta, 3: Rho
   * @return The specified coordinate
   */
  def apply(i: Int): Double = Seq(x, y, r, t)(i)

  /**
   * Apply a transformation to the point
   *
   * To each component (x, y) is applied the transformation T
   *
   * @param t A function that transform a coordinate of the point
   * @return A new point which is a transformation of the current one
   */
  def :=( t: Double => Double ): Vector2D = Vector2D.fromXY( t(this.x), t(this.y) )

  //def := (that: Vector2D)(t: (Double, Double) => Double): Vector2D = Vector2D.fromXY( t(this.x, v.x), t(this.y, that.y) )

  /**
   * Apply a transformation to the point
   *
   * To each component (x, y) is applied the transformation T. The current component
   * is given through the Int parameter of T
   *
   * @param t A function that transform a coordinate of the point
   * @return A new point which is a transformation of the current one
   */
  def :=( t: (Double, Int) => Double ): Vector2D = Vector2D.fromXY( t(this.x, 0), t(this.y, 1) )

  /**
   * Map a point through another one
   *
   * Each cartesian component is multiplied by each cartesian component of the other vector
   *
   * @param that The point to use as a map
   * @return A new point which is a point-to-point multiplication with `that`
   */
  def :=( that: Vector2D ): Vector2D = this := { _ * that(_) }

  /**
   * Apply a transformation to the point
   *
   * To each component (r, t) is applied the transformation T.
   *
   * @param t A function that transform a coordinate of the point
   * @return A new point which is a transformation of the current one
   */
  def @=( t: Double => Double ): Vector2D = Vector2D.fromRT( t(this.r), t(this.t) )

  /**
   * Apply a transformation to the point
   *
   * To each component (r, t) is applied the transformation T. The current component
   * is given through the Int parameter of T
   *
   * @param t A function that transform a coordinate of the point
   * @return A new point which is a transformation of the current one
   */
  def @=( t: (Double, Int) => Double ): Vector2D = Vector2D.fromRT( t(this.r, 2), t(this.t, 3) )

  /**
   * Map a point through another one
   *
   * Each cartesian component is multiplied by each cartesian component of the other vector
   *
   * @param that The point to use as a map
   * @return A new point which is a point-to-point multiplication with `that`
   */
  def @=( that: Vector2D ): Vector2D = this @= { _ * that(_) }

  /**
   * Projects a vector onto another
   *
   * @param that The vector identifying the projection axis
   */
  def ->(that: Vector2D): Vector2D = this.r * cos(this.t - that.t) * that.v

  /**
   * Rotates the vector of a given angle
   *
   * @param angle The angle of rotation, in radians
   */
  def ¬(angle: Double): Vector2D = Vector2D.fromRT(this.r, this.t + angle)//+ (angle % 2 * PI) % -2 * PI)

  /**
   * Finds the ccw perpendicular vector, rotated CCW
   */
  def -| = Vector2D.fromRT(this.r, this.t + Math.PI / 2)

  /**
   * Finds the cw perpendicular vector, rotated CW
   */
  def |- = Vector2D.fromRT(this.r, this.t - Math.PI / 2)

  /**
   * Finds the normal to this vector
   *
   * @return The unit vector of the ccw rotation of the current vector
   */
  def n = this.-|.v

  /**
   * Gets this vector's versor
   *
   * @return A unit vector with the same direction as the current vector
   */
  def v = Vector2D.fromRT( 1, this.t )

  /**
   * Adds a scalar to both the cartesian coordinates of the vector
   *
   * @param that The quantity to add
   * @return A new vector moved of that quantity
   */
  def +(that: Double) = Vector2D.fromXY(this.x + that, this.y + that)

  /**
   * Adds two vectors
   *
   * @param that The vector to add to the current one
   * @return A new vector which is the sum between the current and the given vectors
   */
  def +(that: Vector2D) = Vector2D.fromXY(this.x + that.x, this.y + that.y)

  /**
   * Subtracts a scalar to both the cartesian coordinates of the vector
   *
   * @param that The quantity to add
   * @return A new vector moved of that quantity
   */
  def -(that: Double) = Vector2D.fromXY(this.x - that, this.y - that)

  /**
   * Subtracts two vectors
   *
   * @param that The vector to subtract to the current one
   * @return A new vector which is the difference between the current and the given vectors
   */
  def -(that: Vector2D) = Vector2D.fromXY(this.x - that.x, this.y - that.y)

  /**
   * Scalar product (scaling)
   *
   * @param alpha Scalar value to multiply by
   * @return A new vector following the scalar multiplication rules
   */
  def *(alpha: Double): Vector2D = Vector2D.fromXY(this.x * alpha, this.y * alpha)
  def *(alpha: Vector2D): Vector2D = {
    require( this.x == this.y )
    this := alpha
  }

  /**
   * Inner or Dot product
   *
   * @param that Vector to multiply by
   * @return A new vector following the inner product rules
   */
  def x(that: Vector2D): Double = this.x * that.x + this.y * that.y

  /**
   * Vector or Cross product
   *
   * As we are treating a special case where our input vectors are always lying
   * in the XY plane, the resultant vector will always be parallel to the Z axis
   * and in this case there's no need of a vector as output
   *
   * @param that Vector to multiply by
   * @return A number over the Z axis
   */
  def ^(that: Vector2D): Double = this.x * that.y - this.y * that.x

  /**
   * By-Scalar division
   *
   * @param alpha Scalar value to divide by
   * @return A new vector following the by-scalar multiplication rules
   */
  def /(alpha: Double) = Vector2D.fromXY(this.x / alpha, this.y / alpha)

  def <(that: Vector2D): Boolean = this.r < that.r
  def <(distance: Double): Boolean = this.r < distance

  def <=(that: Vector2D): Boolean = this.r <= that.r
  def <=(distance: Double): Boolean = this.r <= distance

  def >=(that: Vector2D): Boolean = this.r >= that.r
  def >=(distance: Double): Boolean = this.r >= distance

  def >(that: Vector2D): Boolean = this.r > that.r
  def >(distance: Double): Boolean = this.r > distance
}

object Vector2D {

  /**
   * A generic Cartesian Vector
   *
   * @param polar The ending point of a origin centered vector in polar coordinates
   */
  def apply( polar: PolarCoord ) = new Vector2D(polar)

  /**
   * Creates a vector starting from polar coordinates
   *
   * @param t Length of the vector
   * @param r Rotation relative to the X-Axis, in radians
   * @return A new vector
   */
  def fromRT( r: Double, t: Double ) = Vector2D(new PolarCoord(r, t))

  /**
   * Creates a vector starting from cartesian coordinates
   *
   * @param x Distance on the X-Axis
   * @param y Distance on the Y-Axis
   * @return A new vector
   */
  def fromXY( x: Double, y: Double ) = Vector2D(new CartesianCoord(x, y))
}

/**
 * Implicits for Vector2D
 */
object Vector2DImplicits {

  implicit def double2Vector2D( x: Double ): Vector2D = Vector2D.fromXY(x, x)

}