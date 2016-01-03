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

package com.colofabrix.test.scala.neuralnetwork

import com.colofabrix.scala.math.Matrix
import com.colofabrix.scala.neuralnetwork.NetworkMatrix
import org.scalatest.{ Matchers, WordSpec }

import scala.Double._
import scala.collection.mutable

/**
 * Unit Testing for `NetworkMatrix`
 */
class NetworkMatrixTest extends WordSpec with Matchers {

  "Constructor" must {

    val reference = new NetworkMatrix(
      Seq(
        Seq( NaN, NaN, 1.0, -1.0 ),
        Seq( NaN, NaN, -0.5, 0.5 ),
        Seq( NaN, NaN, NaN, NaN ),
        Seq( NaN, NaN, NaN, NaN ),
        Seq( 0.0, NaN, NaN, 0.0 )
      ), Seq( 0 ), Seq( 1 )
    )

    "From matrix" in {
      val matrix = new Matrix[Double](
        Seq(
          Seq( NaN, NaN, 1.0, -1.0 ),
          Seq( NaN, NaN, -0.5, 0.5 ),
          Seq( NaN, NaN, NaN, NaN ),
          Seq( NaN, NaN, NaN, NaN ),
          Seq( 0.0, NaN, NaN, 0.0 )
        )
      )
      val test = new NetworkMatrix( matrix, Seq( 0 ), Seq( 1 ) )

      test should equal( reference )
    }

    "From mutable" in {
      val test = new NetworkMatrix(
        mutable.Buffer(
          mutable.Buffer( NaN, NaN, 1.0, -1.0 ),
          mutable.Buffer( NaN, NaN, -0.5, 0.5 ),
          mutable.Buffer( NaN, NaN, NaN, NaN ),
          mutable.Buffer( NaN, NaN, NaN, NaN ),
          Seq( 0.0, NaN, NaN, 0.0 )
        ), Seq( 0 ), Seq( 1 )
      )

      test should equal( reference )
    }

  }

  "Constraints" must {

    "Matrix is empty" in {

      intercept[IllegalArgumentException] {
        new NetworkMatrix( Seq( Seq() ), Seq(), Seq() )
      }
      intercept[IllegalArgumentException] {
        new NetworkMatrix( Seq( Seq() ), Seq( 0 ), Seq() )
      }
      intercept[IllegalArgumentException] {
        new NetworkMatrix( Seq( Seq() ), Seq(), Seq( 1 ) )
      }
      intercept[IllegalArgumentException] {
        new NetworkMatrix( Seq( Seq() ), Seq( 0 ), Seq( 1 ) )
      }

    }

    "On input and output roots formats" when {

      "No roots, neither inputs nor outputs" in {

        intercept[IllegalArgumentException] {
          new NetworkMatrix(
            Seq(
              Seq( NaN, NaN, 1.0, -1.0 ),
              Seq( NaN, NaN, -0.5, 0.5 ),
              Seq( NaN, NaN, NaN, NaN ),
              Seq( NaN, NaN, NaN, NaN ),
              Seq( NaN, NaN, NaN, NaN )
            ), Seq(), Seq()
          )
        }

      }

      "Too many outputs" in {

        intercept[IllegalArgumentException] {
          new NetworkMatrix(
            Seq(
              Seq( NaN, NaN, 1.0, -1.0 ),
              Seq( NaN, NaN, -0.5, 0.5 ),
              Seq( NaN, NaN, NaN, NaN ),
              Seq( NaN, NaN, NaN, NaN ),
              Seq( 0.0, 0.0, 0.0, 0.0 )
            ), Seq( 0, 1 ), Seq( 2, 3, 4 )
          )
        }

      }

      "Too many inputs" in {

        intercept[IllegalArgumentException] {
          new NetworkMatrix(
            Seq(
              Seq( NaN, NaN, 1.0, -1.0 ),
              Seq( NaN, NaN, -0.5, 0.5 ),
              Seq( NaN, NaN, NaN, NaN ),
              Seq( NaN, NaN, NaN, NaN ),
              Seq( 0.0, 0.0, 0.0, 0.0 )
            ), Seq( 0, 1, 2 ), Seq( 3, 4 )
          )
        }

      }

    }

    "On input and output roots semantic" when {

      "Non existent input" in {

        intercept[IllegalArgumentException] {
          new NetworkMatrix(
            Seq(
              Seq( NaN, NaN, 1.0, -1.0 ),
              Seq( NaN, NaN, -0.5, 0.5 ),
              Seq( NaN, NaN, NaN, NaN ),
              Seq( NaN, NaN, NaN, NaN ),
              Seq( 0.0, 0.0, 0.0, 0.0 )
            ), Seq( 0, 5 ), Seq( 2, 3 )
          )
        }

      }

      "Non existent output" in {

        intercept[IllegalArgumentException] {
          new NetworkMatrix(
            Seq(
              Seq( NaN, NaN, 1.0, -1.0 ),
              Seq( NaN, NaN, -0.5, 0.5 ),
              Seq( NaN, NaN, NaN, NaN ),
              Seq( NaN, NaN, NaN, NaN ),
              Seq( 0.0, 0.0, 0.0, 0.0 )
            ), Seq( 0, 1 ), Seq( 2, 5 )
          )
        }

      }

      "Duplicate input roots" in {

        intercept[IllegalArgumentException] {
          new NetworkMatrix(
            Seq(
              Seq( NaN, NaN, 1.0, -1.0 ),
              Seq( NaN, NaN, -0.5, 0.5 ),
              Seq( NaN, NaN, NaN, NaN ),
              Seq( NaN, NaN, NaN, NaN ),
              Seq( 0.0, 0.0, 0.0, 0.0 )
            ), Seq( 0, 0 ), Seq( 2, 3 )
          )
        }

      }

      "Duplicate output roots" in {

        intercept[IllegalArgumentException] {
          new NetworkMatrix(
            Seq(
              Seq( NaN, NaN, 1.0, -1.0 ),
              Seq( NaN, NaN, -0.5, 0.5 ),
              Seq( NaN, NaN, NaN, NaN ),
              Seq( NaN, NaN, NaN, NaN ),
              Seq( 0.0, 0.0, 0.0, 0.0 )
            ), Seq( 0, 1 ), Seq( 2, 2 )
          )
        }

      }

    }

    "matrix is not square-plus-one" when {

      "Not enough rows" in {

        intercept[IllegalArgumentException] {
          new NetworkMatrix(
            Seq(
              Seq( NaN, NaN, 1.0, -1.0 ),
              Seq( NaN, NaN, -0.5, 0.5 ),
              Seq( NaN, NaN, NaN, NaN ),
              Seq( NaN, NaN, NaN, NaN )
            ), Seq( 0, 1 ), Seq( 2, 3 )
          )
        }

      }

      "Too many rows" in {

        intercept[IllegalArgumentException] {
          new NetworkMatrix(
            Seq(
              Seq( NaN, NaN, 1.0, -1.0 ),
              Seq( NaN, NaN, -0.5, 0.5 ),
              Seq( NaN, NaN, NaN, NaN ),
              Seq( NaN, NaN, NaN, NaN ),
              Seq( NaN, NaN, NaN, NaN ),
              Seq( NaN, NaN, NaN, NaN )
            ), Seq( 0, 1 ), Seq( 2, 3 )
          )
        }

      }

    }

  }

