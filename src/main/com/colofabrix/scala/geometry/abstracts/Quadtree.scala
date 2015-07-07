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
 * An immutable Quadtree
 *
 * A quadtree is a try of tree with 4 children nodes per parent used to partition a cartesian plane and speed up
 * object-object interactions in graphical environments.
 *
 * @see http://gamedevelopment.tutsplus.com/tutorials/quick-tip-use-quadtrees-to-detect-likely-collisions-in-2d-space--gamedev-374
 */
trait Quadtree[T <: Shape, U <: PhysicalObject] extends Renderable {

  /**
   * Remove the object from the quadtree.
   *
   * Nothing bad happens if the Shape is not in the Quadtree
   *
   * @return A new quadtree without the specified PhysicalObject.
   */
  def -( p: U ): Quadtree[T, U]

  /**
   * Insert the object into the quadtree. If the node exceeds the capacity, it will split and add all objects to their corresponding nodes.
   *
   * @return A new quadtree containing the new PhysicalObject in the appropriate position
   */
  def +( p: U ): Quadtree[T, U]

  /**
   * Tells if the Quadtree is empty of Nodes
   *
   * @return true is the quadtree doesn't contain any subnode
   */
  def areNodesEmpty: Boolean

  /**
   * Tells if the Quadtree is empty of Shapes
   *
   * @return true is the quadtree doesn't contain any Shape
   */
  def areShapesEmpty: Boolean

  /**
   * Area covered by the quadtree
   */
  def bounds: Shape

  /**
   * Reset the status of the Quadtree
   *
   * @return A new quadtree, with the same parameters as the current one, but empty
   */
  def clear( ): Quadtree[T, U]

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
  def findNode( s: Shape ): Option[Quadtree[T, U]]

  /**
   * Return all PhysicalObjects that could collide with the given Shape
   *
   * @param s A Shape used to collect other shapes that are spatially near it
   * @return All PhysicalObjects that could collide with the given object
   */
  def lookAround( s: T ): List[U]

  /**
   * The children nodes of the current node, or an empty list if we are on a leaf
   */
  def nodes: List[Quadtree[T, U]]

  /**
   * An object responsible to renderer the class where this trait is applied
   *
   * @return A renderer that can draw the object where it's applied
   */
  def renderer: Renderer

  /**
   * The shapes contained by the node.
   */
  def shapes: List[U]

  /**
   * Create 4 quadrants into the node
   *
   * Split the node into four subnodes by dividing the node info four equal parts, initialising the four subnodes with
   * the new bounds and inserts the contained shapes in the subnodes where they fit
   *
   * @return A new Quadtree with 4 new subnodes
   */
  def split( ): Quadtree[T, U]

  /**
   * The number of items a node can contain before it splits
   */
  def splitSize: Int
}
