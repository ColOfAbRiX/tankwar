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

import com.colofabrix.scala.geometry.shapes.{ Box, Circle }
import com.colofabrix.scala.math.{ RTVect, XYVect }
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
  override protected def testShape( bounds: Box ): Circle = Circle( bounds.center, Math.min( bounds.width, bounds.height ) )

  "A Circle" must "Check overlapping when matched with another circle" in {

    val circle1 = Circle( XYVect( 10, 10 ), 50 )
    val circle2 = Circle( XYVect( -60, 80 ), 20 )
    val circle3 = Circle( XYVect( 20, 20 ), 20 )

    // Check circle1
    circle1.intersects( circle1 ) should equal( true )
    circle1.intersects( circle2 ) should equal( false )
    circle1.intersects( circle3 ) should equal( true )

    // Check circle2
    circle2.intersects( circle2 ) should equal( true )
    circle2.intersects( circle3 ) should equal( false )

    // Check circle3
    circle1.intersects( circle3 ) should equal( true )

  }

  "A Circle" must "Check overlapping when matched with a point" in {

    val insidePoint = RTVect( 50, Math.PI / 4 )
    val outsidePoint = RTVect( 100, -Math.PI )
    val centered = Circle( XYVect( 0, 0 ), 50 )
    val offCenter = Circle( XYVect( 50, 50 ), 50 )

    // The point is on the circumference
    centered.contains( insidePoint ) should equal( true )
    // The point is inside
    offCenter.contains( insidePoint ) should equal( true )

    // The point is outside
    centered.contains( outsidePoint ) should equal( false )
    offCenter.contains( outsidePoint ) should equal( false )

  }

  "A Shape is a polygon" must "work" in {
    val circle = new Circle( XYVect( 50, 50 ), 50 )

    // Overlaps the vertices
    val contained1 = Box( XYVect( 50, 50 ), Math.sqrt( 2 ) * circle.radius, Math.sqrt( 2 ) * circle.radius )
    // Fully inside the box
    val contained2 = Box( XYVect( 50, 50 ), 10, 10 )
    // Partially inside the box
    val notContained1 = Box( XYVect( -10, -10 ), XYVect( 75, 75 ) )
    // Fully outside the box
    val notContained2 = Box( XYVect( -100, 0 ), XYVect( -50, 50 ) )

    circle.contains( contained1 ) should equal( true )
    circle.contains( contained2 ) should equal( true )

    circle.contains( notContained1 ) should equal( false )
    circle.contains( notContained2 ) should equal( false )
  }

  "Shape is a circle" must "work" in {
    val circle = new Circle( XYVect( 50, 50 ), 50 )

    // Overlaps one border
    val contained1 = new Circle( XYVect( 50, 50 ), 10 )
    // Fully inside the box
    val contained2 = new Circle( XYVect( 50, 50 ), 50 )
    // Partially inside the box
    val notContained1 = new Circle( XYVect( 75, 50 ), 50 )
    // Fully outside the box
    val notContained2 = new Circle( XYVect( 150, 150 ), 10 )

    circle.contains( contained1 ) should equal( true )
    circle.contains( contained2 ) should equal( true )

    circle.contains( notContained1 ) should equal( false )
    circle.contains( notContained2 ) should equal( false )
  }
}