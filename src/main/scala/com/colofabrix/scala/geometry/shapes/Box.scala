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
import com.colofabrix.scala.math.{ Vect, XYVect }

/**
  * Rectangle shape with edges parallel to the cartesian axis
  *
  * This kind of shape is particularly useful in checking overlaps and collisions as it
  * is done in constant time O(k) and without complex mathematical operations. For this
  * reasons, more than being only a {ConvexPolygon} it is also a {Container} to implement
  * an AABB collision detection.
  * See: http://stackoverflow.com/questions/22512319/what-is-aabb-collision-detection
  *
  * @see http://geomalgorithms.com/a08-_containers.html
  * @param bottomLeft Rectangle left-bottom-most point, in any quadrant of the plane
  * @param topRight   Rectangle right-top point, in any quadrant of the plane
  */
final class Box private ( val bottomLeft: Vect, val topRight: Vect ) extends ConvexPolygon(
  Seq(
    bottomLeft,
    XYVect( bottomLeft.x, topRight.y ),
    topRight,
    XYVect( topRight.x, bottomLeft.y )
  )
) with Container {

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
  lazy val center = bottomLeft + XYVect( ( topRight.x - bottomLeft.x ) / 2.0, ( topRight.y - bottomLeft.y ) / 2.0 )
  /**
    * Height of the rectangle
    */
  val height = topRight.y - bottomLeft.y
  /**
    * Width of the rectangle
    */
  val width = topRight.x - bottomLeft.x
  /**
    * The vertex that is closer to the origin of the axes.
    */
  lazy val origin = Seq( topRight, bottomLeft ).minBy( _.ρ )

  /**
    * Moves a polygon shifting all its vertices by a vector quantity
    *
    * @param where The vector specifying how to move the polygon
    * @return A new polygon moved of {where}
    */
  override def move( where: Vect ): Box = Box( bottomLeft + where, topRight + where )

  /**
    * Determines if a point is inside or on the boundary the shape
    *
    * @param p The point to be checked
    * @return True if the point is inside the shape
    */
  override def contains( p: Vect ) =
    p.x >= bottomLeft.x &&
      p.x <= topRight.x &&
      p.y >= bottomLeft.y &&
      p.y <= topRight.y

  /**
    * Determines if a shape is inside or on the boundary this shape
    *
    * @param that The point to be checked
    * @return True if the point is inside the shape
    */
  override def intersects( that: Shape ): Boolean = that match {

    // Box-circle case I use the code in Circle, as it's already present, and the commutative property of intersection
    case c: Circle ⇒ c.intersects( this )

    // Box-box case I use a faster check
    case b: Box ⇒ this.vertices.foldLeft( false )( _ && this.contains( _ ) )

    // For other comparisons I fell back to the parent
    case _ ⇒ super.intersects( that )

  }

  /**
    * A renderer for a box
    *
    * @return A new instance of BoxRenderer for the current polygon
    */
  override def renderer = new BoxRenderer( this )

  override def toString = s"Box($bottomLeft, $topRight)"

}

object Box {

  /**
    * Constructor that uses width, height and centers the Box at a specific point
    *
    * TODO: Fix the "can be negative" part as now there is no checking for this
    *
    * @param center Center of the box
    * @param width  Width of the box
    * @param height Height of the box
    */
  def apply( center: Vect, width: Double, height: Double ): Box = {
    require( width > 0.0, "The Box width can't be negative" )
    require( height > 0.0, "The Box height can't be negative" )

    new Box(
      XYVect( center.x - width / 2.0, center.y - height / 2.0 ),
      XYVect( center.x + width / 2.0, center.y + height / 2.0 )
    )
  }

  /**
    * Constructor that uses width, height and starts the box at the origin of the axis.
    *
    * The widht and height can be negative, so it's possible to create a Box on all the quadrants of the plane
    *
    * @param width  Width of the box, can be negative
    * @param height Height of the box, can be negative
    */
  def apply( width: Double, height: Double ): Box = Box( Vect.origin, XYVect( width, height ) )

  /**
    * Creates a new Box using the two opposite vertices
    *
    * @param p0 The first vertex of the Box
    * @param p1 The second vertex of the Box opposite to p0
    * @return
    */
  def apply( p0: Vect, p1: Vect ): Box = {
    val topX = Math.max( p0.x, p1.x )
    val topY = Math.max( p0.y, p1.y )
    val bottomX = Math.min( p0.x, p1.x )
    val bottomY = Math.min( p0.y, p1.y )

    new Box( XYVect( bottomX, bottomY ), XYVect( topX, topY ) )
  }

  /**
    * Returns an instance of a Shape as a Box if it is actually a Box
    *
    * @param s The shape to convert type from
    * @return The same shape as a Box
    */
  def getAsBox( s: Shape ): Box = s match {
    case b: Box ⇒ b
    case _ ⇒ throw new IllegalArgumentException( "The specified object cannot be used as Box" )
  }

  /**
    * Finds the container that best contain a given Shape
    *
    * "Best" means the container that has the minimal area and that fully contains the shape
    *
    * @param s The shape that must be surrounded by a container
    * @return A new `Container` that contains the Shape and that has the minimal area between the available containers
    */
  @SuppressWarnings( Array( "org.brianmckenna.wartremover.warts.Var" ) )
  def bestFit( s: Shape ): Container = s match {

    // If it's a box, return it - O(1)
    case b: Box ⇒ b

    // If it's a circle, it's simple to find the enclosing box - O(1)
    case c: Circle ⇒ Box( c.center, c.radius * 2, c.radius * 2 )

    // If it's a polygon, find its limits - O(n)
    case p: Polygon ⇒
      // Finds the minimum and maximum coordinates for the points
      var ( minX, minY ) = ( Double.MaxValue, Double.MaxValue )
      var ( maxX, maxY ) = ( Double.MinValue, Double.MinValue )

      for ( v ← p.vertices ) {
        minX = if ( minX > v.x ) v.x else minX
        minY = if ( minY > v.y ) v.y else minY
        maxX = if ( maxX < v.x ) v.x else maxX
        maxY = if ( maxY < v.y ) v.y else maxY
      }

      // Creates the Box
      val bottomLeft = XYVect( minX, minY )
      val topRight = XYVect( maxX, maxY )

      Box( bottomLeft, topRight )

    // Other cases, error
    case _ ⇒ throw new IllegalArgumentException
  }

}