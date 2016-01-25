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
import org.scalatest.{ Matchers, WordSpec }

/**
  * Unit testing for OrtoRectangles
  */
class BoxTest extends WordSpec with Matchers {

  private val tolerance = 1E-5

  "A Box" must {

    "Check overlapping" when {

      "Matched with a point" must {

        val insidePoint = XYVect( 5, 5 )
        val outsidePoint = XYVect( 20, 20 )
        val rect = Box( XYVect( 0, 0 ), XYVect( 10, 10 ) )

        // The point is outside the rectangle
        rect.contains( insidePoint ) should equal( true )
        // The point is inside the rectangle
        rect.contains( outsidePoint ) should equal( false )

      }

      "Is a Box" must {

        val touchRect1 = Box( XYVect( 0, 0 ), XYVect( 10, 15 ) )
        val touchRect2 = Box( XYVect( 5, 10 ), XYVect( 15, 20 ) )
        val separateRect = Box( XYVect( 30, 35 ), XYVect( 40, 45 ) )

        touchRect1.intersects( touchRect1 ) should equal( true )
        touchRect1.intersects( touchRect2 ) should equal( true )
        touchRect1.intersects( separateRect ) should equal( false )

        touchRect2.intersects( touchRect2 ) should equal( true )
        touchRect2.intersects( separateRect ) should equal( false )

        separateRect.intersects( separateRect ) should equal( true )

      }

    }

    "Check containment" when {

      "Shape is a polygon" in {
        val box = Box( Vect.zero, XYVect( 100, 100 ) )

        // Overlaps one border
        val contained1 = Box( XYVect( 50, 50 ), XYVect( 100, 100 ) )
        // Fully inside the box
        val contained2 = Box( XYVect( 25, 25 ), XYVect( 75, 75 ) )
        // Partially inside the box
        val notContained1 = Box( XYVect( -10, -10 ), XYVect( 75, 75 ) )
        // Fully outside the box
        val notContained2 = Box( XYVect( -100, 0 ), XYVect( -50, 50 ) )

        box.contains( contained1 ) should equal( true )
        box.contains( contained2 ) should equal( true )

        box.contains( notContained1 ) should equal( false )
        box.contains( notContained2 ) should equal( false )
      }

      "Shape is a circle" in {
        val box = Box( Vect.zero, XYVect( 100, 100 ) )

        // Overlaps one border
        val contained1 = new Circle( XYVect( 50, 50 ), 10 )
        // Fully inside the box
        val contained2 = new Circle( XYVect( 50, 50 ), 50 )
        // Partially inside the box
        val notContained1 = new Circle( XYVect( 150, 50 ), 100 )
        // Fully outside the box
        val notContained2 = new Circle( XYVect( 150, 50 ), 10 )

        box.contains( contained1 ) should equal( true )
        box.contains( contained2 ) should equal( true )

        box.contains( notContained1 ) should equal( false )
        box.contains( notContained2 ) should equal( false )
      }

    }

  }

}