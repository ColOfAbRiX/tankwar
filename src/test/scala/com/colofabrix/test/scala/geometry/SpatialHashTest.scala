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

import com.colofabrix.scala.geometry.abstracts.Shape
import com.colofabrix.scala.geometry.collections.SpatialHash
import com.colofabrix.scala.geometry.shapes.{ Box, Circle }
import com.colofabrix.scala.math.Vector2D
import org.scalatest.{ Matchers, WordSpec }

import scala.language.implicitConversions

/**
 *
 */
class SpatialHashTest extends WordSpec with Matchers {

  val arena = Box( Vector2D.origin, Vector2D.new_xy( 100, 100 ) )

  "spatialHash" in {
    val circle1 = Circle( Vector2D.new_xy( 10, 10 ), 5 )
    val circle2 = Circle( Vector2D.new_xy( 80, 30 ), 12 )
    val circle3 = Circle( Vector2D.new_xy( 30, 70 ), 20 )
    val elements = List[Shape]( circle1, circle2, circle3 )

    val newIndexer = SpatialHash[Shape]( arena, elements, 4, 4 )

    println( newIndexer.buckets )
    newIndexer.buckets
  }

}
