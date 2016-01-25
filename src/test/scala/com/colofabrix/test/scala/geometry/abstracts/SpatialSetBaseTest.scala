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
import org.scalatest.{ FlatSpec, Matchers }

/**
  * Test to cover the common features of all the Spatial Sets
  */
trait SpatialSetBaseTest[+T <: SpatialSet[Shape]] extends FlatSpec with Matchers {
  /**
    * Default Box object used to define
    */
  protected val defaultBox = Box( 400, 300 )

  /**
    * Creates a new object of type T to test
    *
    * @param bounds  The area covered by the object
    * @param objects The objects to add to the list
    * @return A new instance of a SpatialSet[T]
    */
  protected def getNewSpatialSet( bounds: Box, objects: List[Shape] ): T

  //
  // Constructor testing
  //

  "The base constructor" must "create a valid empty Set" in {
    val result = getNewSpatialSet( defaultBox, List.empty[Shape] )

    result.isEmpty should equal( true )
    result.size should equal( 0 )
    result.toList.length should equal( 0 )

    result.bounds should equal( defaultBox )
  }

  "The base constructor" must "create a set with initial data" in {
    val boxes = List.fill( 3 )( ShapeUtils.randomCircle( defaultBox ) )
    val result = getNewSpatialSet( defaultBox, boxes )

    result.isEmpty should equal( false )
    result.size should equal( boxes.size )
    result.toList.length shouldNot equal( 0 )

    result.bounds should equal( defaultBox )
  }

  //
  // +() methond testing
  //

  "The add member" must "add a new Shape" in {
    val set = getNewSpatialSet( defaultBox, List.empty[Shape] )
    val testShape = ShapeUtils.randomCircle( defaultBox )

    val result = set + testShape

    result.isEmpty should equal( false )
    result.size should equal( 1 )
    result.toList.length should equal( 1 )

    result.toList.contains( testShape ) should equal( true )
  }

  "The add member" must "not add an existing Shape" in {
    val set = getNewSpatialSet( defaultBox, List.empty[Shape] )
    val testShape = ShapeUtils.randomCircle( defaultBox )

    val intermediateResult = set + testShape
    val result = intermediateResult + testShape

    result.isEmpty should equal( false )
    result.size should equal( 1 )
    result.toList.length should equal( 1 )

    result.toList.contains( testShape ) should equal( true )
  }

  "The add member" must "add many shapes" in {
    val set: SpatialSet[Shape] = getNewSpatialSet( defaultBox, List.empty[Shape] )
    val shapes = List.fill( 100 )( ShapeUtils.randomCircle( defaultBox ) )

    val result = shapes.foldLeft( set )( _ + _ )

    result.isEmpty should equal( false )
    result.size should equal( shapes.size )
    result.toList.length should equal( shapes.size )
  }

  //
  // -() methond testing
  //

  "The remove member" must "remove an existing Shape" in {
    val testShape = ShapeUtils.randomCircle( defaultBox )
    val step1 = getNewSpatialSet( defaultBox, testShape :: Nil )

    try {
      val result = step1 - testShape

      result.isEmpty should equal( true )
      result.size should equal( 0 )
      result.toList.length should equal( 0 )
    }
    catch {
      case e: Exception ⇒ fail( )
    }
  }

  "The remove member" must "do nothing when the shape doesn't exist" in {
    val testShape = ShapeUtils.randomCircle( defaultBox )
    val testShape2 = ShapeUtils.randomCircle( defaultBox )
    val step1 = getNewSpatialSet( defaultBox, testShape :: Nil )

    try {
      val result = step1 - testShape2

      result.isEmpty should equal( false )
      result.size should equal( 1 )
      result.toList.length should equal( 1 )
    }
    catch {
      case e: Exception ⇒ fail( )
    }
  }

