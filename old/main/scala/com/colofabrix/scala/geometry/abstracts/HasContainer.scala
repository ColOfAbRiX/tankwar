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

import com.colofabrix.scala.geometry.shapes.Box
import com.colofabrix.scala.simulation.abstracts.PhysicalObject

/**
  * Typeclass to define object that can be indexed spatially.
  *
  * A container for the object is the minimum requirement to allow to index spatially.
  *
  * @tparam T The type of object we want to convert
  */
trait HasContainer[T] {
  /**
    * Gets the container of the object
    *
    * @param t The object for which a container should be found
    * @return A new [[HasContainer]] instance that fully contains the object
    */
  def container( t: T ): Container

  /**
    * Gets the Box container of the object
    *
    * @param t The object for which a container should be found
    * @return A new Box instance that fully contains the object
    */
  def boxContainer( t: T ): Box
}

object HasContainer {

  /**
    * Converter [[Shape]] -> [[HasContainer]] with a generic Container
    *
    * @tparam T The type of Shape to convert
    * @return A new instance of SpatialIndexable that can extract information from a [[Shape]]
    */
  implicit def iShapeCont[T <: Shape] = new HasContainer[T] {
    def container( t: T ) = t.container

    def boxContainer( t: T ) = Box.bestFit( t )
  }

  /**
    * Converter PhysicalObject -> HasContainer
    *
    * @tparam T The type of PhysicalObject to convert
    * @return A new instance of SpatialIndexable that can extract information from a PhysicalObject
    */

  implicit def iPhysicalObjectCont[T <: PhysicalObject] = new HasContainer[T] {
    def container( t: T ) = t.objectShape.container

    def boxContainer( t: T ) = Box.bestFit( t.objectShape )
  }

}