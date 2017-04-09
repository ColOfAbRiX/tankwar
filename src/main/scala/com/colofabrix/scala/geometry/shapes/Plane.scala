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
import com.colofabrix.scala.math.Vect

/**
  * Plane shape
  */
case class Plane(
  normal: Vect,
  distance: Double
) extends Shape {

  lazy
  override
  val area: Double = 0.0

  override
  def moveOf(where: Vect): Plane = ???

  override
  def moveTo(where: Vect): Plane = ???

  override
  def toString = s"Plane((${normal.x }, ${normal.y }) / $distance)"
}