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

import com.colofabrix.scala.geometry.shapes.{ Box, Seg }
import com.colofabrix.scala.math.{ Vect, XYVect }
import com.colofabrix.test.scala.geometry.abstracts.ShapeTest

/**
  * Testing class for [[Seg]]
  */
class SegTest extends ShapeTest[Seg] {
  /**
    * Creates a new object of type T to test
    *
    * @param bounds The area covered by the object
    * @return A new instance of a SpatialSet[T]
    */
  override protected def testShape( bounds: Box ): Seg = Seg( bounds.topRight, bounds.bottomLeft )

  /**
    * Creates a new object of type T to test that must have at least one
    * point of its boundary known. The known point will lie on the right
    * edge of boundary and a `touch` distance from the topRight vertex.
    *
    * @param bounds The area covered by the object
    * @param touch  A parameter between 0.0 and 1.0 that tells the desired point on the right edge of bounds
    * @return A tuple with 1) a new instance of a T and 2) The point that must be touched
    */
  override protected def testShape( bounds: Box, touch: Double ): (Seg, Vect) = (
    Seg( bounds.topRight, bounds.bottomRight ),
    bounds.topRight + (bounds.bottomRight - bounds.topRight) * touch
    )

  //
  // area member
  //

  "The area member" must "be always zero" in {
    testShape( Box( 10, 20 ) ).area should equal( 0.0 )
  }

  //
  // vect member
  //

  "The vect member" must "represent the distance vector from the two endpoints" in {
    val v0 = XYVect( 10, 10 )
    val v1 = XYVect( 120, 210 )

    val test = Seg( v0, v1 )

    test.vect should equal( v1 - v0 )
  }

  //
  // length member
  //

  "The length member" must "be the length of the vect member" in {
    val v0 = XYVect( 10, 10 )
    val v1 = XYVect( 120, 210 )

    val test = Seg( v0, v1 )

    test.length should equal( (v1 - v0).ρ )
  }

  //
  // isX and isY members
  //

  "The isX and isY members" must "return false when the segment is not  parallel to any axis" in {
    val test = Seg( XYVect( 10, 20 ), XYVect( 30, 40 ) )
    test.isX should equal( false )
    test.isY should equal( false )
  }

  "The isX and isY members" must "each return true when the segment is parallel to the X or the Y axe" in {
    val testX = Seg( XYVect( 10, 10 ), XYVect( 120, 10 ) )
    testX.isX should equal( true )

    val testY = Seg( XYVect( 10, 10 ), XYVect( 10, 210 ) )
    testY.isY should equal( true )
  }

}