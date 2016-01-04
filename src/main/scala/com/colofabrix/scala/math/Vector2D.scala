/*
 * Copyright (C) 2015 Fabrizio Colonna
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language
 * governing permissions and limitations under the License.
 */

package com.colofabrix.scala.math

import java.lang.Math._
import scala.language.implicitConversions

// scalastyle:off number.of.methods

/**
 * A generic Cartesian Vector
 *
 * The vectors represented here are generic, thus they are non applied vectors. This means that there isn't a convention
 * to interpret them. It can either be that they are indicating a point related to the origin of the axes or they can
 * represents a difference vector with origin not in the origin of axes.
 *
 * @param _cartesian The ending point of a origin centered vector in cartesian coordinates
 * @param _polar The ending point of a origin centered vector in polar coordinates
 */
final case class Vector2D private ( _cartesian: CartesianCoord, _polar: PolarCoord ) {
  require( _cartesian != null || _polar != null, "A set of coordinates must be specified" )

  import com.colofabrix.scala.math.Vector2DImplicits._

  /**
   * Cartesian representation of this vectors
   */
  lazy val cartesian: CartesianCoord = if ( _cartesian == null ) CoordinatesImplicits.Polar2Cartesian( polar ) else _cartesian

  /**
   * Polar representation of this vectors
   */
  lazy val polar: PolarCoord = if ( _polar == null ) CoordinatesImplicits.Cartesian2Polar( cartesian ) else _polar

  /**
   * Distance on the X-Axis
   */
  lazy val x: Double = cartesian.x

  /**
   * Distance on the Y-Axis
   */
  lazy val y: Double = cartesian.y

  /**
   * Length of the vector, modulus
   */
  lazy val t: Double = polar.t

  /**
   * Rotation relative to the X-Axis, in radians
   */
  lazy val r: Double = polar.r

  /**
   * Gets one of the cartesian components of the point position
   *
   * @param i 0: x coordinate, 1: y coordinate, 2: Theta, 3: Rho
   * @return The specified coordinate
   */
  def apply( i: Int ): Double = Seq( x, y, r, t )( i )

  /**
   * Apply a transformation to the point
   *
   * To each component (x, y) is applied the transformation T
   *
   * @param t A function that transform a coordinate of the point
   * @return A new point which is a transformation of the current one
   */
  @inline
  def :=( t: Double ⇒ Double ): Vector2D = Vector2D.new_xy( t( this.x ), t( this.y ) )

  /**
   * Apply a transformation to the point
   *
   * To each component (x, y) is applied the transformation T. The current component
   * is given through the Int parameter of T
   *
   * @param t A function that transform a coordinate of the point
   * @return A new point which is a transformation of the current one
   */
  @inline
  def :=( t: ( Double, Int ) ⇒ Double ): Vector2D = Vector2D.new_xy( t( this.x, 0 ), t( this.y, 1 ) )

  /**
   * Map a point through another one
   *
   * Each cartesian component is multiplied by each cartesian component of the other vector
   *
   * @param that The point to use as a map
   * @return A new point which is a point-to-point multiplication with `that`
   */
  @inline
  def :=( that: Vector2D ): Vector2D = this := { _ * that( _ ) }

  /**
   * Apply a transformation to the point
   *
   * To each component (r, t) is applied the transformation T.
   *
   * @param t A function that transform a coordinate of the point
   * @return A new point which is a transformation of the current one
   */
  @inline
  def @=( t: Double ⇒ Double ): Vector2D = Vector2D.new_rt( t( this.r ), t( this.t ) )

  /**
   * Apply a transformation to the point
   *
   * To each component (r, t) is applied the transformation T. The current component
   * is given through the Int parameter of T
   *
   * @param t A function that transform a coordinate of the point
   * @return A new point which is a transformation of the current one
   */
  @inline
  def @=( t: ( Double, Int ) ⇒ Double ): Vector2D = Vector2D.new_rt( t( this.r, 2 ), t( this.t, 3 ) )

  /**
   * Map a point through another one
   *
   * Each cartesian component is multiplied by each cartesian component of the other vector
   *
   * @param that The point to use as a map
   * @return A new point which is a point-to-point multiplication with `that`
   */
  @inline
  def @=( that: Vector2D ): Vector2D = this @= { _ * that( _ ) }

  /**
   * Projects a vector onto another
   *
   * @param that The vector identifying the projection axis
   */
  @inline
  def → ( that: Vector2D ): Vector2D = this.r * cos( this.t - that.t ) * that.v

  /**
   * Rotates the vector of a given angle
   *
   * @param angle The angle of rotation, in radians
   */
  @inline
  def ¬( angle: Double ): Vector2D = Vector2D.new_rt( this.r, this.t + angle )

  /**
   * Finds the ccw perpendicular vector, rotated CCW
   */
  @inline
  def -| = Vector2D.new_rt( this.r, this.t + Math.PI / 2 )

  /**
   * Finds the cw perpendicular vector, rotated CW
   */
  @inline
  def |- = Vector2D.new_rt( this.r, this.t - Math.PI / 2 )

  /**
   * Finds the normal to this vector
   *
   * @return The unit vector of the ccw rotation of the current vector
   */
  @inline
  def n = this.-|.v

  /**
   * Gets this vector's versor
   *
   * @return A unit vector with the same direction as the current vector
   */
  @inline
  def v = Vector2D.new_rt( 1, this.t )

  /**
   * Adds a scalar to both the cartesian coordinates of the vector
   *
   * @param that The quantity to add
   * @return A new vector moved of that quantity
   */
  @inline
  def +( that: Double ) = Vector2D.new_xy( this.x + that, this.y + that )

  /**
   * Adds two vectors
   *
   * @param that The vector to add to the current one
   * @return A new vector which is the sum between the current and the given vectors
   */
  @inline
  def +( that: Vector2D ) = Vector2D.new_xy( this.x + that.x, this.y + that.y )

  /**
   * Subtracts a scalar to both the cartesian coordinates of the vector
   *
   * @param that The quantity to add
   * @return A new vector moved of that quantity
   */
  @inline
  def -( that: Double ) = Vector2D.new_xy( this.x - that, this.y - that )

  /**
   * Subtracts two vectors
   *
   * @param that The vector to subtract to the current one
   * @return A new vector which is the difference between the current and the given vectors
   */
  @inline
  def -( that: Vector2D ) = Vector2D.new_xy( this.x - that.x, this.y - that.y )

  /**
   * Scalar product (scaling)
   *
   * @param alpha Scalar value to multiply by
   * @return A new vector following the scalar multiplication rules
   */
  @inline
  def *( alpha: Double ): Vector2D = Vector2D.new_xy( this.x * alpha, this.y * alpha )

  @inline
  def *( alpha: Vector2D ): Vector2D = {
    require( this.x == this.y )
    this := alpha
  }

  /**
   * Inner or Dot product
   *
   * @param that Vector to multiply by
   * @return A new vector following the inner product rules
   */
  @inline
  def x( that: Vector2D ): Double = this.x * that.x + this.y * that.y

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
  @inline
  def ^( that: Vector2D ): Double = this.x * that.y - this.y * that.x

  /**
   * By-Scalar division
   *
   * @param alpha Scalar value to divide by
   * @return A new vector following the by-scalar multiplication rules
   */
  @inline
  def /( alpha: Double ) = Vector2D.new_xy( this.x / alpha, this.y / alpha )

  /**
   * Compare the current vector with a given one and determine if it is less than the other
   *
   * The comparison is made between the length of the two vectors
   *
   * @param that The vector to compare
   * @return true if the current instance is shorter than {that}, false otherwise
   */
  @inline
  def <( that: Vector2D ): Boolean = this.r < that.r

  /**
   * Compare the current vector with a number and determine if is less than it
   *
   * The comparison is made with the length of the vector and the number itself
   *
   * @param distance The number
   * @return true if the current instance is shorter than the number {distance}, false otherwise
   */
  @inline
  def <( distance: Double ): Boolean = this.r < distance

  /**
   * Compare the current vector with a given one and determine if it is less or equal than the other
   *
   * The comparison is made between the length of the two vectors
   *
   * @param that The vector to compare
   * @return true if the current instance is shorter or equal than {that}, false otherwise
   */
  @inline
  def <=( that: Vector2D ): Boolean = this.r <= that.r

  /**
   * Compare the current vector with a number and determine if is less or equal than it
   *
   * The comparison is made with the length of the vector and the number itself
   *
   * @param distance The number
   * @return true if the current instance is shorter or equal than the number {distance}, false otherwise
   */
  @inline
  def <=( distance: Double ): Boolean = this.r <= distance

  /**
   * Compare the current vector with a given one and determine if it is greater or equal than the other
   *
   * The comparison is made between the length of the two vectors
   *
   * @param that The vector to compare
   * @return true if the current instance is longer or equal than {that}, false otherwise
   */
  @inline
  def >=( that: Vector2D ): Boolean = this.r >= that.r

  /**
   * Compare the current vector with a number and determine if is less than it
   *
   * The comparison is made with the length of the vector and the number itself
   *
   * @param distance The number
   * @return true if the current instance is longer or equal than the number {distance}, false otherwise
   */
  @inline
  def >=( distance: Double ): Boolean = this.r >= distance

  /**
   * Compare the current vector with a given one and determine if it is greater than the other
   *
   * The comparison is made between the length of the two vectors
   *
   * @param that The vector to compare
   * @return true if the current instance is longer than {that}, false otherwise
   */
  @inline
  def >( that: Vector2D ): Boolean = this.r > that.r

  /**
   * Compare the current vector with a number and determine if is greater than it
   *
   * The comparison is made with the length of the vector and the number itself
   *
   * @param distance The number
   * @return true if the current instance is longer than the number {distance}, false otherwise
   */
  @inline
  def >( distance: Double ): Boolean = this.r > distance

  override def toString: String = s"Vector[C($x, $y), P($r, $t)]"
}

