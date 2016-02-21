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

import java.util.concurrent.ConcurrentHashMap

import com.colofabrix.scala.geometry.abstracts.{ Container, Shape, SpatialIndexable }
import com.colofabrix.scala.gfx.renderers.BoxRenderer
import com.colofabrix.scala.math.{ Vect, XYVect }

import scala.collection.JavaConverters._

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
final class Box private( val bottomLeft: Vect, val topRight: Vect ) extends ConvexPolygon(
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
    * Center of the Box
    */
  lazy val center = bottomLeft + XYVect( width / 2.0, height / 2.0 )

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
  lazy val origin = vertices.minBy( _.ρ )

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
    // Box-box case - Ref: https://stackoverflow.com/questions/401847/circle-rectangle-collision-detection-intersection
    case c: Circle ⇒
      this.contains( c.center ) || edges.exists( c.intersects )
    case _ ⇒ super.intersects( that )
  }

  /**
    * Splits a rectangular area in different boxes
    *
    * The area is divided in equal parts as specified by the parameters
    *
    * @param hSplit Number of horizontal divisions
    * @param vSplit Number of vertical divisions
    * @return A list of Box that cover the area
    */
  def split( hSplit: Int, vSplit: Int ) = {
    val width = this.width / hSplit
    val height = this.height / vSplit
    val templateBox = Box( Vect.origin, XYVect( width, height ) ).move( this.bottomLeft )

    for( j ← 0 until hSplit; i ← 0 until vSplit ) yield {
      templateBox.move( XYVect( width * i, height * j ) )
    }
  }

  /**
    * A renderer for a box
    *
    * @return A new instance of BoxRenderer for the current polygon
    */
  override def renderer = new BoxRenderer( this )

  override def toString = s"Box($bottomLeft, $topRight)"

  override def equals( other: Any ): Boolean = other match {
    case that: Box ⇒
      bottomLeft == that.bottomLeft && topRight == that.topRight
    case _ ⇒ false
  }

  override def hashCode( ): Int = {
    val state = Seq( bottomLeft, topRight )
    state.map( _.hashCode( ) ).foldLeft( 0 )( ( a, b ) ⇒ 31 * a + b )
  }
}

object Box {

  /**
    * Constructor that uses width, height and centers the Box at a specific point
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
    * The width and height can be negative, so it's possible to create a Box on all the quadrants of the plane
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
    * @return A new [[Container]] that contains the Shape and that has the minimal area between the available containers
    */
  @SuppressWarnings( Array( "org.brianmckenna.wartremover.warts.Var" ) )
  def bestFit( s: Shape ): Box = s match {
    case g: Seg => Box( g.v0, g.v1 )

    // If it's a box, return it - O(1)
    case b: Box ⇒ b

    // If it's a circle, it's simple to find the enclosing box - O(1)
    case c: Circle ⇒ Box( c.center, c.radius * 2, c.radius * 2 )

    // If it's a polygon, find its limits - O(n)
    case p: Polygon ⇒
      // Using VARs to optimize performance
      var xMin, yMin = Double.MaxValue
      var xMax, yMax = Double.MinValue

      // Finds the minimum and maximum coordinates for the points
      for( v ← p.vertices ) {
        xMin = Math.min( xMin, v.x )
        yMin = Math.min( yMin, v.y )
        xMax = Math.max( xMax, v.x )
        yMax = Math.max( yMax, v.y )
      }

      // Creates the Box
      val bottomLeft = XYVect( xMin, yMin )
      val topRight = XYVect( xMax, yMax )

      Box( bottomLeft, topRight )

    // Other cases, error
    case _ ⇒ throw new IllegalArgumentException( "Unexpected Shape type" )
  }

  /**
    * Distributes the objects in the buckets that contain it.
    *
    * It is the most expensive function of the data structure, use it with care!
    *
    * @param nodes   The list of all the buckets that cover the whole area
    * @param objects The objects to assign
    * @param compact If true the function will not include in the output Boxes with empty content
    * @tparam T Type of the object that must have a conversion to SpatialIndexable
    * @return A Map that connects the boxes with a list of objects that contains. Objects can be present in multiple buckets
    */
  @SuppressWarnings( Array( "org.brianmckenna.wartremover.warts.Var" ) )
  def spreadAcross[T: SpatialIndexable](
    nodes: Seq[Box],
    objects: Seq[T],
    compact: Boolean = true
  ): Map[Box, Seq[T]] = {
    // Using VAR to optimize performance
    val acc = new ConcurrentHashMap[Box, Seq[T]].asScala

    @inline
    def intersects( b: Box, o: T ) = b.intersects( implicitly[SpatialIndexable[T]].container[Box]( o ) )

    for( b ← nodes ) {
      val objInBox = objects.filter( intersects( b, _ ) )
      if( objInBox.nonEmpty || !compact ) acc += ((b, objInBox))
    }

    acc.toMap
  }
}