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
import com.colofabrix.scala.math.Vect

import scala.language.reflectiveCalls

/**
  * A convex polygon
  */
class ConvexPolygon( private val v: Seq[Vect] ) extends Polygon( v ) {
  require( this.isConvex, "The vertices don't define a convex polygon" )

  private def containsCondition(
    f: {def contains( a: Vect ): Boolean},
    it: Iterable[Vect]
  ) = it.forall( f.contains )

  /**
    * Determines if a shape is inside or on the boundary the current shape
    *
    * @param s The shape to be checked
    * @return True if the given shape is inside the shape or on its boundary
    */
  override def contains( s: Shape ): Boolean = s match {
    case g: Seg ⇒ containsCondition( this, g.endpoints )
    case p: Polygon ⇒ p.vertices.forall( this.contains )
    case c: Circle ⇒
      this.contains( c.center ) && edges.forall { e ⇒
        !c.intersects( e ) || c.circumferenceDistance( e )._1 == Vect.zero
      }
    case _ ⇒ super.contains( s )
  }

  /**
    * Determines if a shape is inside or on the boundary this shape
    *
    * @param that The point to be checked
    * @return True if the point is inside the shape
    */
  override def intersects( that: Shape ): Boolean = that match {
    /*
    * Implementation of the Separating Axes Theorem. This is faster than the default {Polygon} implementation and
    * valid only for convex polygons
    *
    * @see http://www.sevenson.com.au/actionscript/sat/
    */
    case cp: ConvexPolygon ⇒
      this.edges.map( _.vect.n ).forall { edge_normal ⇒
        // Chosen an edge of this polygon I get the projections of all
        // vertices on the normal of the edge
        val prVxThis = vertices.map( v ⇒ (v → edge_normal).ρ )
        val prVxThat = cp.vertices.map( v ⇒ (v → edge_normal).ρ )

        // Then I check if the extremities of the projections of the two polygons overlaps
        if( prVxThis.min < prVxThat.min ) {
          prVxThis.max >= prVxThat.min
        }
        else {
          prVxThat.max >= prVxThis.min
        }
      }

    // For other comparisons I fell back to the parent
    case _ ⇒ super.intersects( that )

  }

  /**
    * Moves a polygon shifting all its vertices by a vector quantity
    *
    * @param where The vector specifying how to move the polygon
    * @return A new polygon moved of {where}
    */
  override def move( where: Vect ) = new ConvexPolygon( vertices.map( v ⇒ v + where ) )

}