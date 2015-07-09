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
import com.colofabrix.scala.gfx.abstracts.Renderer
import com.colofabrix.scala.gfx.renderers.QuadtreeRenderer
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
class LinkedQuadtree[T <: PhysicalObject] protected(
  override val toList: List[T],
  private val _quadtree: Quadtree[T]
) extends abstracts.SpatialTree[T] {
  require( toList != null, "A shape list must be specified, even empty" )
  require( _quadtree != null, "A quadtree must be specified" )

  /**
   * Remove the object from the quadtree.
   *
   * Nothing bad happens if the Shape is not in the LinkedQuadtreeTmp
   *
   * @return A new quadtree without the specified Shape.
   */
  def -( p: T ): LinkedQuadtree[T] = new LinkedQuadtree[T]( toList.filter( _ != p ), _quadtree - p )

  /**
   * Insert the object into the quadtree. If the node exceeds the capacity, it will split and add all objects to their corresponding nodes.
   *
   * @return A new quadtree containing the new Shape in the appropriate position
   */
  def +( p: T ): LinkedQuadtree[T] = new LinkedQuadtree[T]( p :: toList, _quadtree + p )

  /**
   * Reset the status of the LinkedQuadtreeTmp
   *
   * @return A new quadtree, with the same parameters as the current one, but empty
   */
  def clear( ): LinkedQuadtree[T] = new LinkedQuadtree[T]( List[T]( ), _quadtree.clear( ) )

  /**
   * Determines where an object belongs in the quadtree by determining which node the object can fit into.
   *
   * @param s The shape to check
   * @return An Option containing the Quadtree that contains the Shape or nothing
   */
  def findNode( s: Shape ) = _quadtree.findNode( s )

  /**
   * Tells if the LinkedQuadtreeTmp is empty of Shapes
   *
   * @return true is the quadtree doesn't contain any Shape
   */
  def isEmpty: Boolean = _quadtree.isEmpty

  /**
   * Return all Shapes that could collide with the given object
   *
   * @param s A Shape used to collect other shapes that are spatially near it
   * @return All Shapes that could collide with the given object
   */
  def lookAround( s: Shape ): List[T] = _quadtree.lookAround( s )

  /**
   * The children nodes of the current node, or an empty list if we are on a leaf
   */
  override def nodes: List[abstracts.SpatialTree[T]] = _quadtree.nodes

  /**
   * Updates the quadtree
   *
   * The objects inside the quadtree can move and thus their position inside the tree can change
   *
   * @return A new instance of LinkedQuadtree with the updated objects
   */
  def refresh( ): LinkedQuadtree[T] =
    new LinkedQuadtree[T]( toList, Quadtree( bounds, toList, splitSize, depth ) )

  /**
   * Area covered by the quadtree
   */
  override def bounds: Box = _quadtree.bounds

  /**
   * The maximum depth of the Quadtree
   */
  override def depth: Int = _quadtree.depth

  /**
   * The number of items a node can contain before it splits
   */
  override def splitSize: Int = _quadtree.splitSize

  /**
   * An object responsible to renderer the class where this trait is applied
   *
   * @return A renderer that can draw the object where it's applied
   */
  override def renderer: Renderer = new QuadtreeRenderer( this._quadtree )

  /**
   * The shapes contained by the node.
   */
  override def shapes: List[T] = _quadtree.shapes

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
   * @return A new LinkedQuadtreeTmp with 4 new subnodes
   */
  def split( ): LinkedQuadtree[T] = new LinkedQuadtree[T]( toList, _quadtree.split( ) )
}


object LinkedQuadtree {

  /**
   * Creates a new LinkedQuadtree
   *
   * @param bounds The area that the LinkedQuadtreeTmp will cover
   * @param initialSet The initial data contained by the LinkedQuadtreeTmp
   * @param splitSize Max size of each node before a split happens
   * @param depth Depth of the LinkedQuadtreeTmp
   * @tparam T Type of `PhysicalObject` that the LinkedQuadtreeTmp will contain
   * @return A new instance of LinkedQuadtreeTmp
   */
  def apply[T <: PhysicalObject]( bounds: Box, initialSet: List[T] = List( ), splitSize: Int = 1, depth: Int = 1 ): LinkedQuadtree[T] =
    new LinkedQuadtree[T]( List[T]( ), Quadtree( bounds, initialSet, splitSize, depth ) )

}
