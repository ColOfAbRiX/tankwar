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

import com.colofabrix.scala.math.{ DoubleWithAlmostEquals, Vect }
import com.colofabrix.scala.tankwar.geometry.Shape

/**
  * Circle shape
  *
  * A Circle is a very convenient shape to check for geometrical properties as
  * it is, generally speaking, very fast to compute them. For this reason it is also marked as a {Container}
  *
  * @param center Center of the circle
  * @param radius Radius of the circle. Must be non-negative
  */
case class Circle(center: Vect, radius: Double) extends Shape {
  // If the radius is 0... it's a point!
  require(radius ~> 0.0, "The circle must have a non-zero radius")

  override lazy val area: Double = Math.PI * Math.pow(radius, 2.0)

  override def border(p: Vect): Boolean = (p - center).ρ ~== radius

  override def inside(p: Vect): Boolean = (p - center).ρ ~< radius

  override def distance(p: Vect): Option[Vect] = {
    if( this.borderOrInside(p) ) {
      None
    }
    else {
      // The distance of the point from the center of the circle. This vector is not related to the origin of axes
      val distanceFromCenter = p - center

      // A radius (segment that starts in the center of the circle and ends on a point in the circumference) directed
      // towards p.
      // This vector is not related to the origin of axes
      val radiusTowardsPoint = distanceFromCenter.v * radius

      // Distance of the point from the circumference calculated subtracting the two above vectors. This vector is not
      // related to the origin of axes
      Some(distanceFromCenter - radiusTowardsPoint)
    }
  }

  override def move(where: Vect): Shape = new Circle(center + where, radius)
}

object Circle {
  /**
    * Creates a Circle known its area
    *
    * @param center Center of the circle
    * @param area   Area of the circle. Must be non-negative
    *
    * @return A new instance of Circle with an area equals to the specified one
    */
  def fromArea(center: Vect, area: Double) = {
    require(area > 0, "The area specified for a circle must be positive")
    new Circle(center, Math.sqrt(area / Math.PI))
  }
}