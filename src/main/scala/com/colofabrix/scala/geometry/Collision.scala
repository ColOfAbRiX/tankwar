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

package com.colofabrix.scala.geometry

import com.colofabrix.scala.geometry.shapes._
import com.colofabrix.scala.math._


/** Information about a collision between objects  */
case class Collision(
  reference: Shape,
  collider: Shape,
  normal: Vect,
  distance: Double
)

object Collision {

  /**
    * Detectes if two shapes collide
    * http://www.wildbunny.co.uk/blog/2011/04/20/collision-detection-for-dummies/#
    */
  def collide(s1: Shape, s2: Shape): Boolean = s1 match {
    case c1: Circle ⇒ collide(c1, s2)
    case c1: Canvas ⇒ collide(c1, s2)
    case b1: Box ⇒ collide(b1, s2)
  }

  /** Detectes if a Circle collides with another shape */
  def collide(c1: Circle, s2: Shape): Boolean = s2 match {
    case c2: Circle ⇒
      val p = (c1.center - c2.center).ρ - (c1.radius + c2.radius)
      return p ~<= 0.0

    case c2: Canvas ⇒
      val c2c = c2.center - c1.center
      val r2c = XYVect(Math.max(c2c.x, c2.width / 2.0), Math.max(c2c.y, c2.height / 2.0))
      val d = (c1.center - r2c).ρ - c1.radius
      return d ~>= 0.0

    case b2: Box ⇒
      val c2c = b2.center - c1.center
      val r2c = XYVect(Math.max(c2c.x, b2.width / 2.0), Math.max(c2c.y, b2.height / 2.0))
      val d = (c1.center - r2c).ρ - c1.radius
      return d ~<= 0.0
  }

  /** Detectes if a Box collides with another shape */
  def collide(b1: Box, s2: Shape): Boolean = s2 match {
    case c2: Circle ⇒ collide(c2, b1) // Reusing existing method

    case c2: Canvas ⇒
      val dx = (b1.center.x - c2.center.x).abs - (b1.width + c2.width) / 2.0
      val dy = (b1.center.y - c2.center.y).abs - (b1.height + c2.height) / 2.0

      return (dx ~>= 0.0) || (dy ~>= 0.0)

    case b2: Box ⇒
      val dx = (b1.center.x - b2.center.x).abs - (b1.width + b2.width) / 2.0
      val dy = (b1.center.y - b2.center.y).abs - (b1.height + b2.height) / 2.0

      return (dx ~<= 0.0) && (dy ~<= 0.0)
  }

  /** Detectes if a Canvas collides with another shape */
  def collide(c1: Canvas, s2: Shape): Boolean = s2 match {
    case c2: Circle ⇒ collide(c2, c1) // Reusing existing method
    case c2: Canvas ⇒ ???
    case b2: Box ⇒ collide(b2, c1) // Reusing existing method
  }


  def info(s1: Shape, s2: Shape, p1: Vect, p2: Vect): Option[Collision] = s1 match {
    case c1: Circle ⇒ info(c1, s2, p1, p2)
    case c1: Canvas ⇒ info(c1, s2, p1, p2)
    case b1: Box ⇒ info(b1, s2, p1, p2)
  }

  def info(c1: Circle, s2: Shape, p1: Vect, p2: Vect): Option[Collision] = s2 match {
    case c2: Circle ⇒
      val d = c2.center - c1.center
      val p = d.ρ - (c1.radius + c2.radius)
      ???

    case c2: Canvas ⇒ ???
    case b2: Box ⇒ ???
  }

  def info(c1: Canvas, s2: Shape, p1: Vect, p2: Vect): Option[Collision] = s2 match {
    case c2: Circle ⇒ info(c2, c1, p1, p2)
    case c2: Canvas ⇒ ???
    case b2: Box ⇒ ???
  }

  def info(b1: Box, s2: Shape, p1: Vect, p2: Vect): Option[Collision] = s2 match {
    case c2: Circle ⇒ ???
    case c2: Canvas ⇒ ???
    case b2: Box ⇒ ???
  }

}
