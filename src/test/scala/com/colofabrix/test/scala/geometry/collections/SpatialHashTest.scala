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
import com.colofabrix.scala.geometry.collections.SpatialHash
import com.colofabrix.scala.geometry.shapes.{ Box, Circle }
import com.colofabrix.scala.math.XYVect
import com.colofabrix.test.scala.geometry.ShapeUtils
import com.colofabrix.test.scala.geometry.abstracts.SpatialSetTest

import scala.language.implicitConversions

/**
  * Unit testing specific for the [[SpatialHash]] class
  */
final class SpatialHashTest extends SpatialSetTest[SpatialHash[Shape]] {

  private val _hSplit = 2
  private val _vSplit = 2
  private val _bucketList = testArea.split( _hSplit, _vSplit )

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
    SpatialHash( bounds, objects, _hSplit, _vSplit )

  //
  // lookAround() testing specific of this implementation
  //

  "The lookAround member" must "return all the Shapes in the same bucket" in {
    val shapesPerBucket = 5

    val filledBuckets = for( b <- _bucketList ) yield {
      List.fill( shapesPerBucket )( ShapeUtils.rndCircle( b ) )
    }

    val set1 = newSpatialSet( testArea, filledBuckets.flatMap( x => x ).toList )

    for( b <- filledBuckets; s <- b ) {
      set1.lookAround( s ).size should equal( shapesPerBucket )
    }
  }

  "The lookAround member" must "not return any Shape from other buckets" in {
    val shapesPerBucket = 5

    val filledBuckets = for( b <- _bucketList ) yield {
      List.fill( shapesPerBucket )( ShapeUtils.rndCircle( b ) )
    }

    val set1 = newSpatialSet( testArea, filledBuckets.flatMap( x => x ).toList )

    for( b <- filledBuckets;
         s <- b;
         ob <- filledBuckets if ob != b;
         os <- ob ) {
      set1.lookAround( s ).contains( os ) should equal( false )
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
}
