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

import com.colofabrix.scala.geometry.shapes.{ Box, Circle, Seg }
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

  import scala.reflect.runtime.universe._

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
    * @tparam A The type of the Container that we want as output
    * @return A new instance of a Container A where the current shape is completely inside its boundaries
    */
  def container[A <: Container : TypeTag]: Container =
    typeOf[A] match {
      case t if t =:= typeOf[Box] => Box.bestFit( this )
      case t if t =:= typeOf[Circle] => Circle.bestFit( this )
      case _ => Container.bestFit( this )
    }

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