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

package com.colofabrix.test.scala.geometry.abstracts

import com.colofabrix.scala.geometry.abstracts.{ Shape, SpatialTree }
import com.colofabrix.scala.geometry.shapes.Box
import com.colofabrix.test.scala.geometry.ShapeUtils

/**
  * Abstract class to define tests for [[SpatialTree]] implementations
  */
trait SpatialTreeTest[T <: SpatialTree[Shape]] extends SpatialSetTest[T] {
  protected val splitSize = 2
  protected val maxDepth = 3

  /**
    * Checks the size of a Set
    *
    * @param result   The set to validate
    * @param expected The expected number of results
    * @tparam A
    */
  protected def validateSize[A <: SpatialTree[Shape]]( result: A, expected: Int ): Unit = {
    super.validateSize( result, expected )
    result.objects.size + result.nodes.foldLeft( 0 )( _ + _.size ) should equal( expected )
  }

  "The max depth of the tree" must "not be exceeded" in {
    val smallestSplit = Math.pow( splitSize.toDouble, maxDepth.toDouble )
    val smallestUnit = Box( testArea.width / smallestSplit, testArea.height / smallestSplit )

    val shapes = List.fill( splitSize * maxDepth )( ShapeUtils.rndCircle( smallestUnit ) )
    val set: SpatialTree[Shape] = newSpatialSet( testArea, List.empty[Shape] )

    val result = shapes.foldLeft( set )( _ + _ )

    def depth( t: SpatialTree[Shape] ): Int =
      if( t.nodes.isEmpty ) 1 else t.nodes.map( n => 1 + depth( n ) ).max

    depth( result ) <= maxDepth should equal( true )
  }

  //
  // ++ member
  //

  "The add-list member" must "add more than one Shape at the time" in {
    val shapes = List.fill( 5 )( ShapeUtils.rndCircle( testArea ) )
    val set = newSpatialSet( testArea, List.empty[Shape] )

    val result = set ++ shapes

    validateSize( result, shapes.size )
  }

  "The add-list member" must "add duplicate elements" in {
    val set = newSpatialSet( testArea, List.empty[Shape] )
    val shapes = List.fill( 2 )( ShapeUtils.rndCircle( testArea ) )

    val intermediateResult = set ++ shapes
    val result = intermediateResult ++ shapes

    validateSize( result, 2 * shapes.size )
  }
}