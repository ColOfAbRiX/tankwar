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

import com.colofabrix.scala.geometry.abstracts.Shape
import com.colofabrix.scala.geometry.shapes.{ Box, Circle, Polygon }
import com.colofabrix.scala.math.{ Vect, XYVect }
import org.scalatest.{ FlatSpec, Matchers }

/**
  *
  */
trait ShapeTest[T <: Shape] extends FlatSpec with Matchers {

  /**
    * Creates a new object of type T to test
    *
    * @param bounds The area covered by the object
    * @return A new instance of a SpatialSet[T]
    */
  protected def testShape( bounds: Box ): T

  //
  // intersects() member
  //

  "The intersects member" must "return true when given a Shape fully inside the test Shape" in {
    val where = Box( 100, 200 )
    val test = testShape( where )

    val testShapes = Seq(
      Box( XYVect( 20, 20 ), XYVect( 80, 80 ) ),
      Circle( where.center, 20 ),
      new Polygon( XYVect( 10, 10 ) :: XYVect( 32, 16 ) :: XYVect( 21, 40 ) :: XYVect( 1, 5 ) :: Nil )
    )

    testShapes foreach {
      test.intersects( _ ) should equal( true )
    }
  }

  "The intersects member" must "return true when given a Shape partially inside the test Shape" in {
    val where = Box( 100, 200 )
    val test = testShape( where )

    val testShapes = Seq(
      Box( XYVect( 20, 20 ), XYVect( 180, 280 ) ),
      Circle( where.center, 120 ),
      new Polygon( XYVect( 10, 10 ) :: XYVect( 132, 316 ) :: XYVect( 121, 40 ) :: XYVect( 1, 5 ) :: Nil )
    )

    testShapes foreach {
      test.intersects( _ ) should equal( true )
    }
  }

  "The intersects member" must "return false when given a Shape completely outside the test Shape" in {
    val where = Box( 100, 200 )
    val test = testShape( where )

    val testShapes = Seq(
      Box( XYVect( 120, 220 ), XYVect( 180, 280 ) ),
      Circle( where.center + XYVect( 100, 200 ), 20 ),
      new Polygon( XYVect( 110, 210 ) :: XYVect( 132, 216 ) :: XYVect( 121, 240 ) :: XYVect( 101, 105 ) :: Nil )
    )

    testShapes foreach {
      test.intersects( _ ) should equal( false )
    }
  }

  //
  // contains() member
  //

  "The contains member" must "return true when given a Vect inside or on the boundary of the Shape" in {
    val insidePoint1 = XYVect( 5, 5 )
    val insidePoint2 = XYVect( 10, 5 )
    val test = testShape( Box( 10, 10 ) )

    test.contains( insidePoint1 ) should equal( true )
    test.contains( insidePoint2 ) should equal( true )
  }

  "The contains member" must "return false when given a Vect outside the Shape" in {
    val outsidePoint = XYVect( 20, 20 )
    val test = testShape( Box( 10, 10 ) )

    test.contains( outsidePoint ) should equal( false )
  }

  //
  // move() member
  //

  "The move member" must "move the Shape of the given Vect" in {
    val test = testShape( Box( 10, 20 ) )
    val reference = testShape( Box( XYVect( 15, 15 ), XYVect( 25, 35 ) ) )

    test.move( XYVect( 15, 15 ) ) == reference should equal( true )
    test.move( Vect.zero ) == test should equal( true )
  }

  //
  // equal()
  //

  "The equal member" must "return true when given instances that represent the same Box" in {
    val test1 = testShape( Box( XYVect( 15, 15 ), XYVect( 25, 35 ) ) )
    val test2 = testShape( Box( XYVect( 15, 15 ), XYVect( 25, 35 ) ) )

    test1 == test1 should equal( true )
    test1 == test2 should equal( true )
  }

  "The equal member" must "return false when given different Boxes" in {
    val test1 = testShape( Box( XYVect( 15, 15 ), XYVect( 25, 35 ) ) )
    val test2 = testShape( Box( XYVect( 1, 5 ), XYVect( 5, 3 ) ) )

    test1 == test2 should equal( false )
  }
}
