/*
 * Copyright (C) 2017 Fabrizio
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

package com.colofabrix.scala.tankwar.physics

import com.colofabrix.scala.math.{ Vect, XYVect }

object PhysicsUtils {

  /**
    * Enrichment for Vect to make some Physical calculations easier
    *
    * @param vector The vector to apply the conversion
    * @tparam T The type of vector
    */
  implicit final class VectUtils[T <: Vect](vector: T) {
    def xy = Seq(vector.x, vector.y)

    def map(f: Double => Double): Vect = XYVect(f(vector.x), f(vector.y))

    def **(t: (Double, Double)): Vect = XYVect(vector.x * t._1, vector.y * t._2)
  }

}
