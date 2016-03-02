/*
 * Copyright (C) 2016 Fabrizio
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

package com.colofabrix.scala.geometry.collections

import com.colofabrix.scala.geometry.abstracts._
import com.colofabrix.scala.geometry.shapes._
import com.colofabrix.scala.gfx.OpenGL.{ Colour, Frame }
import com.colofabrix.scala.gfx.abstracts.Renderer
import com.colofabrix.scala.gfx.renderers.BoxRenderer

/**
  * A dummy quadtree. It's not a quadtree, it's a list
  *
  * Used to do performance tests and comparisons
  */
class DummySet[T: HasContainer] protected(
    override val toSeq: List[T],
    override val bounds: Box
) extends SpatialSet[T] {

  /**
    * Remove the object from the quadtree.
    *
    * Nothing bad happens if the Shape is not in the DummyQuadtree
    *
    * @return A new quadtree without the specified Shape.
    */
  override def -( p: T ): DummySet[T] = new DummySet[T]( toSeq.filter( _ != p ), bounds )

  /**
    * Insert the object into the quadtree. If the node exceeds the capacity, it will split and add all objects to
    * their corresponding nodes.
    *
    * @return A new quadtree containing the new Shape in the appropriate position
    */
  override def +( p: T ): DummySet[T] =
    if ( toSeq.contains( p ) ) {
      this
    }
    else {
      new DummySet[T]( p :: toSeq, bounds )
    }

  /**
    * Insert a list of objects into the SpatialTree.
    *
    * @return A new quadtree containing the new PhysicalObject in the appropriate position
    */
  def ++( pi: Seq[T] ): DummySet[T] = {
    val newObjects = pi.filterNot( toSeq.contains )
    new DummySet[T]( newObjects ++: toSeq, bounds )
  }

  /**
    * Reset the status of the DummyQuadtree
    *
    * @return A new quadtree, with the same parameters as the current one, but empty
    */
  override def clear(): DummySet[T] = new DummySet[T]( List.empty[T], bounds )

  /**
    * Tells if the DummyQuadtree is empty of Shapes
    *
    * @return true is the quadtree doesn't contain any Shape
    */
  override def isEmpty: Boolean = toSeq.isEmpty

  /**
    * Return all Shapes that could collide with the given object
    *
    * @param s A Shape used to collect other shapes that are spatially near it
    * @return All Shapes that could collide with the given object
    */
  override def lookAround( s: Shape ): Seq[T] = if ( !bounds.intersects( s ) ) Nil else toSeq

  /**
    * Updates the quadtree
    *
    * The objects inside the quadtree can move and thus their position inside the tree can change
    *
    * @return A new instance of DummyQuadtree with the updated objects
    */
  override def refresh(): DummySet[T] = this

  /**
    * An object responsible to renderer the class where this trait is applied
    *
    * @return A renderer that can draw the object where it's applied
    */
  def renderer: Renderer = new BoxRenderer( bounds, defaultFrame = Frame( Colour.DARK_GREY ) )

  /**
    * The number of shapes contained in the quadtree
    */
  override def size: Int = toSeq.size
}

object DummySet {

  /**
    * Creates a new DummyQuadtree
    *
    * @param bounds  The area that the DummyQuadtree will cover
    * @param objects The initial data contained by the DummyQuadtree
    * @tparam T Type of [[com.colofabrix.scala.simulation.abstracts.PhysicalObject]] that the DummyQuadtree will contain
    * @return A new instance of DummyQuadtree
    */
  def apply[T: HasContainer](
    bounds: Shape,
    objects: List[T] = List.empty[T]
  ) = new DummySet[T]( objects, Box.getAsBox( bounds ) )

}