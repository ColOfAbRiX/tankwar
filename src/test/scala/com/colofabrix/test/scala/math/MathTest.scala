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

package com.colofabrix.test.scala.math

import org.scalatest.{ FlatSpec, Matchers }

/**
  *
  */
class MathTest extends FlatSpec with Matchers {

  import com.colofabrix.scala.math.DoubleWithAlmostEquals

  "~== method" must "return true if and only if Doubles are equals" in {
    val a = 0.0
    val b = 0.0
    val c = Double.MinPositiveValue
    val d = Math.PI
    val e = Math.PI - Double.MinPositiveValue
    val f = Double.NaN
    val g = Double.NegativeInfinity
    val h = Double.PositiveInfinity


    (a ~== a) should equal(true)
    (c ~== c) should equal(true)
    (d ~== d) should equal(true)

    (a ~== b) should equal(true)
    (b ~== a) should equal(true)

    (b ~== c) should equal(true)
    (c ~== b) should equal(true)

    (a ~== c) should equal(true)
    (c ~== a) should equal(true)


    (d ~== e) should equal(true)
    (e ~== d) should equal(true)


    (a ~== d) should equal(false)
    (d ~== a) should equal(false)

    (f ~== f) should equal(false)
    (g ~== g) should equal(true)
    (f ~== g) should equal(false)
    (g ~== f) should equal(false)
    (g ~== h) should equal(false)
    (h ~== g) should equal(false)

    (f ~== d) should equal(false)
    (d ~== f) should equal(false)

    (g ~== d) should equal(false)
    (d ~== g) should equal(false)
  }

  "~> method" must "return true if and only if Double1 > Double2" in {
    val a = 0.0
    val b = 0.0
    val c = Double.MinPositiveValue
    val d = Math.PI

    (a ~> b) should equal(false)
    (b ~> a) should equal(false)

    (a ~> c) should equal(false)
    (c ~> a) should equal(false)

    (d ~> a) should equal(true)
    (a ~> d) should equal(false)
  }

  "~>= method" must "return true if and only if Double1 >= Double2" in {
    val a = 0.0
    val b = 0.0
    val c = -Double.MinPositiveValue
    val d = Math.PI

    (a ~>= b) should equal(true)
    (b ~>= a) should equal(true)

    (a ~>= c) should equal(true)
    (c ~>= a) should equal(true)

    (d ~>= a) should equal(true)
    (a ~>= d) should equal(false)
  }

  "~<= method" must "return true if and only if Double1 <= Double2" in {
    val a = 0.0
    val b = 0.0
    val c = -Double.MinPositiveValue
    val d = Math.PI

    (a ~<= b) should equal(true)
    (b ~<= a) should equal(true)

    (a ~<= c) should equal(true)
    (c ~<= a) should equal(true)

    (d ~<= a) should equal(false)
    (a ~<= d) should equal(true)
  }

  "~< method" must "return true if and only if Double1 , Double2" in {
    val a = 0.0
    val b = 0.0
    val c = -Double.MinPositiveValue
    val d = Math.PI

    (a ~< b) should equal(false)
    (b ~< a) should equal(false)

    (a ~< c) should equal(false)
    (c ~< a) should equal(false)

    (d ~< a) should equal(false)
    (a ~< d) should equal(true)
  }

}
