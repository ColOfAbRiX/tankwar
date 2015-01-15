package com.colofabrix.scala.geometry.shapes

import com.colofabrix.scala.geometry.Vector2D
import com.colofabrix.scala.geometry.abstracts._

/**
 * A convex polygon
 *
 * Created by Fabrizio on 10/01/2015.
 */
class ConvexPolygon(private val v: Seq[Vector2D]) extends Polygon (v) {
  require( this.isConvex, "The vertices don't define a convex polygon" )

   /**
    * Determines if two shapes touches each other
    *
    * Implementation of the Separating Axes Theorem
    * Ref: http://www.sevenson.com.au/actionscript/sat/
    * This is faster than the `Polygon` implementation
    *
    * @param that The shape to be checked
    * @return True if the shape touches the current shape
    */
  def overlaps( that: ConvexPolygon ): Boolean = {
    this.edges.map( edge => edge.n ).
      forall { edge_normal =>
        val prVxThis = this.vertices.map(v => (v -> edge_normal).r)
        val prVxThat = that.vertices.map(v => (v -> edge_normal).r)

        if (prVxThis.min < prVxThat.min) prVxThis.max >= prVxThat.min
        else prVxThat.max >= prVxThis.min
    }
  }

  override def overlaps( that: Shape ): Boolean = {
    that match {
      case p: Polygon => p.overlaps(this)
      case c: Circle => c.overlaps(this)
    }
  }
}