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

package com.colofabrix.scala.geometry.shapes

import com.colofabrix.scala.math.{ Vect, XYVect }

/**
  * A Canvas is reverse Box
  */
class Canvas protected(bottomLeft: Vect, topRight: Vect) extends Box(bottomLeft, topRight) {

  override def toString = s"Canvas($bottomLeft -> $topRight)"

}

object Canvas {
  /** Constructor that uses width, height and starts the box at the origin of the axis. */
  def apply(width: Double, height: Double): Canvas = new Canvas(Vect.zero, XYVect(width, height))
}
