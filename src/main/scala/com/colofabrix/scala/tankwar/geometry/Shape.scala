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

package com.colofabrix.scala.tankwar.geometry

import com.colofabrix.scala.math.Vect

/**
  * Represents a geometric closed shape on a geometric space
  *
  * This train contains definitions of basic geometric operations like distance from
  * a point or a check to see if a line segment intersects the [[Shape]]. Upon this
  * elementary operation the derived shapes will build their behaviour and specialize
  * the implementation to take advantage of their properties for a faster access
  */
trait Shape {

  /**
    * The surface area of the Shape
    */
  def area: Double

  /**
    * Determines if a point is inside or on the boundary the shape
    *
    * @param p The point to be checked
    * @return True if the point is inside the shape or on its boundary
    */
  def borderOrInside( p: Vect ): Boolean = inside( p ) || border( p )

  /**
    * Determines if a point is inside but not on the boundary the current shape
    *
    * @param p The vector to be checked
    * @return True if the given point is inside the shape without touching the boundary
    */
  def inside( p: Vect ): Boolean


  /**
    * Determines if a point is inside but not on the boundary the current shape
    *
    * @param p The vector to be checked
    * @return True if the given point is inside the shape without touching the boundary
    */
  def border( p: Vect ): Boolean

  /**
    * Compute the distance between a point and the boundary of the shape
    *
    * @param p The point to check
    * @return The distance vector from the point
    */
  def distance( p: Vect ): Option[Vect]

  /**
    * Shifts a shape on the space
    *
    * Provided a vector, every vertex or equivalent of the shape will be moved following that vector
    *
    * @param v The vector specifying where to move the shape
    * @return A new shape moved of a vector {where}
    */
  def move( v: Vect ): Shape
}
