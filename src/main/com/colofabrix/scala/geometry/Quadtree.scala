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

import com.colofabrix.scala.geometry.shapes.{Box, ConvexPolygon}

/**
 * Quadtree implementation
 *
 * A quadtree is a try of tree with 4 children nodes per parent used
 * to partition a cartesian plane and speed up object-object interactions
 * in graphical environments
 */
class Quadtree[T <: ConvexPolygon]( val dimensions: Box, val I: Quadtree[T], val II: Quadtree[T], val III: Quadtree[T], val IV: Quadtree[T], val elements: Seq[T] = Seq() ) {

  def insert( o: T ): Quadtree[T] = ???

  def delete( o: T ): Quadtree[T] = ???

  def update( o: T ): Quadtree[T] = ???

  def build( objects: Seq[T] ): Quadtree[T] = ???

  def notFullyContained( o: T ): Boolean =
    o.vertices.foldLeft(false) { ( r, v ) => r || !dimensions.overlaps(v) }

  def collision( o: T ): Option[T] = Option.empty

}
