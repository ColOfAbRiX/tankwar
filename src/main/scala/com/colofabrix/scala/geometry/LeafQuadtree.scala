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

import com.colofabrix.scala.geometry.abstracts.Shape
import com.colofabrix.scala.geometry.abstracts.SpatialTree._
import com.colofabrix.scala.geometry.shapes.Box
import com.colofabrix.scala.gfx.abstracts.Renderer
import com.colofabrix.scala.gfx.renderers.QuadtreeRenderer

import scala.annotation.tailrec
import scala.reflect.ClassTag

/**
 * An immutable LeafQuadtree implementation
 *
 * A quadtree is a trie with 4 children nodes per parent used to partition a cartesian plane and speed up
 * object-object interactions in graphical environments.
 *
 * @see http://gamedevelopment.tutsplus.com/tutorials/quick-tip-use-quadtrees-to-detect-likely-collisions-in-2d-space--gamedev-374
 * @param bounds The boundary of the quadtree
 * @param level The level of the root of the quadtree. If the quadtree is not a subtree of any other node, this parameter is 0
 * @param nodes The children nodes of the current node, or an empty list if we are on a leaf
 * @param objects The shapes contained by the node.
 * @param splitSize The size of `shapes` after which the node splits
 * @param depth The maximum depth of the quadtree
 * @tparam T The type of objects contained by the quadtree. They must be `PhysicalObject`
 */
