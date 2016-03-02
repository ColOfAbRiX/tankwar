/*
 * Copyright (C) 2016 Fabrizio
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

/**
  * Vector with Cartesian Coordinates as preferential coordinates system
  *
  * @param _x Position on the X-Axis
  * @param _y Position on the Y-Axis
  */
final case class XYVect(
  private val _x: Double,
  private val _y: Double
) extends Vect( Right[PolarCoord, CartesianCoord]( CartesianCoord( _x, _y ) ) ) {
  override def toString: String = s"Vec(x: $x, y: $y)"
}

/**
  * Vector with Polar Coordinates as preferential coordinates system
  *
  * @param _ρ Distance of the vector from the origin of the axis
  * @param _ϑ Angle formed between the positive side of the X-Asis and the vector in radians
  */
final case class RTVect(
  private val _ρ: Double,
  private val _ϑ: Double
) extends Vect( Left[PolarCoord, CartesianCoord]( PolarCoord( _ρ, _ϑ ) ) ) {
  override def toString: String = s"Vec(ρ: $ρ, ϑ: $ϑ)"
}

/**
  * A generic Cartesian Vector
  *
  * The vectors represented here are generic, thus they are non applied vectors. This means that there isn't a convention
  * to interpret them. It can either be that they are indicating a point related to the origin of the axes or they can
  * represents a difference vector with origin not in the origin of axes.
  *
  * @param value The vector representation in either cartesian or polar coordinates
  */
sealed abstract class Vect protected( value: Either[PolarCoord, CartesianCoord] ) {

  import com.colofabrix.scala.math.VectConversions._

  /**
    * Cartesian representation of this vectors
    */
  lazy val cartesian: CartesianCoord = value match {
    case Left( p ) ⇒ CartesianCoord( p )
    case Right( c ) ⇒ c
  }

  /**
    * Polar representation of this vectors
    */
  lazy val polar: PolarCoord = value match {
    case Left( p ) ⇒ p
    case Right( c ) ⇒ PolarCoord( c )
  }

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
  lazy val ϑ: Double = polar.t
  lazy val t: Double = polar.t

  /**
    * Rotation relative to the X-Axis, in radians
    */
  lazy val ρ: Double = polar.r
  lazy val r: Double = polar.r

  /**
    * Gets one of the cartesian components of the point position
    *
    * @param i 0: x coordinate, 1: y coordinate, 2: Theta, 3: Rho
    * @return The specified coordinate
    */
  def apply( i: Int ): Double = Seq( x, y, ρ, ϑ )( i )

  /**
    * Apply a transformation to the point
    *
    * To each component (x, y) is applied the transformation T
    *
    * @param t A function that transform a coordinate of the point
    * @return A new point which is a transformation of the current one
    */
  @inline
  def :=( t: Double ⇒ Double ): Vect = XYVect( t( this.x ), t( this.y ) )

  @inline
  def ⟹( t: Double ⇒ Double ): Vect = XYVect( t( this.x ), t( this.y ) )

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
  def :=( t: (Double, Int) ⇒ Double ): Vect = XYVect( t( this.x, 0 ), t( this.y, 1 ) )

  @inline
  def ⟹( t: (Double, Int) ⇒ Double ): Vect = XYVect( t( this.x, 0 ), t( this.y, 1 ) )

  /**
    * Map a point through another one
    *
    * Each cartesian component is multiplied by each cartesian component of the other vector
    *
    * @param that The point to use as a map
    * @return A new point which is a point-to-point multiplication with `that`
    */
  @inline
  def :=( that: Vect ): Vect = this := { _ * that( _ ) }

  @inline
  def ⟹( that: Vect ): Vect = this := { _ * that( _ ) }

  /**
    * Apply a transformation to the point
    *
    * To each component (r, t) is applied the transformation T.
    *
    * @param t A function that transform a coordinate of the point
    * @return A new point which is a transformation of the current one
    */
  @inline
  def @=( t: Double ⇒ Double ): Vect = RTVect( t( this.ρ ), t( this.ϑ ) )

  @inline
  def ↝( t: Double ⇒ Double ): Vect = RTVect( t( this.ρ ), t( this.ϑ ) )

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
  def @=( t: (Double, Int) ⇒ Double ): Vect = RTVect( t( this.ρ, 2 ), t( this.ϑ, 3 ) )

  @inline
  def ↝( t: (Double, Int) ⇒ Double ): Vect = RTVect( t( this.ρ, 2 ), t( this.ϑ, 3 ) )

