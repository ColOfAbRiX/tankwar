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

package com.colofabrix.scala.math

/**
  *
  */
object VectUtils {
  /**
    * Enrichment for numeric types to allow commuting of the operations
    *
    * This class implements the same operation of Vect for verse order
    *
    * @param number The object to apply the conversion
    * @tparam T The type of the object that must be convertible in a Numeric
    */
  implicit final class Support[T: Numeric]( number: T ) {
    private val _number = implicitly[Numeric[T]].toDouble( number )
    def *( v: Vect ): Vect = v * _number
  }
}
