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
import com.colofabrix.scala.geometry.shapes._
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

  /**
    * Creates a list of shapes to be used as tests
    *
    * @param where The Box that delimits where the Shapes will be created
    * @return A Seq of different Shapes, one for each type
    */
  private def testShapesSet( where: Box ) = Seq(
    Seg( XYVect( 90, 20 ), XYVect( 10, 190 ) ),
    Box( XYVect( 30, 50 ), XYVect( 70, 150 ) ),
    Circle( where.center, 20 ),
    new ConvexPolygon( XYVect( 50, 150 ) :: XYVect( 30, 90 ) :: XYVect( 35, 50 ) :: XYVect( 70, 110 ) :: Nil ),
    new Polygon( XYVect( 50, 90 ) :: XYVect( 72, 96 ) :: XYVect( 61, 120 ) :: XYVect( 41, 85 ) :: Nil )
  )

  //
  // Properties
  //

  "The area member" must "represent the area of the Shape" in {
    val test = testShapesSet( Box( 100, 200 ) )

    test( 0 ).area should equal( 0.0 )
    test( 1 ).area should equal( (70 - 30) * (150 - 50) )
    test( 2 ).area should equal( Math.pow( 20, 2 ) * Math.PI )
    test( 3 ).area should equal( 2350.0 )
    test( 4 ).area should equal( 129.5 )
  }

  //
  // container member
  //

  "The container member" must "fully contain the shape" in {
    val test = testShapesSet( Box( 100, 200 ) )

    test.foreach { s ⇒
      s.container.contains( s ) should equal( true )
    }
  }

  //
  // intersects() member
  //

  "The intersects member" must "return true when given a Shape fully inside the test Shape" in {
    val where = Box( 100, 200 )
    val test = testShape( where )

    testShapesSet( where ) foreach {
      test.intersects( _ ) should equal( true )
    }
  }

  "The intersects member" must "return true when given a Shape partially inside the test Shape" in {
    val where = Box( 100, 200 )
    val test = testShape( where )

    testShapesSet( where ) foreach { s ⇒
      test.intersects( s.move( XYVect( 50, 0 ) ) ) should equal( true )
    }
  }

  "The intersects member" must "return false when given a Shape completely outside the test Shape" in {
    val where = Box( 100, 200 )
    val test = testShape( where )

    testShapesSet( where ) foreach { s ⇒
      test.intersects( s.move( XYVect( 100, 0 ) ) ) should equal( false )
    }
  }

  "The intersects member" must "return true when given intersecting segments" in {
    //val ref = Seg( XYVect( 100, 200 ), XYVect( 100, 0 ) )
    val ref = testShape( Box( 100, 200 ) )

    ref.intersects( Seg( XYVect( 100, 90 ), XYVect( 100, 100 ) ) ) should equal( true )
    ref.intersects( Seg( XYVect( 100, 100 ), XYVect( 90, 100 ) ) ) should equal( true )
    ref.intersects( Seg( XYVect( 90, 100 ), XYVect( 120, 120 ) ) ) should equal( true )
  }

  "The intersects member" must "return false when given non-intersecting segments" in {
    //val ref = Seg( XYVect( 100, 200 ), XYVect( 100, 0 ) )
    val ref = testShape( Box( 100, 200 ) )

    ref.intersects( Seg( XYVect( 110, 90 ), XYVect( 110, 30 ) ) ) should equal( false )
    ref.intersects( Seg( XYVect( 110, 90 ), XYVect( 130, 90 ) ) ) should equal( false )
    ref.intersects( Seg( XYVect( 100, 210 ), XYVect( 100, 290 ) ) ) should equal( false )
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

  "The contains member" must "return true when given a Shape inside or on the boundary of the Shape" in {
    val test = testShape( Box( 300, 300 ).move( XYVect( -100, -100 ) ) )

    testShapesSet( Box( 100, 200 ) ) foreach { s ⇒
      test.contains( s ) should equal( true )
    }
  }

  "The contains member" must "return false when given a Shape outside the Shape" in {
    val test = testShape( Box( 100, 200 ) )

    testShapesSet( Box( 100, 200 ) ) foreach { s ⇒
      test.contains( s.move( XYVect( 101, 201 ) ) ) should equal( false )
    }
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
  // distance() member
  //

  "The distance member" must "return a non-zero distance when given a Vect that doesn't lie on it" in {
  }

  "The distance member" must "return a zero distance when given a Vect that does lie on it" in {
    val where = Box( 100, 200 )
    val test = testShape( where )

    val dist = test.distance( where.center )
    dist._1 should equal( Vect.zero )
    dist._2 should equal( Vect.zero )
  }

  "The distance member" must "return a proper distance when given a Seg outside it" in {
    val testSegments = XYVect( 100, 90 ) :: XYVect( 100, 100 ) :: XYVect( 90, 100 ) :: XYVect( 120, 120 ) :: XYVect( 120, 80 ) :: Nil
    val reference = Seg( XYVect( 100, 200 ), XYVect( 100, 0 ) )

    testSegments.sliding( 2 ).foreach {
      case p0 :: p1 :: Nil ⇒ reference.intersects( Seg( p0, p1 ) )
    }
  }

  "The distance member" must "return a zero distance when given a Seg that intersects it" in {
    val where = Box( 100, 200 )
    val test = testShape( where )
    val seg = Seg( where.center - XYVect( -10, -10 ), where.center - XYVect( 10, 10 ) )

    val dist = test.distance( seg )
    dist._1 should equal( Vect.zero )
    dist._2 should equal( Vect.zero )
  }

  //
  // equal()
  //

  "The equal member" must "return true when given instances that represent the same Shape" in {
    val test1 = testShape( Box( XYVect( 15, 15 ), XYVect( 25, 35 ) ) )
    val test2 = testShape( Box( XYVect( 15, 15 ), XYVect( 25, 35 ) ) )

    test1 == test1 should equal( true )
    test1 == test2 should equal( true )
  }

  "The equal member" must "return false when given different Shapes" in {
    val test1 = testShape( Box( XYVect( 15, 15 ), XYVect( 25, 35 ) ) )
    val test2 = testShape( Box( XYVect( 1, 5 ), XYVect( 5, 3 ) ) )

    test1 == test2 should equal( false )
  }
}
