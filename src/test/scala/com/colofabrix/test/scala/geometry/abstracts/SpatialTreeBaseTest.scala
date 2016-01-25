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

import com.colofabrix.scala.geometry.abstracts.{ Shape, SpatialSet, SpatialTree }
import com.colofabrix.scala.geometry.shapes.Box

/**
  * Abstract class to define tests for [[SpatialTree]] implementations
  */
trait SpatialTreeBaseTest[+T <: SpatialTree[Shape]] extends SpatialSetBaseTest[SpatialTree[Shape]] {
  protected val splitSize = 2
  protected val maxDepth = 3

  "The max depth of the tree" must "not be exceeded" in {
    val smallestSplit = Math.pow( splitSize, maxDepth )
    val smallestUnit = Box( defaultBox.width / smallestSplit, defaultBox.height / smallestSplit )

    val shapes = List.fill( splitSize * maxDepth )( ShapeUtils.randomCircle( smallestUnit ) )
    val set: SpatialSet[Shape] = getNewSpatialSet( defaultBox, List.empty[Shape] )

    val result = shapes.foldLeft( set )( _ + _ ) match {
      case st: SpatialTree[Shape] => st
      case _ => fail( )
    }

    def depth( t: SpatialTree[Shape] ): Int =
      if( t.nodes.isEmpty ) 1 else t.nodes.map( n => 1 + depth( n ) ).max

    depth( result ) should equal( maxDepth )
  }

  //
  // nodes member
  //

  "The nodes member" must "remain empty when adding less than splitSize items" in {
    val set: SpatialSet[Shape] = getNewSpatialSet( defaultBox, List.empty[Shape] )
    val shapes = List.fill( splitSize - 1 )( ShapeUtils.randomCircle( defaultBox ) )

    val result = shapes.foldLeft( set )( _ + _ ) match {
      case st: SpatialTree[Shape] => st
      case _ => fail( )
    }

    result.nodes.isEmpty should equal( true )
  }

  "The nodes member" must "be filled when adding more than splitSize items" in {
    val set: SpatialSet[Shape] = getNewSpatialSet( defaultBox, List.empty[Shape] )
    val shapes = List.fill( splitSize + 1 )( ShapeUtils.randomCircle( defaultBox ) )

    val result = shapes.foldLeft( set )( _ + _ ) match {
      case st: SpatialTree[Shape] => st
      case _ => fail( )
    }

    result.nodes.isEmpty should equal( false )
  }

  "The nodes member" must "become empty when enough items are removed from them" in {
    val shapes1 = List.fill( splitSize - 1 )( ShapeUtils.randomCircle( defaultBox ) )
    val shapes2 = List.fill( 2 )( ShapeUtils.randomCircle( defaultBox ) )
    val set: SpatialSet[Shape] = getNewSpatialSet( defaultBox, shapes1 ::: shapes2 )

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
    val set: SpatialSet[Shape] = getNewSpatialSet( defaultBox, List.empty[Shape] )
    val shapes = List.fill( splitSize - 1 )( ShapeUtils.randomCircle( defaultBox ) )

    val result = shapes.foldLeft( set )( _ + _ ) match {
      case st: SpatialTree[Shape] => st
      case _ => fail( )
    }

    result.objects.size should equal( shapes.size )
  }

  "The objects member" must "empty when more than splitSize items are added" in {
    val set: SpatialSet[Shape] = getNewSpatialSet( defaultBox, List.empty[Shape] )
    val shapes = List.fill( splitSize + 1 )( ShapeUtils.randomCircle( defaultBox ) )

    val result = shapes.foldLeft( set )( _ + _ ) match {
      case st: SpatialTree[Shape] => st
      case _ => fail( )
    }

    result.objects.size should equal( 0 )
  }

  "The objects member" must "fill when enough items are removed from the subnodes" in {
    val shapes1 = List.fill( splitSize - 1 )( ShapeUtils.randomCircle( defaultBox ) )
    val shapes2 = List.fill( 2 )( ShapeUtils.randomCircle( defaultBox ) )
    val set: SpatialSet[Shape] = getNewSpatialSet( defaultBox, shapes1 ::: shapes2 )

    val result = shapes2.foldLeft( set )( _ - _ ) match {
      case st: SpatialTree[Shape] => st
      case _ => fail( )
    }

    result.objects.size should equal( shapes1.size )
  }

  //
  // ++ member
  //

  "The add-list member" must "add more than one Shape at the time" in {
    val shapes = List.fill( 5 )( ShapeUtils.randomCircle( defaultBox ) )
    val set = getNewSpatialSet( defaultBox, List.empty[Shape] )

    val result = set ++ shapes

    result.isEmpty should equal( false )
    result.size should equal( shapes.size )
    result.toList.length should equal( shapes.size )
  }

  "The add-list member" must "not add duplicate elements" in {
    val set = getNewSpatialSet( defaultBox, List.empty[Shape] )
    val shapes = List.fill( 5 )( ShapeUtils.randomCircle( defaultBox ) )

    val intermediateResult = set ++ shapes
    val result = intermediateResult ++ shapes

    result.isEmpty should equal( false )
    result.size should equal( shapes.size )
    result.toList.length should equal( shapes.size )
  }
}