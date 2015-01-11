package com.colofabrix.scala.geometry.tests

import com.colofabrix.scala.geometry.Vector2D
import com.colofabrix.scala.geometry.shapes.OrtoRectangle
import org.scalatest.{Matchers, WordSpec}

/**
 * Unit testing for OrtoRectangles
 *
 * Created by Fabrizio on 06/01/2015.
 */
class UTOrtoRectangle extends WordSpec with Matchers {

  private val tolerance = 1E-5

  "An OrtoRectangle" must {

    "Check overlapping" when {

      "Matched with a point" must {

        val insidePoint = Vector2D.fromXY(5, 5)
        val outsidePoint = Vector2D.fromXY(20, 20)
        val rect = OrtoRectangle( Vector2D.fromXY(0,0), Vector2D.fromXY(10,10) )

        // The point is outside the rectangle
        rect.overlaps(insidePoint) should equal (true)
        // The point is inside the rectangle
        rect.overlaps(outsidePoint) should equal (false)

      }

      "Is a OrtoRectangle" must {

        val touchRect1 = OrtoRectangle( Vector2D.fromXY(0, 0), Vector2D.fromXY(10, 15) )
        val touchRect2 = OrtoRectangle( Vector2D.fromXY(5, 10), Vector2D.fromXY(15, 20) )
        val separateRect = OrtoRectangle( Vector2D.fromXY(30, 35), Vector2D.fromXY(40, 45) )

        touchRect1.overlaps(touchRect1) should equal (true)
        touchRect1.overlaps(touchRect2) should equal (true)
        touchRect1.overlaps(separateRect) should equal (false)

        touchRect2.overlaps(touchRect2) should equal (true)
        touchRect2.overlaps(separateRect) should equal (false)

        separateRect.overlaps(separateRect) should equal (true)

      }

    }

  }

}
