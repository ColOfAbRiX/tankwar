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

package com.colofabrix.test.scala.geometry

import com.colofabrix.scala.geometry.shapes.Box
import com.colofabrix.scala.math.Vector2D
import org.scalatest.{Matchers, WordSpec}

/**
 * Unit testing for OrtoRectangles
 *
 * Created by Fabrizio on 06/01/2015.
 */
class BoxTest extends WordSpec with Matchers {

  private val tolerance = 1E-5

  "An OrtoRectangle" must {

    "Check overlapping" when {

      "Matched with a point" must {

        val insidePoint = Vector2D.new_xy(5, 5)
        val outsidePoint = Vector2D.new_xy(20, 20)
        val rect = Box( Vector2D.new_xy(0,0), Vector2D.new_xy(10,10) )

        // The point is outside the rectangle
        rect.overlaps(insidePoint) should equal (true)
        // The point is inside the rectangle
        rect.overlaps(outsidePoint) should equal (false)

      }

      "Is a OrtoRectangle" must {

        val touchRect1 = Box( Vector2D.new_xy(0, 0), Vector2D.new_xy(10, 15) )
        val touchRect2 = Box( Vector2D.new_xy(5, 10), Vector2D.new_xy(15, 20) )
        val separateRect = Box( Vector2D.new_xy(30, 35), Vector2D.new_xy(40, 45) )

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