  "Check graph properties" when {

    "The graph is forward only" in {
      val matrix = new NetworkMatrix(
        Seq(
          Seq( NaN, NaN, 1.0, -1.0 ),
          Seq( NaN, NaN, -0.5, 0.5 ),
          Seq( NaN, NaN, NaN, NaN ),
          Seq( NaN, NaN, NaN, NaN ),
          Seq( NaN, NaN, NaN, NaN )
        ), Seq( 0, 1 ), Seq( 2, 3 )
      )

      matrix.isForwardOnly should equal( true )
      matrix.isAcyclic should equal( true )
    }

    "The graph is acyclic" in {
      val matrix = new NetworkMatrix(
        Seq(
          Seq( NaN, NaN, 1.0, -1.0 ),
          Seq( NaN, NaN, -0.5, 0.5 ),
          Seq( NaN, NaN, NaN, 0.3 ),
          Seq( NaN, NaN, NaN, NaN ),
          Seq( NaN, NaN, NaN, NaN )
        ), Seq( 0, 1 ), Seq( 2, 3 )
      )

      matrix.isForwardOnly should equal( false )
      matrix.isAcyclic should equal( true )
    }

    "The graph is generic" in {
      val matrix = new NetworkMatrix(
        Seq(
          Seq( NaN, NaN, 1.0, -1.0 ),
          Seq( NaN, NaN, -0.5, 0.5 ),
          Seq( NaN, -0.2, NaN, 0.3 ),
          Seq( NaN, NaN, NaN, NaN ),
          Seq( NaN, NaN, NaN, NaN )
        ), Seq( 0, 1 ), Seq( 2, 3 )
      )

      matrix.isForwardOnly should equal( false )
      matrix.isAcyclic should equal( false )
    }
  }

