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

import com.colofabrix.scala.geometry.abstracts.{ Shape, SpatialSet }
import com.colofabrix.scala.geometry.shapes.Box
import com.colofabrix.test.scala.geometry.ShapeUtils
import org.scalatest.{ FlatSpec, Matchers }

/**
  * Test to cover the common features of all the Spatial Sets
  */
trait SpatialSetTest[T <: SpatialSet[Shape]] extends FlatSpec with Matchers {

  /**
    * Default Box object used to define
    */
  protected val testArea = Box( 400, 300 )

  /**
    * Creates a new object of type T to test
    *
    * @param bounds  The area covered by the object
    * @param objects The objects to add to the list
    * @return A new instance of a SpatialSet[T]
    */
  protected def newSpatialSet[U <: Shape]( bounds: Box, objects: List[U] ): T

  /**
    * Checks the size of a Set
    *
    * @param result   The set to validate
    * @param expected The expected number of results
    */
  protected def validateSize( result: SpatialSet[Shape], expected: Int ): Unit = {
    result.isEmpty should equal( expected == 0 )
    val test = result.toList
    val size = result.size
    size should equal( expected )
    result.toList.size should equal( expected )
  }

  private def getAsT[U]( t: U ): T = t match {
    case x: T => x
    case _ => throw new IllegalArgumentException
  }

  //
  // Constructor testing
  //

  "The base constructor" must "create a valid empty Set" in {
    val result = newSpatialSet( testArea, List.empty[Shape] )
    validateSize( result, 0 )
    result.bounds should equal( testArea )
  }

  "The base constructor" must "create a set with initial data" in {
    val boxes = List.fill( 10 )( ShapeUtils.rndCircle( testArea ) )
    val result = newSpatialSet( testArea, boxes )

    validateSize( result, boxes.size )
    result.bounds should equal( testArea )
  }

  //
  // +() methond testing
  //

  "The add member" must "add a new Shape" in {
    val set = newSpatialSet( testArea, List.empty[Shape] )
    val testShape = ShapeUtils.rndCircle( testArea )
    val result = set + testShape

    validateSize( result, 1 )
    result.toList.contains( testShape ) should equal( true )
  }

  "The add member" must "not add a duplicate Shape" in {
    val set = newSpatialSet( testArea, List.empty[Shape] )
    val testShape = ShapeUtils.rndCircle( testArea )

    val intermediateResult = set + testShape
    val result = intermediateResult + testShape

    validateSize( result, 1 )
    result.toList.contains( testShape ) should equal( true )
  }

  "The add member" must "add many shapes" in {
    val set = newSpatialSet( testArea, List.empty[Shape] )
    val shapes = List.fill( 10 )( ShapeUtils.rndCircle( testArea ) )

    val result = shapes.foldLeft( set )( ( a, x ) => getAsT( a + x ) )

    validateSize( result, shapes.size )
  }

  //
  // -() methond testing
  //

  "The remove member" must "remove an existing Shape" in {
    val testShape = ShapeUtils.rndCircle( testArea )
    val step1 = newSpatialSet( testArea, testShape :: Nil )

    try {
      val result = step1 - testShape
      validateSize( result, 0 )
    }
    catch {
      case e: Exception ⇒ fail( )
    }
  }

  "The remove member" must "do nothing when the shape doesn't exist" in {
    val testShape = ShapeUtils.rndCircle( testArea )
    val testShape2 = ShapeUtils.rndCircle( testArea )
    val step1 = newSpatialSet( testArea, testShape :: Nil )

    try {
      val result = step1 - testShape2
      validateSize( result, 1 )
    }
    catch {
      case e: Exception ⇒ fail( )
    }
  }

  "The remove member" must "not raise an exception when removing from an empty set" in {
    val set = newSpatialSet( testArea, List.empty[Shape] )
    val testShape = ShapeUtils.rndCircle( testArea )

    try {
      val result = set - testShape

      validateSize( result, 0 )
      result.toList.contains( testShape ) should equal( false )
    }
    catch {
      case e: Exception ⇒ fail( )
    }
  }

