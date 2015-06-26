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

package com.colofabrix.scala.geometry

import com.colofabrix.scala.geometry.shapes.Box
import com.colofabrix.scala.math.Vector2D
import com.colofabrix.scala.simulation.abstracts.PhysicalObject

/**
 * An immutable Quadtree implementation
 *
 * A quadtree is a try of tree with 4 children nodes per parent used to partition a cartesian plane and speed up
 * object-object interactions in graphical environments.
 *
 * @see http://gamedevelopment.tutsplus.com/tutorials/quick-tip-use-quadtrees-to-detect-likely-collisions-in-2d-space--gamedev-374
 */
class Quadtree[T <: PhysicalObject] private(
  val bounds: Box,
  val level: Int,
  val nodes: Seq[Quadtree[T]],
  val shapes: Seq[T],
  val bucketSize: Int,
  val maxLevels: Int
) {
  require( bounds != null, "A box must be specified to indicate the Quadtree area" )
  require( nodes != null, "A node list must be specified, even empty" )
  require( shapes != null, "A shape list must be specified, even empty" )
  require( bucketSize > 0, "The bucket size must be an integer greater than zero" )
  require( maxLevels > 0, "The number of levels must be an integer greater than zero" )

  /**
   * Lookup table to have a quick access to the quadrant definitions
   */
  protected val quadLookup = Seq(
    new Box(
      // Top-Right quadrant
      Vector2D.new_xy( bounds.bottomLeft.x + bounds.width / 2, bounds.bottomLeft.y + bounds.height / 2 ),
      bounds.topRight
    ),
    new Box(
      // Top-Left quadrant
      Vector2D.new_xy( bounds.bottomLeft.x, bounds.bottomLeft.y + bounds.height / 2 ),
      Vector2D.new_xy( bounds.bottomLeft.x + bounds.width / 2, bounds.bottomLeft.y + bounds.height )
    ),
    new Box(
      // Bottom-Left quadrant
      bounds.bottomLeft,
      Vector2D.new_xy( bounds.bottomLeft.x + bounds.width / 2, bounds.bottomLeft.y + bounds.height / 2 )
    ),
    new Box(
      // Bottom-Right quadrant
      Vector2D.new_xy( bounds.bottomLeft.x + bounds.width / 2, bounds.bottomLeft.y ),
      Vector2D.new_xy( bounds.bottomLeft.x + bounds.width, bounds.bottomLeft.y + bounds.height / 2 )
    )
  )

  /**
   * Determines where an object belongs in the quadtree by determining which node the object can fit into.
   *
   * @param s The shape to check
   * @return The number of the quadrant that contains it or -1 if an object cannot completely fit within a child node and is part of the parent node
   */
  protected def findNode( s: T ) = nodes.find( _.bounds.contains( s.objectShape ) )

  /**
   * Determines the quadrant that can contain the given Shape
   *
   * @param s The shape to check
   * @return The number of the quadrant that contains it or -1 if an object cannot completely fit within a child node and is part of the parent node
   */
  protected def findQuadrant( s: T ): Option[Int] = quadLookup.zipWithIndex.find( _._1.contains( s.objectShape ) ).map( _._2 )

  def -( s: T ): Quadtree[T] = ???

  /**
   * Insert the object into the quadtree. If the node exceeds the capacity, it will split and add all objects to their corresponding nodes.
   *
   * @return A new quadtree containing the new Shape in the appropriate position
   */
  def +( s: T ): Quadtree[T] = {
    // If there are subnodes, try to add the new Shape there.
    if( nodes.nonEmpty ) {
      val where = findNode( s )

      // Try to add the shape into one subnode
      if( where.isDefined ) {
        val newNodes = nodes.map { q => if( where.get == q ) where.get + s else q }
        return new Quadtree[T]( bounds, level, newNodes, shapes, bucketSize, maxLevels )
      }

      // The shape is not contained by any subnode. If there is space add it to the current node
      if( shapes.size < bucketSize ) {
        return new Quadtree[T]( bounds, level, nodes, shapes :+ s, bucketSize, maxLevels )
      }

      // Nothing else we can do. The quadtree cannot contain the Shape. Exception
      throw new IndexOutOfBoundsException( "Quadtree is full and cannot contain the new object" )
    }

    // If there is still space into this node, add the Shape here
    if( shapes.length < bucketSize ) {
      return new Quadtree[T]( bounds, level, nodes, shapes :+ s, bucketSize, maxLevels )
    }

    // FIXME: If the current bucket is full but the new Shape doesn't fit any quadrant... What happens? Now there is an infinite loop calling "split() + s"
    // There is not enough space into the current node and no defined subnodes. Split and add the shape
    if( level < maxLevels ) {
      return split( ) + s
    }



    // Nothing else we can do. The quadtree cannot contain the Shape. Exception
    throw new IndexOutOfBoundsException( "Quadtree is full and cannot contain the new object" )
  }

  /**
   * Selects an element by its index in the sequence.
   *
   * As a Quadtree is a tree, the single index allow to access the element in a breath-first fashion
   *
   * @param idx The index to select.
   * @return the element of this sequence at index idx, where 0 indicates the first element.
   */
  def apply( idx: Int ): T = iterator.toList( idx )

  /**
   * Reset the status of the Quadtree
   *
   * @return A new quadtree, with the same parameters as the current one, but empty
   */
  def clear( ): Quadtree[T] = new Quadtree[T]( bounds, level, Seq( ), Seq( ), bucketSize, maxLevels )

  /**
   * Creates a new iterator over all elements contained in this iterable object.
   *
   * @return The new iterator
   */
  def iterator: Iterator[T] = shapes.iterator ++ nodes.flatMap( _.shapes.iterator ).iterator ++ nodes.flatMap( _.iterator ).iterator

  /**
   * The length of the sequence.
   *
   * @return The number of elements in this sequence.
   */
  def length: Int = shapes.length + nodes.foldLeft( 0 )( _ + _.length )

  /**
   * Return all Shapes that could collide with the given object
   *
   * @param s A Shape used to collect other shapes that are spatially near it
   * @return All Shapes that could collide with the given object
   */
  def lookAround( s: T ): Seq[T] = {
    val where = findQuadrant( s )

    // If the Shape doesn't fit any subnode, just return the content of the current node
    if( where.isEmpty ) {
      return shapes.filter( _ != s )
    }

    // Otherwise, look recursively into the subnode
    return shapes.filter( _ != s ) ++ nodes( where.get ).lookAround( s )
  }

  /**
   * Create 4 quadrants into the node
   *
   * Split the node into four subnodes by dividing the node info four equal parts, initialising the four subnodes with
   * the new bounds and inserts the contained shapes in the subnodes where they fit
   *
   * @return A new Quadtree with 4 new subnodes
   */
  def split( ): Quadtree[T] = {
    // Create a Quadtree on each quadrant and insert in each one of the the Shapes that it's able to contain
    val quads = quadLookup map { q =>
      new Quadtree[T]( q, level + 1, Seq( ), shapes.filter( s => q.contains( s.objectShape ) ), bucketSize, maxLevels )
    }

    // And return a whole new Quadtree as a result of the split
    new Quadtree[T]( bounds, level, quads, shapes.diff( quads.flatMap( _.shapes ) ), bucketSize, maxLevels )
  }
}


object Quadtree {

  def apply[T <: PhysicalObject]( bounds: Box, initialSet: Seq[T] = Seq(), bucketSize: Int = 1, maxLevels: Int = 1 ) = {
    initialSet.foldLeft( new Quadtree[T]( bounds, 0, Seq( ), Seq( ), bucketSize, maxLevels ) ) {_ + _}
  }

}
