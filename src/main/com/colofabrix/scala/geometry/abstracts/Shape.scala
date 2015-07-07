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

import com.colofabrix.scala.gfx.abstracts.{ Renderable, Renderer }
import com.colofabrix.scala.math.Vector2D

/**
 * Represents a geometric closed shape on a geometric space
 *
 * This train contains definitions of basic geometric operations like distance to a point of a check to see if
 * a line segment intersects the Shape. Upon this elementary operation the derived shapes will build their behaviour
 */
trait Shape extends Renderable {
  /**
   * An object responsible to renderer the class where this trait is applied
   *
   * @return A renderer that can draw the object where it's applied
   */
  def renderer: Renderer

  /**
   * Determines if a point is inside or on the boundary the shape
   *
   * @param p The point to be checked
   * @return True if the point is inside the shape or on its boundary
   */
  def contains( p: Vector2D ): Boolean

  /**
   * Determines if a line segment touches in any way this shape
   *
   * @param p0 The first point that defines the line segment
   * @param p1 The second point that defines the line segment
   * @return True if the line intersects the shape
   */
  def intersects( p0: Vector2D, p1: Vector2D ): Boolean

  /**
   * Determines if a shape touches in any way this shape
   *
   * @param that The shape to be checked
   * @return True if the point is inside the shape
   */
  def intersects( that: Shape ): Boolean

  /**
   * Compute the distance between a point and the boundary of the shape
   *
   * @param p The point to check
   * @return A tuple containing 1) the distance vector from the point to the boundary and 2) the edge or the point from which the distance is calculated
   */
  def distance( p: Vector2D ): (Vector2D, Vector2D)

  /**
   * Compute the distance between a line segment and the nearest edge of the shape.
   *
   * @param p0 The first point that defines the line
   * @param p1 The second point that defines the line
   * @return A tuple containing 1) the distance vector from the point to the perimeter and 2) the edge or the point from which the distance is calculated
   */
  def distance( p0: Vector2D, p1: Vector2D ): (Vector2D, Vector2D)

  /**
   * Compute the distance between a point and a line segment
   *
   * This is a problem of geometry and not directly related to the Shape, but it's something that it is used by many
   * other methods.
   *
   * @see http://geomalgorithms.com/a02-_lines.html
   * @param v0 First end of the segment
   * @param v1 Second end of the segment
   * @param p Point to check
   * @return A distance vector from the point to the segment or one of its ends
   */
  protected def distance( v0: Vector2D, v1: Vector2D, p: Vector2D ): Vector2D = {
    val v = v1 - v0
    val w = p - v0
    val c1 = v x w
    val c2 = v x v

    if( c1 <= 0.0 ) {
      return v0 - p
    }
    else if( c2 <= c1 ) {
      return v1 - p
    }

    val pb = v0 + v * (c1 / c2)
    pb - p
  }

  /**
   * Shifts a shape on the space
   *
   * Provided a vector, every vertex or equivalent of the shape will be moved following that vector
   *
   * @param where The vector specifying where to move the shape
   * @return A new shape moved of a vector {where}
   */
  def move( where: Vector2D ): Shape

  /**
   * Find a containing box for the current shape.
   *
   * A container is used as a faster and simpler way to obtain information or do some
   * actions on a Shape that would otherwise require more computation.
   * Ideally the computation should take O(n) or less. This trait should be applied
   * to shapes that guarantee this fast computation
   *
   * @return A new Container where the current shape is completely inside its boundaries
   */
  def container: Container

  /**
   * The surface area of the Shape
   */
  def area: Double
}
