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

import com.colofabrix.scala.geometry.abstracts.{ Shape, SpatialSet, SpatialTree }
import com.colofabrix.scala.geometry.collections.LeafQuadtree
import com.colofabrix.scala.geometry.shapes.Box
import com.colofabrix.test.scala.geometry.abstracts.{ ShapeUtils, SpatialTreeBaseTest }

/**
  * Unit testing specific for the [[LeafQuadtree]] class
  */
class LeafQuadtreeTest extends SpatialTreeBaseTest[LeafQuadtree[Shape]] {

  /**
    * Creates a new object of type T to test
    *
    * @param bounds  The area covered by the object
    * @param objects The objects to add to the list
    * @return A new instance of a SpatialSet[T]
    */
  override
  protected
  def getNewSpatialSet[U <: Shape]( bounds: Box, objects: List[U] ) =
    LeafQuadtree( bounds, objects, splitSize, maxDepth )

  //
  // nodes member
  //

  "The nodes member" must "remain empty when adding less than splitSize items" in {
    val set = getNewSpatialSet( testArea, List.empty[Shape] )
    val shapes = List.fill( splitSize - 1 )( ShapeUtils.rndCircle( testArea ) )

    val result = shapes.foldLeft( set )( _ + _ )

    result.nodes.isEmpty should equal( true )
  }

  "The nodes member" must "be filled when adding more than splitSize items" in {
    val set = getNewSpatialSet( testArea, List.empty[Shape] )
    val shapes = List.fill( splitSize + 1 )( ShapeUtils.rndCircle( testArea ) )

    val result = shapes.foldLeft( set )( _ + _ )

    result.nodes.isEmpty should equal( false )
  }

  "The nodes member" must "become empty when enough items are removed from them" in {
    val shapes1 = List.fill( splitSize - 1 )( ShapeUtils.rndCircle( testArea ) )
    val shapes2 = List.fill( 2 )( ShapeUtils.rndCircle( testArea ) )

    val set: SpatialSet[Shape] = getNewSpatialSet( testArea, shapes1 ::: shapes2 )
    val result = shapes2.foldLeft( set )( _ - _ ) match {
      case st: SpatialTree[Shape] => st
      case _ => fail( )
    }

    result.nodes.isEmpty should equal( true )
  }

  //
  // objects member
  //

  "The objects member" must "fill when less than splitSize items are added" in {
    val set: SpatialSet[Shape] = getNewSpatialSet( testArea, List.empty[Shape] )
    val shapes = List.fill( splitSize - 1 )( ShapeUtils.rndCircle( testArea ) )

    val result = shapes.foldLeft( set )( _ + _ ) match {
      case st: SpatialTree[Shape] => st
      case _ => fail( )
    }

    result.objects.size should equal( shapes.size )
  }

  "The objects member" must "empty when more than splitSize items are added" in {
    val set: SpatialSet[Shape] = getNewSpatialSet( testArea, List.empty[Shape] )
    val shapes = List.fill( splitSize + 1 )( ShapeUtils.rndCircle( testArea ) )

    val result = shapes.foldLeft( set )( _ + _ ) match {
      case st: SpatialTree[Shape] => st
      case _ => fail( )
    }

    result.objects.size should equal( 0 )
  }

  "The objects member" must "fill when enough items are removed from the subnodes" in {
    val shapes1 = List.fill( splitSize - 1 )( ShapeUtils.rndCircle( testArea ) )
    val shapes2 = List.fill( 2 )( ShapeUtils.rndCircle( testArea ) )
    val set: SpatialSet[Shape] = getNewSpatialSet( testArea, shapes1 ::: shapes2 )

    val result = shapes2.foldLeft( set )( _ - _ ) match {
      case st: SpatialTree[Shape] => st
      case _ => fail( )
    }

    result.objects.size should equal( shapes1.size )
  }

}