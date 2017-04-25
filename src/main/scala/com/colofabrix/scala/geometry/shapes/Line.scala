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
  * An infinite line
  */
case class Line(
  normal: Vect,
  distance: Double
) extends Shape {

  /** Drawing normal. This normal has always positive components. */
  val dNormal: Vect = XYVect(normal.x.abs, normal.y.abs)

  /** Known point on the line. */
  val p = dNormal * distance

  /** Parameter "m" of the line equation y = mx + q */
  val m = {
    val phi = dNormal.ϑ - Math.PI / 2.0

    // Use precise values for known points
    if( phi ~== Math.PI / 2.0 )
      Double.PositiveInfinity
    else if( phi ~== -Math.PI / 2.0 )
      Double.NegativeInfinity
    else if( (phi ~== 0.0) || (phi ~== Math.PI) )
      0.0
    else
      Math.tan(phi)
  }

  /** Parameter "q" of the line equation y = mx + q */
  val q = p.y - m * p.x

  /** Drawing equation. */
  def dEquation(x: Double): Vect = XYVect(x, m * x + q)

  override val area: Double = 0.0

  override def moveOf(where: Vect): Line = ???

  override def moveTo(where: Vect): Line = ???

  override def scale(k: Double): Shape = this

  override def toString = s"Line((${normal.x }, ${normal.y }) / $distance)"
}