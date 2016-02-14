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

import com.colofabrix.scala.geometry.abstracts.{ Shape, SpatialIndexable, SpatialSet }
import com.colofabrix.scala.geometry.shapes.Box
import com.colofabrix.scala.gfx.abstracts.Renderer

import scala.language.postfixOps

/**
  * An object to index shapes in space. It implements the concept of Spatial Hashing, Spatial hashing is a process by
  * which a 3D or 2D domain space is projected into a 1D hash table.
  * The data structure has been implemented to be as efficient as possible and from my measurements it can be twice as
  * fast as the plain brute force collision detection.
  *
  * @param splitSize
  * @param maxDepth
  * @param hSplit
  * @param vSplit
  * @param bounds
  * @param level
  * @param children
  * @param toSeq
  * @tparam T The type of objects contained in the Set
  */
class SpatialTree[T: SpatialIndexable] protected (
    val splitSize: Int,
    val maxDepth: Int,
    val hSplit: Int,
    val vSplit: Int
)(
    val bounds: Box,
    val level: Int,
    val children: Seq[SpatialTree[T]],
    val toSeq: Seq[T]
) extends SpatialSet[T] {

  /* Used to shorten the creation of a new instance */
  @inline
  private def quickCreate( b: Box, lvl: Int, cont: Seq[SpatialTree[T]], obj: Seq[T] ) =
    new SpatialTree[T]( splitSize, maxDepth, hSplit, vSplit )( b, lvl, cont, obj )

  /**
    * Split the node into subnodes
    *
    * It divides the node into equal parts, initialising the subnodes with the new bounds
    * and inserts the contained shapes in the subnodes where they fit
    *
    * @return A new Quadtree with 4 new subnodes
    */
  protected def split( objs: Seq[T] ): SpatialTree[T] = {
    val quads = Box.spreadAcross(
      bounds.split( hSplit, vSplit ),
      objs, compact = false
    )

    val subtrees = quads.map { q ⇒
      SpatialTree[T]( splitSize, maxDepth, hSplit, vSplit )( q._1, level + 1, Seq.empty[SpatialTree[T]], q._2 )
    }

    quickCreate( bounds, level, subtrees.toSeq, objs )
  }

  /**
    * Merges the subnodes of the current node and returns back a single node
    *
    * @param objs The objects to fit in the structure
    * @return A new instance of Quadtree without subnodes
    */
  protected def merge( objs: Seq[T] ): SpatialTree[T] =
    quickCreate( bounds, level, Seq.empty[SpatialTree[T]], objs )

  /**
    *
    * @param objs
    * @return
    */
  protected def updateObjects( objs: Seq[T] ): SpatialTree[T] = {
    if ( objs.size != toSeq.size ) {
      if ( objs.size > splitSize && level < maxDepth ) {
        // Quadtree is growing
        split( objs )
      }
      else if ( objs.size <= splitSize && level > 0 ) {
        // Quadtree is shrinking
        merge( objs )
      }
      else {
        // No splitting and no merging. I can use new Quadtree()
        quickCreate( bounds, level, Seq.empty[SpatialTree[T]], objs )
      }
    }
    else {
      this
    }
  }

  /**
    * An object responsible to renderer the class where this trait is applied
    *
    * @return A renderer that can draw the object where it's applied
    */
  def renderer: Renderer = ???

  /**
    * Updates the collection
    *
    * The objects inside the collection can move and thus their internal representation
    * can change. This function is required because there might be objects T that are
    * mutable in their states.
    *
    * @return A new instance of a SpatialSet with the updated objects
    */
  def refresh(): SpatialTree[T] = SpatialTree( splitSize, maxDepth, bounds, toSeq, hSplit, vSplit )

  /**
    * Insert an object into the SpatialSet.
    *
    * Objects can be added multiple times to the set
    *
    * @return A new SpatialSet[T} containing the new object
    */
  def +( p: T ): SpatialTree[T] = {
    if ( bounds.intersects( super.shape( p ) ) && !toSeq.contains( p ) ) {
      updateObjects( p +: toSeq )
    }
    else {
      this
    }
  }

  /**
    * Remove the object from the collection.
    *
    * The function does not throw exceptions if the given object does not exists in the set
    *
    * @return A new SpatialTree[T] without the specified object.
    */
  def -( p: T ): SpatialTree[T] = {
    if ( bounds.intersects( super.shape( p ) ) && toSeq.contains( p ) ) {
      updateObjects( toSeq.filterNot( _ == p ) )
    }
    else {
      this
    }
  }

  /**
    * Find all objects that can potentially collide with the given Shape
    *
    * @param s A Shape used to collect other shapes that are spatially near it
    * @return A list of object that can collide with the given Shape
    */
  def lookAround( s: Shape ): Seq[T] = {
    if ( bounds.intersects( super.shape( s ) ) ) {
      if ( children.isEmpty ) {
        toSeq
      }
      else {
        children.flatMap { _.lookAround( s ) } distinct
      }
    }
    else {
      Seq.empty[T]
    }
  }

  /**
    * Reset the status of the collection
    *
    * @return A new SpatialSet[T], with the same parameters as the current one, but empty
    */
  def clear(): SpatialTree[T] = quickCreate( bounds, 0, Seq.empty[SpatialTree[T]], Seq.empty[T] )

  /** The number of objects that this collection is containing */
  def size: Int = toSeq.size

  /**
    * The current Set as a list
    *
    * @return A new List containing all the elements of the tree
    */
  //def toSeq: List[T] = toSeq.toList

  /**
    * Tells if the collection is empty
    *
    * @return true is the SpatialTree doesn't contain any Shape
    */
  def isEmpty: Boolean = toSeq.isEmpty
}

object SpatialTree {

  /**
    *
    * @param bounds
    * @param splitSize
    * @param maxDepth
    * @param objects
    * @tparam T
    * @return
    */
  def apply[T: SpatialIndexable](
    splitSize: Int,
    maxDepth: Int,
    bounds: Box,
    objects: Seq[T] = Seq.empty[T],
    hSplit: Int = 2,
    vSplit: Int = 2
  ): SpatialTree[T] = {
    SpatialTree( splitSize, maxDepth, hSplit, vSplit )( bounds, 0, Seq.empty[SpatialTree[T]], objects )
  }

  /**
    *
    * @param splitSize
    * @param maxDepth
    * @param hSplit
    * @param vSplit
    * @param bounds
    * @param level
    * @param containers
    * @param objects
    * @tparam T
    * @return
    */
  protected def apply[T: SpatialIndexable](
    splitSize: Int,
    maxDepth: Int,
    hSplit: Int,
    vSplit: Int
  )(
    bounds: Box,
    level: Int,
    containers: Seq[SpatialTree[T]],
    objects: Seq[T]
  ): SpatialTree[T] = {
    val zero = new SpatialTree( splitSize, maxDepth, hSplit, vSplit )( bounds, level, containers, Seq.empty[T] )
    objects.foldLeft( zero ) { case ( r, o ) ⇒ r + o }
  }
}