  "Other operators" must {

    "ensure equality" when {

      val test = new NetworkMatrix(
        Seq(
          Seq( NaN, NaN, 1.0, -1.0 ),
          Seq( NaN, NaN, -0.5, 0.5 ),
          Seq( NaN, NaN, NaN, NaN ),
          Seq( NaN, NaN, NaN, NaN ),
          Seq( 0.0, NaN, NaN, 0.0 )
        ), Seq( 0, 1 ), Seq( 2, 3 )
      )

      val isEqual = new NetworkMatrix(
        Seq(
          Seq( NaN, NaN, 1.0, -1.0 ),
          Seq( NaN, NaN, -0.5, 0.5 ),
          Seq( NaN, NaN, NaN, NaN ),
          Seq( NaN, NaN, NaN, NaN ),
          Seq( 0.0, NaN, NaN, 0.0 )
        ), Seq( 0, 1 ), Seq( 2, 3 )
      )

      val isNotEqual1 = new NetworkMatrix(
        Seq(
          Seq( NaN, NaN, 1.0, -1.0 ),
          Seq( NaN, 0.2, NaN, 0.5 ),
          Seq( 1.3, NaN, NaN, NaN ),
          Seq( NaN, NaN, NaN, NaN ),
          Seq( 0.0, 0.0, NaN, NaN )
        ), Seq( 0, 1 ), Seq( 2, 3 )
      )

      val isNotEqual2 = new NetworkMatrix(
        Seq(
          Seq( NaN, NaN, 1.0, NaN ),
          Seq( NaN, NaN, -0.5, 0.5 ),
          Seq( NaN, -0.5, NaN, NaN ),
          Seq( NaN, NaN, NaN, NaN ),
          Seq( NaN, 0.0, 0.0, 0.0 )
        ), Seq( 0, 1 ), Seq( 1, 2 )
      )

      "compared with another NetworkMatrix" in {
        ( test equals isEqual ) should equal( true )
        ( test equals isNotEqual1 ) should equal( false )
        ( test equals isNotEqual2 ) should equal( false )
      }

      "compared with an adjacency matrix" in {
        ( test equals isEqual.adjacencyOnly ) should equal( true )
        ( test equals isNotEqual1.adjacencyOnly ) should equal( false )
        ( test equals isNotEqual2.adjacencyOnly ) should equal( false )
      }

      "compared with a biases row" in {
        ( test equals isEqual.biases ) should equal( true )
        ( test equals isNotEqual1.biases ) should equal( false )
        ( test equals isNotEqual2.biases ) should equal( false )
      }

    }

    "toNaN" when {

      val reference = new NetworkMatrix(
        Seq(
          Seq( NaN, NaN, NaN, NaN ),
          Seq( NaN, NaN, NaN, NaN ),
          Seq( NaN, NaN, NaN, NaN ),
          Seq( NaN, NaN, NaN, NaN ),
          Seq( NaN, NaN, NaN, NaN )
        ), Seq( 0 ), Seq( 1 )
      ).toNaN

      "Comparing a non-NaN matrix" in {

        val testNotNaN = new NetworkMatrix(
          Seq(
            Seq( NaN, NaN, 1.0, -1.0 ),
            Seq( NaN, NaN, -0.5, 0.5 ),
            Seq( NaN, NaN, NaN, NaN ),
            Seq( NaN, NaN, NaN, NaN ),
            Seq( 1.0, NaN, NaN, NaN )
          ), Seq( 0 ), Seq( 1 )
        )

        ( testNotNaN equals reference ) should equal( false )

      }

      "Comparing a NaN matrix" in {

        val testNaN = new NetworkMatrix(
          Seq(
            Seq( NaN, NaN, NaN, NaN ),
            Seq( NaN, NaN, NaN, NaN ),
            Seq( NaN, NaN, NaN, NaN ),
            Seq( NaN, NaN, NaN, NaN ),
            Seq( NaN, NaN, NaN, NaN )
          ), Seq( 0 ), Seq( 1 )
        )

        ( testNaN equals reference ) should equal( true )
      }
    }

    "isAllNaN" must {

      val testNotNaN = new NetworkMatrix(
        Seq(
          Seq( NaN, NaN, 1.0, -1.0 ),
          Seq( NaN, NaN, -0.5, 0.5 ),
          Seq( NaN, NaN, NaN, NaN ),
          Seq( NaN, NaN, NaN, NaN ),
          Seq( NaN, NaN, NaN, NaN )
        ), Seq( 0 ), Seq( 1 )
      )

      val testNaN = new NetworkMatrix(
        Seq(
          Seq( NaN, NaN, NaN, NaN ),
          Seq( NaN, NaN, NaN, NaN ),
          Seq( NaN, NaN, NaN, NaN ),
          Seq( NaN, NaN, NaN, NaN ),
          Seq( NaN, NaN, NaN, NaN )
        ), Seq( 0 ), Seq( 1 )
      )

      testNotNaN.isAllNaN should equal( false )
      testNaN.isAllNaN should equal( true )
    }

  }

}