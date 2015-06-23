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

import com.colofabrix.scala.geometry.shapes.{ Box, ConvexPolygon }
import com.colofabrix.scala.math.Vector2D

/**
 * Quadtree implementation
 *
 * A quadtree is a try of tree with 4 children nodes per parent used
 * to partition a cartesian plane and speed up object-object interactions
 * in graphical environments
 */
class Quadtree[T <: ConvexPolygon] private( val bounds: Box, val level: Int, private val nodes: Seq[Quadtree[T]], val bucketSize: Int, val maxObjects: Int ) {

  // Top-Right quadrant
  private val I = new Box(
    Vector2D.new_xy(bounds.bottomLeft.x + bounds.width / 2, bounds.bottomLeft.y + bounds.height / 2),
    bounds.topRight
  )

  // Top-Left quadrant
  private val II = new Box(
    Vector2D.new_xy(bounds.bottomLeft.x, bounds.bottomLeft.y + bounds.height / 2),
    Vector2D.new_xy(bounds.bottomLeft.x + bounds.width / 2, bounds.bottomLeft.y + bounds.height)
  )

  // Bottom-Left quadrant
  private val III = new Box(
    bounds.bottomLeft,
    Vector2D.new_xy(bounds.bottomLeft.x + bounds.width / 2, bounds.bottomLeft.y + bounds.height / 2)
  )

  // Bottom-Right quadrant
  private val IV = new Box(
    Vector2D.new_xy(bounds.bottomLeft.x + bounds.width, bounds.bottomLeft.y + bounds.height / 2),
    Vector2D.new_xy(bounds.bottomLeft.x + bounds.width / 2, bounds.bottomLeft.y)
  )

  /**
   * Reset the status of the Quadtree
   *
   * @return A new quadtree, with the same parameters as the current one, but empty
   */
  def clear( ): Quadtree[T] = new Quadtree[T](bounds, level, Seq(), bucketSize, maxObjects)

  /**
   * Create 4 quadrants
   *
   * Split the node into four subnodes by dividing the node info four equal parts and initialising the four
   * subnodes with the new bounds
   *
   * @return A new Quadtree with 4 new subnodes
   */
  protected def split( ): Quadtree[T] = {
    // Create a Quadtree on each node
    val quads = Seq(
      new Quadtree[T](I, level + 1, Seq(), bucketSize, maxObjects),
      new Quadtree[T](II, level + 1, Seq(), bucketSize, maxObjects),
      new Quadtree[T](III, level + 1, Seq(), bucketSize, maxObjects),
      new Quadtree[T](IV, level + 1, Seq(), bucketSize, maxObjects)
    )

    // And return a whole new Quadtree as a result of the split
    new Quadtree[T](bounds, level + 1, quads, bucketSize, maxObjects)
  }

  protected def getIndex( ): Int = ???

  def insert( ) = ???

  def retrieve( ) = ???

}


object Quadtree {

  def apply[T <: ConvexPolygon]( bounds: Box, bucketSize: Int = 1, maxObjects: Int = 1 ) =
    new Quadtree[T](bounds, 0, Seq(), bucketSize, maxObjects)

}