class LeafQuadtree[T: SpatialIndexable] protected (
    override val bounds: Shape,
    val level: Int,
    override val nodes: List[LeafQuadtree[T]],
    override val objects: List[T],
    override val splitSize: Int,
    override val depth: Int
)(
    implicit
    ct: ClassTag[T]
) extends abstracts.SpatialTree[T] {

  require( bounds != null, "A box must be specified to indicate the LeafQuadtree area" )
  require( nodes != null, "A node list must be specified, even empty" )
  require( objects != null, "A shape list must be specified, even empty" )
  require( splitSize > 0, "The bucket size must be an integer greater than zero" )
  require( depth > 0, "The number of levels must be an integer greater than zero" )

  private def box: Box = bounds match {
    case b: Box ⇒ b
    case _ ⇒ throw new IllegalArgumentException( "Variable 'bound' is not of type Box" )
  }

  @inline
  private def shape( t: T ): Shape = implicitly[SpatialIndexable[T]].container( t )

  /**
   * Create 4 quadrants into the node
   *
   * Split the node into four subnodes by dividing the node info four equal parts, initialising the four subnodes with
   * the new bounds and inserts the contained shapes in the subnodes where they fit
   *
   * @return A new LeafQuadtree with 4 new subnodes
   */
  override protected def split() = {
    import com.colofabrix.scala.math.Vector2D._

    val quadLookup = List(
      new Box(
        // Top-Right quadrant
        new_xy( box.bottomLeft.x + box.width / 2, box.bottomLeft.y + box.height / 2 ),
        box.topRight
      ),
      new Box(
        // Top-Left quadrant
        new_xy( box.bottomLeft.x, box.bottomLeft.y + box.height / 2 ),
        new_xy( box.bottomLeft.x + box.width / 2, box.bottomLeft.y + box.height )
      ),
      new Box(
        // Bottom-Left quadrant
        box.bottomLeft,
        new_xy( box.bottomLeft.x + box.width / 2, box.bottomLeft.y + box.height / 2 )
      ),
      new Box(
        // Bottom-Right quadrant
        new_xy( box.bottomLeft.x + box.width / 2, box.bottomLeft.y ),
        new_xy( box.bottomLeft.x + box.width, box.bottomLeft.y + box.height / 2 )
      )
    )

    // Create a LeafQuadtree on each quadrant and insert in each one of the the Shapes that it's able to contain
    val quads = quadLookup map { q ⇒
      val shapesInNode = objects.filter( s ⇒ q.intersects( shape( s ) ) )
      new LeafQuadtree[T]( q, level + 1, List(), shapesInNode, splitSize, depth )
    }

    // And return a whole new LeafQuadtree as a result of the split
    new LeafQuadtree[T]( box, level, quads, List(), splitSize, depth )
  }

  /**
   * Remove the object from the quadtree.
   *
   * Nothing bad happens if the Shape is not in the LeafQuadtree
   *
   * @return A new quadtree without the specified Shape.
   */
  @inline
  override def -( p: T ): LeafQuadtree[T] = {
    // This check avoids to parse the subnodes that don't contain the object
    if ( !bounds.intersects( shape( p ) ) ) {
      return this
    }

    if ( nodes.nonEmpty ) {
      var newNodes = for ( n ← nodes ) yield n - p

      // Un-split subnodes when they are empty
      val tmp = unsplit( newNodes )
      newNodes = tmp._2

      return new LeafQuadtree[T]( bounds, level, newNodes, tmp._1, splitSize, depth ).refresh()
    }

    return new LeafQuadtree[T]( bounds, level, List(), objects.filter( _ != p ), splitSize, depth ).refresh()
  }

  /**
   * Insert the object into the quadtree. If the node exceeds the capacity, it will split and add all objects to their corresponding nodes.
   *
   * @return A new quadtree containing the new Shape in the appropriate position
   */
  @inline
  override final def +( p: T ): LeafQuadtree[T] = {
    // This check avoids to parse the subnodes that don't contain the object
    if ( !bounds.intersects( shape( p ) ) ) {
      return this
    }

    // Treats the case of the node not being a leaf
    if ( nodes.nonEmpty ) {
      // Try to add the object to every node (the + method itself checks if the object belongs to its)
      val newNodes = nodes map { _ + p }
      return new LeafQuadtree[T]( bounds, level, newNodes, List(), splitSize, depth )
    }

    // Never add twice the same object
    if ( objects.contains( p ) ) {
      return this
    }

    // Check if it has to split or not
    if ( level < depth && objects.length >= splitSize ) {
      return split() + p
    }

    // Leaf case, add the object and exit
    return new LeafQuadtree[T]( bounds, level, List(), p :: objects, splitSize, depth )
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
    if ( shapesInNode.isEmpty ) {
      return this
    }
    // Fall back in the single element add (avoid list scans, not sure how helpful)
    if ( shapesInNode.size == 1 ) {
      return this + shapesInNode( 0 )
    }

    // Try to add the object to every node (the + method itself checks if the object belongs to its)
    if ( nodes.nonEmpty ) {
      val newNodes = nodes map { _ ++ shapesInNode }
      return new LeafQuadtree[T]( bounds, level, newNodes, List(), splitSize, depth )
    }

    // Never add twice the same object in the node
    val uniqueObjects = shapesInNode.filter( !objects.contains( _ ) )

    // Check if it has to split or not
    if ( level < depth && objects.length >= splitSize ) {
      return split() ++ uniqueObjects
    }

    // Leaf case, add the objects and exit
    return new LeafQuadtree[T]( bounds, level, List(), uniqueObjects ::: objects, splitSize, depth )
  }

  /**
   * Reset the status of the LeafQuadtree
   *
   * @return A new quadtree, with the same parameters as the current one, but empty
   */
  override def clear() = new LeafQuadtree[T]( bounds, level, List(), List(), splitSize, depth )

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
    if ( !bounds.intersects( s ) ) {
      return List()
    }

    // Leaf case, return all the objects in the node
    if ( nodes.isEmpty ) {
      return objects
    }

    nodes flatMap { n ⇒
      if ( s contains n.bounds ) {
        // If the shape fully covers the node, return all node's content
        n.toList
      }
      else if ( n.bounds intersects s ) {
        // If the shape intersects the node, recurse
        n lookAround s
      }
      else {
        // Otherwise return nothing
        List()
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
  override def refresh(): LeafQuadtree[T] = {

    def recurse( q: LeafQuadtree[T] ): ( List[T], LeafQuadtree[T] ) = {
      // Objects that were previously contained in one or more subnodes but moved from it now
      var movedFromNodes = List[T]()
      // Objects that didn't move from their node
      var stillObjects = List[T]()
      // Subnodes that have been refreshed
      var refreshedNodes = List[LeafQuadtree[T]]()

      // Intermediate nodes case
      for ( n ← q.nodes ) {
        val ( moved, still ) = recurse( n )

        movedFromNodes = moved ::: movedFromNodes
        refreshedNodes = still :: refreshedNodes
      }

      // Un-split subnodes when they are empty
      val tmp = unsplit( refreshedNodes )
      stillObjects = tmp._1 ::: stillObjects

      // Leaf nodes case
      for ( s ← q.objects ) {
        if ( q.bounds.intersects( shape( s ) ) ) {
          stillObjects = s :: stillObjects
        }
        else {
          movedFromNodes = s :: movedFromNodes
        }
      }

      ( movedFromNodes, new LeafQuadtree[T]( q.bounds, q.level, tmp._2, stillObjects, q.splitSize, q.depth ) )
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

    s"""|${"    " * level}Type: ${this.className}[${ct.runtimeClass.toString.replaceFirst( "^class (\\w+\\.)*", "" )}]
        |${"    " * level}Objects: ${objects.size}
        |${"    " * level}Subnodes: ${nodes.map( _.toString ).mkString( "{\n", "", s"${"    " * level}}\n" )}"""
      .stripMargin
  }

  /**
   * Un-splits a set of nodes if they are empty
   *
   * @param q The list of subnodes to check for un-splitting
   * @return
   */
  @inline
  def unsplit( q: List[LeafQuadtree[T]] ): ( List[T], List[LeafQuadtree[T]] ) = {
    val objectsInSubnodes = q.flatMap( _.objects )

    if ( q.nonEmpty && q.forall( _.nodes.isEmpty ) && objectsInSubnodes.size <= splitSize ) {
      return ( objectsInSubnodes, List() )
    }

    ( List(), q )
  }

}

object LeafQuadtree {

  /**
   * Creates a new LeafQuadtree
   *
   * @param bounds The area that the LinkedLeafQuadtreeTmp will cover
   * @param initialList The initial data contained by the LinkedLeafQuadtreeTmp
   * @param splitSize Max size of each node before a split happens
   * @param depth Depth of the LinkedLeafQuadtreeTmp
   * @tparam T Type of `PhysicalObject` that the LinkedLeafQuadtreeTmp will contain
   * @return A new instance of LinkedLeafQuadtreeTmp
   */
  def apply[T: SpatialIndexable](
    bounds: Shape,
    initialList: List[T] = List(),
    splitSize: Int = 1,
    depth: Int = 1
  )( implicit ct: ClassTag[T] ) = {
    if ( initialList.isEmpty ) {
      new LeafQuadtree[T]( bounds, 0, List(), List(), splitSize, depth )
    }
    else {
      new LeafQuadtree[T]( bounds, 0, List(), List(), splitSize, depth ) ++ initialList
    }
  }

}