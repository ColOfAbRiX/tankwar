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

package com.colofabrix.test.scala.math

import com.colofabrix.scala.math.{ RTVect, XYVect }
import org.scalatest.{ Matchers, WordSpec }

import scala.language.postfixOps

/**
  * Unit testing for Vect
  *
  * Created by Fabrizio on 06/01/2015.
  */
class VectTest extends WordSpec with Matchers {

  val tolerance = 1E-5

  "Apply method" must {

    "Return the correct sequence of coordinates" in {
      val test = XYVect( 10, -10 )

      test( 0 ) should equal( 10.0 )
      test( 1 ) should equal( -10.0 )
      test( 2 ) should equal( 14.14213 +- tolerance )
      test( 3 ) should equal( Math.PI * 7.0 / 4.0 +- tolerance )
    }

  }

  "Algebra operators" when {

    "Equality" must {

      val cartesian1 = XYVect( 10.000000000000002, 10 )
      val cartesian2 = XYVect( 10.000000000000002, 10 )
      val polar1 = RTVect( 14.142135623730951, Math.PI / 4 )
      val polar2 = RTVect( 14.142135623730951, Math.PI / 4 )

      assert( cartesian1 == cartesian2 )
      assert( polar1 == polar2 )
      assert( cartesian1 == polar1 )

    }

    "Are addition/subtraction" must {

      val vec1 = XYVect( 10, 20 )
      val vec2 = XYVect( 30, 40 )

      val addition = vec1 + vec2
      val subtraction = vec1 - vec2

      addition.x should equal( 10 + 30 )
      addition.y should equal( 20 + 40 )

      subtraction.x should equal( 10 - 30 )
      subtraction.y should equal( 20 - 40 )

    }

    "Is scalar product" must {
      import com.colofabrix.scala.math.VectConversions._

      val vector = XYVect( 15, 25 )
      val scalar: Double = 12.345

      val result1 = vector * scalar
      val result2 = scalar * vector // With implicit
      val result3 = vector / scalar

      result1.x should equal( 15 * 12.345 +- tolerance )
      result1.y should equal( 25 * 12.345 +- tolerance )

      result2.x should equal( 15 * 12.345 +- tolerance )
      result2.y should equal( 25 * 12.345 +- tolerance )

      result3.x should equal( 15 / 12.345 +- tolerance )
      result3.y should equal( 25 / 12.345 +- tolerance )
    }

    "Is inner product" must {

      val vector1 = XYVect( 5, 10 )
      val vector2 = XYVect( 15, 20 )

      val result = vector1 x vector2

      result should equal( 5 * 15 + 10 * 20 )

    }

    "Is vector product" must {

      val vector1 = XYVect( 15, 25 )
      val vector2 = XYVect( 30, 35 )

      val result = vector1 ^ vector2

      //this.x * that.y - this.y * that.x
      result should equal( 15.0 * 35.0 - 25.0 * 30.0 +- tolerance )

    }

  }

  "Geometrical operators" when {

    "Map" must {

      val vector = XYVect( 12, 34 )
      val map = XYVect( 56, 78 )
      val projection = vector := map

      projection.x should equal( 12 * 56 )
      projection.y should equal( 34 * 78 )

    }

    "Projection" must {

      val vector = XYVect( 100, 100 )
      val axis = XYVect( 10, 0 )
      val projection = vector → axis

      projection.x should equal( 100.0 +- tolerance )
      projection.y should equal( 0.0 )

    }

    "Rotation" must {

      val vector = XYVect( 10, 10 )
      // Rotation half a turn
      val rotation1 = vector ¬ Math.PI
      // Rotation of 3 turns
      val rotation2 = vector ¬ ( 6 * Math.PI )

      rotation1.x should equal( -10.0 +- tolerance )
      rotation1.y should equal( -10.0 +- tolerance )

      rotation2.x should equal( 10.0 +- tolerance )
      rotation2.y should equal( 10.0 +- tolerance )

    }

    "Perpendicular" must {

      val vector = XYVect( 10, 10 )

      val ccw_perpendicular = vector -|
      val cw_perpendicular = vector |-

      ccw_perpendicular.x should equal( -10.0 +- tolerance )
      ccw_perpendicular.y should equal( 10.0 +- tolerance )

      cw_perpendicular.x should equal( 10.0 +- tolerance )
      cw_perpendicular.y should equal( -10.0 +- tolerance )

    }

    "Normal" must {

      val vector = XYVect( 25, 25 )
      val result = vector n

      result.ρ should equal( 1.0 )
      result.ϑ should equal( Math.PI / 4 + Math.PI / 2 +- tolerance )

    }

    "Unit vector" must {

      val vector = XYVect( 78, 78 )
      val result = vector v

      result.ρ should equal( 1.0 )
      result.ϑ should equal( Math.PI / 4 )

    }

  }

}