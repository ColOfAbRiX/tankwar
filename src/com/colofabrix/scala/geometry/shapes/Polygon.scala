package com.colofabrix.scala.geometry.shapes

import com.colofabrix.scala.geometry.Vector2D
import com.colofabrix.scala.geometry.abstracts.Shape

/**
 * A generic 2D polygon
 *
 * Created by Fabrizio on 10/01/2015.
 */
class Polygon(val vertices: Seq[Vector2D]) extends Shape {
  // The smallest polygon is a triangle!!
  require( vertices.length > 2 )

  /**
   * Edges of the shape, built from the vertices
   */
  val edges: Seq[Vector2D] = (vertices :+ vertices.head).sliding(2) map { v => v(1) - v(0) } toList

  /**
   * List of all adjacent vertexes of the polygon
   */
  val verticesIterator = (vertices :+ vertices.head).sliding(2).toSeq

  /**
   * List of all adjacent edges of the polygon
   */
  val edgesIterator = (edges :+ edges.head).sliding(2).toSeq

  /**
   * Checks if a polygon is convex
   *
   * To be convex, the edges of a polygon must form between each others angles greater that 180. This is
   * checked calculating, for every adjacent edges, their rotation, given by the cross product, of the second
   * edge compared to the first.
   * Ref: https://stackoverflow.com/questions/471962/how-do-determine-if-a-polygon-is-complex-convex-nonconvex
   *
   * @return true if the polygon is convex
   */
  lazy val isConvex = {
    // The direction or rotation can be either CW or CCW as far as it is always the same or zero
    val direction = Math.signum( this.edges(1) ^ this.edges(0) )

    this.edgesIterator.tail forall {
      case u :: v :: Nil =>
        val r = v ^ u
        Math.signum( r ) == direction || r == 0
    }
  }

  lazy val isSimple = { false }

  /**
   * Compute the distance between a point and the boundary polygon
   *
   * @param p Point to check
   * @return A distance vector from the point to polygon and the edge from which the distance is calculated
   */
  def distance(p: Vector2D): (Vector2D, Vector2D) = {
    // If the point is inside the polygon....
    if( this.overlaps(p) ) return (Vector2D.fromXY(0, 0), Vector2D.fromXY(0, 0))

    verticesIterator.map({
      case v0 :: v1 :: Nil =>
        (distance(v0, v1, p), v1 - v0)
    }).toList.minBy( _._1.r )
  }

  /**
   * Compute the distance between a point and a line segment
   *
   * Implementation of the algorithm: http://geomalgorithms.com/a02-_lines.html
   *
   * @param v0 First end of the segment
   * @param v1 Second end of the segment
   * @param p Point to check
   * @return A distance vector from the point to the segment or one of its ends
   */
  def distance(v0: Vector2D, v1: Vector2D, p: Vector2D): Vector2D = {
    val v = v1 - v0
    val w = p - v0
    val c1 = v x w
    val c2 = v x v

    if( c1 <= 0.0 )
      return v0 - p
    else if( c2 <= c1 )
      return v1 - p

    val pb = v0 + v * (c1 / c2)
    pb - p
  }

  /**
   * Determines if a shape is inside or on the boundary this shape
   *
   * @param that The point to be checked
   * @return True if the point is inside the shape
   */
  override def overlaps(that: Shape): Boolean = that match {
    case c: Circle => c.overlaps(this)
    case _ => false
  }

  /**
   * Determines if a point is inside or on the boundary the shape
   *
   * Implementation of the algorithm http://geomalgorithms.com/a03-_inclusion.html#cn_PinPolygon%28%29
   *
   * @param p The point to be checked
   * @return True if the point is inside the shape
   */
  override def overlaps(p: Vector2D): Boolean = {
    var wn = 0

    verticesIterator foreach {
      case v0 :: v1 :: Nil =>
        if (v0.y <= p.y) {
          if (v1.y > p.y) {
            if (checkTurn(v0, v1, p) > 0.0) wn += 1
          }
        }
        else {
          if (v1.y <= p.y) {
            if (checkTurn(v0, v1, p) < 0.0) wn -= 1
          }
        }
    }

    wn != 0
  }

  /**
   * Tests if a point is Left|On|Right of an infinite line.
   *
   * This is a faster version (less calculations) of the vector product of two vectors
   * defined by three points.
   * Reference: http://algs4.cs.princeton.edu/91primitives/
   *
   * @param v0 First segment point
   * @param v1 Second segment point
   * @param p Point to check
   * @return >0 for P2 left of the line through P0 and P1, =0 for P2  on the line, <0 for P2  right of the line
   */
  private def checkTurn(v0: Vector2D, v1: Vector2D, p: Vector2D): Double =
    (v1.x - v0.x) * (p.y - v0.y) - (p.x - v0.x) * (v1.y - v0.y)

  /**
   * Determines if a shape is inside or on the boundary this shape
   *
   * I use the simple assumption that if one of the vertices is inside the other polygon
   * (for both the polygons) then the two polygon overlaps. There is absolutely no proof
   * of this and it probably the complexity is not optimal
   *
   * @param that The point to be checked
   * @return True if the point is inside the shape
   */
  def overlaps(that: Polygon): Boolean = {
    val thisInThat = this.vertices.foldLeft(false) { (r, v) => r || that.overlaps(v) }
    val thatInThis = that.vertices.foldLeft(false) { (r, v) => r || this.overlaps(v) }
    thisInThat || thatInThis
  }
}
