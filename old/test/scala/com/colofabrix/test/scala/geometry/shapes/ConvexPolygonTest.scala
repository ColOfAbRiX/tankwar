/*
 * Copyright (C) 2016 Fabrizio
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

package com.colofabrix.test.scala.geometry.shapes

import com.colofabrix.scala.geometry.shapes.{ Box, ConvexPolygon, Polygon }
import com.colofabrix.scala.math.Vect
import com.colofabrix.test.scala.geometry.abstracts.ShapeTest

/**
  * Unit testing for Polygons
  */
class ConvexPolygonTest extends ShapeTest[Polygon] {
  /**
    * Creates a new object of type T to test
    *
    * @param bounds The area covered by the object
    * @return A new instance of a T
    */
  override protected def testShape( bounds: Box ): Polygon = new ConvexPolygon( bounds.vertices )

  /**
    * Creates a new object of type T to test that must have at least one
    * point of its boundary known. The known point will lie on the right
    * edge of boundary and a `touch` distance from the topRight vertex.
    *
    * @param bounds The area covered by the object
    * @param touch  A parameter between 0.0 and 1.0 that tells the desired point on the right edge of bounds
    * @return A tuple with 1) a new instance of a T and 2) The point that must be touched
    */
  override protected def testShape( bounds: Box, touch: Double ): (Polygon, Vect) =
    (new ConvexPolygon( bounds.vertices ), bounds.topRight + (bounds.bottomRight - bounds.topRight) * touch)
}