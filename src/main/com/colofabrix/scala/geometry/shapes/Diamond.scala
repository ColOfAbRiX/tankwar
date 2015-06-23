/*
 * Copyright (C) 2015 Fabrizio Colonna
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

import com.colofabrix.scala.geometry.abstracts.{ Container, Shape }
import com.colofabrix.scala.math.Vector2D

/**
 * TODO: Complete the class
 * @see http://geomalgorithms.com/a08-_containers.html
 */
class Diamond(v: Seq[Vector2D]) extends ConvexPolygon(v) with Container {
  /**
   * Determines if the container fully contain a Shape
   *
   * @param s The shape to check
   * @return true if the container fully contain the other shape. Boundaries are included in the container
   */
  override def contains( s: Shape ): Boolean = ???
}
