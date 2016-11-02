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

import com.colofabrix.scala.geometry.shapes._
import com.colofabrix.scala.math.{ Vect, XYVect }
import com.colofabrix.test.scala.geometry.abstracts.ShapeTest

/**
  * Unit testing for Circle Shape
  *
  * Created by Fabrizio on 11/01/2015.
  */
class CircleTest extends ShapeTest[Circle] {

  /**
    * Creates a new object of type T to test
    *
    * @param bounds The area covered by the object
    * @return A new instance of a SpatialSet[T]
    */
  override protected def testShape( bounds: Box ): Circle = Circle( bounds.center, Math.min( bounds.width, bounds.height ) / 2.0 )

  /**
    * Creates a new object of type T to test that must have at least one
    * point of its boundary known. The known point will lie on the right
    * edge of boundary and a `touch` distance from the topRight vertex.
    *
    * @param bounds The area covered by the object
    * @param touch  A parameter between 0.0 and 1.0 that tells the desired point on the right edge of bounds
    * @return A tuple with 1) a new instance of a T and 2) The point that must be touched
    */
  override protected def testShape( bounds: Box, touch: Double ): (Circle, Vect) = {
    require( touch >= 0.0 && touch <= 1.0 )

    val maxRadius = Math.min( bounds.width, bounds.height )
    val validBounds = Box( bounds.center, maxRadius, maxRadius )

    if( touch <= 0.5 ) {
      val possibleCenters = validBounds.bottomLeft - validBounds.topRight
      val touchPoint = (validBounds.bottomRight - validBounds.topRight) * touch

      val center = possibleCenters * touch
      val radius = (touchPoint - center).ρ

      Tuple2(
        Circle( validBounds.topRight + center, radius ),
        validBounds.topRight + touchPoint
      )
    }
    else {
      val possibleCenters = validBounds.bottomRight - validBounds.topLeft
      val touchPoint = (validBounds.bottomRight - validBounds.topRight) * touch

      val center = possibleCenters * touch + (validBounds.topLeft - validBounds.topRight)
      val radius = (touchPoint - center).ρ

      Tuple2(
        Circle( validBounds.topRight + center, radius ),
        validBounds.topRight + touchPoint
      )
    }
  }

  "The area member" must "be a valid area" in {
    val test = Circle( XYVect( 10, 10 ), 100 )
    test.area should equal( Math.PI * Math.pow( 100, 2.0 ) )
  }

  "The fromArea member" must "create a Circle given its area" in {
    val test = Circle.fromArea( XYVect( 10, 10 ), Math.PI * Math.pow( 100, 2.0 ) )
    test.radius should equal( 100 )
  }

  //
  // boundaryDistance() member
  //

  "The member boundaryDistance" must "return a non-zero distance when given a Vect inside the Circle" in {
    val test = Circle( XYVect( 10, 10 ), 100 )
    val expDistance = XYVect( test.radius / 2.0, test.radius / 2.0 )
    val distance = test.boundaryDistance( test.center + (expDistance.v * test.radius) - expDistance )

    distance._1 should equal( expDistance )
  }

  "The member boundaryDistance" must "return a non-zero distance when given a Seg inside the Circle" in {
    val test = Circle( XYVect( 100, 150 ), 100 )

    val expDistance = XYVect( test.radius / 2.0, 0.0 )
    val expPoint = test.center + expDistance.v * test.radius

    val reference = Seg(
      test.center + XYVect( 0.0, test.radius ) + expDistance,
      test.center + XYVect( 0.0, -test.radius ) + expDistance
    )
    val distance = test.boundaryDistance( reference )

    distance._1 should equal( expDistance )
    distance._2 should equal( expPoint )
  }

  //
  // Companion object bestFit member
  //

  "The bestFit member" must "find the minimum-area Box that contains a Shape" in {
    testShapesSet( Box( 100, 200 ) ).foreach { s ⇒
      Circle.bestFit( s ).contains( s ) should equal( true )
    }
  }

}