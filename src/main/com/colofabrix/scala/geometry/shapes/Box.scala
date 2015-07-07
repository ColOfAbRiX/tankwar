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
import com.colofabrix.scala.gfx.renderers.BoxRenderer
import com.colofabrix.scala.math.Vector2D


/**
 * Rectangle shape with edges parallel to the cartesian axis
 *
 * This kind of shape is particularly useful in checking overlaps and collisions
 * as it is done in constant time O(k) and without complex mathematical operations.
 * For this reasons, more than being only a {ConvexPolygon} it is also a {Container}
 *
 * @see http://geomalgorithms.com/a08-_containers.html
 * @param bottomLeft Rectangle left-bottom-most point, in any quadrant of the plane
 * @param topRight Rectangle right-top point, in any quadrant of the plane
 */
case class Box( bottomLeft: Vector2D, topRight: Vector2D )
  extends ConvexPolygon(
    Seq(
      bottomLeft,
      Vector2D.new_xy( bottomLeft.x, topRight.y ),
      topRight,
      Vector2D.new_xy( topRight.x, bottomLeft.y )
    )
  )
  with Container {

  require( bottomLeft.x < topRight.x && bottomLeft.y < topRight.y, "The points of the rectangle must respect their spatial meaning" )

  /**
   * Area of the box
   */
  override lazy val area = width * height
  /**
   * Find a containing box for the current shape.
   *
   * @return A Box that is the same as the current one (as it's always the minimal container for this Shape)
   */
  override lazy val container: Container = this
  /**
   * Center of the Box
   */
  val center = bottomLeft + Vector2D.new_xy( (topRight.x - bottomLeft.x) / 2.0, (topRight.y - bottomLeft.y) / 2.0 )
  /**
   * Height of the rectangle
   */
  val height = topRight.y - bottomLeft.y
  /**
   * Width of the rectangle
   */
  val width = topRight.x - bottomLeft.x

  /**
   * Constructor that uses width, height and the center of the box
   *
   * @param center Center of the box
   * @param width Width of the box
   * @param height Height of the box
   */
  def this( center: Vector2D, width: Double, height: Double ) {
    this(
      Vector2D.new_xy( center.x - width / 2.0, center.y - height / 2.0 ),
      Vector2D.new_xy( center.x + width / 2.0, center.y + height / 2.0 )
    )
  }

  /**
   * Determines if the container fully contain a Shape
   *
   * The Box contains the Shape if all the vertices of the Shape are inside the Box. The
   * condition is sufficient as a Box is a `ConvexPolygon`
   *
   * @param s The shape to check
   * @return true if the container fully contain the other shape. Boundaries are included in the container
   */
  override def contains( s: Shape ): Boolean = s match {
    // For the case Box-Polygon I check that all its vertices are inside the box - O(n)
    case p: Polygon => p.vertices.forall( v => this.contains( v ) )

    // For the case Box-Circle I use the fact the an enclosing Box for the Circle has its borders parallel to the current one - O(n)
    case c: Circle => this.contains( Box.bestFit( c ) )

    // Other cases are not contained
    case _ => false
  }

  /**
   * Determines if a shape is inside or on the boundary this shape
   *
   * @param that The point to be checked
   * @return True if the point is inside the shape
   */
  def intersects( that: Circle ): Boolean = that.intersects( this )

  /**
   * A renderer for a box
   *
   * @return A new instance of BoxRenderer for the current polygon
   */
  override def renderer = new BoxRenderer( this )

  /**
   * Determines if a point is inside or on the boundary the shape
   *
   * @param p The point to be checked
   * @return True if the point is inside the shape
   */
  override def contains( p: Vector2D ) =
    p.x >= bottomLeft.x &&
      p.x <= topRight.x &&
      p.y >= bottomLeft.y &&
      p.y <= topRight.y

}


object Box {

  /**
   * Finds the container that best contain a given Shape
   *
   * "Best" means the container that has the minimal area and that fully contains the shape
   *
   * @param s The shape that must be surrounded by a container
   * @return A new `Container` that contains the Shape and that has the minimal area between the available containers
   */
  def bestFit( s: Shape ): Container = s match {

    // If it's a box, return it - O(1)
    case b: Box => b

    // If it's a circle, it's simple to find the enclosing box - O(1)
    case c: Circle => new Box( c.center, c.radius * 2, c.radius * 2 )

    // If it's a polygon, find its limits - O(n)
    case p: Polygon => {
      // Finds the minimum and maximum coordinates for the points
      var (minX, minY) = (Double.MaxValue, Double.MaxValue)
      var (maxX, maxY) = (Double.MinValue, Double.MinValue)

      for( v <- p.vertices ) {
        minX = if( minX > v.x ) v.x else minX
        minY = if( minY > v.y ) v.y else minY
        maxX = if( maxX < v.x ) v.x else maxX
        maxY = if( maxY < v.y ) v.y else maxY
      }

      // Creates the Box
      val bottomLeft = Vector2D.new_xy( minX, minY )
      val topRight = Vector2D.new_xy( maxX, maxY )

      new Box( bottomLeft, topRight )
    }

    // Other cases, error
    case _ => throw new IllegalArgumentException
  }

}