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
import com.colofabrix.scala.geometry.shapes.Box
import com.colofabrix.scala.math.{ Vect, XYVect }

/**
  * An object to index shapes in space. It implements the concept of Spatial Hashing, Spatial hashing is a process by
  * which a 3D or 2D domain space is projected into a 1D hash table.
  * The data structure has been implemented to be as efficient as possible and from my measurements it can be twice as
  * fast as the plain brute force collision detection.
  *
  * @param _objects    The list of the objects to include in the SpatialHash
  * @param bounds      The boundary of the area covered by the SpatialHash
  * @param hSplit      The number of buckets in the horizontal axis
  * @param vSplit      The number of buckets in the vertical axis
  * @param _bucketList The list of the predefined buckets, used as a lookup
  * @param buckets     A sequence of the bucket where each contains the list of the objects that contains
  * @tparam T The type of object that this collection will contain. Must have a conversion to SpatialIndexable
  */
class SpatialHash[T: SpatialIndexable] private (
    val bounds: Box,
    val hSplit: Int,
    val vSplit: Int,
    val buckets: Map[Box, Seq[T]],
    protected val _objects: List[T],
    protected val _bucketList: Seq[Box]
) extends SpatialSet[T] {

  /**
    * Remove the object from the collection.
    *
    * @return A new SpatialSet[T] without the specified object.
    */
  override def -( p: T ): SpatialSet[T] = {
    // The list of the objects, the buckets and their lists are scanned once
    val newObjects = _objects.filter( _ != p )
    val newBuckets = buckets.map { b ⇒ ( b._1, b._2.filter( _ != p ) ) }

    new SpatialHash[T]( bounds, hSplit, vSplit, newBuckets, newObjects, _bucketList )
  }

  /**
    * Find all objects that can potentially collide with the given Shape
    *
    * @param s A Shape used to collect other shapes that are spatially near it
    * @return A list of object that can collide with the given Shape
    */
  override def lookAround( s: Shape ): List[T] =
    buckets.filter( x ⇒ s.intersects( x._1 ) ).flatMap( _._2 ).toList

  /**
    * Reset the status of the collection
    *
    * @return A new SpatialSet[T], with the same parameters as the current one, but empty
    */
  override def clear(): SpatialSet[T] =
    new SpatialHash[T]( bounds, hSplit, vSplit, Map.empty[Box, List[T]], List.empty[T], _bucketList )

  /**
    * The number of objects that this collection is containing
    */
  override def size: Int = _objects.size

  /**
    * Insert an object into the SpatialSet.
    *
    * @return A new SpatialSet[T} containing the new object
    */
  override def +( p: T ): SpatialSet[T] = {
    // Never add twice the same object
    if ( _objects.contains( p ) ) {
      return this
    }

    val newObjects = p +: _objects

    // For this operation there is only one scan that goes through the buckets to find the ones which contain
    // the shape
    val newBuckets = for ( b ← buckets ) yield {
      if ( b._1.intersects( shape( p ) ) ) ( b._1, p +: b._2 ) else ( b._1, b._2 )
    }

    new SpatialHash[T]( bounds, hSplit, vSplit, newBuckets, newObjects, _bucketList )
  }

  /**
    * Updates the collection
    *
    * The objects inside the collection can move and thus their internal representation can change
    *
    * @return A new instance of a SpatialSet with the updated objects
    */
  override def refresh(): SpatialSet[T] =
    new SpatialHash[T]( bounds, hSplit, vSplit, SpatialHash.assignToBuckets( _bucketList, _objects ), _objects, _bucketList )

  /**
    * Tells if the collection is empty of Shapes
    *
    * @return true is the SpatialTree doesn't contain any Shape
    */
  override def isEmpty: Boolean = _objects.isEmpty

  /**
    * Get the current collection as a list
    *
    * @return A new List containing all the elements of the SpatialSet
    */
  override def toList: List[T] = _objects
}

object SpatialHash {

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
    * Distributes the objects in the buckets that contain it.
    *
    * It is the most expensive function of the data structure, use it with care!
    *
    * @param rawBuckets The list of all the buckets that cover the whole area
    * @param objects    The objects to assign
    * @tparam T Type of the object that must have a conversion to SpatialIndexable
    * @return A Map that connects the boxes with a list of objects that contains. Objects can be present in multiple buckets
    */
  protected def assignToBuckets[T: SpatialIndexable]( rawBuckets: Seq[Box], objects: List[T] ): Map[Box, Seq[T]] = {
    // I create the pairs (object, box that intersects)
    val assigned = for (
      b ← rawBuckets;
      s ← objects if b.intersects( shape( s ) )
    ) yield ( b, s )

    // Clean the format of the association above, from (Box, (Box, Shape)) -> (Box, Shape)
    assigned
      .groupBy( _._1 )
      .map { x ⇒
        ( x._1, x._2.map( _._2 ) )
      }
  }

  /**
    * Creates a new SpatialHash object
    *
    * @param objects The list of the objects to include in the SpatialHash
    * @param bounds  The boundary of the area covered by the SpatialHash
    * @param hSplit  The number of buckets in the horizontal axis
    * @param vSplit  The number of buckets in the vertical axis
    * @tparam T The type of object that this collection will contain. Must have a conversion to SpatialIndexable
    * @return A new SpatialHash containing the specified objects
    */
  def apply[T: SpatialIndexable]( bounds: Shape, objects: List[T], hSplit: Int, vSplit: Int ) = {
    val box = Box.getAsBox( bounds )

    // This list of buckets is created only once per SpatialHash to save time and memory
    val bucketList = for ( j ← 0 until vSplit; i ← 0 until hSplit ) yield {
      val templateBox = Box(
        Vect.origin,
        XYVect( box.width / hSplit, box.height / vSplit )
      )

      Box.getAsBox( templateBox.move( XYVect( templateBox.width * i, templateBox.height * j ) ) )
    }

    new SpatialHash[T]( box, hSplit, vSplit, assignToBuckets( bucketList, objects ), objects, bucketList )
  }

}