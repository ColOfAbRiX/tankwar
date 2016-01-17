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

import com.colofabrix.scala.geometry.abstracts._
import com.colofabrix.scala.geometry.shapes.Box
import com.colofabrix.scala.math.Vector2D

/**
 *
 */
class SpatialHash[T: SpatialIndexable]( val toList: List[T], val bounds: Box, val hSplit: Int, val vSplit: Int )
    extends SpatialSet[T] {

  /**
   * Creates the list of all the buckets required to cover the bounds area. This is a commodity field
   */
  private val _bucketList = for ( j ← 0 until vSplit; i ← 0 until hSplit ) yield {
    def moveBox( b: Box, v: Vector2D ) = new Box( b.bottomLeft + v, b.topRight + v )
    val templateBox = new Box(
      Vector2D.origin,
      Vector2D.new_xy( bounds.width / hSplit, bounds.height / vSplit )
    )

    moveBox( templateBox, Vector2D.new_xy( templateBox.width * i, templateBox.height * j ) )
  }

  /**
   * The list of objects split in the various buckets. An object T can be part of more than one bucket because
   * it can be bigger than a bucket.
   *
   * @return A sequence of the bucket where each contains the list of the objects that contains
   */
  val buckets: List[List[(Box, T)]] =
    ( for (
      b ← _bucketList;
      s ← toList if b.intersects( containingBox( s ) )
    ) yield ( b, s ) )
      .toStream
      .groupBy( _._1 )
      .map( _._2.toList )
      .toList

  /**
   * Finds the containing Box of the T being indexed. A Box is used to maximize performance
   *
   * @param t The shape you want to know the containing box
   * @return A Box containing the object
   */
  protected def containingBox( t: T ): Box =
    implicitly[SpatialIndexable[T]].container( t ) match {
      case b: Box ⇒ b
      case c: Container ⇒ Box.bestFit( c ) match {
        case b2: Box ⇒ b2
        case _ ⇒ throw new IllegalArgumentException( "Expected Box as a containing shape" )
      }
    }

  /**
   * Remove the object from the quadtree.
   *
   * Nothing bad happens if the Shape is not in the Quadtree
   *
   * @return A new quadtree without the specified PhysicalObject.
   */
  override def -( p: T ): SpatialSet[T] = new SpatialHash[T]( toList.filter(_ != p), bounds, hSplit, vSplit )

  /**
   * Return all PhysicalObjects that could collide with the given Shape
   *
   * @param s A Shape used to collect other shapes that are spatially near it
   * @return All PhysicalObjects that could collide with the given object
   */
  override def lookAround( s: Shape ): List[T] = ???

  /**
   * Reset the status of the Quadtree
   *
   * @return A new quadtree, with the same parameters as the current one, but empty
   */
  override def clear(): SpatialSet[T] = new SpatialHash[T]( List.empty, bounds, hSplit, vSplit )

  /**
   * The number of shapes contained in the quadtree
   */
  override def size: Int = toList.size

  /**
   * Insert the object into the SpatialSet.
   *
   * @return A new quadtree containing the new PhysicalObject in the appropriate position
   */
  override def +( p: T ): SpatialSet[T] = new SpatialHash[T]( p :: toList, bounds, hSplit, vSplit )

  /**
   * Updates the quadtree
   *
   * The objects inside the quadtree can move and thus their position inside the tree can change
   *
   * @return A new instance of a SpatialSet with the updated objects
   */
  override def refresh(): SpatialSet[T] = new SpatialHash[T]( toList, bounds, hSplit, vSplit )

  /**
   * Tells if the Quadtree is empty of Shapes
   *
   * @return true is the quadtree doesn't contain any Shape
   */
  override def isEmpty: Boolean = toList.isEmpty
}