  /**
    * Map a point through another one
    *
    * Each cartesian component is multiplied by each cartesian component of the other vector
    *
    * @param that The point to use as a map
    * @return A new point which is a point-to-point multiplication with `that`
    */
  @inline
  def @=( that: Vect ): Vect = this @= { _ * that( _ ) }

  @inline
  def ↝( that: Vect ): Vect = this @= { _ * that( _ ) }

  /**
    * Projects a vector onto another
    *
    * @param that The vector identifying the projection axis
    */
  @inline
  def →( that: Vect ): Vect = this.ρ * cos( this.ϑ - that.ϑ ) * that.v

  /**
    * Rotates the vector of a given angle
    *
    * @param angle The angle of rotation, in radians
    */
  @inline
  def ¬( angle: Double ): Vect = RTVect( this.ρ, this.ϑ + angle )

  @inline
  def ↺( angle: Double ): Vect = RTVect( this.ρ, this.ϑ + angle )

  /**
    * Finds the ccw perpendicular vector, rotated CCW
    */
  @inline
  def -| : Vect = RTVect( this.ρ, this.ϑ + Math.PI / 2 )

  @inline
  def ⊣ : Vect = RTVect( this.ρ, this.ϑ + Math.PI / 2 )

  /**
    * Finds the cw perpendicular vector, rotated CW
    */
  @inline
  def |- : Vect = RTVect( this.ρ, this.ϑ - Math.PI / 2 )

  @inline
  def ⊢ : Vect = RTVect( this.ρ, this.ϑ - Math.PI / 2 )

  /**
    * Finds the normal to this vector
    *
    * @return The unit vector of the ccw rotation of the current vector
    */
  @inline
  def n: Vect = this.-|.v

  /**
    * Gets this vector's versor
    *
    * @return A unit vector with the same direction as the current vector
    */
  @inline
  def v: Vect = RTVect( 1, this.ϑ )

  /**
    * Adds a scalar to both the cartesian coordinates of the vector
    *
    * @param that The quantity to add
    * @return A new vector moved of that quantity
    */
  @inline
  def +( that: Double ): Vect = XYVect( this.x + that, this.y + that )

  /**
    * Adds two vectors
    *
    * @param that The vector to add to the current one
    * @return A new vector which is the sum between the current and the given vectors
    */
  @inline
  def +( that: Vect ): Vect = XYVect( this.x + that.x, this.y + that.y )

  /**
    * Subtracts a scalar to both the cartesian coordinates of the vector
    *
    * @param that The quantity to add
    * @return A new vector moved of that quantity
    */
  @inline
  def -( that: Double ): Vect = XYVect( this.x - that, this.y - that )

  /**
    * Subtracts two vectors
    *
    * @param that The vector to subtract to the current one
    * @return A new vector which is the difference between the current and the given vectors
    */
  @inline
  def -( that: Vect ): Vect = XYVect( this.x - that.x, this.y - that.y )

  /**
    * Scalar product (scaling)
    *
    * @param alpha Scalar value to multiply by
    * @return A new vector following the scalar multiplication rules
    */
  @inline
  def *( alpha: Double ): Vect = XYVect( this.x * alpha, this.y * alpha )

  @inline
  def *( alpha: Vect ): Vect = {
    require( (this.x - this.y).abs <= 1E-10 )
    this := alpha
  }

  /**
    * Inner or Dot product
    *
    * @param that Vector to multiply by
    * @return A new vector following the inner product rules
    */
  @inline
  def x( that: Vect ): Double = this.x * that.x + this.y * that.y

  @inline
  def ∙( that: Vect ): Double = this.x * that.x + this.y * that.y

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
  def ^( that: Vect ): Double = this.x * that.y - this.y * that.x

  @inline
  def ×( that: Vect ): Double = this.x * that.y - this.y * that.x

  /**
    * By-Scalar division
    *
    * @param alpha Scalar value to divide by
    * @return A new vector following the by-scalar multiplication rules
    */
  @inline
  def /( alpha: Double ): Vect = this * (1.0 / alpha)

  /**
    * Compare the current vector with a given one and determine if it is less than the other
    *
    * The comparison is made between the length of the two vectors
    *
    * @param that The vector to compare
    * @return true if the current instance is shorter than {that}, false otherwise
    */
  @inline
  def <( that: Vect ): Boolean = this.ρ < that.ρ

