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

import com.colofabrix.scala.math.{ CartesianCoord, PolarCoord }
import org.scalatest.prop.PropertyChecks
import org.scalatest.{ FlatSpec, Matchers }

/**
  * Unit testing for coordinate systems
  *
  * Created by Fabrizio on 06/01/2015.
  */
class CoordinatesTest extends FlatSpec with Matchers {

  val tolerance = 1E-6

  "A polar coordinate" must "be transformed to valid cartesian coordinates" in {
    // Test vector 1: t=10u r=90° -> x=0, y=10
    val polar_1 = PolarCoord( 10, Math.PI / 2 )
    // Test vector 1: t=10u r=180° -> x=-10, y=0
    val polar_2 = PolarCoord( 10, Math.PI )
    // Test vector 2: t=10u r=-45° -> x=7.0710678, y=-7.0710678
    val polar_3 = PolarCoord( 10, -Math.PI / 4 )

    // Conversions
    val cartesian_1 = CartesianCoord( polar_1 )
    val cartesian_2 = CartesianCoord( polar_2 )
    val cartesian_3 = CartesianCoord( polar_3 )

    // Test vector 1
    cartesian_1.x should equal( 0.0 +- tolerance )
    cartesian_1.y should equal( 10.0 +- tolerance )

    // Test vector 2
    cartesian_2.x should equal( -10.0 +- tolerance )
    cartesian_2.y should equal( 0.0 +- tolerance )

    // Test vector 3
    cartesian_3.x should equal( 7.0710678 +- tolerance )
    cartesian_3.y should equal( -7.0710678 +- tolerance )
  }

  "A cartesian coordinate" must "be transformed to valid polar coordinates" in {
    // Test vector 1: t=10u r=90° -> x=0, y=10
    val cartesian_1 = CartesianCoord( 0, 10 )
    // Test vector 1: t=10u r=180° -> x=-10, y=0
    val cartesian_2 = CartesianCoord( -10, 0 )
    // Test vector 2: t=10u r=-45° -> x=7.0710678, y=-7.0710678
    val cartesian_3 = CartesianCoord( 7.0710678, -7.0710678 )

    // Conversions
    val polar_1 = PolarCoord( cartesian_1 )
    val polar_2 = PolarCoord( cartesian_2 )
    val polar_3 = PolarCoord( cartesian_3 )

    // Test vector 1
    polar_1.r should equal( 10.0 +- tolerance )
    polar_1.t should equal( Math.PI / 2 +- tolerance )

    // Test vector 2
    polar_2.r should equal( 10.0 +- tolerance )
    polar_2.t should equal( Math.PI +- tolerance )

    // Test vector 3
    polar_3.r should equal( 10.0 +- tolerance )
    polar_3.t should equal( 7.0 / 4.0 * Math.PI +- tolerance )
  }

  "A polar coordinate" must "satisfy equality when compared to another polar coordinate" in {
    val polar_1 = PolarCoord( 10, Math.PI )
    val polar_2 = PolarCoord( 10, Math.PI )
    val polar_3 = PolarCoord( 14, Math.PI / 2.0 )

    polar_1 should equal( polar_2 )
    polar_1 shouldNot equal( polar_3 )
  }

  "A polar coordinate" must "satisfy equality when compared to cartesian coordinate" in {
    val polar_1 = PolarCoord( 10, Math.PI )
    val polar_2 = PolarCoord( 10, Math.PI )
    val cartesian_1 = CartesianCoord( -10, 0 )
    val cartesian_2 = CartesianCoord( 5, 4 )

    polar_1 should equal( cartesian_1 )
    polar_2 should equal( cartesian_1 )
    polar_2 shouldNot equal( cartesian_2 )
  }

  "A cartesian coordinate" must "satisfy equality when compared to another cartesian coordinate" in {
    val cartesian_1 = CartesianCoord( 10, Math.PI )
    val cartesian_2 = CartesianCoord( 10, Math.PI )
    val cartesian_3 = CartesianCoord( 14, Math.PI / 2.0 )

    cartesian_1 should equal( cartesian_2 )
    cartesian_1 shouldNot equal( cartesian_3 )
  }

  "A cartesian coordinate" must "satisfy equality when compared to polar coordinate" in {
    val polar_1 = PolarCoord( 10, Math.PI )
    val polar_2 = PolarCoord( 10, Math.PI )
    val cartesian_1 = CartesianCoord( -10, 0 )
    val cartesian_2 = CartesianCoord( 5, 4 )

    cartesian_1 should equal( polar_1 )
    cartesian_1 should equal( polar_2 )
    cartesian_2 shouldNot equal( polar_2 )
  }
}