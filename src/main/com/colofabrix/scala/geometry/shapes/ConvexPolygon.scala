package com.colofabrix.scala.geometry.shapes

import com.colofabrix.scala.math.Vector2D

/**
 * A convex polygon
 */
class ConvexPolygon(private val v: Seq[Vector2D]) extends Polygon (v) {
  require( this.isConvex, "The vertices don't define a convex polygon" )

   /**
    * Determines if two shapes touches each other
    *
    * Implementation of the Separating Axes Theorem. This is faster than the default {Polygon} implementation and
    * valid only for convex polygons
    *
    * @see http://www.sevenson.com.au/actionscript/sat/
    * @param that The shape to be checked
    * @return True if the shape touches the current shape
    */
  def overlaps( that: ConvexPolygon ): Boolean = {
    this.edges.map( edge => edge.n ).
      // Chosen an edge of this polygon...
      forall { edge_normal =>
        // ...I get the projections of all vertices on the normal of the edge
        val prVxThis = this.vertices.map(v => (v -> edge_normal).r)
        val prVxThat = that.vertices.map(v => (v -> edge_normal).r)

        // Then I check if the extremities of the projections of the two polygons overlaps
        if (prVxThis.min < prVxThat.min)
          prVxThis.max >= prVxThat.min
        else
          prVxThat.max >= prVxThis.min
    }
  }
}