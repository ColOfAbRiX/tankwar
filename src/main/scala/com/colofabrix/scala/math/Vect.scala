/*
 * Copyright (C) 2017 Fabrizio
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

import com.colofabrix.scala.math.VectUtils._

/**
  * Vector with Cartesian Coordinates (X-Y) as preferential coordinates system
  *
  * @param x Position on the X-Axis
  * @param y Position on the Y-Axis
  */
final case class XYVect(override val x: Double, override val y: Double) extends Vect {
  override def ρ: Double = Math.hypot(x, y)

  override def ϑ: Double =
    restrictAngle(
      Math.atan2(y, x)
    )

  override def toString: String = s"Vec(x: $x, y: $y)"
}

/**
  * Vector with Polar Coordinates (R-T) as preferential coordinates system
  *
  * @param ρ  Distance of the vector from the origin of the axis
  * @param _ϑ Angle formed between the positive side of the X-Axis and the vector in radians
  */
final case class RTVect(override val ρ: Double, _ϑ: Double) extends Vect {
  require(ρ >= 0, "Distance of the vector from the origin must be non-negative")

  override def ϑ: Double = restrictAngle(_ϑ)

  override def x: Double = ρ * Math.cos(ϑ)

  override def y: Double = ρ * Math.sin(ϑ)

  override def toString: String = s"Vec(ρ: $ρ, ϑ: $ϑ)"
}

/**
  * A generic Cartesian Vector
  *
  * The vectors represented here are generic, thus they are non applied vectors. This means that there isn't a
  * convention
  * to interpret them. It can either be that they are indicating a point related to the origin of the axes or they can
  * represents a difference vector with origin not in the origin of axes.
  */
sealed abstract class Vect extends AnyRef with scalaz.Equal[Vect] {
  /** Distance on the X-Axis */
  def x: Double

  /** Distance on the Y-Axis */
  def y: Double

  /** Length of the vector, modulus */
  def ρ: Double

  /** Rotation relative to the X-Axis, in radians */
  def ϑ: Double

  /**
    * Projects a vector onto another
    *
    * @param that The vector identifying the projection axis
    */
  @inline
  def →(that: Vect): Vect = {
    if( this == Vect.zero ) this
    else if( that == Vect.zero ) that
    else this.ρ * Math.cos(this.ϑ - that.ϑ) * that.v
  }


  /**
    * Rotates the vector of a given angle
    *
    * @param angle The angle of rotation, in radians
    */
  @inline
  def ↺(angle: Double): Vect = this match {
    case _: RTVect =>
      RTVect(this.ρ, this.ϑ + angle)
    case _: XYVect => XYVect(
      this.x * Math.cos(angle) - this.y * Math.sin(angle),
      this.x * Math.sin(angle) - this.y * Math.cos(angle)
    )
  }

  /** The CCW perpendicular vector, rotated CCW */
  @inline
  def ⊣ : Vect = this match {
    case _: XYVect => this match {
      case XYVect(0, 0) =>
        throw new UnsupportedOperationException("Finding the CCW perpendicular of the zero vector has no meaning.")
      case _ => XYVect(this.y, -this.x)
    }
    case _: RTVect => this match {
      case RTVect(0, 0) =>
        throw new UnsupportedOperationException("Finding the CCW perpendicular of the zero vector has no meaning.")
      case _ => RTVect(this.ρ, this.ϑ + Math.PI / 2.0)
    }
  }

  /** Finds the CW perpendicular vector, rotated CW */
  @inline
  def ⊢ : Vect = this match {
    case _: XYVect => this match {
      case XYVect(0, 0) =>
        throw new UnsupportedOperationException("Finding the CW perpendicular of the zero vector has no meaning.")
      case _ => XYVect(-this.y, this.x)
    }
    case _: RTVect => this match {
      case RTVect(0, 0) =>
        throw new UnsupportedOperationException("Finding the CW perpendicular of the zero vector has no meaning.")
      case _ => RTVect(this.ρ, this.ϑ - Math.PI / 2.0)
    }
  }

  /**
    * Gets this vector's versor
    *
    * @return A unit vector with the same direction as the current vector
    */
  @inline
  def v: Vect = this match {
    case _: XYVect => this match {
      case XYVect(0, 0) =>
        throw new UnsupportedOperationException("The zero vector has no versor, as it is a point.")
      case _ => this / this.ρ
    }
    case _: RTVect => this match {
      case RTVect(0, 0) =>
        throw new UnsupportedOperationException("The zero vector has no versor, as it is a point.")
      case _ => RTVect(1, this.ϑ)
    }
  }

  /**
    * Finds the normal to this vector
    *
    * @return The unit vector of the ccw rotation of the current vector
    */
  @inline
  def n: Vect = this match {
    case XYVect(0, 0) =>
      throw new UnsupportedOperationException("The normal is not defined for a point.")
    case RTVect(0, 0) =>
      throw new UnsupportedOperationException("The normal is not defined for a point.")
    case _ => this.⊣.v
  }

