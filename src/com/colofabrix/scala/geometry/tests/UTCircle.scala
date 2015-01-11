package com.colofabrix.scala.geometry.tests

import com.colofabrix.scala.geometry.Vector2D
import com.colofabrix.scala.geometry.shapes.Circle
import org.scalatest.{Matchers, WordSpec}

/**
 * Unit testing for Circle Shape
 *
 * Created by Fabrizio on 11/01/2015.
 */
class UTCircle extends WordSpec with Matchers {

  "A Circle" must {

    "Check overlapping" when {

      "Matched with another circle" in {

        val circle1 = Circle(Vector2D.fromXY(10, 10), 50)
        val circle2 = Circle(Vector2D.fromXY(-60, 80), 20)
        val circle3 = Circle(Vector2D.fromXY(20, 20), 20)

        // Check circle1
        circle1.overlaps(circle1) should equal(true)
        circle1.overlaps(circle2) should equal(false)
        circle1.overlaps(circle3) should equal(true)

        // Check circle2
        circle2.overlaps(circle2) should equal(true)
        circle2.overlaps(circle3) should equal(false)

        // Check circle3
        circle1.overlaps(circle3) should equal(true)

      }

      "Matched with a point" must {

        val insidePoint = Vector2D.fromRT(50, Math.PI / 4)
        val outsidePoint = Vector2D.fromRT(100, -Math.PI)
        val centered = Circle(Vector2D.fromXY(0, 0), 50)
        val offCenter = Circle(Vector2D.fromXY(50, 50), 50)

        // The point is on the circumference
        centered.overlaps(insidePoint) should equal(true)
        // The point is inside
        offCenter.overlaps(insidePoint) should equal(true)

        // The point is outside
        centered.overlaps(outsidePoint) should equal(false)
        offCenter.overlaps(outsidePoint) should equal(false)

      }
    }

  }

}
