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

import com.colofabrix.scala.math.{ DoubleWithAlmostEquals, Vect }
import com.colofabrix.scala.tankwar.geometry.shapes.{ Arena, Box, Circle }

/**
  * Collection of method to check overlapping between different Shapes
  */
object ShapesOverlapping {
  /**
    * Checks overlapping between a Box and other shapes
    *
    * @param b The box as the first shape to check
    * @param s The second shape to check
    *
    * @return Returns true if @s touches, overlaps or is entirely inside @b
    */
  def borderOrInside(b: Box, s: Shape): Boolean = s match {
    case b2: Box ⇒
      // Ref: http://stackoverflow.com/questions/306316/determine-if-two-rectangles-overlap-each-other
      // Answer of Charles Bretana
      return (b.left ~<= b2.right) && (b.right ~>= b2.left) && (b.top ~<= b2.bottom) && (b.bottom ~>= b2.top)

    case a: Arena ⇒
      return (b.left ~>= a.right) && (b.right ~<= a.left) && (b.top ~>= a.bottom) && (b.bottom ~<= a.top)

    case c: Circle ⇒
      // Ref: https://stackoverflow.com/questions/401847/circle-rectangle-collision-detection-intersection
      // Answer of e.James
      val halfWidth = b.width / 2.0
      val halfHeight = b.height / 2

      // Calculate the absolute values of the x and y difference between the center of the circle and the center of
      // the rectangle. This collapses the four quadrants down into one, so that the calculations do not have to be
      // done four times.
      val dx = Math.abs(c.center.x - b.center.x)
      val dy = Math.abs(c.center.y - b.center.y)

      // Eliminate the easy cases where the circle is far enough away from the rectangle (in either direction) that
      // no intersection is possible.
      if( dx ~> (halfWidth + c.radius) || dy ~> (halfHeight + c.radius) ) {
        return false
      }

      // Handle the easy cases where the circle is close enough to the rectangle (in either direction) that an
      // intersection is guaranteed.
      if( (dx ~<= halfWidth) || (dy ~<= halfHeight) ) {
        return true
      }

      // Calculate the difficult case where the circle may intersect the corner of the rectangle. To solve, compute
      // the distance from the center of the circle and the corner, and then verify that the distance is not more
      // than the radius of the circle.
      (Math.pow(dx - halfWidth, 2) + Math.pow(dy - halfHeight, 2)) ~<= Math.pow(c.radius, 2)

    case _ ⇒ false
  }

  /**
    * Checks overlapping between a Circle and other shapes
    *
    * @param c The circle as the first shape to check
    * @param s The second shape to check
    *
    * @return Returns true if @s touches, overlaps or is entirely inside @c
    */
  def borderOrInside(c: Circle, s: Shape): Boolean = s match {
    case b: Box ⇒
      borderOrInside(b, c) // This because of the commutative property

    case a: Arena ⇒
      borderOrInside(a, c) // This because of the commutative property

    case c2: Circle =>
      (c.center - c2.center).ρ ~<= (c.radius + c2.radius)

    case _ ⇒ false
  }

  /**
    * Checks overlapping between an Arena and other shapes
    *
    * @param a The arena as the first shape to check
    * @param s The second shape to check
    *
    * @return Returns true if @s touches, overlaps or is entirely inside @a
    */
  def borderOrInside(a: Arena, s: Shape): Boolean = s match {
    case b: Box ⇒
      borderOrInside(b, a) // This because of the commutative property

    case a2: Arena ⇒ ???

    case c: Circle =>
      // Ref: https://stackoverflow.com/questions/401847/circle-rectangle-collision-detection-intersection
      // Answer of e.James
      val halfWidth = a.width / 2.0
      val halfHeight = a.height / 2

      // Calculate the absolute values of the x and y difference between the center of the circle and the center of
      // the rectangle. This collapses the four quadrants down into one, so that the calculations do not have to be
      // done four times.
      val dx = Math.abs(c.center.x - a.center.x)
      val dy = Math.abs(c.center.y - a.center.y)

      // Eliminate the easy cases where the circle is far enough away from the rectangle (in either direction) that
      // no intersection is possible.
      if( dx ~< (halfWidth + c.radius) || dy ~< (halfHeight + c.radius) ) {
        return false
      }

      // Handle the easy cases where the circle is close enough to the rectangle (in either direction) that an
      // intersection is guaranteed.
      if( (dx ~>= halfWidth) || (dy ~>= halfHeight) ) {
        return true
      }

      // Calculate the difficult case where the circle may intersect the corner of the rectangle. To solve, compute
      // the distance from the center of the circle and the corner, and then verify that the distance is not more
      // than the radius of the circle.
      (Math.pow(dx - halfWidth, 2) + Math.pow(dy - halfHeight, 2)) ~>= Math.pow(c.radius, 2)

    case _ ⇒ false
  }
}
