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

package com.colofabrix.scala.geometry.abstracts

/**
  * A SpatialSet is a set of objects that can be located in a 2D Space. This trait is used to implement efficient ways
  * for storing and retrieving objects in the cartesian plan, in particular for collision detection
  */
trait SpatialSet[T] {

  /**
   * Remove the object from the quadtree.
   *
   * Nothing bad happens if the Shape is not in the Quadtree
   *
   * @return A new quadtree without the specified PhysicalObject.
   */
  def -( p: T ): SpatialSet[T]

  /**
   * Insert the object into the SpatialSet.
   *
   * @return A new quadtree containing the new PhysicalObject in the appropriate position
   */
  def +( p: T ): SpatialSet[T]

  /**
   * Area covered by the quadtree
   */
  def bounds: Shape

  /**
   * Reset the status of the Quadtree
   *
   * @return A new quadtree, with the same parameters as the current one, but empty
   */
  def clear(): SpatialSet[T]

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
   * Updates the quadtree
   *
   * The objects inside the quadtree can move and thus their position inside the tree can change
   *
   * @return A new instance of a SpatialSet with the updated objects
   */
  def refresh(): SpatialSet[T]

  /**
   * The number of shapes contained in the quadtree
   */
  def size: Int

  /**
   * Get the current tree as a list
   *
   * @return A new List containing all the elements of the tree
   */
  def toList: List[T]

  override def toString: String = this.toList.toString()
}
