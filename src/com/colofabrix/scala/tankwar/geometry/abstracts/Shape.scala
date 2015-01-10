package com.colofabrix.scala.tankwar.geometry.abstracts

import com.colofabrix.scala.tankwar.geometry.Vector2D

/**
 * Represents a graphical shape
 *
 * TODO: Unit test this class
 */
trait Shape {
  /**
   * Vertexes of the shape, if any. The vertexes should be enumerated
   * clockwise starting from the bottom-left one.
   */
  val vertices: Seq[Vector2D]

  /**
   * Edges of the shape, built from the vertices
   */
  val edges: Seq[Vector2D] = if (vertices.length > 2) (vertices :+ vertices.head).sliding(2) map { v => v(1) - v(0) } toList else Seq()

  /**
   * Determines if a point is inside or on the boundary the shape
   *
   * @param p The point to be checked
   * @return True if the point is inside the shape
   */
  def overlaps( p: Vector2D ): Boolean

  /**
   * Determines if two shapes touches each other
   *
   * Implementation of the Separating Axes Theorem
   * Ref: http://www.sevenson.com.au/actionscript/sat/
   *
   * @param that The shape to be checked
   * @return True if the shape touches the current shape
   */
  def overlaps( that: Shape ): Boolean = {
    // Circles can't be compared here as long as lines
    require( this.edges.length > 0 && that.vertices.length > 0 )

    this.edges.map( edge => edge.n ).
      forall { edge_normal =>
        // Project all vertices of this to the normal
        val prVxThis = this.vertices.map(v => (v -> edge_normal).r)
        // Project all vertices of that to the normal
        val prVxThat = that.vertices.map(v => (v -> edge_normal).r)

        // If there is no overlapping for all the vertices, then the 2 shapes touche
        if (prVxThis.min < prVxThat.min) prVxThis.max >= prVxThat.min
        else prVxThat.max >= prVxThis.min
      }
  }

  /**
   * Strip a point position from its current to one which is inside the shape.
   *
   * Usually this mean that the point is projected, over the boundary of the shape,
   * on the vector that represents the closer distance between the two objects
   *
   * @param p The point to move
   * @return A new point which is the nearest possible to the old one and inside the shape
   */
  def trimInside( p: Vector2D ): Vector2D
}
