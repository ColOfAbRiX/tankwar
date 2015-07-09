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

/**
 * Various utilities
 */
object Util {

  /**
   * Wrap action into a timer and displays the result
   *
   * @param description A string containing a description of the timer. The string "$time" will be replaced with the actual time
   * @param action The actions to perform and measure
   */
  def measureTime( description: String )( action: => Unit ): Unit = {
    val start = java.lang.System.currentTimeMillis( );

    action

    val end = java.lang.System.currentTimeMillis( );
    println( description.replaceAll( "\\$time", (end - start) + "ms" ) )
  }

}
