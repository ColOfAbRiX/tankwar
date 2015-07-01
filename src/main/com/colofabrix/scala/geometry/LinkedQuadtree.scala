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

package com.colofabrix.scala.geometry.shapes

import com.colofabrix.scala.geometry.abstracts._
import com.colofabrix.scala.math.Vector2D
import com.colofabrix.scala.simulation.abstracts.PhysicalObject

/**
 * An immutable Quadtree implementation with a support List
 *
 * A quadtree is a try of tree with 4 children nodes per parent used to partition a cartesian plane and speed up
 * object-object interactions in graphical environments.
 * This implementation includes a List to have fast access to the complete set of object contained in the Quadtree
 * and perform common and useful operations on that
 *
 * @see http://gamedevelopment.tutsplus.com/tutorials/quick-tip-use-quadtrees-to-detect-likely-collisions-in-2d-space--gamedev-374
 */
class LinkedQuadtree[-T <: Shape, U <: PhysicalObject] protected(
  val bounds: Box,
  val level: Int,
  val nodes: List[LinkedQuadtree[T, U]],
  val shapes: List[U],
  val asList: List[U],
  val splitSize: Int,
  val depth: Int
) {
  require( bounds != null, "A box must be specified to indicate the LinkedQuadtree area" )
  require( nodes != null, "A node list must be specified, even empty" )
  require( shapes != null, "A shape list must be specified, even empty" )
  require( splitSize > 0, "The bucket size must be an integer greater than zero" )
  require( depth > 0, "The number of levels must be an integer greater than zero" )

  /**
   * Determines where an object belongs in the quadtree by determining which node the object can fit into.
   *
   * @param s The shape to check
   * @return The number of the quadrant that contains it or -1 if an object cannot completely fit within a child node and is part of the parent node
   */
  protected def findNode( s: Shape ) = nodes.find( _.bounds.intersects( s ) )

  /**
   * Remove the object from the quadtree.
   *
   * Nothing bad happens if the Shape is not in the LinkedQuadtree
   *
   * @return A new quadtree without the specified Shape.
   */
  def -( p: U ): LinkedQuadtree[T, U] = {
    // If there are subnodes, try to add the new Shape there.
    if( nodes.nonEmpty ) {
      val where = findNode( p.objectShape )

      // Try to add the shape into one subnode
      if( where.isDefined ) {
        val output = where.get - p

        if( output.areNodesEmpty ) {
          return new LinkedQuadtree[T, U]( bounds, level, List( ), output.shapes, output.asList, splitSize, depth )
        }

        return output
      }
    }

    // This is a leaf node, so shapes == asList.
    val remainingShapes = shapes.filter( _ != p )
    // Return a new leaf without the Shape
    return new LinkedQuadtree[T, U]( bounds, level, nodes, remainingShapes, remainingShapes, splitSize, depth )
  }

  /**
   * Insert the object into the quadtree. If the node exceeds the capacity, it will split and add all objects to their corresponding nodes.
   *
   * @return A new quadtree containing the new Shape in the appropriate position
   */
  def +( p: U ): LinkedQuadtree[T, U] = {
    // If there are subnodes, try to add the new Shape there.
    if( nodes.nonEmpty ) {
      val where = findNode( p.objectShape )

      // Try to add the shape into one subnode
      if( where.isDefined ) {
        val newNodes = nodes.map { q => if( where.get == q ) where.get + p else q }
        return new LinkedQuadtree[T, U]( bounds, level, newNodes, shapes, p :: asList, splitSize, depth )
      }

      // The shape is not contained by any subnode. If there is space add it to the current node
      return new LinkedQuadtree[T, U]( bounds, level, nodes, p :: shapes, p :: asList, splitSize, depth )
    }

    // There is not enough space into the current node and no defined subnodes. Split and add the shape
    if( level < depth && shapes.length >= splitSize ) {
      return split( ) + p
    }

    return new LinkedQuadtree[T, U]( bounds, level, nodes, p :: shapes, p :: asList, splitSize, depth )
  }

  /**
   * Tells if the LinkedQuadtree is empty of Nodes
   *
   * @return true is the quadtree doesn't contain any subnode
   */
  def areNodesEmpty: Boolean = nodes.isEmpty && nodes.forall( _.areNodesEmpty )

  /**
   * Tells if the LinkedQuadtree is empty of Shapes
   *
   * @return true is the quadtree doesn't contain any Shape
   */
  def areShapesEmpty: Boolean = shapes.isEmpty && nodes.forall( _.areShapesEmpty )

  /**
   * Reset the status of the LinkedQuadtree
   *
   * @return A new quadtree, with the same parameters as the current one, but empty
   */
  def clear( ): LinkedQuadtree[T, U] = new LinkedQuadtree[T, U]( bounds, level, List( ), List( ), List( ), splitSize, depth )

  /**
   * Return all Shapes that could collide with the given object
   *
   * @param s A Shape used to collect other shapes that are spatially near it
   * @return All Shapes that could collide with the given object
   */
  def lookAround( s: T ): List[U] = {
    if( nodes.isEmpty ) {
      return shapes.filter( _.objectShape != s )
    }

    val where = findNode( s )

    // If the Shape doesn't fit any subnode, just return the content of the current node
    if( where.isEmpty ) {
      return shapes.filter( _.objectShape != s )
    }

    // Otherwise, look recursively into the subnode
    return shapes.filter( _.objectShape != s ) ::: where.get.lookAround( s )
  }

  /**
   * Create 4 quadrants into the node
   *
   * Split the node into four subnodes by dividing the node info four equal parts, initialising the four subnodes with
   * the new bounds and inserts the contained shapes in the subnodes where they fit
   *
   * @return A new LinkedQuadtree with 4 new subnodes
   */
  def split( ): LinkedQuadtree[T, U] = {
    // Lookup table to access the quadrants in a functional way
    val quadLookup = List(
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

    // Create a LinkedQuadtree on each quadrant and insert in each one of the the Shapes that it's able to contain
    val quads = quadLookup map { q =>
      // Shapes that belong to the "q" quadrant
      val quadShapes = shapes.filter( s => q.contains( s.objectShape ) )
      // Create a new Quadtree for the quadrant
      new LinkedQuadtree[T, U]( q, level + 1, List( ), quadShapes, quadShapes, splitSize, depth )
    }

    // These are the shapes that don't fit any subnode
    val upperShapes = shapes.diff( quads.flatMap( _.shapes ) )
    // And return a whole new LinkedQuadtree as a result of the split
    new LinkedQuadtree[T, U](
      bounds, level,
      quads, upperShapes, asList,
      splitSize, depth
    )
  }
}


object LinkedQuadtree {

  /**
   * Creates a new LinkedQuadtree
   *
   * @param bounds The area that the LinkedQuadtree will cover
   * @param initialSet The initial data contained by the LinkedQuadtree
   * @param splitSize Max size of each node before a split happens
   * @param depth Depth of the LinkedQuadtree
   * @tparam T Type of `Shape` that the LinkedQuadtree will use for spatial checking
   * @tparam U Type of `PhysicalObject` that the LinkedQuadtree will contain
   * @return A new instance of LinkedQuadtree
   */
  def apply[T <: Shape, U <: PhysicalObject]( bounds: Box, initialSet: List[U] = List( ), splitSize: Int = 1, depth: Int = 1 ): LinkedQuadtree[T, U] = {
    if( initialSet.isEmpty ) {
      // Fast initialization of an empty quadtree
      new LinkedQuadtree[T, U]( bounds, 0, List( ), List( ), List( ), splitSize, depth )
    }
    else {
      // For quadtree with initial shapes, I add them one by one
      initialSet.foldLeft( LinkedQuadtree( bounds, List[U]( ), splitSize, depth ) ) {_ + _}
    }
  }

}
