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

package com.colofabrix.scala.physix

import com.colofabrix.scala.math._
import com.colofabrix.scala.physix.shapes._

/** Information about a collision between objects  */
trait Collision {
  sealed def flip(): Collision
}

final case class Overlap(normal: Vect, distance: Double) extends Collision {
  sealed override def flip(): Collision = this.copy(normal = normal * -1.0)
}

final case class Separate(normal: Vect, distance: Double) extends Collision {
  sealed override def flip(): Collision = this.copy(normal = normal * -1.0)
}

/**
  * Implementations of collision detection between shapes.
  */
object Collision {

  /** Detects if two shapes collide */
  def check(s1: Shape, s2: Shape): Collision = s1 match {
    case c1: Circle => s2 match {
      case c2: Circle => checkCircleCircle(c1, c2)
      case b2: Box => checkCircleBox(c1, b2)
      case l2: Line => checkCircleLine(c1, l2)
    }
    case b1: Box => s2 match {
      case c2: Circle => checkCircleBox(c2, b1).flip()
      case b2: Box => checkBoxBox(b1, b2)
      case l2: Line => checkBoxLine(b1, l2)
    }
    case l1: Line => s2 match {
      case c2: Circle => checkCircleLine(c2, l1).flip()
      case b2: Box => checkBoxLine(b2, l1).flip()
      case l2: Line => checkLineLine(l1, l2)
    }
  }

  private def checkCircleBox(circle: Circle, box: Box): Collision = {
    val c2c = box.center - circle.center
    val b2c = XYVect(
      Math.max(c2c.x, box.width / 2.0),
      Math.max(c2c.y, box.height / 2.0)
    )
    val d = b2c.ρ - circle.radius

    if( d <~ 0.0 )
      Overlap(b2c.n, d)
    else
      Separate(b2c.n, d)
  }

  private def checkCircleCircle(circle1: Circle, circle2: Circle): Collision = {
    val c2c = circle1.center - circle2.center
    val d = c2c.ρ - (circle1.radius + circle2.radius)

    if( d <~ 0.0 )
      Overlap(c2c.v, d)
    else
      Separate(c2c.v, d)
  }

  private def checkCircleLine(circle: Circle, line: Line): Collision = {
    val d = line.distance(circle.center) - circle.radius

    if( d <~ 0.0 )
      Overlap(line.normal, d)
    else
      Separate(line.normal, d)
  }

  private def checkBoxBox(box1: Box, box2: Box): Collision = ???

  private def checkBoxLine(box: Box, line: Line): Collision = ???

  private def checkLineLine(line1: Line, line2: Line): Collision = ???
}
