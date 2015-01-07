package com.colofabrix.scala.tankwar.tests

import com.colofabrix.scala.tankwar.geometry.{CartesianCoord, PolarCoord}
import org.scalatest.{Matchers, WordSpec}

/**
 * Created by Fabrizio on 06/01/2015.
 */
class UTCoordinates extends WordSpec with Matchers {

  val tolerance = 1E-5

  "A polar coordinate" must {

    "Respect transformation rules" when {

      "Is transformed to valid cartesian coordinates" in {
        // Test vector 1: t=10u r=90° -> x=0, y=10
        val test_polar_1 = new PolarCoord(10, Math.PI / 2)
        // Test vector 1: t=10u r=180° -> x=-10, y=0
        val test_polar_2 = new PolarCoord(10, Math.PI)
        // Test vector 2: t=10u r=-45° -> x=7.0710678, y=-7.0710678
        val test_polar_3 = new PolarCoord(10, -Math.PI / 4)

        // Conversions
        val test_cartesian_1 = CartesianCoord(test_polar_1)
        val test_cartesian_2 = CartesianCoord(test_polar_2)
        val test_cartesian_3 = CartesianCoord(test_polar_3)

        // Test vector 1
        test_cartesian_1.x should equal (0.0 +- tolerance)
        test_cartesian_1.y should equal (10.0 +- tolerance)

        // Test vector 2
        test_cartesian_2.x should equal (-10.0 +- tolerance)
        test_cartesian_2.y should equal (0.0 +- tolerance)

        // Test vector 3
        test_cartesian_3.x should equal (7.0710678 +- tolerance)
        test_cartesian_3.y should equal (-7.0710678 +- tolerance)
      }

    }

  }

  "A cartesian coordinate" must {

    "Respect transformation rules" when {

      "Is transformed to valid polar coordinates" in {
        // Test vector 1: t=10u r=90° -> x=0, y=10
        val cartesian_1 = new CartesianCoord(0, 10)
        // Test vector 1: t=10u r=180° -> x=-10, y=0
        val cartesian_2 = new CartesianCoord(-10, 0)
        // Test vector 2: t=10u r=-45° -> x=7.0710678, y=-7.0710678
        val cartesian_3 = new CartesianCoord(7.0710678, -7.0710678)

        // Conversions
        val polar_1 = PolarCoord(cartesian_1)
        val polar_2 = PolarCoord(cartesian_2)
        val polar_3 = PolarCoord(cartesian_3)

        // Test vector 1
        polar_1.r should equal (10.0 +- tolerance)
        polar_1.t should equal (Math.PI / 2 +- tolerance)

        // Test vector 2
        polar_2.r should equal (10.0 +- tolerance)
        polar_2.t should equal (Math.PI +- tolerance)

        // Test vector 3
        polar_3.r should equal (10.0 +- tolerance)
        polar_3.t should equal (-Math.PI / 4 +- tolerance)
      }

    }

  }

}
