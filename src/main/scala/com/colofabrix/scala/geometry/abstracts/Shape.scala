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

package com.colofabrix.scala.geometry.abstracts

import com.colofabrix.scala.geometry.shapes.Seg
import com.colofabrix.scala.gfx.abstracts.{ Renderable, Renderer }
import com.colofabrix.scala.math.Vect

/**
  * Represents a geometric closed shape on a geometric space
  *
  * This train contains definitions of basic geometric operations like distance from
  * a point or a check to see if a line segment intersects the [[Shape]]. Upon this
  * elementary operation the derived shapes will build their behaviour and specialize
  * the implementation to take advantage of their properties for a faster access
  */
trait Shape extends Renderable {
  /**
    * The surface area of the Shape
    */
  def area: Double

  /**
    * Find a containing box for the current shape.
    *
    * A container is used as a faster and simpler way to obtain information or do some
    * actions on a Shape that would otherwise require more computation.
    * Ideally the computation should take O(n*log n) or less. This trait should be applied
    * to shapes that guarantee this fast computation
    *
    * @return A new Container where the current shape is completely inside its boundaries
    */
  lazy val container: Container = Container.bestFit( this )

  /**
    * Determines if a point is inside or on the boundary the shape
    *
    * @param p The point to be checked
    * @return True if the point is inside the shape or on its boundary
    */
  def contains( p: Vect ): Boolean

  /**
    * Determines if a shape is inside or on the boundary the current shape
    *
    * @param s The shape to be checked
    * @return True if the given shape is inside the shape or on its boundary
    */
  def contains( s: Shape ): Boolean

  /**
    * Compute the distance between a point and the boundary of the shape
    *
    * @param p The point to check
    * @return A tuple containing 1) the distance vector from the point to the boundary and 2) the edge or the point from which the distance is calculated
    */
  def distance( p: Vect ): ( Vect, Vect )

  /**
    * Compute the distance between a line segment and the nearest edge of the shape.
    *
    * @param s The line segment to check
    * @return A tuple containing 1) the distance vector from the point to the perimeter and 2) the edge or the point from which the distance is calculated
    */
  def distance( s: Seg ): ( Vect, Vect )

  /**
    * Determines if a line segment touches in any way this shape
    *
    * @param s The line segment to check
    * @return True if the line intersects the shape
    */
  def intersects( s: Seg ): Boolean

  /**
    * Determines if a shape touches in any way this shape
    *
    * @param that The shape to be checked
    * @return True if the point is inside the shape
    */
  def intersects( that: Shape ): Boolean

  /**
    * Shifts a shape on the space
    *
    * Provided a vector, every vertex or equivalent of the shape will be moved following that vector
    *
    * @param where The vector specifying where to move the shape
    * @return A new shape moved of a vector {where}
    */
  def move( where: Vect ): Shape

  /**
    * An object responsible to renderer the class where this trait is applied
    *
    * @return A renderer that can draw the object where it's applied
    */
  def renderer: Renderer
}

// VARs are used because the algorithms have been translated from a C++ code
@SuppressWarnings( Array( "org.brianmckenna.wartremover.warts.Var" ) )
object Shape {

  import Math._

  import com.colofabrix.scala.math.VectConversions._

  /**
    * Compute the distance between a point and a line segment
    *
    * This is a problem of geometry and not directly related to the Shape, but it's something that it is used by many
    * other methods.
    *
    * @see http://geomalgorithms.com/a02-_lines.html
    * @param s The segment to check against
    * @param p Point to check
    * @return A distance vector from the point to the segment or one of its ends
    */
  @inline
  def distance( s: Seg, p: Vect ): Vect = {
    val v = s.v1 - s.v0
    val w = p - s.v0
    val c1 = v x w
    val c2 = v x v

    if ( c1 <= 0.0 ) {
      return s.v0 - p
    }
    else if ( c2 <= c1 ) {
      return s.v1 - p
    }

    val pb = s.v0 + v * ( c1 / c2 )
    pb - p
  }

