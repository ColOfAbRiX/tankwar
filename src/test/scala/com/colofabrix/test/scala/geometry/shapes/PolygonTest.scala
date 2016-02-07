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

import org.scalatest.{ Matchers, WordSpec }

/**
  * Unit testing for polygons
  *
  * Created by Fabrizio on 11/01/2015.
  */
class PolygonTest extends WordSpec with Matchers {

  /*
  "A generic polygon" when {

    "Matched with a point" must {

      "Find the correct distance" in {

        val triangle = new Polygon( Seq( XYVect( 0, 0 ), XYVect( 20, 0 ), XYVect( 10, 20 ) ) )

        // It's inside, distance = 0
        val p0 = XYVect( 10, 10 )
        triangle.distance( p0 )._1 should equal( XYVect( 0, 0 ) )
        triangle.distance( p0 )._2 should equal( XYVect( 0, 0 ) )

        // Distance must be calculated from the 3rd vertex
        val p1 = XYVect( 20, 40 )
        triangle.distance( p1 )._1 should equal( XYVect( -10, -20 ) )
        triangle.distance( p1 )._2 should equal( XYVect( -10, 20 ) )

        //   "   "   "   " from the 2nd edge
        val p2 = XYVect( 20, 20 )
        triangle.distance( p2 )._1 should equal( XYVect( -8, -4 ) )
        triangle.distance( p2 )._2 should equal( XYVect( -10, 20 ) )

        //   "   "   "   " from the 2nd vertex
        val p3 = XYVect( 20, -20 )
        triangle.distance( p3 )._1 should equal( XYVect( 0, 20 ) )
        triangle.distance( p3 )._2 should equal( XYVect( 20, 0 ) )

        //   "   "   "   " from the 2nd vertex, 1st edge
        val p4 = XYVect( 30, 0 )
        triangle.distance( p4 )._1 should equal( XYVect( -10, 0 ) )
        triangle.distance( p4 )._2 should equal( XYVect( 20, 0 ) )

      }

      "Determine if they overlap" in {

        val triangle = new Polygon( Seq( XYVect( 0, 0 ), XYVect( 20, 0 ), XYVect( 10, 20 ) ) )

        val insidePoint = XYVect( 10, 10 )
        val outsidePoint1 = XYVect( 20, 40 )
        val outsidePoint2 = XYVect( 40, 0 )

        triangle.contains( insidePoint ) should equal( true )
        triangle.contains( outsidePoint1 ) should equal( false )
        triangle.contains( outsidePoint2 ) should equal( false )

      }

    }

    "Matched with a line segment" must {

      "Find the correct distance" in {
      }

      "Determine if they overlap" in {
      }

    }

    "Matched with another polygon" must {

      "Determine if they overlap" in {

        val convex = new Polygon( Seq( XYVect( 0, 0 ), XYVect( 20, 0 ), XYVect( 10, 20 ) ) )
        val concaveNoOverlap = new Polygon( Seq( XYVect( 40, 0 ), XYVect( 60, 0 ), XYVect( 55, 20 ), XYVect( 45, -20 ) ) )
        val concaveOverlap = new Polygon( Seq( XYVect( 10, 10 ), XYVect( 30, 10 ), XYVect( 45, 30 ), XYVect( 15, -10 ) ) )

        convex.intersects( concaveNoOverlap ) should equal( false )
        convex.intersects( concaveOverlap ) should equal( true )
        concaveNoOverlap.intersects( concaveOverlap ) should equal( false )

      }

    }

    "Matched with another circle" must {

      "Find the correct distance" in {
      }

      "Determine if they overlap" in {

      }

    }

  }

  "A generic polygon" must {

    "Must have at least 3 edges" in {

      // Valid polygon
      new Polygon( Seq( XYVect( 0, 0 ), XYVect( 20, 0 ), XYVect( 10, 20 ) ) )

      // Invalid polygon
      intercept[IllegalArgumentException] {
        val notValid = new Polygon( Seq( XYVect( 0, 0 ), XYVect( 20, 0 ) ) )
      }

    }

    "Be checked if it is convex" in {

      val convex = new Polygon( Seq( XYVect( 0, 0 ), XYVect( 20, 0 ), XYVect( 10, 20 ) ) )
      val concave = new Polygon( Seq( XYVect( 0, 0 ), XYVect( 20, 0 ), XYVect( 15, 20 ), XYVect( 5, -20 ) ) )

      convex.isConvex should equal( true )
      concave.isConvex should equal( false )

    }

    "Must find an enclosing Box" in {

    }

    "Must have a valida area" in {
      val polygon = new ConvexPolygon( Seq( XYVect( 0, 0 ), XYVect( 20, 0 ), XYVect( 20, 20 ), XYVect( 0, 20 ) ) )

      polygon.area should equal( 400.0 )
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
  */
}