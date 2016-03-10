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

import com.colofabrix.scala.geometry.abstracts.Shape
import com.colofabrix.scala.gfx.abstracts.{ Renderable, Renderer }
import com.colofabrix.scala.gfx.renderers.PolygonRenderer
import com.colofabrix.scala.math.{ Vect, _ }

import scala.language.{ postfixOps, reflectiveCalls }

/**
  * A generic two-dimensional polygon
  *
  * This class is not meant to be efficient but only to provide generic algorithms and
  * to study how they work. Don't use this class in production code
  */
//@SuppressWarnings( Array( "TraversableHead" ) ) // A polygon has always got at least 3 edges
class Polygon( val vertices: Seq[Vect] ) extends Shape with Renderable {

  /** To represent corners between two edges */
  final case class Corner( e0: Seg, e1: Seg ) {val edges = e0 :: e1 :: Nil}

  // The smallest polygon is a triangle!!
  require( vertices.length > 2 )

  /** Edges of the Polygon, built from the vertices. Edges are {Vect} from one vertex to its adjacent one */
  lazy val edges: Seq[Seg] = (vertices :+ vertices.head).sliding( 2 ).map( Seg( _ ) ).toSeq

  /** Corners of the Polygon */
  lazy val corners: Seq[Corner] = (edges :+ edges.head).sliding( 2 ).map( { case e0 +: e1 +: Nil ⇒ Corner( e0, e1 ) } ).toSeq

  /**
    * Area of the polygon
    * Ref: http://geomalgorithms.com/a01-_area.html
    */
  lazy val area = {
    val partials = (vertices :+ vertices.head)
      .sliding( 3 )
      .map {
        case v0 +: v1 +: v2 +: Nil ⇒ v1.x * (v2.y - v0.y)
      }

    partials.sum / 2.0
  }

  /**
    * Checks if a polygon is convex
    *
    * To be convex, the edges of a polygon must form between each others angles greater that 180. This is
    * checked calculating, for every adjacent edges, their rotation, given by the cross product, of the second
    * edge compared to the first.
    *
    * Ref: https://stackoverflow.com/questions/471962/how-do-determine-if-a-polygon-is-complex-convex-nonconvex
    *
    * @return true if the polygon is convex
    */
  lazy val isConvex = {
    // The direction or rotation can be either CW or CCW as far as it is always the same or zero. This is the
    // direction of the first edge as a reference.
    val direction = Math.signum( corners.head.e0.vect ^ corners.head.e1.vect ).toInt

    corners.forall { c ⇒
      val r = c.e0.vect ^ c.e1.vect
      Math.signum( r ).toInt == direction || r.abs <= FP_PRECISION
    }
  }

  /**
    * Determines if a point is inside or on the boundary the shape
    *
    * @see http://geomalgorithms.com/a03-_inclusion.html#cn_PinPolygon%28%29
    * @param p The point to be checked
    * @return True if the point is inside the shape
    */
  override def contains( p: Vect ): Boolean = {
    val wind = edges.foldLeft( 0 ) { ( a, e ) ⇒
      if( e.v0.y <= p.y ) {
        if( e.v1.y > p.y && e.orientation( p ) > 0.0 ) a + 1 else a
      }
      else {
        if( e.v1.y <= p.y && e.orientation( p ) < 0.0 ) a - 1 else a
      }
    }
    wind != 0
  }

  /**
    * Determines if a shape is inside or on the boundary the current shape
    *
    * @param s The shape to be checked
    * @return True if the given shape is inside the shape or on its boundary
    */
  override def contains( s: Shape ): Boolean = s match {
    case p: Polygon ⇒
      p.vertices.forall( this.contains ) &&
        !p.edges.exists( e ⇒ edges.exists( e.intersects ) )

    case g: Seg ⇒
      g.endpoints.forall( this.contains ) &&
        !edges.exists( _.intersects( g ) )

    case c: Circle ⇒
      !vertices.exists( c.contains ) &&
        !edges.exists( _.intersects( c ) )

    case _ ⇒ throw new IllegalArgumentException( "Unexpected Shape type" )
  }

  /**
    * Compute the distance between a point and the edges of the polygon
    *
    * Checks the distances from all the edges and returns the nearest one
    *
    * @param p Point to check
    * @return A tuple containing 1) the distance vector from the point to the polygon and 2) the edge from which the distance is calculated
    */
  def distance( p: Vect ): (Vect, Vect) = {
    if( contains( p ) ) {
      (Vect.origin, Vect.origin)
    }
    else {
      // Check all the vertices and return the nearest one.
      edges.map( _.distance( p ) ).minBy( _._1.ρ )
    }
  }

  /**
    * Compute the distance between a line segment and the edges of the polygon
    *
    * @param s The line segment to check
    * @return A distance vector from the point to polygon and the edge or point from which the distance is calculated
    */
  override def distance( s: Seg ): (Vect, Vect) = {
    if( intersects( s ) ) {
      (Vect.zero, Vect.zero)
    }
    else {
      // Check all the vertices and return the nearest one.
      val distance = edges.map( s.distance ).minBy( _._1.ρ )
      (distance._1 * -1.0, distance._2)
    }
  }

  /**
    * Determines if a line segment touches in any way this shape
    *
    * @param s The line segment to check
    * @return True if the point is inside the shape
    */
  override def intersects( s: Seg ): Boolean =
    s.endpoints.exists( contains ) || edges.exists( _.intersects( s ) )

  /**
    * Determines if a shape is inside or on the boundary this shape
    *
    * @param that The point to be checked
    * @return True if the point is inside the shape
    */
  override def intersects( that: Shape ): Boolean = that match {
    // With circles I find the nearest edge to the center and then I compare it to the radius to see if it's inside
    case c: Circle ⇒ distance( c.center )._1 <= c.radius

    case p: Polygon ⇒
      vertices.exists( p.contains ) ||
        p.vertices.exists( this.contains ) ||
        edges.exists { e ⇒
          p.edges.exists( e.intersects )
        }

    case g: Seg ⇒ intersects( g )

    // Other comparisons are not possible, including Seg with its own method
    case _ ⇒ throw new IllegalArgumentException( "Unexpected Shape type" )
  }

  /**
    * Moves a polygon shifting all its vertices by a vector quantity
    *
    * @param where The vector specifying how to move the polygon
    * @return A new polygon moved of {where}
    */
  override def move( where: Vect ): Shape = new Polygon( vertices.map( v ⇒ v + where ) )

  /**
    * A renderer for a generic polygon
    *
    * @return A new instance of PolygonRenderer for the current polygon
    */
  override def renderer: Renderer = new PolygonRenderer( this )
}