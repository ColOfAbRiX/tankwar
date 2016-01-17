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

package com.colofabrix.scala.geometry

import com.colofabrix.scala.Tools
import com.colofabrix.scala.geometry.abstracts._
import com.colofabrix.scala.geometry.shapes.Box
import com.colofabrix.scala.math.Vector2D

/**
  * An object to index shapes in space. It implements the concept of Spatial Hashing, Spatial hashing is a process by
  * which a 3D or 2D domain space is projected into a 1D hash table.
  * It is an efficient way of implementing a collision detection system for moving objects as it is very fast during
  * the indexing and refreshing phase.
  */
class SpatialHash[T: SpatialIndexable] private (
    private val _objects: List[T],
    val bounds: Box,
    val hSplit: Int,
    val vSplit: Int,
    private val _bucketList: Seq[Box]
) extends SpatialSet[T] {

  /**
    * The list of objects split in the various buckets. An object T can be part of more than one bucket because
    * it can be bigger than a bucket.
    *
    * @return A sequence of the bucket where each contains the list of the objects that contains
    */
  val buckets: Map[Box, List[T]] =
    ( for (
      b ← _bucketList;
      s ← _objects if b.intersects( shape( s ) )
    ) yield ( b, s ) )
      .groupBy( _._1 )
      .map( x ⇒ ( x._1, x._2.map( _._2 ).toList ) )

  /**
    * Remove the object from the quadtree.
    *
    * Nothing bad happens if the Shape is not in the Quadtree
    *
    * @return A new quadtree without the specified PhysicalObject.
    */
  override def -( p: T ): SpatialSet[T] = new SpatialHash[T]( _objects.filter( _ != p ), bounds, hSplit, vSplit, _bucketList )

  /**
    * Return all PhysicalObjects that could collide with the given Shape
    *
    * @param s A Shape used to collect other shapes that are spatially near it
    * @return All PhysicalObjects that could collide with the given object
    */
  override def lookAround( s: Shape ): List[T] = {
    buckets.filter( x ⇒ s.intersects( x._1 ) ).flatMap( _._2 ).toList
  }

  /**
    * Reset the status of the Quadtree
    *
    * @return A new quadtree, with the same parameters as the current one, but empty
    */
  override def clear(): SpatialSet[T] = new SpatialHash[T]( List.empty[T], bounds, hSplit, vSplit, _bucketList )

  /**
    * The number of shapes contained in the quadtree
    */
  override def size: Int = _objects.size

  /**
    * Insert the object into the SpatialSet.
    *
    * @return A new quadtree containing the new PhysicalObject in the appropriate position
    */
  override def +( p: T ): SpatialSet[T] = new SpatialHash[T]( p +: _objects, bounds, hSplit, vSplit, _bucketList )

  /**
    * Updates the quadtree
    *
    * The objects inside the quadtree can move and thus their position inside the tree can change
    *
    * @return A new instance of a SpatialSet with the updated objects
    */
  override def refresh(): SpatialSet[T] = new SpatialHash[T]( _objects, bounds, hSplit, vSplit, _bucketList )

  /**
    * Tells if the Quadtree is empty of Shapes
    *
    * @return true is the quadtree doesn't contain any Shape
    */
  override def isEmpty: Boolean = _objects.isEmpty

  /**
    * Get the current tree as a list
    *
    * @return A new List containing all the elements of the tree
    */
  override def toList: List[T] = _objects
}

object SpatialHash {

  def apply[T: SpatialIndexable]( objects: List[T], bounds: Shape, hSplit: Int, vSplit: Int ) = {
    val box = bounds match {
      case b: Box ⇒ b
      case _ ⇒ throw new IllegalArgumentException( "Variable 'bound' is not of type Box" )
    }

    // This list of buckets is created only once per SpatialHash to save time and memory
    val _bucketList = for ( j ← 0 until vSplit; i ← 0 until hSplit ) yield {
      def moveBox( b: Box, v: Vector2D ) = new Box( b.bottomLeft + v, b.topRight + v )

      val templateBox = new Box(
        Vector2D.origin,
        Vector2D.new_xy( box.width / hSplit, box.height / vSplit )
      )

      moveBox( templateBox, Vector2D.new_xy( templateBox.width * i, templateBox.height * j ) )
    }

    new SpatialHash[T]( objects, box, hSplit, vSplit, _bucketList )
  }

}