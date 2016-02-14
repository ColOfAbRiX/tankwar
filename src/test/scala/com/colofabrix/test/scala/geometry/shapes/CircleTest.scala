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

import com.colofabrix.scala.geometry.shapes.{ Box, Circle, Polygon }
import com.colofabrix.scala.math.XYVect
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

  "The area member" must "be a valid area" in {
    val test = Circle( XYVect( 10, 10 ), 100 )
    test.area should equal( Math.PI * Math.pow( 100, 2.0 ) )
  }

  "The fromArea member" must "create a Circle given its area" in {
    val test = Circle.fromArea( XYVect( 10, 10 ), Math.PI * Math.pow( 100, 2.0 ) )
    test.radius should equal(100)
  }

  "The bestFit member" must "find the minimum-area Circle that contains a Shape" in {
    val container = Box( XYVect( 50, 60 ), XYVect( 150, 160 ) )

    val testShapes = Seq(
      container,
      Circle( container.center, container.width / 2 ),
      new Polygon( XYVect( 50, 60 ) :: XYVect( 132, 116 ) :: XYVect( 121, 140 ) :: XYVect( 150, 160 ) :: Nil )
    )

    testShapes.foreach { s ⇒
      Box.bestFit( s ) should equal( container )
    }
  }

}