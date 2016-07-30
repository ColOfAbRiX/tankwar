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

import com.colofabrix.scala.geometry.abstracts.Coordinates

import scala.language.implicitConversions

/**
  * Cartesian Coordinate representation
  *
  * @see https://en.wikipedia.org/wiki/Cartesian_coordinate_system
  * @param x Distance on the X-Axis
  * @param y Distance on the Y-Axis
  */
final class CartesianCoord private ( val x: Double, val y: Double ) extends Coordinates {

  /**
    * Equality check between coordinates
    *
    * @param that The other object to compare
    * @return true if {that} represents the same coordinate as the current instance
    */
  override def equals( that: Any ) = that match {
    // With the same type I check the single coordinates
    case cc: CartesianCoord ⇒ ( this.x - cc.x ).abs <= FP_PRECISION && ( this.y - cc.y ).abs <= FP_PRECISION

    // For polar coordinates I first transform them in polar form
    case pc: PolarCoord ⇒ CartesianCoord( pc ) == this

    // Comparison not possible with other types
    case _ ⇒ false
  }

  override def hashCode: Int = 41 + this.x.hashCode + 41 * this.y.hashCode
}

object CartesianCoord {

  import com.colofabrix.scala.math.CoordinatesImplicits._

  /**
    * Cartesian Coordinate representation
    *
    * Constructor from polar coordinates
    *
    * @param p Input coordinates in polar notation
    * @return An equivalent point in cartesian coordinates
    */
  def apply( p: PolarCoord ): CartesianCoord = p

  /**
    * Cartesian Coordinate representation
    *
    * @param x Distance on the X-Axis
    * @param y Distance on the Y-Axis
    * @return An equivalent point in cartesian coordinates
    */
  def apply( x: Double, y: Double ): CartesianCoord = new CartesianCoord( x, y )
}

/**
  * Polar Coordinate representation
  *
  * Polar coordinates are always made to comply to the rules where the length is never negative and the angle is always
  * between 0 and 2.0 * PI
  *
  * @see https://en.wikipedia.org/wiki/Polar_coordinate_system
  * @param t Length of the vector, modulus. Must not be negative
  * @param r Rotation relative to the X-Axis, in radians. It's always non-negative or converted in a non-negative angle
  */
final class PolarCoord private ( val r: Double, val t: Double ) extends Coordinates {
  require( r >= 0.0, "The length of the vector must not be negative" )

  /**
    * Equality check between coordinates
    *
    * @param that The other object to compare
    * @return true if {that} represents the same coordinate as the current instance
    */
  override def equals( that: Any ) = that match {
    // With the same type I check the single coordinates
    case pc: PolarCoord ⇒ ( this.r - pc.r ).abs <= FP_PRECISION && ( this.t - pc.t ).abs <= FP_PRECISION

    // For cartesian coordinates I first transform them in polar form
    case cc: CartesianCoord ⇒
      PolarCoord( cc ) == this

    // Comparison not possible with other types
    case _ ⇒ false
  }

  /**
    * Determines if two matrices are equals
    *
    * @return true if the other object is a matrix identical to the current one
    */
  override def hashCode: Int = 41 + this.r.hashCode + 41 * this.t.hashCode
}

object PolarCoord {

  import com.colofabrix.scala.math.CoordinatesImplicits._

  /**
    * Polar Coordinate representation
    *
    * Constructor from cartesian coordinates
    *
    * @param c A reference in polar coordinates
    * @return An equivalent point in cartesian coordinates
    */
  def apply( c: CartesianCoord ): PolarCoord = c

  /**
    * Polar Coordinate representation
    *
    * This method is used to enforce that coordinates rules, like the angle to be between zero and 2.0 * PI
    *
    * @param t Length of the vector, modulus. Must not be negative
    * @param r Rotation relative to the X-Axis, in radians. It's always non-negative or converted in a non-negative angle
    * @return An equivalent point in cartesian coordinates
    */
  def apply( r: Double, t: Double ): PolarCoord =
    new PolarCoord(
      r, if ( t >= 0.0 ) t % ( 2.0 * Math.PI ) else trimAngles( t )
    )

  /**
    * Trim an angle making sure that the resulting angle is always non-negative and less than 2 * PI
    *
    * @param t The angle to trim
    * @return An equivalent angle that is always non-negative and less than 2 * PI
    */
  @inline
  def trimAngles( t: Double ): Double = t % ( -2.0 * Math.PI ) + 2.0 * Math.PI
}

/**
  * Implicit conversions to work with coordinates
  */
object CoordinatesImplicits {

  import java.lang.Math._

  /**
    * Convert the coordinates from polar to cartesian representation
    *
    * @see https://en.wikipedia.org/wiki/Polar_coordinate_system#Converting_between_polar_and_Cartesian_coordinates
    * @param c The input cartesian coordinates
    * @return The same point represented in polar coordinates
    */
  @inline
  implicit def Cartesian2Polar( c: CartesianCoord ): PolarCoord =
    PolarCoord(
      hypot( c.x, c.y ),
      PolarCoord.trimAngles( atan2( c.y, c.x ) )
    )

  /**
    * Converts a tuple of double into cartesian coordinates
    *
    * @param t A tuple where the first field is the length of the vector and the second is its angle
    * @return A new CartesianCoord object created using the provided values
    */
  implicit def Double2Cartesian( t: ( Double, Double ) ): CartesianCoord = CartesianCoord( t._1, t._2 )

  /**
    * Converts a tuple of double into polar coordinates
    *
    * @param t A tuple where the first field is the length of the vector and the second is its angle
    * @return A new PolarCoord object created using the provided values
    */
  implicit def Double2Polar( t: ( Double, Double ) ): PolarCoord = PolarCoord( t._1, t._2 )

  /**
    * Convert the coordinates from polar to cartesian representation
    *
    * @see https://en.wikipedia.org/wiki/Polar_coordinate_system#Converting_between_polar_and_Cartesian_coordinates
    * @param p The input polar coordinates
    * @return The same point represented in cartesian coordinates
    */
  @inline
  implicit def Polar2Cartesian( p: PolarCoord ): CartesianCoord =
    CartesianCoord(
      p.r * cos( p.t ),
      p.r * sin( p.t )
    )
}