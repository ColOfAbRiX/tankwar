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

  lazy val isSimple = false

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
    val direction = Math.signum( edges(1) ^ edges(0) )

    this.edgesIterator.tail forall {
      case u :: v :: Nil =>
        val r = v ^ u
        Math.signum( r ) == direction || r == 0
    }
  }

  /**
   * Compute the distance between a point and the edges of the polygon
   *
   * @param p Point to check
   * @return A distance vector from the point to polygon and the edge from which the distance is calculated
   */
  def distance(p: Vector2D): (Vector2D, Vector2D) = {
    // If the point is inside the polygon....
    if( overlaps(p) ) return (Vector2D.new_xy(0, 0), Vector2D.new_xy(0, 0))

    verticesIterator.map({
      case v0 :: v1 :: Nil =>
        (distance(v0, v1, p), v1 - v0)
    }).toList.minBy( _._1.r )
  }

  /**
   * Compute the distance between a line and the edges of the polygon
   *
   * @param p0 The first point that defines the line
   * @param p1 The second point that defines the line
   * @return A distance vector from the point to polygon and the edge or point from which the distance is calculated
   */
  override def distance(p0: Vector2D, p1: Vector2D): (Vector2D, Vector2D) = {
    // If the point is inside the polygon....
    if( overlaps(p0, p1) ) return (Vector2D.new_xy(0, 0), Vector2D.new_xy(0, 0))

    vertices.map({
      v => (distance(p0, p1, v), v)
    }).toList.minBy( _._1.r )
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
   * Determines if a shape is inside or on the boundary this shape
   *
   * I use the simple assumption that if one of the vertices is inside the other polygon
   * (for both the polygons) then the two polygon overlaps. There is absolutely no proof
   * of this and it probably the complexity is not optimal
   * UPDATE: Proved to be not true for every polygon
   *
   * @param that The point to be checked
   * @return True if the point is inside the shape
   */
  def overlaps(that: Polygon): Boolean = {
    val thisInThat = vertices.foldLeft(false) { (r, v) => r || that.overlaps(v) }
    val thatInThis = that.vertices.foldLeft(false) { (r, v) => r || overlaps(v) }
    thisInThat || thatInThis
  }

  /**
   * Determines if a line touches in any way this shape
   *
   * @param p0 The first point that defines the line
   * @param p1 The second point that defines the line
   * @return True if the point is inside the shape
   */
  override def overlaps(p0: Vector2D, p1: Vector2D): Boolean = distance(p0, p1)._1.r == 0.0

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
   * Moves a polygon
   *
   * @param where The vector specifying how to move the polygon
   * @return A new polygon moved of `where`
   */
  override def move(where: Vector2D) = {
    new Polygon( vertices.map(v => v + where) )
  }

  /**
   * Find a containing box for the current shape.
   *
   * It returns the smallest area between a box or a circle that fully contain
   * the current shape.
   * Implementation of the algorithm ref: http://geomalgorithms.com/a08-_containers.html
   *
   * @return A shape that fully contains this shape
   */
  override lazy val container: Shape = {
    // Finds the minimum and maximum coordinates for the points
    var (minX, minY) = (Double.MaxValue, Double.MaxValue)
    var (maxX, maxY) = (Double.MinValue, Double.MinValue)

    for (v <- vertices) {
      minX = if(minX > v.x) v.x else minX
      minY = if(minY > v.y) v.y else minY
      maxX = if(maxX < v.x) v.x else maxX
      maxY = if(maxY < v.y) v.y else maxY
    }

    // Creates the Box
    val bottomLeft = Vector2D.new_xy(minX, minY)
    val topRight = Vector2D.new_xy(maxX, maxY)
    val box = new Box(bottomLeft, topRight)

    // Creates the Circle
    val distance = (topRight - bottomLeft) / 2
    val circle = new Circle(distance + bottomLeft, distance.r)

    // Returns whichever has got the smallest area
    if(box.width * box.height < 2.0 * circle.radius * Math.PI)
      box
    else
      circle
  }
}
