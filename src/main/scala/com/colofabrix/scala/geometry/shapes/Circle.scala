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
import com.colofabrix.scala.gfx.renderers.CircleRenderer
import com.colofabrix.scala.math.Vect

/**
  * Circle shape
  *
  * A Circle is a very convenient shape to check for geometrical properties as
  * it is, generally speaking, very fast to compute them. For this reason it is also marked as a {Container}
  *
  * @param center Center of the circle
  * @param radius Radius of the circle. Must be non-negative
  */
final case class Circle( center: Vect, radius: Double ) extends Shape with Container with Renderable {
  // If the radius is 0... it's a point!
  require( radius > 0, "The circle must have a non-zero radius" )

  /**
    * Area of the circle
    */
  override lazy val area: Double = Math.PI * Math.pow( radius, 2.0 )

  /**
    * Determines if the container fully contain a Shape
    *
    * @param s The shape to check
    * @return true if the container fully contain the other shape. Boundaries are included in the container
    */
  override def contains( s: Shape ): Boolean = s match {
    case g: Seg ⇒ g.endpoints.forall( contains )

    // For the case Circle-Circle I check that the center and the points on the circumference are inside this one - O(1)
    case c: Circle ⇒ (this.center - c.center).ρ + c.radius <= this.radius

    // For the case Polygon-Circle I check that all the vertices of the polygon lie inside the circle - O(n)
    case p: Polygon ⇒ p.vertices.forall( contains )

    case _ ⇒ throw new IllegalArgumentException( "Unexpected Shape type" )
  }

  /**
    * Determines if a point is inside or on the boundary the shape
    *
    * @param p The point to be checked
    * @return True if the point is inside the shape or on its boundary
    */
  override def contains( p: Vect ): Boolean = (p - center).ρ <= radius

  /**
    * Compute the distance between a line and the circle
    *
    * @param s The line segment to check
    * @return A tuple containing 1) the distance vector from the line to the perimeter and 2) the edge or the point from which the distance is calculated
    */
  override def distance( s: Seg ): (Vect, Vect) = {
    // Distance from the point on the segment nearest to the circle
    val pSeg = s.distance( center )._1
    distance( center + pSeg )
  }

  /**
    * Compute the distance between a point and the circle
    *
    * @param p The point to check
    * @return A tuple containing 1) the distance vector from the point to the boundary and 2) the edge or the point from which the distance is calculated
    */
  override def distance( p: Vect ): (Vect, Vect) = {
    val distance = circumferenceDistance( p )

    if( distance._1 <= radius ) {
      return (Vect.zero, Vect.zero)
    }
    else {
      distance
    }
  }

  /**
    * Compute the distance between a line and the circumference of the Circle
    *
    * @param s The line segment to check
    * @return A tuple containing 1) the distance vector from the line to the perimeter and 2) the edge or the point from which the distance is calculated
    */
  def circumferenceDistance( s: Seg ): (Vect, Vect) = {
    // Distance from the point on the segment nearest to the circle
    val pSeg = s.distance( center )._1
    circumferenceDistance( center + pSeg )
  }

  /**
    * Compute the distance between a point and the circumference of the Circle
    *
    * @param p The point to check
    * @return A tuple containing 1) the distance vector from the point to the boundary and 2) the edge or the point from which the distance is calculated
    */
  def circumferenceDistance( p: Vect ): (Vect, Vect) = {
    // The distance of the point from the center of the circle. This vector is not related to the origin of axes
    val distanceFromCenter = p - center

    // A radius (segment that starts in the center of the circle and ends on a point in the circumference) directed towards p.
    // This vector is not related to the origin of axes
    val radiusTowardsPoint = distanceFromCenter.v * radius

    // Distance of the point from the circumference calculated subtracting the two above vectors. This vector is not
    // related to the origin of axes
    val distance = distanceFromCenter - radiusTowardsPoint
    // The couching point is point on the circumference closer to p, but this time related to the origin of axes
    val touchingPoint = center + radiusTowardsPoint

    (distance, touchingPoint)
  }

  /**
    * Determines if a line segment touches in any way this shape
    *
    * @param s The line segment to check
    * @return True if the line intersects the shape
    */
  override def intersects( s: Seg ): Boolean = s.distance( center )._1 <= radius

  /**
    * Determines if a shape touches this one
    *
    * Circle can't use the default STA implementation as it doesn't have vertices!
    * Instead it uses two different comparisons, one circle-circle, the other circle-shape
    *
    * @param that The shape to be checked
    * @return True if the shape touches the current shape
    */
  override def intersects( that: Shape ): Boolean = that match {
    // For segments I check the two endpoints
    case g: Seg ⇒ intersects( g )

    // For circles is enough to check the distance from the two centers
    case c: Circle ⇒ center - c.center < radius + c.radius

    // For Boxes I exploit its property to be parallel to the axis
    case b: Box ⇒ b.contains( center ) || b.edges.exists( this.intersects )

    // For polygons I check the distance from the nearest edge
    case p: Polygon ⇒ p.distance( center )._1 <= radius

    case _ ⇒ throw new IllegalArgumentException( "Unexpected Shape type" )
  }

  /**
    * Moves the circle of the specified vector
    *
    * @param where The vector specifying how to move the shape
    * @return A new shape moved of {where}
    */
  override def move( where: Vect ): Shape = new Circle( center + where, radius )

  /**
    * An object responsible to renderer the class where this trait is applied
    *
    * @return A renderer that can draw the object where it's applied
    */
  override def renderer: Renderer = new CircleRenderer( this )
}

