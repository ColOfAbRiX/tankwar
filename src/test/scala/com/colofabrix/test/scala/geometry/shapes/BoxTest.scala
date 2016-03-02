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
import com.colofabrix.test.scala.geometry.ShapeUtils
import com.colofabrix.test.scala.geometry.abstracts.ShapeTest

/**
  * Unit testing for OrtoRectangles
  */
class BoxTest extends ShapeTest[Box] {

  private val _hSplit = 2
  private val _vSplit = 3

  /**
    * Creates a new object of type T to test
    *
    * @param bounds The area covered by the object
    * @return A new instance of a SpatialSet[T]
    */
  override protected def testShape( bounds: Box ): Box = bounds

  //
  // Width, height, center, origin, topRight/bottomLeft members
  //

  "The width member" must "be the X-distance between the opposite corners" in {
    Box( 10, 20 ).width should equal( 10 )
  }

  "The height member" must "be the Y-distance between the opposite corners" in {
    Box( 10, 20 ).height should equal( 20 )
  }

  "The center member" must "be the center of the Box" in {
    Box( 10, 20 ).center should equal( XYVect( 5, 10 ) )
  }

  "The area member" must "be width times height" in {
    Box( 10, 20 ).area should equal( 10 * 20 )
  }

  "The origin member" must "be the closest vertex to the origin of axis" in {
    Box( 10, 20 ).origin should equal( Vect.origin )
    Box( -10, 20 ).origin should equal( Vect.origin )
    Box( -10, -20 ).origin should equal( Vect.origin )
    Box( 10, -20 ).origin should equal( Vect.origin )
  }

  "The opposite member" must "be the farthest vertex to the origin of axis" in {
    Box( 10, 20 ).opposite should equal( XYVect( 10, 20 ) )
    Box( -10, 20 ).opposite should equal( XYVect( -10, 20 ) )
    Box( -10, -20 ).opposite should equal( XYVect( -10, -20 ) )
    Box( 10, -20 ).opposite should equal( XYVect( 10, -20 ) )
  }

  "The topRight member" must "always be the top-right vertex of the Box" in {
    Box( 10, 20 ).topRight should equal( XYVect( 10, 20 ) )
    Box( -10, 20 ).topRight should equal( XYVect( 0, 20 ) )
    Box( -10, -20 ).topRight should equal( XYVect( 0, 0 ) )
    Box( 10, -20 ).topRight should equal( XYVect( 10, 0 ) )
  }

  "The bottomLeft member" must "always be the bottom-left vertex of the Box" in {
    Box( 10, 20 ).bottomLeft should equal( XYVect( 0, 0 ) )
    Box( -10, 20 ).bottomLeft should equal( XYVect( -10, 0 ) )
    Box( -10, -20 ).bottomLeft should equal( XYVect( -10, -20 ) )
    Box( 10, -20 ).bottomLeft should equal( XYVect( 0, -20 ) )
  }

  //
  // split member
  //

  "The split member" must "divide the Box in a list of Boxes" in {
    val test = Box( 100, 300 ).move( XYVect( 50, 50 ) )
    val result = test.split( 2, 3 )

    result( 0 ) should equal( Box( XYVect( 50, 50 ), XYVect( 100, 150 ) ) )
    result( 1 ) should equal( Box( XYVect( 100, 50 ), XYVect( 150, 150 ) ) )
    result( 2 ) should equal( Box( XYVect( 150, 50 ), XYVect( 200, 150 ) ) )
    result( 3 ) should equal( Box( XYVect( 50, 150 ), XYVect( 100, 250 ) ) )
    result( 4 ) should equal( Box( XYVect( 100, 150 ), XYVect( 150, 250 ) ) )
    result( 5 ) should equal( Box( XYVect( 150, 150 ), XYVect( 200, 250 ) ) )
  }

  //
  // Companion object apply member
  //

  "The apply member" must "create a Box when given center, width and height" in {
    val test = Box( XYVect( 50, 40 ), 30, 20 )
    test.topRight should equal( XYVect( 65, 50 ) )
    test.bottomLeft should equal( XYVect( 35, 30 ) )
  }

  "The apply member" must "create a Box when given width and height" in {
    val test = Box( 30, 20 )
    test.topRight should equal( XYVect( 30, 20 ) )
    test.bottomLeft should equal( Vect.zero )
  }

  "The apply member" must "create a Box when given opposite vertices" in {
    val test = Box( XYVect( 10, 20 ), XYVect( 30, 40 ) )
    test.topRight should equal( XYVect( 30, 40 ) )
    test.bottomLeft should equal( XYVect( 10, 20 ) )
  }

  //
  // Companion object bestFit member
  //

  "The bestFit member" must "find the minimum-area Box that contains a Shape" in {
    val container = Box( XYVect( 50, 60 ), XYVect( 150, 160 ) )

    testShapesSet( container ).foreach { s ⇒
      Box.bestFit( s ).contains( s ) should equal( true )
    }
  }

  //
  // Companion spreadAcross apply member
  //

  "The spreadAcross member" must "position each Shape in a different Box" in {
    val container = Box( 100, 300 )
    val splitContainer = container.split( _hSplit, _vSplit )

    // I create a new Shape from the raw ones for every box in splitContainer
    val testShapes =
      for {s ← rndTestShapeSet( splitContainer.head )
           b ← splitContainer
      } yield {
        s.move( b.center - splitContainer.head.center )
      }

    val result = Box.spreadAcross( splitContainer, testShapes, compact = false )

    result.size should equal( _hSplit * _vSplit )
    result.foreach {
      case (b, s) ⇒ s.size should equal( rndTestShapeSet( container ).size )
    }
  }

  "The spreadAcross member" must "place the same Shape in multiple boxes when it spans more than one" in {
    val container = Box( 100 * _hSplit, 100 * _vSplit )
    val splitContainer = container.split( _hSplit, _vSplit )
    val testShape = Circle( container.center, Math.max( container.width, container.height ) / 2.0 )

    val result = Box.spreadAcross( splitContainer, Seq( testShape ), compact = false )

    result.foreach {
      case (b, s) ⇒ s.size should equal( 1 )
    }
  }

  "The spreadAcross member" must "include boxes with empty content when the option is false" in {
    val container = Box( 100, 300 )
    val splitContainer = container.split( _hSplit, _vSplit )
    val testShapes = Seq( ShapeUtils.rndCircle( splitContainer( 0 ) ), ShapeUtils.rndCircle( splitContainer( 1 ) ) )

    val result = Box.spreadAcross( splitContainer, testShapes, compact = false )

    result.size should equal( splitContainer.size )
  }

  "The spreadAcross member" must "not include boxes with empty content when the option is true" in {
    val container = Box( 100, 300 )
    val splitContainer = container.split( _hSplit, _vSplit )
    val testShapes = ShapeUtils.rndCircle( splitContainer( 0 ) ) :: ShapeUtils.rndCircle( splitContainer( 1 ) ) :: Nil

    val result = Box.spreadAcross( splitContainer, testShapes, compact = true )

    result.size should equal( 2 )
  }
}