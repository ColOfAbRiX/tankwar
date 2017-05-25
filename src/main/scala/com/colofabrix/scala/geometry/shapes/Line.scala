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

import com.colofabrix.scala.geometry.Shape
import com.colofabrix.scala.math.{ DoubleWithAlmostEquals, Vect, XYVect }

/**
  * An infinite line.
  */
class Line private (
    val normal: Vect,
    val distance: Double
) extends Shape {

  require(normal != Vect.zero, "A line must be defined with a non-zero normal.")

  /** Parameter "m" of the line equation y = mx + q */
  val m = -(normal.x / normal.y)

  /** Parameter "q" of the line equation y = mx + q */
  val q = -distance / normal.y

  /** Distance of a point from the line. */
  def distance(v: Vect): Double = ((normal ∙ v) + distance).abs

  /** Line equation. */
  def equation(x: Double): Vect = XYVect(x, m * x + q)

  /** Known point on the line. */
  val p = equation(0.0)

  /** Clip the line into a segment fully contained in a Box. */
  def clip(frame: Box): Option[Segment] = {
    val seg = if (m ==~ 0.0)
      Segment(
        XYVect(frame.left, p.y),
        XYVect(frame.right, p.y)
      )
    else if (m.abs ==~ Double.PositiveInfinity)
      Segment(
        XYVect(distance, frame.bottom),
        XYVect(distance, frame.top)
      )
    else
      Segment(
        equation(frame.left),
        equation(frame.right)
      )

    seg.clip(frame)
  }

  override val area: Double = 0.0

  override def move(where: Vect): Line = Line(normal, distance + (normal ∙ where))

  override def scale(k: Double): Shape = this

  override def toString = s"Line((${normal.x}, ${normal.y}) / $distance)"

  override def idFields: Seq[Any] = Seq(normal, distance)

  override def canEqual(a: Any): Boolean = a.isInstanceOf[Line]
}

object Line {
  def apply(normal: Vect, distance: Double): Line = new Line(normal.v, distance)

  def apply(m: Double, q: Double): Line = {
    val k = Math.sqrt(m * m + 1)
    Line(XYVect(m / k, -1 / k), -q / k)
  }
}