  /**
    * Adds two vectors
    *
    * @param that The vector to add to the current one
    *
    * @return A new vector which s the sum between the current and the given vectors
    */
  @inline
  def +(that: Vect): Vect = XYVect(this.x + that.x, this.y + that.y)

  /**
    * Subtracts two vectors
    *
    * @param that The vector to subtract to the current one
    *
    * @return A new vector which is the difference between the current and the given vectors
    */
  @inline
  def -(that: Vect): Vect = XYVect(this.x - that.x, this.y - that.y)

  /**
    * Scalar multiplication (scaling)
    *
    * @param alpha Scalar value to multiply by
    *
    * @return A new vector following the scalar multiplication rules
    */
  @inline
  def *(alpha: Double): Vect = this match {
    case _: XYVect => XYVect(this.x * alpha, this.y * alpha)
    case _: RTVect => RTVect(this.ρ * alpha, this.ϑ)
  }

  /**
    * Scalar or Dot product
    *
    * @param that Vector to multiply by
    *
    * @return A new vector following the inner product rules
    */
  @inline
  def ∙(that: Vect): Double = this match {
    case _: XYVect => this.x * that.x + this.y * that.y
    case _: RTVect => this.ρ * that.ρ * Math.cos(this.ϑ - that.ϑ)
  }

  /**
    * Vector or Cross product
    *
    * As we are treating a special case where our input vectors are always lying
    * in the XY plane, the resultant vector will always be parallel to the Z axis
    * and in this case there's no need of a vector as output
    *
    * @param that Vector to multiply by
    *
    * @return A number over the Z axis
    */
  @inline
  def ×(that: Vect): Double = this match {
    case _: XYVect => this.x * that.y - this.y * that.x
    case _: RTVect => this.ρ * that.ρ * Math.sin(that.ϑ - this.ϑ)
  }

  /**
    * Scalar division
    *
    * @param alpha Scalar value to divide by
    *
    * @return A new vector following the by-scalar multiplication rules
    */
  @inline
  def /(alpha: Double): Vect = this * (1.0 / alpha)

  /** The Cartesian Plane quadrant where the vector lies */
  def quadrant: Int = this match {
    case _: XYVect =>
      if( x ~> 0.0 && y ~> 0.0 ) 1
      else if( x ~< 0.0 && y ~> 0.0 ) 2
      else if( x ~< 0.0 && y ~< 0.0 ) 3
      else if( x ~> 0.0 && y ~< 0.0 ) 4
      else 0

    case _: RTVect =>
      if( this.ϑ % (Math.PI / 2.0) ~== 0.0 ) 0
      else Math.floor(this.ϑ / (Math.PI / 2.0)).toInt + 1
  }

  override def toString: String

  override def equals(that: Any): Boolean = that match {
    case xy: XYVect => (this.x ~== xy.x) && (this.y ~== xy.y)
    case rt: RTVect => (this.ϑ ~== rt.ϑ) && (this.ρ ~== rt.ρ)
    case _ => return false
  }

  override def hashCode(): Int = {
    Seq(x, y)
      .map(_.hashCode())
      .foldLeft(0) {
        (a, b) => 31 * a + b
      }
  }

  /**
    * Trim an angle making sure that 0 <= α < 2π
    *
    * @param angle The angle to trim
    *
    * @return An equivalent angle that is always 0 <= α < 2π
    */
  @inline
  protected def restrictAngle(angle: Double): Double = {
    if( angle ~>= 2.0 * Math.PI ) {
      angle % (-2.0 * Math.PI)
    }
    else if( angle ~< 0.0 ) {
      angle % (-2.0 * Math.PI) + 2.0 * Math.PI
    }
    else {
      angle
    }
  }

  override def equal(v1: Vect, v2: Vect): Boolean = v1.equals(v2)
}

object Vect {
  /**
    * Zero vector. It's a null vector
    *
    * @return A vector with both coordinates equals to zero
    */
  def zero = XYVect.zero

  /**
    * Arbitrary unit vector
    *
    * @param t The angle of the unit vector
    *
    * @return A vector of magnitude 1.0 and angle specified
    */
  def unit(t: Double) = RTVect.unit(t)
}

object XYVect {
  /** Zero vector. */
  def zero = XYVect(0.0, 0.0)

  /**
    * Arbitrary unit vector
    *
    * @param t The angle of the unit vector
    *
    * @return A vector of magnitude 1.0 and angle specified
    */
  def unit(t: Double) = XYVect(Math.cos(t), Math.sin(t))
}

object RTVect {
  /** Zero vector. */
  def zero = RTVect(0.0, 0.0)

  /**
    * Arbitrary unit vector
    *
    * @param t The angle of the unit vector
    *
    * @return A vector of magnitude 1.0 and angle specified
    */
  def unit(t: Double) = RTVect(1.0, t)
}