object Vector2D {

  /**
   * Creates a generic Cartesian Vector
   *
   * @param polar The ending point of a origin centered vector in polar coordinates
   */
  def apply( polar: PolarCoord ): Vector2D = new Vector2D( null, polar )

  /**
   * Creates a generic Cartesian Vector
   *
   * @param cartesian The ending point of a origin centered vector in cartesian coordinates
   */
  def apply( cartesian: CartesianCoord ): Vector2D = new Vector2D( cartesian, null )

  /**
   * Creates a vector starting from polar coordinates
   *
   * @param t Length of the vector
   * @param r Rotation relative to the X-Axis, in radians
   * @return A new vector
   */
  def new_rt( r: Double, t: Double ): Vector2D = Vector2D( PolarCoord( r, t ) )

  /**
   * Creates a vector starting from cartesian coordinates
   *
   * @param x Distance on the X-Axis
   * @param y Distance on the Y-Axis
   * @return A new vector
   */
  def new_xy( x: Double, y: Double ): Vector2D = Vector2D( CartesianCoord( x, y ) )

  /**
   * Vector Origin
   *
   * It's a null vector
   *
   * @return A vector with both coordinates equals to zero
   */
  def origin: Vector2D = Vector2D.new_xy( 0, 0 )

  /**
   * Zero vector
   *
   * It's a null vector, alias for {origin}
   *
   * @return A vector with both coordinates equals to zero
   */
  def zero = origin
}

/**
 * Implicits for Vector2D
 */
object Vector2DImplicits {

  implicit def double2Vector2D( x: Double ): Vector2D = Vector2D.new_xy( x, x )

}