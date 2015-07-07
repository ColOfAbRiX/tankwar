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

package com.colofabrix.scala.geometry.shapes

import com.colofabrix.scala.geometry.abstracts.{ Container, Shape }
import com.colofabrix.scala.gfx.abstracts.{ Renderable, Renderer }
import com.colofabrix.scala.gfx.renderers.PolygonRenderer
import com.colofabrix.scala.math.Vector2D

/**
 * A generic two-dimensional polygon
 * A generic two-dimensional polygon
 */
class Polygon( val vertices: Seq[Vector2D] ) extends Shape with Renderable {
  // The smallest polygon is a triangle!!
  require(vertices.length > 2)

  /**
   * Edges of the shape, built from the vertices. Edges are {Vector2D} from one vertex to its adjacent one
   */
  val edges: Seq[Vector2D] = (vertices :+ vertices.head).sliding(2) map { v => v(1) - v(0) } toList

  /**
   * List of all adjacent vertexes of the polygon. An iterator to go from the first to the last vertex
   */
  val verticesIterator = (vertices :+ vertices.head).sliding(2).toSeq

  /**
   * List of all adjacent edges of the polygon. An iterator to go from the first to the last edge
   */
  val edgesIterator = (edges :+ edges.head).sliding(2).toSeq

  /**
   * Checks if a polygon is convex
   *
   * To be convex, the edges of a polygon must form between each others angles greater that 180. This is
   * checked calculating, for every adjacent edges, their rotation, given by the cross product, of the second
   * edge compared to the first.
   *
   * @see https://stackoverflow.com/questions/471962/how-do-determine-if-a-polygon-is-complex-convex-nonconvex
   * @return true if the polygon is convex
   */
  lazy val isConvex = {
    // The direction or rotation can be either CW or CCW as far as it is always the same or zero. This is the
    // direction of the first edge as a reference.
    val direction = Math.signum(edges(1) ^ edges(0))

    // Check the condition on all edges
    this.edgesIterator.tail forall {
      case u :: v :: Nil =>
        val r = v ^ u
        Math.signum(r) == direction || r == 0
    }
  }

  /**
   * Compute the distance between a point and the edges of the polygon
   *
   * Checks the distances from all the edges and returns the nearest one
   *
   * @param p Point to check
   * @return A tuple containing 1) the distance vector from the point to the polygon and 2) the edge from which the distance is calculated
   */
  def distance( p: Vector2D ): (Vector2D, Vector2D) = {
    // If the point is inside the polygon....
    if( contains(p) ) return (Vector2D.new_xy(0, 0), Vector2D.new_xy(0, 0))

    // Check all the vertices and return the nearest one.
    verticesIterator.map(
    {
      case v0 :: v1 :: Nil =>
        (distance(v0, v1, p), v1 - v0)
    }
    ).toList.minBy(_._1.r)
  }

  /**
   * Compute the distance between a line segment and the edges of the polygon
   *
   * @param p0 The first point that defines the line
   * @param p1 The second point that defines the line
   * @return A distance vector from the point to polygon and the edge or point from which the distance is calculated
   */
  override def distance( p0: Vector2D, p1: Vector2D ): (Vector2D, Vector2D) = {
    // FIXME: The logic is correct, but there is a circular reference with `intersects`
    // If the point is inside the polygon....
    if( intersects(p0, p1) ) return (Vector2D.new_xy(0, 0), Vector2D.new_xy(0, 0))

    // Check all the vertices and return the nearest one.
    vertices.map(
      v => (distance(p0, p1, v), v)
    ).minBy(_._1.r)
  }

  /**
   * Determines if a shape is inside or on the boundary this shape
   *
   * @param that The point to be checked
   * @return True if the point is inside the shape
   */
  override def intersects( that: Shape ): Boolean = that match {
    // With circles I find the nearest edge to the center and then I compare it to the radius to see if it's inside
    case c: Circle => distance(c.center)._1 <= c.radius

    // For polygons I use the proper internal function
    case p: Polygon => intersects(p)

    // Other comparisons are not possible
    case _ => false
  }

  /**
   * Determines if a point is inside or on the boundary the shape
   *
   * @see http://geomalgorithms.com/a03-_inclusion.html#cn_PinPolygon%28%29
   * @param p The point to be checked
   * @return True if the point is inside the shape
   */
  override def contains( p: Vector2D ): Boolean = {
    var wn = 0

    verticesIterator foreach {
      case v0 :: v1 :: Nil =>
        if( v0.y <= p.y ) {
          if( v1.y > p.y ) {
            if( checkTurn(v0, v1, p) > 0.0 ) wn += 1
          }
        }
        else {
          if( v1.y <= p.y ) {
            if( checkTurn(v0, v1, p) < 0.0 ) wn -= 1
          }
        }
    }

    wn != 0
  }

  /**
   * Determines if a shape is inside or on the boundary this shape
   *
   * I use the simple assumption that if one of the vertices is inside the other polygon
   * (for both the polygons) then the two polygon intersects. There is absolutely no proof
   * of this and it probably the complexity is not optimal
   * UPDATE: Proved to be not true for every polygon
   *
   * @param that The point to be checked
   * @return True if the point is inside the shape
   */
  def intersects( that: Polygon ): Boolean = {
    val thisInThat = vertices.foldLeft(false) { ( r, v ) => r || that.contains(v) }
    val thatInThis = that.vertices.foldLeft(false) { ( r, v ) => r || contains(v) }
    thisInThat || thatInThis
  }

  /**
   * Determines if a line segment touches in any way this shape
   *
   * @param p0 The first point that defines the line
   * @param p1 The second point that defines the line
   * @return True if the point is inside the shape
   */
  override def intersects( p0: Vector2D, p1: Vector2D ): Boolean = distance(p0, p1)._1.r == 0.0

  /**
   * Tests if a point is Left|On|Right of an infinite line.
   *
   * This is a faster version (less calculations) of the vector product of two vectors
   * defined by three points.
   *
   * @see http://algs4.cs.princeton.edu/91primitives/
   * @param v0 First segment point
   * @param v1 Second segment point
   * @param p Point to check
   * @return >0 for P2 left of the line through P0 and P1, =0 for P2  on the line, <0 for P2  right of the line
   */
  private def checkTurn( v0: Vector2D, v1: Vector2D, p: Vector2D ): Double =
    (v1.x - v0.x) * (p.y - v0.y) - (p.x - v0.x) * (v1.y - v0.y)

  /**
   * Moves a polygon shifting all its vertices by a vector quantity
   *
   * @param where The vector specifying how to move the polygon
   * @return A new polygon moved of {where}
   */
  override def move( where: Vector2D ) = new Polygon(vertices.map(v => v + where))

  /**
   * Find a containing box for the current shape.
   *
   * For simplicity the current implementation always returns a {Box}
   *
   * @see http://geomalgorithms.com/a08-_containers.html
   * @return A shape that fully contains this shape
   */
  override lazy val container: Container = Container.bestFit(this)

  /**
   * Area of the polygon
   *
   * @see http://geomalgorithms.com/a01-_area.html
   */
  lazy val area = {
    (vertices :+ vertices.head).sliding(3).map {
      case v0 :: v1 :: v2 :: Nil => v1.x * (v2.y - v0.y)
    }.sum / 2.0
  }

  /**
   * A renderer for a generic polygon
   *
   * @return A new instance of PolygonRenderer for the current polygon
   */
  override def renderer: Renderer = new PolygonRenderer( this )
}
