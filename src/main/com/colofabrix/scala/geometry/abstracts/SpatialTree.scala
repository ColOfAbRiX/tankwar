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

package com.colofabrix.scala.geometry.abstracts

import com.colofabrix.scala.gfx.abstracts.{ Renderable, Renderer }
import com.colofabrix.scala.simulation.abstracts.PhysicalObject

/**
 * A generic spatial tree
 *
 * A spatial tree is a type of tree used to index object in a 2D space and provide fast search for them
 */
trait SpatialTree[T <: PhysicalObject] extends Renderable {

  /**
   * Remove the object from the quadtree.
   *
   * Nothing bad happens if the Shape is not in the Quadtree
   *
   * @return A new quadtree without the specified PhysicalObject.
   */
  def -( p: T ): SpatialTree[T]

  /**
   * Insert the object into the quadtree. If the node exceeds the capacity, it will split and add all objects to their corresponding nodes.
   *
   * @return A new quadtree containing the new PhysicalObject in the appropriate position
   */
  def +( p: T ): SpatialTree[T]

  /**
   * Area covered by the quadtree
   */
  def bounds: Shape

  /**
   * Reset the status of the Quadtree
   *
   * @return A new quadtree, with the same parameters as the current one, but empty
   */
  def clear( ): SpatialTree[T]

  /**
   * The maximum depth of the Quadtree
   */
  def depth: Int

  /**
   * Determines where an object belongs in the quadtree by determining which node the object can fit into.
   *
   * @param s The shape to check
   * @return An Option containing the Quadtree that contains the Shape or nothing
   */
  def findNode( s: Shape ): Option[SpatialTree[T]]

  /**
   * Tells if the Quadtree is empty of Shapes
   *
   * @return true is the quadtree doesn't contain any Shape
   */
  def isEmpty: Boolean

  /**
   * Return all PhysicalObjects that could collide with the given Shape
   *
   * @param s A Shape used to collect other shapes that are spatially near it
   * @return All PhysicalObjects that could collide with the given object
   */
  def lookAround( s: Shape ): List[T]

  /**
   * The children nodes of the current node, or an empty list if we are on a leaf
   */
  def nodes: List[SpatialTree[T]]

  /**
   * Updates the quadtree
   *
   * The objects inside the quadtree can move and thus their position inside the tree can change
   *
   * @return A new instance of a SpatialTree with the updated objects
   */
  def refresh( ): SpatialTree[T]

  /**
   * An object responsible to renderer the class where this trait is applied
   *
   * @return A renderer that can draw the object where it's applied
   */
  def renderer: Renderer

  /**
   * The shapes contained by the node.
   */
  def shapes: List[T]

  /**
   * The number of shapes contained in the quadtree
   */
  def size: Int

  /**
   * Create 4 quadrants into the node
   *
   * Split the node into four subnodes by dividing the node info four equal parts, initialising the four subnodes with
   * the new bounds and inserts the contained shapes in the subnodes where they fit
   *
   * @return A new Quadtree with 4 new subnodes
   */
  def split( ): SpatialTree[T]

  /**
   * The number of items a node can contain before it splits
   */
  def splitSize: Int

  /**
   * Get the current tree as a list
   *
   * @return A new List containing all the elements of the tree
   */
  def toList( ): List[T]
}