object Circle {

  /**
    * Finds the container that best contain a given Shape
    *
    * "Best" means the container that has the minimal area and that fully contains the shape
    *
    * @param s The shape that must be surrounded by a container
    * @return A new [[Container]] that contains the Shape and that has the minimal area between the available containers
    */
  @inline
  def bestFit( s: Shape ): Circle = s match {
    // The segment becomes the diameter of a new Circle
    case g: Seg ⇒
      val center = g.v0 + (g.vect / 2.0)
      // I use Math.max because there can be floating number roundings
      val radius = Math.max( (g.v0 - center).ρ, (center - g.v1).ρ )
      Circle( center, radius )

    // If the shape it's a circle I simply return it - O(1)
    case c: Circle ⇒ c

    // A Box is a very easy case, so I take advantage of this - O(1)
    case b: Box ⇒ new Circle( b.center, (b.topRight - b.bottomLeft).ρ / 2.0 )

    // Generic Polygon - O(n)
    case p: Polygon ⇒ boundingBall( p )

    case _ ⇒ throw new IllegalArgumentException( "Unexpected Shape type" )
  }

  /**
    * Implementation of the Fast Approximate Bounding Ball algorithm
    *
    * Ref: http://geomalgorithms.com/a08-_containers.html
    *
    * @param p A Polygon to encircle
    * @return A new instance of Circle that contains all the vertices of the polygon and has minimal area
    */
  private def boundingBall( p: Polygon ): Circle = {
    // As stated by Ritter, the starting point is a Box
    val bBox = Box.bestFit( p )
    // Connecting line between the most distant points
    val line = (bBox.topRight - bBox.bottomLeft) / 2.0

    // Check if all points are included and if not, increase the ball
    p.vertices.foldLeft( Circle( bBox.origin + line, line.ρ ) ) { ( a, v ) ⇒
      if( !a.contains( v ) ) {
        val d = (a.center - v) / 2.0
        Circle( a.center + d, a.radius + d.ρ )
      }
      else {
        a
      }
    }
  }

  /**
    * Creates a Circle known its area
    *
    * @param center Center of the circle
    * @param area   Area of the circle. Must be non-negative
    * @return A new instance of Circle with an area equals to the specified one
    */
  def fromArea( center: Vect, area: Double ) = {
    require( area > 0, "The area specified for a circle must be positive" )
    new Circle( center, Math.sqrt( area / Math.PI ) )
  }
}