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

package com.colofabrix.test.scala.geometry

import com.colofabrix.scala.geometry.abstracts.SpatialSet
import com.colofabrix.scala.geometry.shapes.Box
import com.colofabrix.scala.math.Vector2D
import org.scalatest.{ Matchers, WordSpec }

/**
  *
  */
abstract class SpatialSetBaseTest[T <: SpatialSet[T]] extends WordSpec with Matchers {

  /**
    * Default Box object used to define
    */
  val defaultBox = new Box( Vector2D.origin, Vector2D.new_xy(400, 300) )
  val offsetBox = new Box( Vector2D.origin, Vector2D.new_xy(400, 300) ).move( Vector2D.new_xy(100, 100) )

  def getNewT( bounds: Box, objects: List[T] ): SpatialSet[T]

  "The constructor" must {

    "create an empty Set" in {
    }

    "create a Set with one element" in {}

    "create a Set with multiple elements" in {}

  }

}
