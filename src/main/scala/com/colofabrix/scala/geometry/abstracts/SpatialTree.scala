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

/**
  * A generic spatial tree to index object in space and allow fast access
  *
  * A spatial tree is a type of tree used to index object in a 2D space and provide fast search for them
  */
abstract class SpatialTree[T: SpatialIndexable] extends SpatialSet[T] with Renderable {

  /**
    * Create 4 quadrants into the node
    *
    * Split the node into four subnodes by dividing the node info four equal parts, initialising the four subnodes with
    * the new bounds and inserts the contained shapes in the subnodes where they fit
    *
    * @return A new Quadtree with 4 new subnodes
    */
  protected def split(): SpatialTree[T]

  /**
    * The level of the root of the quadtree. If the quadtree is not a subtree of any other node, this parameter is 0
    */
  protected[geometry] def level: Int

  /**
    * Insert an object into the SpatialSet.
    *
    * Objects can be added multiple times to the set
    *
    * @return A new SpatialSet[T} containing the new object
    */
  override def +( p: T ): SpatialTree[T]

  /**
    * Remove the object from the collection.
    *
    * The function does not throw exceptions if the given object does not exists in the set
    *
    * @return A new SpatialTree[T] without the specified object.
    */
  override def -( p: T ): SpatialTree[T]

  /**
    * Insert a list of objects into the SpatialTree.
    *
    * @return A new SpatialTree[T] containing the new list of objects in the appropriate positions
    */
  def ++( pi: Seq[T] ): SpatialTree[T]

  /**
    * Updates the collection
    *
    * The objects inside the collection can move and thus their internal representation
    * can change. This function is required because there might be objects T that are
    * mutable in their states.
    *
    * @return A new instance of a SpatialSet with the updated objects
    */
  override def refresh(): SpatialTree[T]

  /**
    * The maximum depth of the Quadtree
    */
  def depth: Int

  /**
    * The children nodes of the current node, or an empty list if we are on a leaf
    */
  def nodes: Seq[SpatialTree[T]]

  /**
    * The shapes contained by the node.
    */
  def objects: Seq[T]

  /**
    * An object responsible to renderer the class where this trait is applied
    *
    * @return A renderer that can draw the object where it's applied
    */
  def renderer: Renderer

  /**
    * The number of items a node can contain before it splits
    */
  def splitSize: Int

}