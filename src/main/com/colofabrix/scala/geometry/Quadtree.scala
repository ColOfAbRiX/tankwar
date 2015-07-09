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

import com.colofabrix.scala.geometry.abstracts._
import com.colofabrix.scala.geometry.shapes.Box
import com.colofabrix.scala.gfx.abstracts.Renderer
import com.colofabrix.scala.gfx.renderers.QuadtreeRenderer
import com.colofabrix.scala.math.Vector2D
import com.colofabrix.scala.simulation.abstracts.PhysicalObject

/**
 * An immutable Quadtree implementation
 *
 * A quadtree is a try of tree with 4 children nodes per parent used to partition a cartesian plane and speed up
 * object-object interactions in graphical environments.
 *
 * @see http://gamedevelopment.tutsplus.com/tutorials/quick-tip-use-quadtrees-to-detect-likely-collisions-in-2d-space--gamedev-374
 * @param bounds The boundary of the quadtree
 * @param level The level of the root of the quadtree. If the quadtree is not a subtree of any other node, this parameter is 0
 * @param nodes The children nodes of the current node, or an empty list if we are on a leaf
 * @param shapes The shapes contained by the node.
 * @param splitSize The size of `shapes` after which the node splits
 * @param depth The maximum depth of the quadtree
 * @tparam T The type of objects contained by the quadtree. They must be `PhysicalObject`
 */
class Quadtree[T <: PhysicalObject] protected(
  override val bounds: Box,
  val level: Int,
  override val nodes: List[Quadtree[T]],
  override val shapes: List[T],
  override val splitSize: Int,
  override val depth: Int
) extends abstracts.SpatialTree[T] {

  require( bounds != null, "A box must be specified to indicate the Quadtree area" )
  require( nodes != null, "A node list must be specified, even empty" )
  require( shapes != null, "A shape list must be specified, even empty" )
  require( splitSize > 0, "The bucket size must be an integer greater than zero" )
  require( depth > 0, "The number of levels must be an integer greater than zero" )

  /**
   * Remove the object from the quadtree.
   *
   * Nothing bad happens if the Shape is not in the Quadtree
   *
   * @return A new quadtree without the specified Shape.
   */
  override def -( p: T ): Quadtree[T] = {
    // If there are subnodes, try to add the new Shape there.
    if( nodes.nonEmpty ) {

      for( where <- findNode( p.objectShape ) ) {
        val output = where - p

        if( output.isEmpty ) {
          return new Quadtree[T]( bounds, level, List( ), output.shapes, splitSize, depth )
        }

        return output
      }
    }

    return new Quadtree[T]( bounds, level, nodes, shapes.filter( _ != p ), splitSize, depth )
  }

  /**
   * Insert the object into the quadtree. If the node exceeds the capacity, it will split and add all objects to their corresponding nodes.
   *
   * @return A new quadtree containing the new Shape in the appropriate position
   */
  override def +( p: T ): Quadtree[T] = {
    // If there are subnodes, try to add the new Shape there.
    if( nodes.nonEmpty ) {

      for( where <- findNode( p.objectShape ) ) {
        val newNodes = nodes.map { q =>
          if( where == q ) where + p else q
        }

        return new Quadtree[T]( bounds, level, newNodes, shapes, splitSize, depth )
      }

      // The shape is not contained by any subnode. If there is space add it to the current node
      return new Quadtree[T]( bounds, level, nodes, p :: shapes, splitSize, depth )
    }

    // There is not enough space into the current node and no defined subnodes. Split and add the shape
    if( level < depth && shapes.length >= splitSize ) {
      return split( ) + p
    }

    return new Quadtree[T]( bounds, level, nodes, p :: shapes, splitSize, depth )
  }

  /**
   * Reset the status of the Quadtree
   *
   * @return A new quadtree, with the same parameters as the current one, but empty
   */
  override def clear( ) = new Quadtree[T]( bounds, level, List( ), List( ), splitSize, depth )

  /**
   * Determines where an object belongs in the quadtree by determining which node the object can fit into.
   *
   * @param s The shape to check
   * @return An Option containing the Quadtree that contains the Shape or nothing
   */
  override def findNode( s: Shape ) = nodes.find( _.bounds.intersects( s ) )

  /**
   * Tells if the Quadtree is empty of Shapes
   *
   * @return true is the quadtree doesn't contain any Shape
   */
  override def isEmpty: Boolean = shapes.isEmpty && nodes.forall( _.isEmpty )

  /**
   * Return all Shapes that could collide with the given object
   *
   * @param s A Shape used to collect other shapes that are spatially near it
   * @return All Shapes that could collide with the given object
   */
  override def lookAround( s: Shape ): List[T] = {
    if( nodes.isEmpty ) {
      return shapes.filter( _.objectShape != s )
    }

    // Look recursively into the subnode
    for( where <- findNode( s ) )
      return shapes.filter( _.objectShape != s ) ::: where.lookAround( s )

    // If the Shape doesn't fit any subnode, just return the content of the current node
    return shapes.filter( _.objectShape != s )
  }

  override def refresh( ): Quadtree[T] = Quadtree( bounds, toList( ), splitSize, depth )

  /**
   * An object responsible to renderer the class where this trait is applied
   *
   * @return A renderer that can draw the object where it's applied
   */
  override def renderer: Renderer = new QuadtreeRenderer( this )

  /**
   * The number of shapes contained in the quadtree
   */
  override def size: Int = shapes.length + nodes.foldLeft( 0 )( _ + _.size )

  /**
   * Create 4 quadrants into the node
   *
   * Split the node into four subnodes by dividing the node info four equal parts, initialising the four subnodes with
   * the new bounds and inserts the contained shapes in the subnodes where they fit
   *
   * @return A new Quadtree with 4 new subnodes
   */
  override def split( ) = {
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

    // Create a Quadtree on each quadrant and insert in each one of the the Shapes that it's able to contain
    val quads = quadLookup map { q =>
      new Quadtree[T]( q, level + 1, List( ), shapes.filter( s => q.contains( s.objectShape ) ), splitSize, depth )
    }

    // And return a whole new Quadtree as a result of the split
    new Quadtree[T]( bounds, level, quads, shapes.diff( quads.flatMap( _.shapes ) ), splitSize, depth )
  }

  /**
   * Get the current tree as a list
   *
   * Building a list from a tree requires a scan of the whole tree
   *
   * @return A new List containing all the elements of the tree
   */
  def toList( ): List[T] = shapes ::: nodes.flatMap( _.toList( ) )
}

object Quadtree {

  /**
   * Creates a new Quadtree
   *
   * @param bounds The area that the LinkedQuadtreeTmp will cover
   * @param initialSet The initial data contained by the LinkedQuadtreeTmp
   * @param splitSize Max size of each node before a split happens
   * @param depth Depth of the LinkedQuadtreeTmp
   * @tparam T Type of `PhysicalObject` that the LinkedQuadtreeTmp will contain
   * @return A new instance of LinkedQuadtreeTmp
   */
  def apply[T <: PhysicalObject]( bounds: Box, initialSet: List[T] = List( ), splitSize: Int = 1, depth: Int = 1 ) = {
    if( initialSet.isEmpty ) {
      new Quadtree[T]( bounds, 0, List( ), List( ), splitSize, depth )
    }
    else {
      initialSet.foldLeft( new Quadtree[T]( bounds, 0, List( ), List( ), splitSize, depth ) ) { _ + _ }
    }
  }

}