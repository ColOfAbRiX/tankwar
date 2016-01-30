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
import com.colofabrix.scala.gfx.abstracts.Renderer
import com.colofabrix.scala.gfx.renderers.QuadtreeRenderer

import scala.reflect.ClassTag

/**
  * An immutable LeafQuadtree implementation
  *
  * A quadtree is a trie with 4 children nodes per parent used to partition a cartesian plane and speed up
  * object-object interactions in graphical environments.
  *
  * @see http://gamedevelopment.tutsplus.com/tutorials/quick-tip-use-quadtrees-to-detect-likely-collisions-in-2d-space--gamedev-374
  * @param bounds    The boundary of the quadtree
  * @param level     The level of the root of the quadtree. If the quadtree is not a subtree of any other node, this parameter is 0
  * @param _nodes    The children nodes of the current node, or an empty list if we are on a leaf
  * @param _objects  The shapes contained by the node.
  * @param splitSize The size of `shapes` after which the node splits
  * @param depth     The maximum depth of the quadtree
  * @tparam T The type of objects contained by the quadtree. They must be `PhysicalObject`
  */
class LeafQuadtree[T: SpatialIndexable : ClassTag] protected(
  override val bounds: Box,
  override val level: Int,
  _nodes: Option[List[LeafQuadtree[T]]],
  _objects: Option[List[T]],
  override val splitSize: Int,
  override val depth: Int
) extends SpatialTree[T] {

  require( splitSize > 0, "The bucket size must be an integer greater than zero" )
  require( depth > 0, "The number of levels must be an integer greater than zero" )

  private def box: Box = Box.getAsBox( bounds )

  /**
    * The children nodes of the current node, or an empty list if we are on a leaf
    */
  val nodes = _nodes.getOrElse( List.empty[LeafQuadtree[T]] )

  /**
    * The shapes contained by the node.
    */
  val objects: List[T] = _objects.getOrElse( List.empty[T] )

  /**
    * List of the boxes that cover the subnodes
    */
  protected lazy val quadLookup = box.split( 2, 2 ).toList

  /**
    * Create 4 quadrants into the node
    *
    * Split the node into four subnodes by dividing the node info four equal parts, initialising the four subnodes with
    * the new bounds and inserts the contained shapes in the subnodes where they fit
    *
    * @return A new LeafQuadtree with 4 new subnodes
    */
  override protected def split( ) = {
    val tmp = Box.spreadAcross( quadLookup, objects, compact = false )

    // Create a LeafQuadtree on each quadrant and insert in each one of the the Shapes that it's able to contain
    val quads =
      tmp map {
        case (box, shapes) =>
          new LeafQuadtree[T]( box, level + 1, None, Some( shapes.toList ), splitSize, depth )
      }

    // And return a whole new LeafQuadtree as a result of the split
    new LeafQuadtree[T]( box, level, Some( quads.toList ), None, splitSize, depth )
  }

  /**
    * Remove the object from the quadtree.
    *
    * Nothing bad happens if the Shape is not in the LeafQuadtree
    *
    * @return A new quadtree without the specified Shape.
    */
  @inline
  @SuppressWarnings( Array( "org.brianmckenna.wartremover.warts.Var" ) )
  override def -( p: T ): LeafQuadtree[T] = {
    // This check avoids to parse the subnodes that don't contain the object
    if( !bounds.intersects( shape( p ) ) ) {
      return this
    }

    if( nodes.nonEmpty ) {
      var newNodes = for( n ← nodes ) yield n - p

      // Un-split subnodes when they are empty
      val tmp = unsplit( newNodes )
      newNodes = tmp._2

      return new LeafQuadtree[T]( bounds, level, Some( newNodes ), Some( tmp._1 ), splitSize, depth ).refresh( )
    }

    return new LeafQuadtree[T]( bounds, level, None, Some( objects.filter( _ != p ) ), splitSize, depth ).refresh( )
  }

  /**
    * Insert the object into the quadtree. If the node exceeds the capacity, it will split and add all objects to their corresponding nodes.
    *
    * @return A new quadtree containing the new Shape in the appropriate position
    */
  @inline
  override final def +( p: T ): LeafQuadtree[T] = {
    // This check avoids to parse the subnodes that don't contain the object
    if( !bounds.intersects( shape( p ) ) ) {
      return this
    }

    // Treats the case of the node not being a leaf
    if( nodes.nonEmpty ) {
      // Try to add the object to every node (the + method itself checks if the object belongs to its)
      val newNodes = nodes map { n => n + p }
      return new LeafQuadtree[T]( bounds, level, Some( newNodes ), None, splitSize, depth )
    }

    // Check if it has to split or not
    if( level < depth - 1 && objects.length >= splitSize ) {
      return split( ) + p
    }

    // Leaf case, add the object and exit
    return new LeafQuadtree[T]( bounds, level, None, Some( p :: objects ), splitSize, depth )
  }

  /**
    * Insert a list of objects into the LeafQuadtree.
    *
    * @return A new quadtree containing the new object in the appropriate position
    */
  @inline
  override def ++( pi: List[T] ): LeafQuadtree[T] = {
    val shapesInNode = pi.filter( s ⇒ bounds.intersects( shape( s ) ) )

    // This check avoids to do any work on an empty set
    if( shapesInNode.isEmpty ) {
      return this
    }
    // Fall back in the single element add (avoid list scans, not sure how helpful)
    if( shapesInNode.size == 1 ) {
      return this + shapesInNode( 0 )
    }

    // Try to add the object to every node (the + method itself checks if the object belongs to its)
    if( nodes.nonEmpty ) {
      val newNodes = nodes map { _ ++ shapesInNode }
      return new LeafQuadtree[T]( bounds, level, Some( newNodes ), None, splitSize, depth )
    }

    // Check if it has to split or not
    if( level < depth - 1 && objects.length >= splitSize ) {
      return split( ) ++ shapesInNode
    }

    // Leaf case, add the objects and exit
    return new LeafQuadtree[T]( bounds, level, None, Some( shapesInNode ::: objects ), splitSize, depth )
  }

  /**
    * Reset the status of the LeafQuadtree
    *
    * @return A new quadtree, with the same parameters as the current one, but empty
    */
  override def clear( ) = new LeafQuadtree[T]( bounds, level, None, None, splitSize, depth )

  /**
    * Tells if the LeafQuadtree is empty of Shapes
    *
    * @return true is the quadtree doesn't contain any Shape
    */
  override def isEmpty: Boolean = objects.isEmpty && nodes.forall( _.isEmpty )

  /**
    * Return all Shapes that could collide with the given object
    *
    * @param s A Shape used to collect other shapes that are spatially near it
    * @return All Shapes that could collide with the given object
    */
  override def lookAround( s: Shape ): List[T] = {
    // Avoid to go further if there is no intersection with the current node
    if( !bounds.intersects( s ) ) {
      return List( )
    }

    // Leaf case, return all the objects in the node
    if( nodes.isEmpty ) {
      return objects
    }

    nodes flatMap { n ⇒
      if( s contains n.bounds ) {
        // If the shape fully covers the node, return all node's content
        n.toList
      }
      else if( n.bounds intersects s ) {
        // If the shape intersects the node, recurse
        n lookAround s
      }
      else {
        // Otherwise return nothing
        List( )
      }
    }
  }

  /**
    * Updates the quadtree
    *
    * The objects inside the quadtree can move and thus their position inside the tree can change
    *
    * @return A new instance of a SpatialTree with the updated objects
    */
  @inline
  override def refresh( ): LeafQuadtree[T] = {

    @SuppressWarnings( Array( "org.brianmckenna.wartremover.warts.Var" ) )
    def recurse( q: LeafQuadtree[T] ): (List[T], LeafQuadtree[T]) = {
      // Objects that were previously contained in one or more subnodes but moved from it now
      var movedFromNodes = List[T]( )
      // Objects that didn't move from their node
      var stillObjects = List[T]( )
      // Subnodes that have been refreshed
      var refreshedNodes = List.empty[LeafQuadtree[T]]

      // Intermediate nodes case
      for( n ← q.nodes ) {
        val (moved, still) = recurse( n )

        movedFromNodes = moved ::: movedFromNodes
        refreshedNodes = still :: refreshedNodes
      }

      // Un-split subnodes when they are empty
      val tmp = unsplit( refreshedNodes )
      stillObjects = tmp._1 ::: stillObjects

      // Leaf nodes case
      for( s ← q.objects ) {
        if( q.bounds.intersects( shape( s ) ) ) {
          stillObjects = s :: stillObjects
        }
        else {
          movedFromNodes = s :: movedFromNodes
        }
      }

      (movedFromNodes, new LeafQuadtree[T]( Box.getAsBox( q.bounds ), q.level, Some( tmp._2 ), Some( stillObjects ), q.splitSize, q.depth ))
    }

    // Refresh all the nodes and collect the moved objects. Then inserts back the moved object to where they belong
    val splitResult = recurse( this )
    splitResult._2 ++ splitResult._1
  }

  /**
    * An object responsible to renderer the class where this trait is applied
    *
    * @return A renderer that can draw the object where it's applied
    */
  override def renderer: Renderer = new QuadtreeRenderer( this )

  /**
    * The number of shapes contained in the quadtree
    */
  override def size: Int = objects.length + nodes.foldLeft( 0 )( _ + _.size )

  /**
    * Get the current tree as a list
    *
    * Building a list from a tree requires a scan of the whole tree
    *
    * @return A new List containing all the elements of the tree
    */
  override def toList: List[T] = objects ::: nodes.flatMap( _.toList )

  /**
    * Returns a string representation of the tree
    *
    * @return A new string containing a textual representation of the tree
    */
  override def toString: String = {
    import com.colofabrix.scala.Tools._
    val ct = implicitly[ClassTag[T]]

    s"""|${"    " * level }Type: ${this.className }[${ct.runtimeClass.toString.replaceFirst( "^class (\\w+\\.)*", "" ) }]
        |${"    " * level }Objects: ${objects.size }
        |${"    " * level }Subnodes: ${nodes.map( _.toString ).mkString( "{\n", "", s"${"    " * level }}\n" ) }"""
      .stripMargin
  }


  /**
    * Un-splits a set of nodes if they are empty
    *
    * @param q The list of subnodes to check for un-splitting
    * @return A tuple containing 1) the merged objects and 2) the list of unmerged nodes. There can be either 1) or 2), not both
    */
  @inline
  protected def unsplit( q: List[LeafQuadtree[T]] ): (List[T], List[LeafQuadtree[T]]) = {
    if( q.nonEmpty ) {
      val objectsInSubnodes = q.flatMap( _.objects )

      if( objectsInSubnodes.size < splitSize && q.forall( _.nodes.isEmpty ) ) {
        return (objectsInSubnodes, List.empty[LeafQuadtree[T]])
      }
    }

    (List.empty[T], q)
  }
}

object LeafQuadtree {

  /**
    * Creates a new LeafQuadtree
    *
    * @param bounds    The area that the LinkedLeafQuadtreeTmp will cover
    * @param objects   The initial data contained by the LinkedLeafQuadtreeTmp
    * @param splitSize Max size of each node before a split happens
    * @param maxDepth  Depth of the LinkedLeafQuadtreeTmp
    * @tparam T Type of [[com.colofabrix.scala.simulation.abstracts.PhysicalObject]] that the LinkedLeafQuadtreeTmp will contain
    * @return A new instance of LinkedLeafQuadtreeTmp
    */
  def apply[T: SpatialIndexable : ClassTag](
    bounds: Shape,
    objects: List[T] = List[T]( ),
    splitSize: Int = 1,
    maxDepth: Int = 1
  ): LeafQuadtree[T] = {
    val emptySet = new LeafQuadtree[T](
      Box.getAsBox( bounds ), 0,
      None, None,
      splitSize, maxDepth
    )

    val tree = if( objects.isEmpty ) emptySet else emptySet ++ objects
    tree match {
      case lq: LeafQuadtree[T] => lq
      case _ => throw new IllegalArgumentException
    }
  }

}