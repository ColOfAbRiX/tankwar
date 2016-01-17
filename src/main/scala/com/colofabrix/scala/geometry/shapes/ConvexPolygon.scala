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
import com.colofabrix.scala.math.Vector2D

/**
  * A convex polygon
  */
class ConvexPolygon( private val v: Seq[Vector2D] ) extends Polygon( v ) {
  require( this.isConvex, "The vertices don't define a convex polygon" )

  /**
    * Determines if a shape is inside or on the boundary the current shape
    *
    * @param s The shape to be checked
    * @return True if the given shape is inside the shape or on its boundary
    */
  override def contains( s: Shape ): Boolean = s match {

    // For ConvexPolygon-Polygon I check if all the vertices are inside it. This follows from the definition of polygon and applies to
    case p: Polygon ⇒ p.vertices.forall( contains )

    // For other comparisons I fell back to the parent
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
      this.edges.map( edge ⇒ edge.n ).
        // Chosen an edge of this polygon...
        forall { edge_normal ⇒
          // ...I get the projections of all vertices on the normal of the edge
          val prVxThis = vertices.map( v ⇒ ( v → edge_normal ).r )
          val prVxThat = cp.vertices.map( v ⇒ ( v → edge_normal ).r )

          // Then I check if the extremities of the projections of the two polygons overlaps
          if ( prVxThis.min < prVxThat.min ) {
            prVxThis.max >= prVxThat.min
          }
          else {
            prVxThat.max >= prVxThis.min
          }
        }

    // For other comparisons I fell back to the parent
    case _ ⇒ super.intersects( that )

  }
}