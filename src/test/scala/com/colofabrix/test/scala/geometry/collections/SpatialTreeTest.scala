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
import com.colofabrix.scala.geometry.collections.{ SpatialHash, SpatialTree }
import com.colofabrix.scala.geometry.shapes.{ Box, Circle }
import com.colofabrix.scala.math.XYVect
import com.colofabrix.test.scala.geometry.ShapeUtils
import com.colofabrix.test.scala.geometry.abstracts.SpatialSetTest

import scala.language.implicitConversions

/**
  * Unit testing specific for the [[SpatialHash]] class
  */
final class SpatialTreeTest extends SpatialSetTest[SpatialTree[Shape]] {

  private val _hSplit = 2
  private val _vSplit = 2
  private val _maxDepth = 2
  private val _splitSize = 2
  private val _bucketList = testArea.split( _hSplit, _vSplit )

  /**
    * Creates a new object of type T to test
    *
    * @param bounds  The area covered by the object
    * @param toSeq The toSeq to add to the list
    * @return A new instance of a SpatialSet[T]
    */
  override protected def newSpatialSet[U <: Shape]( bounds: Box, toSeq: List[U] ) =
    SpatialTree( _splitSize, _maxDepth, bounds, toSeq, _hSplit, _vSplit )

  //
  // General properties
  //

  "The max depth of the tree" must "not be exceeded" in {
    val smallestSplit = Math.pow( _splitSize.toDouble, _maxDepth.toDouble )
    val smallestUnit = Box( testArea.width / smallestSplit, testArea.height / smallestSplit )

    val shapes = List.fill( _splitSize * _maxDepth )( ShapeUtils.rndCircle( smallestUnit ) )
    val set = newSpatialSet( testArea, List.empty[Shape] )

    val result = shapes.foldLeft( set )( _ + _ )

    def depth( t: SpatialTree[Shape] ): Int =
      if ( t.children.isEmpty ) 0 else t.children.map( n ⇒ 1 + depth( n ) ).max

    depth( result ) <= _maxDepth should equal( true )
  }

  //
  // lookAround() testing specific of this implementation
  //

  "The lookAround member" must "return all the Shapes in the same bucket" in {
    val shapesPerBucket = 5

    val filledBuckets = for ( b ← _bucketList ) yield {
      List.fill( shapesPerBucket )( ShapeUtils.rndCircle( b ) )
    }

    val result = SpatialTree( 2, 1, testArea, filledBuckets.flatMap( x ⇒ x ).toList, _hSplit, _vSplit )

    for ( b ← filledBuckets; s ← b ) {
      result.lookAround( s ).size should equal( shapesPerBucket )
    }
  }

  "The lookAround member" must "not return any Shape from other buckets" in {
    val shapesPerBucket = 5

    val filledBuckets = for ( b ← _bucketList ) yield {
      List.fill( shapesPerBucket )( ShapeUtils.rndCircle( b ) )
    }

    val result = SpatialTree( 2, 1, testArea, filledBuckets.flatMap( x ⇒ x ).toList, _hSplit, _vSplit )

    for (
      b ← filledBuckets;
      s ← b;
      ob ← filledBuckets if ob != b;
      os ← ob
    ) {
      result.lookAround( s ).contains( os ) should equal( false )
    }
  }

  "The lookAround member" must "return the same Shape twice when the Shape overlaps buckets" in {
    val center = XYVect( testArea.width / _hSplit, testArea.height / _vSplit ) + testArea.bottomLeft

    val shape = new Circle( center, 10.0 )
    val checkShape1 = new Circle( center - 10.0, 5.0 )
    val checkShape2 = new Circle( center + 10.0, 5.0 )

    val result = newSpatialSet( testArea, List( shape ) )

    result.lookAround( checkShape1 ).contains( shape ) should equal( true )
    result.lookAround( checkShape2 ).contains( shape ) should equal( true )
  }

  //
  // children member
  //

  "The children member" must "remain empty when adding less or the same as  splitSize items" in {
    val set = newSpatialSet( testArea, List.empty[Shape] )
    val shapes = List.fill( _splitSize )( ShapeUtils.rndCircle( testArea ) )

    val result = shapes.foldLeft( set )( _ + _ )

    result.children.isEmpty should equal( true )
  }

  "The children member" must "be filled when adding more than splitSize items" in {
    val set = newSpatialSet( testArea, List.empty[Shape] )
    val shapes = List.fill( _splitSize + 1 )( ShapeUtils.rndCircle( testArea ) )

    val result = shapes.foldLeft( set )( _ + _ )

    result.children.isEmpty should equal( false )
  }

  "The children member" must "become empty when enough items are removed from them" in {
    val shapes1 = List.fill( _splitSize - 1 )( ShapeUtils.rndCircle( testArea ) )
    val shapes2 = List.fill( 2 )( ShapeUtils.rndCircle( testArea ) )

    val set = newSpatialSet( testArea, shapes1 ::: shapes2 )
    val result = shapes2.foldLeft( set )( _ - _ )

    result.children.isEmpty should equal( true )
  }

  //
  // toSeq member
  //

  "The toSeq member" must "contain the items added" in {
    val set = newSpatialSet( testArea, List.empty[Shape] )
    val shapes = List.fill( 2 * _splitSize )( ShapeUtils.rndCircle( testArea ) )

    val result = shapes.foldLeft( set )( _ + _ )

    result.toSeq.size should equal( shapes.size )
  }

  "The toSeq member" must "fill when enough items are removed from the children" in {
    val shapes1 = List.fill( _splitSize - 1 )( ShapeUtils.rndCircle( testArea ) )
    val shapes2 = List.fill( 2 )( ShapeUtils.rndCircle( testArea ) )
    val set = newSpatialSet( testArea, shapes1 ::: shapes2 )

    val result = shapes2.foldLeft( set )( _ - _ )

    result.toSeq.size should equal( shapes1.size )
  }
}
