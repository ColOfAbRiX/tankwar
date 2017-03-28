/*
 * Copyright (C) 2017 Fabrizio Colonna
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

import java.math.MathContext

/**
  * Mathematical constants and functions
  */
package object math {

  /** The absolute value where a smaller, floating point number is considered zero */
  val FP_EPSILON = 1E-12
  val SIG_FIGURES = 4

  implicit final class NumberDisplay[T: Numeric](number: T) {
    private val _number = implicitly[Numeric[T]].toDouble(number)

    /** Rounds a number to N significant figures */
    def sig(significantFigures: Int = SIG_FIGURES): Double = {
      BigDecimal(_number).round(new MathContext(3)).doubleValue()
    }
  }

  /**
    * Implicit approximate comparison between two Doubles
    * Ref: http://stackoverflow.com/questions/4915462/how-should-i-do-floating-point-comparison
    */
  implicit class DoubleWithAlmostEquals(val d1: Double) extends AnyVal {
    def ~==(d2: Double): Boolean = {
      // See: http://www.programgo.com/article/4441958781/
      val dd1 = d1 + 0.0
      val dd2 = d2 + 0.0

      val diff = Math.abs(dd1 - dd2)

      if( dd1 == dd2 ) {
        // Shortcut, handles infinities
        return true
      }
      else if( dd1 == 0 || dd2 == 0 || diff < FP_EPSILON ) {
        // d1 or d2 is zero or both are extremely close to it. The relative error is less meaningful here
        return diff < FP_EPSILON
      }
      else {
        // Use relative error
        return diff / (dd1.abs + dd2.abs) < FP_EPSILON
      }
    }

    def ~!=(d2: Double): Boolean = !(d1 ~== d2)

    def ~<(d2: Double): Boolean = (d1 - d2) < FP_EPSILON && !(d1 ~== d2)

    def ~<=(d2: Double): Boolean = (d1 - d2) < FP_EPSILON || (d1 ~== d2)

    def ~>=(d2: Double): Boolean = (d1 - d2) > FP_EPSILON || (d1 ~== d2)

    def ~>(d2: Double): Boolean = (d1 - d2) > FP_EPSILON && !(d1 ~== d2)
  }

}
