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

package com.colofabrix.scala

import java.util.Locale

import scala.reflect.ClassTag

/**
 * Various utilities
 */
object Tools {

  /**
   * Enrichment for every type
   *
   * @param c The value to work on
   */
  implicit class CustomClass[T: ClassTag]( c: T ) {

    /**
     * Gets the name of the clean name of the class
     * @return The name of the class
     */
    def className: String = c.getClass.toString.replaceFirst( "^class (\\w+\\.)*", "" )

  }

  /**
   * Enrichment for numeric types
   *
   * @param number The value to work on
   */
  implicit class CustomNumeric[T: Numeric]( number: T ) {
    private val conv = implicitly[Numeric[T]]
    private val prefixes = Map(
      -12 → "p",
      -9 → "n",
      -6 → "µ",
      -3 → "m",
      0 → "",
      3 → "K",
      6 → "M",
      9 → "G",
      12 → "T"
    )

    /**
     * Normalize a number to be between 1 and 1000 and applies a metric prefix to the unit
     *
     * E.g.: from 123456789 is converted to 123,456M
     *
     * @see https://en.wikipedia.org/wiki/Engineering_notation
     * @param unit The unit of measure (seconds, meters, watts, ....)
     * @param startingExp The number can originally be expressed in a specific prefix.
     * @return A tuple containing 1) the normalized number and 2) the applied prefix with the unit
     */
    def toAutoEngineering( unit: String = "", startingExp: Int = 0 ): ( Double, String ) = {

      def calcExp( n: Double, e: Int ) = n * Math.pow( 10, e.toDouble )

      // A number can already be expressed in a different prefix, like a given milliseconds
      val normNumber = calcExp( conv.toDouble( number ), startingExp )
      @SuppressWarnings( Array( "org.brianmckenna.wartremover.warts.Var" ) )
      var index = prefixes.keySet.max

      while ( calcExp( normNumber, index ) <= 1.0 || calcExp( normNumber, index ) >= 1000.0 )
        index -= 3

      ( calcExp( normNumber, index ), prefixes( -index ) + unit )
    }

    /**
     * Convert a number to a specific metric prefix
     *
     * @see https://en.wikipedia.org/wiki/Engineering_notation
     * @param unit The unit of measure (seconds, meters, watts, ....)
     * @param startingExp The number can originally be expressed in a specific prefix.
     * @return A tuple containing 1) the normalized number and 2) the applied prefix with the unit
     */
    def toFixedEngineering( unit: String = "", targetPrefix: Int = 0, startingExp: Int = 0 ): ( Double, String ) = {

      def calcExp( n: Double, e: Int ) = n * Math.pow( 10, e.toDouble )

      // A number can already be expressed in a different prefix, like a given milliseconds
      val normNumber = calcExp( conv.toDouble( number ), startingExp )
      ( calcExp( normNumber, -targetPrefix ), prefixes( targetPrefix ) + unit )
    }

  }

  /**
   * Wrap action into a timer and displays the result
   *
   * @param description A string containing a description of the timer. The string "$time" will be replaced with the actual time
   * @param actions The actions to perform and measure
   */
  def measureTime( description: String )( actions: ⇒ Unit ): Unit = {
    val start = java.lang.System.nanoTime()

    actions

    val end = java.lang.System.nanoTime()
    //val time = (end - start).toAutoEngineering( "s", -9 )
    val time = ( end - start ).toFixedEngineering( "s", -3, -9 )

    println( description.replaceAll( "@time", "%.3f".formatLocal( Locale.getDefault, time._1 ) + time._2 ) )
  }

}