  "The remove member" must "not raise an exception when removing from an empty set" in {
    val set = getNewSpatialSet( defaultBox, List.empty[Shape] )
    val testShape = ShapeUtils.randomCircle( defaultBox )

    try {
      val result = set - testShape

      result.isEmpty should equal( true )
      result.size should equal( 0 )
      result.toList.length should equal( 0 )

      result.toList.contains( testShape ) should equal( false )
    }
    catch {
      case e: Exception ⇒ fail( )
    }
  }

  "The remove member" must "remove many shapes" in {
    val shapes = List.fill( 100 )( ShapeUtils.randomCircle( defaultBox ) )
    val set: SpatialSet[Shape] = getNewSpatialSet( defaultBox, shapes )

    val result = shapes.foldLeft( set )( _ - _ )

    result.isEmpty should equal( true )
    result.size should equal( 0 )
    result.toList.length should equal( 0 )
  }

  //
  // clear() member testing
  //

  "The clear member" must "return an empty set" in {
    val testShape = ShapeUtils.randomCircle( defaultBox )
    val step1 = getNewSpatialSet( defaultBox, testShape :: Nil )
    val result = step1.clear( )

    result.isEmpty should equal( true )
    result.size should equal( 0 )
    result.toList.length should equal( 0 )
  }

  //
  // isEmpty testing
  //

  "The isEmpty member" must "return the correct values" in {
    val set = getNewSpatialSet( defaultBox, List.empty[Shape] )
    val testShape = ShapeUtils.randomCircle( defaultBox )

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
    val shapes = List.fill( 5 )( ShapeUtils.randomCircle( defaultBox ) )
    val set1 = getNewSpatialSet( defaultBox, shapes )

    set1.lookAround( shapes( 0 ) ).isEmpty should equal( false )
  }

  "The lookAround member" must "return an empty set when given any object outside the area" in {
    val shapes = List.fill( 5 )( ShapeUtils.randomCircle( defaultBox ) )
    val testShape = ShapeUtils.randomCircle( Box( -100, -200 ) )
    val set1 = getNewSpatialSet( defaultBox, shapes )

    set1.lookAround( testShape ).isEmpty should equal( true )
  }

  "The lookAround member" must "return an empty set when the Set is empty" in {
    val set1 = getNewSpatialSet( defaultBox, List.empty[Shape] )
    val testShape = ShapeUtils.randomCircle( defaultBox )

    set1.lookAround( testShape ).isEmpty should equal( true )
  }

  "The lookAround member" must "search around any Shape" in {
    try {
      val boxes = List.fill( 5 )( ShapeUtils.randomCircle( defaultBox ) )
      val set1 = getNewSpatialSet( defaultBox, boxes )

      set1.lookAround( ShapeUtils.randomBox( defaultBox ) )
      set1.lookAround( ShapeUtils.randomCircle( defaultBox ) )
      set1.lookAround( ShapeUtils.randomPolygon( defaultBox ) )
    }
    catch {
      case e: Exception ⇒ fail( )
    }
  }

  //
  // refresh() testing
  //

  "The refresh member" must "not alter the existing set" in {
    val boxes = List.fill( 3 )( ShapeUtils.randomCircle( defaultBox ) )
    val set1 = getNewSpatialSet( defaultBox, boxes )

    val result = set1.refresh( )

    result.toList.forall( s => boxes.contains( s ) ) should equal( true )
  }

  "The refresh member" must "work for empty sets" in {
    val set1 = getNewSpatialSet( defaultBox, List.empty[Shape] )

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
    val result = getNewSpatialSet( defaultBox, List.empty[Shape] )
    result.size should equal( 0 )
  }

  "The size" must "match the number of items when used on a non-empty set" in {
    val boxes = List.fill( 3 )( ShapeUtils.randomCircle( defaultBox ) )
    val result = getNewSpatialSet( defaultBox, boxes )
    result.size should equal( boxes.length )
  }

  //
  // toList testing
  //

  "The toList member" must "return the list of all the objects" in {
    val boxes = List.fill( 3 )( ShapeUtils.randomCircle( defaultBox ) )
    val result = getNewSpatialSet( defaultBox, boxes )

    result.toList should equal( boxes )
  }
}