  /**
    * Compute the distance between two line segments
    *
    * See http://geomalgorithms.com/a07-_distance.html
    *
    * @param s0 The first segment to check
    * @param s1 The second segment to check
    * @return The distance vector between the two line segment
    */
  @inline
  def distance( s0: Seg, s1: Seg ): Vect = {
    intersects( s0, s1 ) match {
      case None ⇒
        // If two segments don't intersects, the distance is always from the end points
        val distances = distance( s0, s1.v0 ) :: distance( s0, s1.v1 ) :: Nil
        distances.minBy( _.ρ )

      case _ ⇒ Vect.zero
    }
  }

  /**
    * Determines if a point intersects a line segment.
    *
    * In this case it can be interpreted as if the point lies on the segment itself
    *
    * Ref: http://geomalgorithms.com/a05-_intersect-1.html
    *
    * @param p The point to check
    * @param s The segment to check
    * @return true if the points lies inside the segment
    */
  @inline
  def intersects( s: Seg, p: Vect ): Boolean = {
    if ( s.isY ) {
      // S is vertical, so test y coordinate
      s.v0.y <= p.y && p.y <= s.v1.y || s.v0.y >= p.y && p.y >= s.v1.y
    }
    else {
      s.v0.x <= p.x && p.x <= s.v1.x || s.v0.x >= p.x && p.x >= s.v1.x
    }
  }

  /**
    * Determines if two line segments intersects and where they do so
    *
    * Ref: http://geomalgorithms.com/a05-_intersect-1.html
    *
    * @param s0 The first segment to check
    * @param s1 The second segment to check
    * @return An option to indicate if the two segments intersects. If they do a tuple containing 1) The intersection point and 2) and Option with a possible ending intersegtion point
    */
  @inline
  def intersects( s0: Seg, s1: Seg ): Option[( Vect, Option[Vect] )] = {
    val v = s0.vect
    val u = s1.vect
    val w = s1.v0 - s0.v0
    val D = u ^ v

    // Test if  they are parallel (includes either being a point)
    if ( abs( D ) <= Double.MinPositiveValue ) {
      if ( ( u ^ w ) != 0.0 || ( v ^ w ) != 0.0 ) {
        // S1 and S2 are parallel
        return None
      }

      val du = u x u
      val dv = v x v

      // They are collinear or degenerate check if they are degenerate points

      if ( du == 0.0 && dv == 0.0 ) {
        // Both segments are points
        if ( s1.v0.x != s0.v0.x ) {
          // They are distinct  points
          return None
        }
        return Some( ( s1.v0, None ) )
      }

      if ( du == 0.0 ) {
        // S1 is a single point
        if ( !intersects( s0, s1.v0 ) ) {
          // But is not in S2
          return None
        }
        return Some( ( s1.v0, None ) )
      }

      if ( dv == 0.0 ) {
        // S2 a single point
        if ( !intersects( s1, s0.v0 ) ) {
          // But is not in S1
          return None
        }
        return Some( ( s0.v0, None ) )
      }

      // They are collinear segments - get overlap (or not)

      // Endpoints of S1 in eqn for S2
      var t0 = 0.0
      var t1 = 0.0
      val w2 = s1.v1 - s0.v0

      if ( v.x != 0.0 ) {
        t0 = w.x / v.x
        t1 = w2.x / v.x
      }
      else {
        t0 = w.y / v.y
        t1 = w2.y / v.y
      }

      if ( t0 > t1 ) {
        // Must have t0 smaller than t1, swap if not
        val tmp = t0
        t0 = t1
        t1 = tmp
      }

      if ( t0 > 1.0 || t1 < 0.0 ) {
        // NO overlap
        return None
      }

      t0 = max( 0.0, t0 )
      t1 = min( 1.0, t1 )

      if ( t0 == t1 ) {
        // Intersect is a point
        return Some( ( s0.v0 + t0 * v, None ) )
      }

      // They overlap in a valid subsegment
      return Some( ( s0.v0 + t0 * v, Some( s0.v0 + t1 * v ) ) )
    }

    // The segments are skew and may intersect in a point.

    // Get the intersect parameter for S1
    val sI = ( v ^ w ) / D
    if ( sI < 0.0 || sI > 1.0 ) {
      // No intersect with S1
      return None
    }

    // Get the intersect parameter for S2
    val tI = ( u ^ w ) / D
    if ( tI < 0.0 || tI > 1.0 ) {
      // No intersect with S2
      return None
    }

    return Some( ( s1.v0 + sI * u, None ) )
  }
}