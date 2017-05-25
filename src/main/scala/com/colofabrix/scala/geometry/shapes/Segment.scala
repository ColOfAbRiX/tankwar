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

package com.colofabrix.scala.geometry.shapes

import scala.annotation.tailrec
import com.colofabrix.scala.geometry.Shape
import com.colofabrix.scala.math.{ Vect, XYVect }

/**
  * A line segment.
  */
final case class Segment(p0: Vect, p1: Vect) extends Shape {

  /** Cohen-Sutherland Algorithm for segment clipping. */
  protected class CohenSutherland(viewport: Box) {
    // Outcodes definitions
    protected val NEUTRAL = 0x0
    protected val LEFT = 0x1
    protected val RIGHT = 0x2
    protected val TOP = 0x4
    protected val BOTTOM = 0x8

    /** Assigns an outcode to a point. */
    protected def outcode(p: Vect): Int = {
      (if (p.x < viewport.left) LEFT else NEUTRAL) |
        (if (p.x > viewport.right) RIGHT else NEUTRAL) |
        (if (p.y < viewport.bottom) BOTTOM else NEUTRAL) |
        (if (p.y > viewport.top) TOP else NEUTRAL)
    }

    /** Calculates the intersection of the segment with the border that it overlaps. */
    protected def intersection(outcode: Int, s: Segment): Vect = {
      if ((outcode & LEFT) == LEFT)
        XYVect(
          viewport.left,
          s.p0.y + (viewport.left - s.p0.x) * s.slope
        )
      else if ((outcode & RIGHT) == RIGHT)
        XYVect(
          viewport.right,
          s.p0.y + (viewport.right - s.p0.x) * s.slope
        )
      else if ((outcode & TOP) == TOP)
        XYVect(
          s.p0.x + (viewport.top - s.p0.y) / s.slope,
          viewport.top
        )
      else if ((outcode & BOTTOM) == BOTTOM)
        XYVect(
          s.p0.x + (viewport.bottom - s.p0.y) / s.slope,
          viewport.bottom
        )
      else
        throw new IllegalArgumentException("The outcode doesn't represent a segment to cut.")
    }

    /** Clip the given segment into a segment fully contained in a Box. */
    @tailrec
    final def clip(s: Segment): Option[Segment] = {
      val outcode0 = outcode(s.p0)
      val outcode1 = outcode(s.p1)

      // Both endpoints are in the viewport region: trivial accept.
      if (outcode0 == 0x0 && outcode1 == 0x0) Some(s)

      // Both endpoints share at least one non-visible region which implies that the line does not cross the visible
      // region: trivial reject.
      else if ((outcode0 & outcode1) != 0x0) None

      // Partial overlapping
      else
        clip(
          if (outcode0 != 0)
            Segment(intersection(outcode0, s), s.p1)
          else
            Segment(s.p0, intersection(outcode1, s))
        )
    }
  }

  /** Clip the segment into a segment fully contained in a Box. */
  def clip(frame: Box): Option[Segment] = new CohenSutherland(frame).clip(this)

  /** Slope of the segment. */
  val slope = (p1.y - p0.y) / (p1.x - p0.x)

  override def area: Double = 0.0

  override def move(v: Vect): Shape = Segment(p0 + v, p1 + v)

  override def scale(k: Double): Shape = ???

  override def toString = s"Segment($p0 -> $p1)"

  override def idFields: Seq[Any] = Seq(p0, p1)

  override def canEqual(a: Any): Boolean = a.isInstanceOf[Segment]
}