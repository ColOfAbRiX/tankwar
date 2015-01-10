package com.colofabrix.scala.tankwar.tests

import com.colofabrix.scala.tankwar.geometry.{Circle, OrtoRectangle, Vector2D}
import org.scalatest.{Matchers, WordSpec}

/**
 * Unit testing for geometrical shapes
 *
 * Created by Fabrizio on 06/01/2015.
 */
class UTShapes extends WordSpec with Matchers {

  private val tolerance = 1E-5

  "A Shape" must {

      "Check if a point is inside it" when {

        "Is a Circle" must {

          val insidePoint = Vector2D.fromRT(50, Math.PI / 4)
          val outsidePoint = Vector2D.fromRT(100, -Math.PI)
          val centered = Circle(Vector2D.fromXY(0, 0), 50)
          val offCenter = Circle(Vector2D.fromXY(50, 50), 50)

          // The point is on the circumference
          centered.overlaps( insidePoint ) should equal (true)
          // The point is inside
          offCenter.overlaps( insidePoint ) should equal (true)

          // The point is outside
          centered.overlaps( outsidePoint ) should equal (false)
          offCenter.overlaps( outsidePoint ) should equal (false)

        }

        "Is a OrtoRectangle" must {

          val insidePoint = Vector2D.fromXY(5, 5)
          val outsidePoint = Vector2D.fromXY(20, 20)
          val rect = OrtoRectangle( Vector2D.fromXY(0,0), Vector2D.fromXY(10,10) )

          // The point is outside the rectangle
          rect.overlaps(insidePoint) should equal (true)
          // The point is inside the rectangle
          rect.overlaps(outsidePoint) should equal (false)

        }

      }

      "Check if it touches another Shape" when {

        "Is a Circle" must {

          val circle1 = Circle(Vector2D.fromXY(10, 10), 50)
          val circle2 = Circle(Vector2D.fromXY(-60, 80), 20)
          val circle3 = Circle(Vector2D.fromXY(20, 20), 20)

          // Check circle1
          circle1.overlaps(circle1) should equal (true)
          circle1.overlaps(circle2) should equal (false)
          circle1.overlaps(circle3) should equal (true)

          // Check circle2
          circle2.overlaps(circle2) should equal (true)
          circle2.overlaps(circle3) should equal (false)

          // Check circle3
          circle1.overlaps(circle3) should equal (true)

        }

        "Is a OrtoRectangle" must {

          val touchRect1 = OrtoRectangle( Vector2D.fromXY(0, 0), Vector2D.fromXY(10, 15) )
          val touchRect2 = OrtoRectangle( Vector2D.fromXY(5, 10), Vector2D.fromXY(15, 20) )
          val separateRect = OrtoRectangle( Vector2D.fromXY(30, 35), Vector2D.fromXY(40, 45) )

          touchRect1.overlaps(touchRect2) should equal (true)
          touchRect1.overlaps(separateRect) should equal (false)
          touchRect2.overlaps(separateRect) should equal (false)

        }

      }

      "Trim inside one external point" when {

        "Is a Circle" must {

          val insidePoint = Vector2D.fromXY(150, -150)
          val outsidePoint = Vector2D.fromXY(25, -25)

          val centered = Circle(Vector2D.fromXY(0, 0), 50)
          val offCenter = Circle(Vector2D.fromXY(50, -50), 50)

          // The point is projected on the circumference of the centered circle
          val projection1 = centered.trimInside(insidePoint)
          projection1.r should equal (50.0 +- tolerance)
          projection1.t should equal (-Math.PI / 4 +- tolerance)

          // The point is projected on the circumference of the off-center circle
          val projection2 = offCenter.trimInside(insidePoint)
          projection2.x should equal (85.35533905932738 +- tolerance)
          projection2.y should equal (-85.35533905932738 +- tolerance)

          val projection3 = centered.trimInside(outsidePoint)
          projection3.x should equal (25.0 +- tolerance)
          projection3.y should equal (-25.0 +- tolerance)

          val projection4 = offCenter.trimInside(outsidePoint)
          projection4.x should equal (25.0 +- tolerance)
          projection4.y should equal (-25.0 +- tolerance)

        }

        "Is a OrtoRectangle" must {

          val upperPoint = Vector2D.fromXY(5, 20)
          val rightPoint = Vector2D.fromXY(20, 5)
          val insidePoint = Vector2D.fromXY(5, 5)
          val rect = OrtoRectangle( Vector2D.fromXY(0,0), Vector2D.fromXY(10,10) )

          // The point is over the top of the rectangle
          rect.trimInside(upperPoint) should equal (Vector2D.fromXY(5, 10))
          // The point is over the right of the rectangle
          rect.trimInside(rightPoint) should equal (Vector2D.fromXY(10, 5))
          // The point is inside the rectangle
          rect.trimInside(insidePoint) should equal (Vector2D.fromXY(5, 5))
        }

      }

    }
}
