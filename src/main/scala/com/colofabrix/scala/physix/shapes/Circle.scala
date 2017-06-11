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

package com.colofabrix.scala.physix.shapes

import com.colofabrix.scala.math.{ DoubleWithAlmostEquals, Vect }

/**
  * Circle shape
  */
final case class Circle(
    center: Vect,
    radius: Double
) extends Shape {

  require(radius >~ 0.0, "The circle must have a non-zero radius.")

  override val area: Double = Math.PI * Math.pow(radius, 2.0)

  override def move(where: Vect): Circle = copy(center = center + where)

  override def scale(k: Double): Shape = copy(radius = radius * k)

  override def toString = s"Circle(c=$center, r=$radius)"

  override def idFields: Seq[Any] = Seq(center, radius)

  override def canEqual(a: Any): Boolean = a.isInstanceOf[Circle]
}