  /**
    * Compare the current vector with a number and determine if is less than it
    *
    * The comparison is made with the length of the vector and the number itself
    *
    * @param distance The number
    * @return true if the current instance is shorter than the number {distance}, false otherwise
    */
  @inline
  def <( distance: Double ): Boolean = this.ρ < distance

  /**
    * Compare the current vector with a given one and determine if it is less or equal than the other
    *
    * The comparison is made between the length of the two vectors
    *
    * @param that The vector to compare
    * @return true if the current instance is shorter or equal than {that}, false otherwise
    */
  @inline
  def <=( that: Vect ): Boolean = this.ρ <= that.ρ

  /**
    * Compare the current vector with a number and determine if is less or equal than it
    *
    * The comparison is made with the length of the vector and the number itself
    *
    * @param distance The number
    * @return true if the current instance is shorter or equal than the number {distance}, false otherwise
    */
  @inline
  def <=( distance: Double ): Boolean = this.ρ <= distance

  /**
    * Compare the current vector with a given one and determine if it is greater or equal than the other
    *
    * The comparison is made between the length of the two vectors
    *
    * @param that The vector to compare
    * @return true if the current instance is longer or equal than {that}, false otherwise
    */
  @inline
  def >=( that: Vect ): Boolean = this.ρ >= that.ρ

  /**
    * Compare the current vector with a number and determine if is less than it
    *
    * The comparison is made with the length of the vector and the number itself
    *
    * @param distance The number
    * @return true if the current instance is longer or equal than the number {distance}, false otherwise
    */
  @inline
  def >=( distance: Double ): Boolean = this.ρ >= distance

  /**
    * Compare the current vector with a given one and determine if it is greater than the other
    *
    * The comparison is made between the length of the two vectors
    *
    * @param that The vector to compare
    * @return true if the current instance is longer than {that}, false otherwise
    */
  @inline
  def >( that: Vect ): Boolean = this.ρ > that.ρ

  /**
    * Compare the current vector with a number and determine if is greater than it
    *
    * The comparison is made with the length of the vector and the number itself
    *
    * @param distance The number
    * @return true if the current instance is longer than the number {distance}, false otherwise
    */
  @inline
  def >( distance: Double ): Boolean = this.ρ > distance

  /**
    * The Cartesian Plane quadrant where the vector lies
    */
  def quadrant: Int = {
    if( x > 0.0 && y >= 0.0 ) {
      1
    }
    else if( x <= 0.0 && y > 0.0 ) {
      2
    }
    else if( x <= 0.0 && y < 0.0 ) {
      3
    }
    else {
      4
    }
  }

  override def toString: String

  override def equals( other: Any ): Boolean = other match {
    case that: Vect ⇒ cartesian == that.cartesian || polar == that.polar
    case _ ⇒ false
  }

  override def hashCode( ): Int = {
    val state = Seq( x, y )
    state.map( _.hashCode( ) ).foldLeft( 0 )( ( a, b ) ⇒ 31 * a + b )
  }
}

object Vect {
  /**
    * Vector Origin
    *
    * It's a null vector
    *
    * @return A vector with both coordinates equals to zero
    */
  def origin: Vect = XYVect( 0, 0 )

  /**
    * Zero vector
    *
    * It's a null vector, alias for {origin}
    *
    * @return A vector with both coordinates equals to zero
    */
  def zero = origin
}

object VectConversions {

  /**
    * Enrichment for numeric types to allow commuting of the operations
    *
    * This class implements the same operation of Vect for verse order
    *
    * @param number The object to apply the conversion
    * @tparam T The type of the object that must be convertible in a Numeric
    */
  implicit final class Support[T: Numeric]( number: T ) {
    private val _number = implicitly[Numeric[T]].toDouble( number )

    @inline
    def +( v: Vect ): Vect = XYVect( v.x + _number, v.y + _number )

    @inline
    def -( v: Vect ): Vect = XYVect( v.x - _number, v.y - _number )

    @inline
    def *( v: Vect ): Vect = XYVect( v.x * _number, v.y * _number )

    @inline
    def <( v: Vect ): Boolean = _number < v.ρ

    @inline
    def <=( v: Vect ): Boolean = _number <= v.ρ

    @inline
    def >=( v: Vect ): Boolean = _number >= v.ρ

    @inline
    def >( v: Vect ): Boolean = _number > v.ρ
  }

  implicit def doubleTuple2Vect( x: (Double, Double) ): Vect = XYVect( x._1, x._2 )
}
