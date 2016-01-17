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

package com.colofabrix.scala.geometry.abstracts

import com.colofabrix.scala.simulation.abstracts.PhysicalObject

/**
 * Typeclass to define object that can be indexed spatially.
 *
 * A container for the object is the minimum requirement to allow to index spatially.
 *
 * @tparam T The type of object we want to convert
 */
trait SpatialIndexable[-T] {

  /**
   * Gets the container of the object
   *
   * @return A new Container instance that fully contains the object
   */
  def container( t: T ): Container

}

object SpatialIndexable {

  /**
   * Converter `Shape` -> `SpatialIndexable[T]`
   *
   * @return A new instance of SpatialIndexable that can extract information from a `Shape`
   */
  implicit def indexableShape[T <: Shape] = new SpatialIndexable[T] {
    override def container( that: T ): Container = that.container
  }

  /**
   * Converter `PhysicalObject` -> `SpatialIndexable[T]`
   *
   * @return A new instance of SpatialIndexable that can extract information from a `PhysicalObject`
   */
  implicit def indexablePhysicalObject[T <: PhysicalObject] = new SpatialIndexable[T] {
    override def container( that: T ): Container = that.objectShape.container
  }

}