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
import com.colofabrix.scala.geometry.shapes.Box
import com.colofabrix.scala.math.{ Vect, XYVect }
import com.colofabrix.test.scala.geometry.abstracts.{ ShapeUtils, SpatialSetBaseTest }

import scala.language.implicitConversions

/**
  * Unit testing specific for the [[SpatialHash]] class
  */
final class SpatialHashTest extends SpatialSetBaseTest[SpatialHash[Shape]] {

  private val _hSplit = 2
  private val _vSplit = 2

  /**
    * This has been copied from [[SpatialHash.apply()]]
    */
  private val _bucketList = for( j ← 0 until _vSplit; i ← 0 until _hSplit ) yield {
    val templateBox = Box(
      Vect.origin,
      XYVect( defaultBox.width / _hSplit, defaultBox.height / _vSplit )
    )

    Box.getAsBox( templateBox.move( XYVect( templateBox.width * i, templateBox.height * j ) ) )
  }

  /**
    * Creates a new object of type T to test
    *
    * @param bounds  The area covered by the object
    * @param objects The objects to add to the list
    * @return A new instance of a SpatialSet[T]
    */
  override protected def getNewSpatialSet( bounds: Box, objects: List[Shape] ): SpatialHash[Shape] =
    SpatialHash( bounds, objects, 2, 2 )

  //
  // lookAround() testing specific of this implementation
  //

  "The lookAround member" must "return all the Shapes in the same bucket" in {
    val shapesPerBucket = 5

    val filledBuckets = for( b <- _bucketList ) yield {
      List.fill( shapesPerBucket )( ShapeUtils.randomCircle( b ) )
    }

    val set1 = getNewSpatialSet( defaultBox, filledBuckets.flatMap( x => x ).toList )

    for( b <- filledBuckets; s <- b ) {
      set1.lookAround( s ).size should equal(shapesPerBucket)
    }
  }

  "The lookAround member" must "not return any Shape from other buckets" in {
    val shapesPerBucket = 5

    val filledBuckets = for( b <- _bucketList ) yield {
      List.fill( shapesPerBucket )( ShapeUtils.randomCircle( b ) )
    }

    val set1 = getNewSpatialSet( defaultBox, filledBuckets.flatMap( x => x ).toList )

    for( b <- filledBuckets;
         s <- b;
         ob <- filledBuckets if ob != b;
         os <- ob ) {
      set1.lookAround( s ).contains( os ) should equal(false)
    }
  }
}
