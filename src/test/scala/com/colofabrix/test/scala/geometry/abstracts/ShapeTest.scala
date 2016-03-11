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
import com.colofabrix.test.scala.geometry.ShapeUtils
import com.colofabrix.test.scala.geometry.shapes.SegTest
import org.scalatest.{ FlatSpec, Matchers }

/**
  *
  */
trait ShapeTest[T <: Shape] extends FlatSpec with Matchers {

  /**
    * Creates a new object of type T to test
    *
    * @param bounds The area covered by the object
    * @return A new instance of a T
    */
  protected def testShape( bounds: Box ): T

  /**
    * Creates a new object of type T to test that must have at least one
    * point of its boundary known. The known point will lie on the right
    * edge of boundary and a `touch` distance from the topRight vertex.
    *
    * @param bounds The area covered by the object
    * @param touch  A parameter between 0.0 and 1.0 that tells the desired point on the right edge of bounds
    * @return A tuple with 1) a new instance of a T and 2) The point that must be touched
    */
  protected def testShape( bounds: Box, touch: Double ): (T, Vect)

  /**
    * Different kind of shapes to test against.
    *
    * Test shapes must cover as much "where" as possible, specifically its
    * bottom-right diagonal and right edge without exiting it
    *
    * @param where The Box that delimits where the Shapes will be created
    * @return A list of random shapes, one for each type
    */
  protected def testShapesSet( where: Box ) = Seq(
    Seg( where.topRight, where.bottomLeft ),
    where,
    Circle( where.center, Math.min( where.width, where.height ) ),
    new ConvexPolygon( where.vertices ),
    Polygon(
      Seq(
        where.bottomLeft + XYVect( where.width * 0.5, where.height * 0.5 ),
        where.bottomLeft + XYVect( where.width * 1.0, where.height * 0.5 ),
        where.bottomLeft + XYVect( where.width * 1.0, where.height * 1.0 ),
        where.bottomLeft + XYVect( where.width * 0.25, where.height * 0.67 ),
        where.bottomLeft + XYVect( where.width * 0.1, where.height * 0.25 ),
        where.bottomLeft + XYVect( where.width * 0.75, where.height * 0.1 )
      )
    )
  )

  protected def rndTestShapeSet( cont: Box ) = Seq(
    ShapeUtils.rndSeg( cont ),
    ShapeUtils.rndBox( cont ),
    ShapeUtils.rndCircle( cont ),
    ShapeUtils.rndConvexPolygon( cont ),
    ShapeUtils.rndPolygon( cont )
  )

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
    val where = Box( XYVect( 50, 100 ), XYVect( 100, 200 ) )
    val test = testShape( where )

