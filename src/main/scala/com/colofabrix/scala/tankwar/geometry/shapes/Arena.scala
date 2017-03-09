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

package com.colofabrix.scala.tankwar.geometry.shapes

import com.colofabrix.scala.math.{ DoubleWithAlmostEquals, Vect, XYVect }

/**
  * An Arena is the opposite of a Box. It is a shape that includes everything outside
  * its borders.
  *
  * @see http://geomalgorithms.com/a08-_containers.html
  *
  * @param bottomLeft Rectangle left-bottom-most point, in any quadrant of the plane
  * @param topRight   Rectangle right-top point, in any quadrant of the plane
  */
class Arena protected(bottomLeft: Vect, topRight: Vect) extends Box(bottomLeft, topRight) {

  override def inside(p: Vect) = {
    (p.x ~< bottomLeft.x) &&
      (p.x ~> topRight.x) &&
      (p.y ~< bottomLeft.y) &&
      (p.y ~> topRight.y)
  }

  override def toString = s"Arena($bottomLeft, $topRight)"

  override def equals(other: Any): Boolean = other match {
    case that: Arena ⇒
      bottomLeft == that.bottomLeft && topRight == that.topRight
    case _ ⇒ false
  }

}

object Arena {
  /**
    * Constructor that uses width, height and starts the box at the origin of the axis.
    *
    * The width and height can be negative, so it's possible to create a Box on all the quadrants of the plane
    *
    * @param width  Width of the box, can be negative
    * @param height Height of the box, can be negative
    */
  def apply(width: Double, height: Double): Arena = Arena(Vect.zero, XYVect(width, height))

  /**
    * Creates a new Arena using the two opposite vertices
    *
    * @param p0 The first vertex of the Box
    * @param p1 The second vertex of the Box opposite to p0
    *
    * @return
    */
  def apply(p0: Vect, p1: Vect): Arena = {
    val topX = Math.max(p0.x, p1.x)
    val topY = Math.max(p0.y, p1.y)
    val bottomX = Math.min(p0.x, p1.x)
    val bottomY = Math.min(p0.y, p1.y)

    require(topX - bottomX > 0.0, "An Arena must have a width greater than zero")
    require(topY - bottomY > 0.0, "An Arena must have a height greater than zero")

    return new Arena(XYVect(bottomX, bottomY), XYVect(topX, topY))
  }
}
