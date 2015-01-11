package com.colofabrix.scala.geometry.tests

import com.colofabrix.scala.geometry.Vector2D
import com.colofabrix.scala.geometry.shapes.Polygon
import org.scalatest.{Matchers, WordSpec}

/**
 * Unit testing for polygons
 *
 * Created by Fabrizio on 11/01/2015.
 */
class UTPolygon extends WordSpec with Matchers {

  "A generic polygon" when {

    "Matched with a point" must {

      "Find the correct distance" in {

        val triangle = new Polygon(Seq(Vector2D.fromXY(0, 0), Vector2D.fromXY(20, 0), Vector2D.fromXY(10, 20)))

        // It's inside, distance = 0
        val p0 = Vector2D.fromXY(10, 10)
        triangle.distance(p0)._1 should equal(Vector2D.fromXY(0, 0))
        triangle.distance(p0)._2 should equal(Vector2D.fromXY(0, 0))

        // Distance must be calculated from the 3rd vertex
        val p1 = Vector2D.fromXY(20, 40)
        triangle.distance(p1)._1 should equal(Vector2D.fromXY(-10, -20))
        triangle.distance(p1)._2 should equal(Vector2D.fromXY(-10, 20))

        //   "   "   "   " from the 2nd edge
        val p2 = Vector2D.fromXY(20, 20)
        triangle.distance(p2)._1 should equal(Vector2D.fromXY(-8, -4))
        triangle.distance(p2)._2 should equal(Vector2D.fromXY(-10, 20))

        //   "   "   "   " from the 2nd vertex
        val p3 = Vector2D.fromXY(20, -20)
        triangle.distance(p3)._1 should equal(Vector2D.fromXY(0, 20))
        triangle.distance(p3)._2 should equal(Vector2D.fromXY(20, 0))

        //   "   "   "   " from the 2nd vertex, 1st edge
        val p4 = Vector2D.fromXY(30, 0)
        triangle.distance(p4)._1 should equal(Vector2D.fromXY(-10, 0))
        triangle.distance(p4)._2 should equal(Vector2D.fromXY(20, 0))

      }

      "Determine if they overlaps" in {

        val triangle = new Polygon(Seq(Vector2D.fromXY(0, 0), Vector2D.fromXY(20, 0), Vector2D.fromXY(10, 20)))

        val insidePoint = Vector2D.fromXY(10, 10)
        val outsidePoint1 = Vector2D.fromXY(20, 40)
        val outsidePoint2 = Vector2D.fromXY(40, 0)

        triangle.overlaps(insidePoint) should equal (true)
        triangle.overlaps(outsidePoint1) should equal (false)
        triangle.overlaps(outsidePoint2) should equal (false)

      }

    }

    "Matched with another polygon" must {

      "Determine if they overlaps" in {

        val convex = new Polygon(Seq(Vector2D.fromXY(0, 0), Vector2D.fromXY(20, 0), Vector2D.fromXY(10, 20)))
        val concaveNoOverlap = new Polygon(Seq(Vector2D.fromXY(40, 0), Vector2D.fromXY(60, 0), Vector2D.fromXY(55, 20), Vector2D.fromXY(45, -20)))
        val concaveOverlap = new Polygon(Seq(Vector2D.fromXY(10, 10), Vector2D.fromXY(30, 10), Vector2D.fromXY(45, 30), Vector2D.fromXY(15, -10)))

        convex.overlaps(concaveNoOverlap) should equal (false)
        convex.overlaps(concaveOverlap) should equal (true)
        concaveNoOverlap.overlaps(concaveOverlap) should equal (false)

      }

    }

  }

  "A generic polygon" must {

    "Must have at least 3 edges" in {

      // Valid polygon
      new Polygon(Seq(Vector2D.fromXY(0, 0), Vector2D.fromXY(20, 0), Vector2D.fromXY(10, 20)))

      // Invalid polygon
      intercept[IllegalArgumentException] {
        val notValid = new Polygon(Seq(Vector2D.fromXY(0, 0), Vector2D.fromXY(20, 0)))
      }

    }

    "Be checked if it is convex" in {

      val convex = new Polygon(Seq(Vector2D.fromXY(0, 0), Vector2D.fromXY(20, 0), Vector2D.fromXY(10, 20)))
      val concave = new Polygon(Seq(Vector2D.fromXY(0, 0), Vector2D.fromXY(20, 0), Vector2D.fromXY(15, 20), Vector2D.fromXY(5, -20)))

      convex.isConvex should equal (true)
      concave.isConvex should equal (false)

    }

  }

}