    testShapesSet( where ) foreach { s ⇒
      test.intersects( s.move( XYVect( where.width / 2.0, where.height / 2.0 ) ) ) should equal( true )
    }
  }

  "The intersects member" must "return true when is the given a Shape that contains the object" in {
    val where = Box( 100, 200 )
    val test = testShape( Box( where.center, where.width / 2.0, where.height / 2.0 ) )

    testShapesSet( where ) foreach { s ⇒
      // TODO: Complete the test with Seg-Seg case
      if( !test.isInstanceOf[Seg] && !s.isInstanceOf[Seg] ) {
        test.intersects( s ) should equal( true )
      }
    }
  }

  "The intersects member" must "return false when given a Shape completely outside the test Shape" in {
    val where = Box( 100, 200 )
    val test = testShape( where )

    testShapesSet( where ) foreach { s ⇒
      test.intersects( s.move( XYVect( where.width * 3.0, where.height * 3.0 ) ) ) should equal( false )
    }
  }

  "The intersects member" must "return true when given intersecting segments" in {
    val ref = testShape( Box( 100, 200 ), 0.5 )._1

    ref.intersects( Seg( XYVect( 100, 90 ), XYVect( 100, 100 ) ) ) should equal( true )
    ref.intersects( Seg( XYVect( 100, 100 ), XYVect( 90, 100 ) ) ) should equal( true )
    ref.intersects( Seg( XYVect( 90, 100 ), XYVect( 120, 120 ) ) ) should equal( true )
  }

  "The intersects member" must "return false when given non-intersecting segments" in {
    val ref = testShape( Box( 100, 200 ), 0.5 )._1

    ref.intersects( Seg( XYVect( 110, 90 ), XYVect( 110, 30 ) ) ) should equal( false )
    ref.intersects( Seg( XYVect( 110, 90 ), XYVect( 130, 90 ) ) ) should equal( false )
    ref.intersects( Seg( XYVect( 100, 210 ), XYVect( 100, 290 ) ) ) should equal( false )
  }

  //
  // contains() member
  //

  "The contains member" must "return true when given a Vect inside or on the boundary of the Shape" in {
    val test = testShape( Box( 10, 10 ), 0.5 )._1

    val (point1, point2) = if( this.isInstanceOf[SegTest] ) {
      (XYVect( 10, 10 ), XYVect( 10, 5 ))
    }
    else {
      (XYVect( 5, 5 ), XYVect( 10, 5 ))
    }

    test.contains( point1 ) should equal( true )
    test.contains( point2 ) should equal( true )
  }

  "The contains member" must "return false when given a Vect outside the Shape" in {
    val outsidePoint = XYVect( 20, 20 )
    val test = testShape( Box( 10, 10 ) )

    test.contains( outsidePoint ) should equal( false )
  }

  "The contains member" must "return true when given a Shape inside or on the boundary of the Shape" in {
    val where = Box( XYVect( -50, -50 ), XYVect( 150, 250 ) )
    val test = testShape( where )

    if( !this.isInstanceOf[SegTest] ) {
      testShapesSet( Box( where.center, where.width / 3.0, where.height / 3.0 ) ) foreach { s ⇒
        val res = test.contains( s )
        res should equal( true )
      }
    }
  }

  "The contains member" must "return false when given a Shape outside the Shape" in {
    val test = testShape( Box( 100, 200 ) )

    testShapesSet( Box( 100, 200 ) ) foreach { s ⇒
      test.contains( s.move( XYVect( 101, 201 ) ) ) should equal( false )
    }
  }

  "The contains member" must "return false when is the given a Shape that contains the object" in {
    val where = Box( 100, 200 )
    val test = testShape( Box( where.center, where.width / 2.0, where.height / 2.0 ) )

    testShapesSet( where ).filterNot( _.isInstanceOf[Seg] ) foreach { s ⇒
      test.contains( s ) should equal( false )
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
    val where = Box( 100, 200 )
    val test = testShape( where, 0.5 )._1

    val expDistance = XYVect( -50, 0 ) // Distance from the test shape to the segment
    val expPoint = XYVect( where.topRight.x, where.center.y )

    val point = expPoint - expDistance
    val distance = test.distance( point )

    distance._1 should equal( expDistance )
    distance._2 should equal( expPoint )
  }

  "The distance member" must "return a zero distance when given a Vect that lies on it" in {
    val where = Box( 100, 200 )
    val (test, point) = testShape( where, 0.5 )

    val dist = test.distance( point )
    dist._1 should equal( Vect.zero )

    // Additional test for non-segments
    if( !test.isInstanceOf[Seg] ) {
      val dist = test.distance( point - XYVect( 10, 10 ) )
      dist._1 should equal( Vect.zero )
    }
  }

  "The distance member" must "return a proper distance when given a Seg outside it" in {
    val where = Box( 100, 200 )
    val test = testShape( where, 0.5 )._1

    val rightEdge = Seg( where.topRight, XYVect( where.topRight.x, where.bottomLeft.y ) )
    val expDistance = XYVect( -50, 0 ) // Distance from the test shape to the segment

    val ref1 = rightEdge.move( expDistance * -1.0 )
    val distance1 = test.distance( ref1 )

    distance1._1 should equal( expDistance )
  }

  "The distance member" must "return a zero distance when given a Seg that intersects it" in {
    val where = Box( 100, 200 )
    val (test, point) = testShape( where, 0.5 )
    val seg = Seg( point - XYVect( 10, 0 ), point + XYVect( 10, 0 ) )

    val dist = test.distance( seg )

    dist._1 should equal( Vect.zero )
    dist._2 should equal( Vect.zero )

    // Additional test for non-segments
    if( !test.isInstanceOf[Seg] ) {
      val dist = test.distance( seg.move( XYVect( -10, -10 ) ) )

      dist._1 should equal( Vect.zero )
      dist._2 should equal( Vect.zero )
    }
  }

  //
  // equal()
  //

  "The equal member" must "return true when given instances that represent the same Shape" in {
    val test1 = testShape( Box( XYVect( 15, 15 ), XYVect( 25, 35 ) ) )
    val test2 = testShape( Box( XYVect( 15, 15 ), XYVect( 25, 35 ) ) )

    test1 == test1 should equal( true )
    test1 == test2 should equal( true )
    test2 == test1 should equal( true )
  }

  "The equal member" must "return false when given different Shapes" in {
    val test1 = testShape( Box( XYVect( 15, 15 ), XYVect( 25, 35 ) ) )
    val test2 = testShape( Box( XYVect( 1, 5 ), XYVect( 5, 3 ) ) )

    test1 == test2 should equal( false )
    test2 == test1 should equal( false )
  }
}
