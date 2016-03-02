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

import scala.collection.immutable.HashSet

/**
  * An object to index shapes in space. It implements the concept of Spatial Hashing, Spatial hashing is a process by
  * which a 3D or 2D domain space is projected into a 1D hash table.
  * The data structure has been implemented to be as efficient as possible and from my measurements it can be twice as
  * fast as the plain brute force collision detection.
  *
  * @param bounds      The boundary of the area covered by the SpatialHash
  * @param hSplit      The number of buckets in the horizontal axis
  * @param vSplit      The number of buckets in the vertical axis
  * @param _objectsSet The list of the objects to include in the SpatialHash implemented as a Set
  * @param _objectsSeq The list of the objects to include in the SpatialHash implemented as a Seq
  * @param _bucketList The list of the predefined buckets, used as a lookup
  * @param buckets     A sequence of the bucket where each contains the list of the objects that contains
  * @tparam T The type of object that this collection will contain. Must have a conversion to SpatialIndexable
  */
class SpatialHash[T: HasContainer] protected(
    val bounds: Box,
    val hSplit: Int,
    val vSplit: Int,
    val buckets: Map[Box, Seq[T]],
    _objectsSet: Set[T],
    _objectsSeq: Seq[T],
    _bucketList: Seq[Box]
) extends SpatialSet[T] {

  /* Used to shorten the creation of a new instance */
  @inline
  private def quickCreate( buckets: Map[Box, Seq[T]], objSet: Set[T], objSeq: Seq[T] ) =
    new SpatialHash[T]( bounds, hSplit, vSplit, buckets, objSet, objSeq, _bucketList )

  /**
    * Remove the object from the collection.
    *
    * @return A new SpatialSet[T] without the specified object.
    */
  override def -( p: T ): SpatialHash[T] = {
    // The list of the objects, the buckets and their lists are scanned once
    val newObjects = _objectsSet.filter( _ != p )
    val newBuckets = buckets.map {
      b ⇒ ( b._1, b._2.filter( _ != p ) )
    }
    quickCreate( newBuckets, newObjects, newObjects.toList )
  }

  /**
    * Find all objects that can potentially collide with the given Shape
    *
    * @param s A Shape used to collect other shapes that are spatially near it
    * @return A list of object that can collide with the given Shape
    */
  override def lookAround( s: Shape ): Seq[T] =
    buckets.filter( x ⇒ s.intersects( x._1 ) ).flatMap( _._2 ).toSeq

  /**
    * Reset the status of the collection
    *
    * @return A new SpatialSet[T], with the same parameters as the current one, but empty
    */
  override def clear(): SpatialHash[T] = quickCreate( Map.empty[Box, Seq[T]], Set.empty[T], Seq.empty[T] )

  /**
    * Insert an object into the SpatialSet.
    *
    * @return A new SpatialSet[T} containing the new object
    */
  override def +( p: T ): SpatialHash[T] =
    if ( !_objectsSet.contains( p ) ) {
      // For this operation there is only one scan that goes through the buckets to find the ones
      // that contain the shape
      val newBuckets = for ( b ← buckets ) yield {
        if( b._1.intersects( container( p ) ) ) {
          Tuple2( b._1, p +: b._2 )
        }
        else {
          Tuple2( b._1, b._2 )
        }
      }

      quickCreate( newBuckets, _objectsSet + p, p +: _objectsSeq )
    }
    else {
      this
    }

  /**
    * The number of objects that this collection is containing
    */
  override def size: Int = _objectsSet.size

  /**
    * Updates the collection
    *
    * The objects inside the collection can move and thus their internal representation can change
    *
    * @return A new instance of a SpatialSet with the updated objects
    */
  override def refresh(): SpatialHash[T] = quickCreate(
    Box.spreadAcross[T]( _bucketList, _objectsSeq ),
    _objectsSet, _objectsSeq
  )

  /**
    * Tells if the collection is empty of Shapes
    *
    * @return true is the SpatialTree doesn't contain any Shape
    */
  override def isEmpty: Boolean = _objectsSet.isEmpty

  /**
    * Get the current collection as a list
    *
    * @return A new List containing all the elements of the SpatialSet
    */
  override def toSeq: List[T] = _objectsSeq.toList
}

object SpatialHash {
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
  def apply[T: HasContainer]( bounds: Shape, objects: Seq[T], hSplit: Int, vSplit: Int ) = {
    val box = Box.getAsBox( bounds )
    val bucketList = box.split( hSplit, vSplit ).toList

    new SpatialHash[T](
      box, hSplit, vSplit,
      Box.spreadAcross[T]( bucketList, objects ),
      HashSet( objects: _* ),
      objects,
      bucketList
    )
  }
}