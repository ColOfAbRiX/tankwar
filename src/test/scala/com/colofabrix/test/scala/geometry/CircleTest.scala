/*
 * Copyright (C) 2015 Fabrizio Colonna
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

package com.colofabrix.test.scala.geometry

import com.colofabrix.scala.geometry.shapes.{ Box, Circle }
import com.colofabrix.scala.math.Vector2D
import org.scalatest.{ Matchers, WordSpec }

/**
 * Unit testing for Circle Shape
 *
 * Created by Fabrizio on 11/01/2015.
 */
class CircleTest extends WordSpec with Matchers {

  "A Circle" must {

    "Check overlapping" when {

      "Matched with another circle" in {

        val circle1 = Circle( Vector2D.new_xy( 10, 10 ), 50 )
        val circle2 = Circle( Vector2D.new_xy( -60, 80 ), 20 )
        val circle3 = Circle( Vector2D.new_xy( 20, 20 ), 20 )

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

      "Matched with a point" must {

        val insidePoint = Vector2D.new_rt( 50, Math.PI / 4 )
        val outsidePoint = Vector2D.new_rt( 100, -Math.PI )
        val centered = Circle( Vector2D.new_xy( 0, 0 ), 50 )
        val offCenter = Circle( Vector2D.new_xy( 50, 50 ), 50 )

        // The point is on the circumference
        centered.contains( insidePoint ) should equal( true )
        // The point is inside
        offCenter.contains( insidePoint ) should equal( true )

        // The point is outside
        centered.contains( outsidePoint ) should equal( false )
        offCenter.contains( outsidePoint ) should equal( false )

      }
    }

    "Check containment" when {

      "Shape is a polygon" in {
        val circle = new Circle( Vector2D.new_xy( 50, 50 ), 50 )

        // Overlaps the vertices
        val contained1 = new Box( Vector2D.new_xy( 50, 50 ), Math.sqrt( 2 ) * circle.radius, Math.sqrt( 2 ) * circle.radius )
        // Fully inside the box
        val contained2 = new Box( Vector2D.new_xy( 50, 50 ), 10, 10 )
        // Partially inside the box
        val notContained1 = new Box( Vector2D.new_xy( -10, -10 ), Vector2D.new_xy( 75, 75 ) )
        // Fully outside the box
        val notContained2 = new Box( Vector2D.new_xy( -100, 0 ), Vector2D.new_xy( -50, 50 ) )

        circle.contains( contained1 ) should equal( true )
        circle.contains( contained2 ) should equal( true )

        circle.contains( notContained1 ) should equal( false )
        circle.contains( notContained2 ) should equal( false )
      }

      "Shape is a circle" in {
        val circle = new Circle( Vector2D.new_xy( 50, 50 ), 50 )

        // Overlaps one border
        val contained1 = new Circle( Vector2D.new_xy( 50, 50 ), 10 )
        // Fully inside the box
        val contained2 = new Circle( Vector2D.new_xy( 50, 50 ), 50 )
        // Partially inside the box
        val notContained1 = new Circle( Vector2D.new_xy( 75, 50 ), 50 )
        // Fully outside the box
        val notContained2 = new Circle( Vector2D.new_xy( 150, 150 ), 10 )

        circle.contains( contained1 ) should equal( true )
        circle.contains( contained2 ) should equal( true )

        circle.contains( notContained1 ) should equal( false )
        circle.contains( notContained2 ) should equal( false )
      }

    }

  }

  "A circle" when {

    "Matched with a point" must {

      "Find the correct distance" in {
      }

      "Determine if they overlaps" in {
      }

    }

    "Matched with a line segment" must {

      "Find the correct distance" in {
      }

      "Determine if they overlap" in {
      }

    }

    "Matched with a generic polygon" must {

      "Determine if they overlap" in {
      }

    }

    "Matched with another circle" must {

      "Find the correct distance" in {
      }

      "Determine if they overlap" in {
      }

    }

  }
}