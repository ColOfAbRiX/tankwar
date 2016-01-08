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

package com.colofabrix.scala.geometry

import com.colofabrix.scala.geometry.abstracts.SpatialTree.SpatialIndexable
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
class DummyQuadtree[T: SpatialIndexable] protected (
    override val toList: List[T],
    override val bounds: Shape
) extends abstracts.SpatialTree[T] {
  require( toList != null, "A shape list must be specified, even empty" )

  /**
   * Create 4 quadrants into the node
   *
   * Split the node into four subnodes by dividing the node info four equal parts, initialising the four subnodes with
   * the new bounds and inserts the contained shapes in the subnodes where they fit
   *
   * @return A new DummyQuadtree with 4 new subnodes
   */
  protected override def split(): DummyQuadtree[T] = this

  /**
   * Remove the object from the quadtree.
   *
   * Nothing bad happens if the Shape is not in the DummyQuadtree
   *
   * @return A new quadtree without the specified Shape.
   */
  override def -( p: T ): DummyQuadtree[T] = new DummyQuadtree[T]( toList.filter( _ != p ), bounds )

  /**
   * Insert the object into the quadtree. If the node exceeds the capacity, it will split and add all objects to their corresponding nodes.
   *
   * @return A new quadtree containing the new Shape in the appropriate position
   */
  override def +( p: T ): DummyQuadtree[T] = new DummyQuadtree[T]( p :: toList, bounds )

  /**
   * Insert a list of objects into the SpatialTree.
   *
   * @return A new quadtree containing the new PhysicalObject in the appropriate position
   */
  override def ++( pi: List[T] ): SpatialTree[T] = new DummyQuadtree[T]( pi ::: toList, bounds )

  /**
   * Reset the status of the DummyQuadtree
   *
   * @return A new quadtree, with the same parameters as the current one, but empty
   */
  override def clear(): DummyQuadtree[T] = new DummyQuadtree[T]( List[T](), bounds )

  /**
   * The maximum depth of the Quadtree
   */
  override def depth: Int = 0

  /**
   * Tells if the DummyQuadtree is empty of Shapes
   *
   * @return true is the quadtree doesn't contain any Shape
   */
  override def isEmpty: Boolean = toList.isEmpty

  /**
   * Return all Shapes that could collide with the given object
   *
   * @param s A Shape used to collect other shapes that are spatially near it
   * @return All Shapes that could collide with the given object
   */
  override def lookAround( s: Shape ): List[T] = toList

  /**
   * The children nodes of the current node, or an empty list if we are on a leaf
   */
  override def nodes: List[abstracts.SpatialTree[T]] = List()

  /**
   * The shapes contained by the node.
   */
  override def objects: List[T] = toList

  /**
   * Updates the quadtree
   *
   * The objects inside the quadtree can move and thus their position inside the tree can change
   *
   * @return A new instance of DummyQuadtree with the updated objects
   */
  override def refresh(): DummyQuadtree[T] = this

  /**
   * An object responsible to renderer the class where this trait is applied
   *
   * @return A renderer that can draw the object where it's applied
   */
  override def renderer: Renderer = new BoxRenderer( bounds.asInstanceOf[Box], defaultFrame = Frame( Colour.DARK_GREY ) )

  /**
   * The number of shapes contained in the quadtree
   */
  override def size: Int = toList.size

  /**
   * The number of items a node can contain before it splits
   */
  override def splitSize: Int = 0
}

object DummyQuadtree {

  /**
   * Creates a new DummyQuadtree
   *
   * @param bounds The area that the DummyQuadtree will cover
   * @param initialList The initial data contained by the DummyQuadtree
   * @tparam T Type of `PhysicalObject` that the DummyQuadtree will contain
   * @return A new instance of DummyQuadtree
   */
  def apply[T: SpatialIndexable]( bounds: Shape, initialList: List[T] = List() ): DummyQuadtree[T] =
    new DummyQuadtree[T]( initialList, bounds )

}