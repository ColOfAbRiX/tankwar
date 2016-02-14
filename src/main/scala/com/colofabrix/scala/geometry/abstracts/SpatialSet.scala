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
  * A SpatialSet is a set of objects that can be located in a 2D Space.
  *
  * This trait is used to implement efficient ways for storing and retrieving objects
  * in the cartesian plane, in particular for collision detection. Some of its implementation
  * implementation are the [[com.colofabrix.scala.geometry.collections.SpatialTree]], the
  * [[com.colofabrix.scala.geometry.collections.SpatialHash]] and also the fake
  * [[com.colofabrix.scala.geometry.collections.DummySet]] for testing.
  *
  * @tparam T The type of objects contained in the Set
  */
abstract class SpatialSet[T: SpatialIndexable] {

  /**
    * This function is a commodity used to find the Container of a shape
    *
    * @param u The object we want to find the container of
    * @tparam U Thy type of the object that must be convertible into a SpatialIndexable
    * @return A Container that fully encircle the given object in the plane
    */
  @inline
  protected def shape[U: SpatialIndexable]( u: U ): Container = implicitly[SpatialIndexable[U]].container( u )

  /**
    * Remove the object from the collection.
    *
    * The function does not throw exceptions if the given object does not exists in the set
    *
    * @return A new SpatialSet[T] without the specified object.
    */
  def -( p: T ): SpatialSet[T]

  /**
    * Insert an object into the SpatialSet.
    *
    * Objects can be added multiple times to the set
    *
    * @return A new SpatialSet[T} containing the new object
    */
  def +( p: T ): SpatialSet[T]

  /** The area covered by the quadtree */
  def bounds: Shape

  /**
    * Reset the status of the collection
    *
    * @return A new SpatialSet[T], with the same parameters as the current one, but empty
    */
  def clear(): SpatialSet[T]

  /**
    * Tells if the collection is empty
    *
    * @return true is the SpatialTree doesn't contain any Shape
    */
  def isEmpty: Boolean

  /**
    * Find all objects that can potentially collide with the given Shape
    *
    * @param s A Shape used to collect other shapes that are spatially near it
    * @return A list of object that can collide with the given Shape
    */
  def lookAround( s: Shape ): Seq[T]

  /**
    * Updates the collection
    *
    * The objects inside the collection can move and thus their internal representation
    * can change. This function is required because there might be objects T that are
    * mutable in their states.
    *
    * @return A new instance of a SpatialSet with the updated objects
    */
  def refresh(): SpatialSet[T]

  /** The number of objects that this collection is containing */
  def size: Int

  /**
    * The current Set as a Seq
    *
    * @return A new List containing all the elements of the tree
    */
  def toSeq: Seq[T]

  /**
    * The current Set as a Iterable
    *
    * @return A new List containing all the elements of the tree
    */
  //def toSet: Iterable[T]

  /**
    * String representation of the Set
    *
    * @return A string that represents the Set
    */
  override def toString: String = this.toSeq.toString()
}
