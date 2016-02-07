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

package com.colofabrix.test.scala.geometry.collections

import com.colofabrix.scala.geometry.abstracts.Shape
import com.colofabrix.scala.geometry.collections.LinkedQuadtree
import com.colofabrix.scala.geometry.shapes.Box
import com.colofabrix.test.scala.geometry.abstracts.SpatialTreeTest

/**
  *
  */
class LinkedQuadtreeTest extends SpatialTreeTest[LinkedQuadtree[Shape]] {
  private val _splitSize = 2
  private val _maxDepth = 3

  /**
    * Creates a new object of type T to test
    *
    * @param bounds  The area covered by the object
    * @param objects The objects to add to the list
    * @return A new instance of a SpatialSet[T]
    */
  override
  protected
  def newSpatialSet[U <: Shape]( bounds: Box, objects: List[U] ) =
    LinkedQuadtree( bounds, objects, _splitSize, _maxDepth )
}
