/*
 * Copyright (C) 2017 Fabrizio Colonna
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

package com.colofabrix.scala.geometry

import scalaz.{ -\/, \/, \/- }
import com.colofabrix.scala.geometry.shapes._
import com.colofabrix.scala.math._

/** Information about a collision between objects  */
case class Collision(normal: Vect, distance: Double)

object Collision {

  /**
    * Detects if two shapes collide
    * http://www.wildbunny.co.uk/blog/2011/04/20/collision-detection-for-dummies/#
    */
  def collide(s1: Shape, s2: Shape): \/[Collision, Collision] = s1 match {
    case c1: Circle ⇒ collide(c1, s2)
    case b1: Box ⇒ collide(b1, s2)
    case w1: Line ⇒ collide(w1, s2)
  }

  /** Detects if a Circle is colliding with another shape */
  def collide(c1: Circle, s2: Shape): \/[Collision, Collision] = {
    val collision = s2 match {
      case c2: Circle ⇒
        val c2c = c1.center - c2.center
        val d = c2c.ρ - (c1.radius + c2.radius)

        Collision(c2c.v, d)

      case b2: Box ⇒
        val c2c = b2.center - c1.center
        val b2c = XYVect(
          Math.max(c2c.x, b2.width / 2.0),
          Math.max(c2c.y, b2.height / 2.0)
        )
        val d = b2c.ρ - c1.radius

        Collision(b2c.n, d)

      case p2: Line ⇒
        val d = p2.distance(c1.center) - c1.radius
        Collision(p2.normal, d)
    }

    if (collision.distance <=~ 0.0) -\/(collision) else \/-(collision)
  }

  /** Detects if a Box is colliding with another shape */
  def collide(b1: Box, s2: Shape): \/[Collision, Collision] = ??? /*s2 match {
    case c2: Circle => collide(c2, b1)

    case b2: Box =>
      val dx = (b1.center.x - b2.center.x).abs - (b1.width + b2.width) / 2.0
      val dy = (b1.center.y - b2.center.y).abs - (b1.height + b2.height) / 2.0

      return (dx ~<= 0.0) && (dy ~<= 0.0)
  }*/

  /** Detects if a Plane is colliding with another shape */
  def collide(p1: Line, s2: Shape): \/[Collision, Collision] = s2 match {
    case c2: Circle ⇒ collide(c2, p1)
    case b2: Box ⇒ ???
    case p2: Line ⇒ ???
  }
}
