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
import com.colofabrix.scala.gfx.abstracts.Renderer
import com.colofabrix.scala.gfx.renderers.QuadtreeRenderer

/**
  * An immutable Quadtree implementation with a support List
  *
  * This implementation includes a List to have fast access to the complete set of object contained in the Quadtree
  * and perform common and useful operations on that
  */
class LinkedQuadtree[T: SpatialIndexable] protected (
    override val toList: List[T],
    private val _quadtree: SpatialTree[T]
) extends SpatialTree[T] {

  /**
    * Create 4 quadrants into the node
    *
    * Split the node into four subnodes by dividing the node info four equal parts, initialising the four subnodes with
    * the new bounds and inserts the contained shapes in the subnodes where they fit
    *
    * @return A new LinkedQuadtree with 4 new subnodes
    */
  override protected def split(): SpatialTree[T] = this

  /**
    * Remove the object from the quadtree.
    *
    * Nothing bad happens if the Shape is not in the LinkedQuadtree
    *
    * @return A new quadtree without the specified Shape.
    */
  override def -( p: T ): SpatialTree[T] = new LinkedQuadtree[T]( toList.filter( _ != p ), _quadtree - p )

  /**
    * Insert the object into the quadtree. If the node exceeds the capacity, it will split and add all objects to their corresponding nodes.
    *
    * @return A new quadtree containing the new Shape in the appropriate position
    */
  override def +( p: T ): SpatialTree[T] = new LinkedQuadtree[T]( p :: toList, _quadtree + p )

  /**
    * Insert a list of objects into the SpatialTree.
    *
    * @return A new quadtree containing the new PhysicalObject in the appropriate position
    */
  override def ++( pi: Seq[T] ): SpatialTree[T] = new LinkedQuadtree( pi ++: toList, _quadtree ++ pi )

  /**
    * Area covered by the quadtree
    */
  override def bounds: Shape = _quadtree.bounds

  /**
    * Reset the status of the LinkedQuadtree
    *
    * @return A new quadtree, with the same parameters as the current one, but empty
    */
  override def clear(): SpatialTree[T] = new LinkedQuadtree[T]( List.empty[T], _quadtree )

  /**
    * The maximum depth of the Quadtree
    */
  override def depth: Int = _quadtree.depth

  /**
    * Tells if the LinkedQuadtree is empty of Shapes
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
  override def lookAround( s: Shape ): Seq[T] = _quadtree.lookAround( s )

  /**
    * The children nodes of the current node, or an empty list if we are on a leaf
    */
  override def nodes: Seq[SpatialTree[T]] = _quadtree.nodes

  /**
    * The shapes contained by the node.
    */
  override def objects: Seq[T] = _quadtree.objects

  /**
    * Updates the quadtree
    *
    * The objects inside the quadtree can move and thus their position inside the tree can change
    *
    * @return A new instance of LinkedQuadtree with the updated objects
    */
  override def refresh(): SpatialTree[T] = new LinkedQuadtree[T]( toList, _quadtree.refresh() )

  /**
    * An object responsible to renderer the class where this trait is applied
    *
    * @return A renderer that can draw the object where it's applied
    */
  override def renderer: Renderer = new QuadtreeRenderer( _quadtree )

  /**
    * The number of shapes contained in the quadtree
    */
  override def size: Int = toList.size

  /**
    * The number of items a node can contain before it splits
    */
  override def splitSize: Int = _quadtree.splitSize

  /**
    * Returns a string representation of the tree
    *
    * @return A new string containing a textual representation of the tree
    */
  override def toString: String = s"Tree list content: ${toList.size}\n" + _quadtree.toString

  /**
    * The level of the root of the quadtree. If the quadtree is not a subtree of any other node, this parameter is 0
    */
  protected[geometry] override val level: Int = 0
}

object LinkedQuadtree {

  /**
    * Creates a new LinkedQuadtree
    *
    * @param bounds    The area that the LinkedQuadtree will cover
    * @param objects   The initial data contained by the LinkedQuadtree
    * @param splitSize Max size of each node before a split happens
    * @param maxDepth  Depth of the LinkedQuadtree
    * @tparam T Type of `PhysicalObject` that the LinkedQuadtree will contain
    * @return A new instance of LinkedQuadtree
    */
  def apply[T: SpatialIndexable](
    bounds: Shape,
    objects: Seq[T] = Seq.empty[T],
    splitSize: Int = 1,
    maxDepth: Int = 1
  ): LinkedQuadtree[T] =
    new LinkedQuadtree[T]( objects.toList, LeafQuadtree[T]( bounds, objects, splitSize, maxDepth ) )

}