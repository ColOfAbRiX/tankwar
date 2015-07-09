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

import com.colofabrix.scala.geometry.abstracts._
import com.colofabrix.scala.geometry.shapes._
import com.colofabrix.scala.gfx.OpenGL.Colour
import com.colofabrix.scala.gfx.abstracts.Renderer
import com.colofabrix.scala.gfx.renderers.BoxRenderer
import com.colofabrix.scala.simulation.abstracts.PhysicalObject

/**
 * An immutable Quadtree implementation with a support List
 *
 * A quadtree is a try of tree with 4 children nodes per parent used to partition a cartesian plane and speed up
 * object-object interactions in graphical environments.
 * This implementation includes a List to have fast access to the complete set of object contained in the Quadtree
 * and perform common and useful operations on that
 *
 * @see http://gamedevelopment.tutsplus.com/tutorials/quick-tip-use-quadtrees-to-detect-likely-collisions-in-2d-space--gamedev-374
 */
class DummyQuadtree[T <: PhysicalObject] protected(
  override val toList: List[T],
  override val bounds: Box
) extends abstracts.SpatialTree[T] {
  require( toList != null, "A shape list must be specified, even empty" )

  /**
   * Remove the object from the quadtree.
   *
   * Nothing bad happens if the Shape is not in the DummyQuadtreeTmp
   *
   * @return A new quadtree without the specified Shape.
   */
  def -( p: T ): DummyQuadtree[T] = new DummyQuadtree[T]( toList.filter( _ != p ), bounds )

  /**
   * Insert the object into the quadtree. If the node exceeds the capacity, it will split and add all objects to their corresponding nodes.
   *
   * @return A new quadtree containing the new Shape in the appropriate position
   */
  def +( p: T ): DummyQuadtree[T] = new DummyQuadtree[T]( p :: toList, bounds )

  /**
   * Reset the status of the DummyQuadtreeTmp
   *
   * @return A new quadtree, with the same parameters as the current one, but empty
   */
  def clear( ): DummyQuadtree[T] = new DummyQuadtree[T]( List[T]( ), bounds )

  /**
   * Determines where an object belongs in the quadtree by determining which node the object can fit into.
   *
   * @param s The shape to check
   * @return An Option containing the Quadtree that contains the Shape or nothing
   */
  def findNode( s: Shape ) = None

  /**
   * Tells if the DummyQuadtreeTmp is empty of Shapes
   *
   * @return true is the quadtree doesn't contain any Shape
   */
  def isEmpty: Boolean = toList.isEmpty

  /**
   * Return all Shapes that could collide with the given object
   *
   * @param s A Shape used to collect other shapes that are spatially near it
   * @return All Shapes that could collide with the given object
   */
  def lookAround( s: Shape ): List[T] = toList

  /**
   * The children nodes of the current node, or an empty list if we are on a leaf
   */
  override def nodes: List[abstracts.SpatialTree[T]] = List()

  /**
   * Updates the quadtree
   *
   * The objects inside the quadtree can move and thus their position inside the tree can change
   *
   * @return A new instance of DummyQuadtree with the updated objects
   */
  def refresh( ): DummyQuadtree[T] = this

  /**
   * The maximum depth of the Quadtree
   */
  override def depth: Int = depth

  /**
   * The number of items a node can contain before it splits
   */
  override def splitSize: Int = splitSize

  /**
   * An object responsible to renderer the class where this trait is applied
   *
   * @return A renderer that can draw the object where it's applied
   */
  override def renderer: Renderer = new BoxRenderer(bounds, Colour.DARK_GREY)

  /**
   * The shapes contained by the node.
   */
  override def shapes: List[T] = toList

  /**
   * The number of shapes contained in the quadtree
   */
  override def size: Int = toList.size

  /**
   * Create 4 quadrants into the node
   *
   * Split the node into four subnodes by dividing the node info four equal parts, initialising the four subnodes with
   * the new bounds and inserts the contained shapes in the subnodes where they fit
   *
   * @return A new DummyQuadtreeTmp with 4 new subnodes
   */
  def split( ): DummyQuadtree[T] = this
}


object DummyQuadtree {

  /**
   * Creates a new DummyQuadtree
   *
   * @param bounds The area that the DummyQuadtreeTmp will cover
   * @param initialSet The initial data contained by the DummyQuadtreeTmp
   * @tparam T Type of `PhysicalObject` that the DummyQuadtreeTmp will contain
   * @return A new instance of DummyQuadtreeTmp
   */
  def apply[T <: PhysicalObject]( bounds: Box, initialSet: List[T] = List( ) ): DummyQuadtree[T] =
    new DummyQuadtree[T]( initialSet, bounds )

}
