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

package com.colofabrix.scala.geometry.abstracts

import com.colofabrix.scala.geometry.shapes._

/**
 * Represents a Shape container
 *
 * A container is used as a faster and simpler way to obtain information or do some
 * actions on a Shape that would otherwise require more computation.
 * Ideally the computation should take O(n) or O(n*log(n)). This trait should be applied
 * to shapes that guarantee this fast computation
 */
trait Container extends Shape {

  /**
   * Determines if the container fully contain a Shape
   *
   * @param s The shape to check
   * @return true if the container fully contain the other shape. Boundaries are included in the container
   */
  def contains( s: Shape ): Boolean

}


object Container {

  /**
   * Finds the container that best contain a given Shape
   *
   * "Best" means the container that has the minimal area and that fully contains the shape
   *
   * @param s The shape that must be surrounded by a container
   * @return A new `Container` that contains the Shape and that has the minimal area between the available containers
   */
  def bestFit( s: Shape ): Container = {
    // I use a List so I can apply a method on it. For Shapes that are also Containers each `bestFix` is already
    // optimized and it will not consume much time, so I can leave all the calls here
    val tries = Box.bestFit( s ) :: Circle.bestFit( s ) :: Nil

    // Return the Container with the smaller area
    tries.minBy( _.area )
  }

}