  "The remove member" must "remove many shapes" in {
    val shapes = List.fill( 100 )( ShapeUtils.rndCircle( testArea ) )
    val set = newSpatialSet( testArea, shapes )

    val result = shapes.foldLeft( set )( ( a, x ) => getAsT( a - x ) )

    validateSize( result, 0 )
  }

  //
  // clear() member testing
  //

  "The clear member" must "return an empty set" in {
    val testShape = ShapeUtils.rndCircle( testArea )
    val step1 = newSpatialSet( testArea, testShape :: Nil )
    val result = step1.clear( )

    validateSize( result, 0 )
  }

  //
  // isEmpty testing
  //

  "The isEmpty member" must "return the correct values" in {
    val set = newSpatialSet( testArea, List.empty[Shape] )
    val testShape = ShapeUtils.rndCircle( testArea )

    set.isEmpty should equal( true )

    val step1 = set + testShape
    step1.isEmpty should equal( false )

    val step2 = set - testShape
    step2.isEmpty should equal( true )
  }

  //
  // lookAround() testing
  //

  "The lookAround member" must "return at least one Shape when given any object that is present" in {
    val shapes = List.fill( 5 )( ShapeUtils.rndCircle( testArea ) )
    val set1 = newSpatialSet( testArea, shapes )

    set1.lookAround( shapes( 0 ) ).isEmpty should equal( false )
  }

  "The lookAround member" must "return an empty set when given any object outside the area" in {
    val shapes = List.fill( 5 )( ShapeUtils.rndCircle( testArea ) )
    val testShape = ShapeUtils.rndCircle( Box( -100, -200 ) )
    val set1 = newSpatialSet( testArea, shapes )

    set1.lookAround( testShape ).isEmpty should equal( true )
  }

  "The lookAround member" must "return an empty set when the Set is empty" in {
    val set1 = newSpatialSet( testArea, List.empty[Shape] )
    val testShape = ShapeUtils.rndCircle( testArea )

    set1.lookAround( testShape ).isEmpty should equal( true )
  }

  "The lookAround member" must "work for any Shape" in {
    try {
      val boxes = List.fill( 5 )( ShapeUtils.rndCircle( testArea ) )
      val set1 = newSpatialSet( testArea, boxes )

      set1.lookAround( ShapeUtils.rndCircle( testArea ) )
      set1.lookAround( ShapeUtils.rndBox( testArea ) )
      set1.lookAround( ShapeUtils.rndConvexPolygon( testArea ) )
      set1.lookAround( ShapeUtils.rndPolygon( testArea ) )
    }
    catch {
      case e: Exception ⇒ fail( )
    }
  }

  //
  // refresh() testing
  //

  "The refresh member" must "not alter the existing set" in {
    val shapes = List.fill( 3 )( ShapeUtils.rndCircle( testArea ) )
    val set1 = newSpatialSet( testArea, shapes )

    val result = set1.refresh( )

    result.toList.forall( s => shapes.contains( s ) ) should equal( true )
  }

  "The refresh member" must "work for empty sets" in {
    val set1 = newSpatialSet( testArea, List.empty[Shape] )

    try {
      set1.refresh( )
    }
    catch {
      case e: Exception ⇒ fail( )
    }
  }

  //
  // size testing
  //

  "The size" must "be zero when used on an empty set" in {
    val result = newSpatialSet( testArea, List.empty[Shape] )
    result.size should equal( 0 )
  }

  "The size" must "match the number of items when used on a non-empty set" in {
    val shapes = List.fill( 3 )( ShapeUtils.rndCircle( testArea ) )
    val result = newSpatialSet( testArea, shapes )
    result.size should equal( shapes.length )
  }

  //
  // toList testing
  //

  "The toList member" must "return the list of all the objects" in {
    val shapes = List.fill( 3 )( ShapeUtils.rndCircle( testArea ) )
    val result = newSpatialSet( testArea, shapes )

    result.toList.toSet should equal( shapes.toSet )
  }
}
