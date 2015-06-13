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
import com.colofabrix.scala.neuralnetwork.abstracts._
import org.scalatest.{Matchers, WordSpec}

import scala.Double._

/**
 * Unit testing for the trait {NeuralNetwork}
 */
class NeuralNetworkTest extends WordSpec with Matchers {

  "Network analysis" must {

    def getMockMatrix( matrix: Matrix[Double] ): NetworkMatrix = {
      new NetworkMatrix(
        new Matrix[Double](matrix.rowSet :+ Seq.fill(matrix.cols)(Double.NaN)),
        Seq(0), Seq(1)
      )
    }

    def checkTuples( result: (Matrix[Double], Matrix[Double], Matrix[Double]), expected: (Matrix[Double], Matrix[Double], Matrix[Double]) ): Unit = {
      getMockMatrix(result._1) should equal(expected._1)
      getMockMatrix(result._2) should equal(expected._2)
      getMockMatrix(result._3) should equal(expected._3)
    }

    "Detect forward edges" when {

      val matrix = new NetworkMatrix(
        Seq(
          Seq(NaN, NaN, 1.0, -1.0),
          Seq(NaN, NaN, -0.5, 0.5),
          Seq(NaN, NaN, NaN, NaN),
          Seq(NaN, NaN, NaN, NaN),
          Seq(NaN, NaN, NaN, NaN)
        ), Seq(0, 1), Seq(2, 3)
      )

      "Using INPUT-0" in {

        val expected = new Matrix[Double](
          Seq(
            Seq(NaN, NaN, 1.0, -1.0),
            Seq(NaN, NaN, NaN, NaN),
            Seq(NaN, NaN, NaN, NaN),
            Seq(NaN, NaN, NaN, NaN)
          )
        )

        val result = NeuralNetwork.analiseNetwork(matrix, 0)
        checkTuples(result, (expected, NetworkMatrix.toNaN(expected), NetworkMatrix.toNaN(expected)))

      }

      "Using INPUT-1" in {

        val expected = new Matrix[Double](
          Seq(
            Seq(NaN, NaN, NaN, NaN),
            Seq(NaN, NaN, -0.5, 0.5),
            Seq(NaN, NaN, NaN, NaN),
            Seq(NaN, NaN, NaN, NaN)
          )
        )

        val result = NeuralNetwork.analiseNetwork(matrix, 1)
        checkTuples(result, (expected, NetworkMatrix.toNaN(expected), NetworkMatrix.toNaN(expected)))

      }

    }

    "Detect forward+back edges" when {

      val matrix = new NetworkMatrix(
        Seq(
          Seq(0.2, NaN, 1.0, -1.0),
          Seq(NaN, 1.0, -0.5, 0.5),
          Seq(NaN, NaN, NaN, NaN),
          Seq(NaN, NaN, NaN, NaN),
          Seq(0.0, 0.0, 0.0, 0.0)
        ), Seq(0, 1), Seq(2, 3)
      )

      "Using INPUT-0" in {

        val expectedForward = new Matrix[Double](
          Seq(
            Seq(NaN, NaN, 1.0, -1.0),
            Seq(NaN, NaN, NaN, NaN),
            Seq(NaN, NaN, NaN, NaN),
            Seq(NaN, NaN, NaN, NaN)
          )
        )

        val expectedBack = new Matrix[Double](
          Seq(
            Seq(0.2, NaN, NaN, NaN),
            Seq(NaN, NaN, NaN, NaN),
            Seq(NaN, NaN, NaN, NaN),
            Seq(NaN, NaN, NaN, NaN)
          )
        )

        val result = NeuralNetwork.analiseNetwork(matrix, 0)
        checkTuples(result, (expectedForward, expectedBack, NetworkMatrix.toNaN(expectedForward)))

      }

      "Using INPUT-1" in {

        val expectedForward = new Matrix[Double](
          Seq(
            Seq(NaN, NaN, NaN, NaN),
            Seq(NaN, NaN, -0.5, 0.5),
            Seq(NaN, NaN, NaN, NaN),
            Seq(NaN, NaN, NaN, NaN)
          )
        )

        val expectedBack = new Matrix[Double](
          Seq(
            Seq(NaN, NaN, NaN, NaN),
            Seq(NaN, 1.0, NaN, NaN),
            Seq(NaN, NaN, NaN, NaN),
            Seq(NaN, NaN, NaN, NaN)
          )
        )

        val result = NeuralNetwork.analiseNetwork(matrix, 1)
        checkTuples(result, (expectedForward, expectedBack, NetworkMatrix.toNaN(expectedForward)))

      }

    }

    "Detect forward+cross edges" when {

      val matrix = new NetworkMatrix(
        Seq(
          Seq(NaN, NaN, 1.0, -1.0),
          Seq(NaN, NaN, -0.5, 0.5),
          Seq(0.3, NaN, NaN, NaN),
          Seq(1.4, NaN, NaN, NaN),
          Seq(0.0, 0.0, 0.0, 0.0)
        ), Seq(0, 1), Seq(2, 3)
      )

      "Using INPUT-0" in {

        // No cross-edges expected for INPUT-0

        val expectedForward = new Matrix[Double](
          Seq(
            Seq(NaN, NaN, 1.0, -1.0),
            Seq(NaN, NaN, NaN, NaN),
            Seq(NaN, NaN, NaN, NaN),
            Seq(NaN, NaN, NaN, NaN)
          )
        )

        val expectedBack = new Matrix[Double](
          Seq(
            Seq(NaN, NaN, NaN, NaN),
            Seq(NaN, NaN, NaN, NaN),
            Seq(0.3, NaN, NaN, NaN),
            Seq(1.4, NaN, NaN, NaN)
          )
        )

        val result = NeuralNetwork.analiseNetwork(matrix, 0)
        checkTuples(result, (expectedForward, expectedBack, NetworkMatrix.toNaN(expectedForward)))

      }

      "Using INPUT-1" in {

        val expectedForward = new Matrix[Double](
          Seq(
            Seq(NaN, NaN, NaN, NaN),
            Seq(NaN, NaN, -0.5, 0.5),
            Seq(NaN, NaN, NaN, NaN),
            Seq(1.4, NaN, NaN, NaN)
          )
        )

        val expectedBack = new Matrix[Double](
          Seq(
            Seq(NaN, NaN, NaN, -1.0),
            Seq(NaN, NaN, NaN, NaN),
            Seq(0.3, NaN, NaN, NaN),
            Seq(NaN, NaN, NaN, NaN)
          )
        )

        val expectedCross = new Matrix[Double](
          Seq(
            Seq(NaN, NaN, 1.0, NaN),
            Seq(NaN, NaN, NaN, NaN),
            Seq(NaN, NaN, NaN, NaN),
            Seq(NaN, NaN, NaN, NaN)
          )
        )

        val result = NeuralNetwork.analiseNetwork(matrix, 1)
        checkTuples(result, (expectedForward, expectedBack, expectedCross))

      }

    }

    "Detect forward+back+cross edges" when {

      val matrix = new NetworkMatrix(
        Seq(
          Seq(NaN, NaN, 1.0, -1.0),
          Seq(NaN, NaN, -0.5, 0.5),
          Seq(NaN, 0.3, NaN, NaN),
          Seq(1.4, NaN, NaN, NaN),
          Seq(0.0, 0.0, 0.0, 0.0)
        ), Seq(0, 1), Seq(2, 3)
      )

      "Using INPUT-0" in {

        val expectedForward = new Matrix[Double](
          Seq(
            Seq(NaN, NaN, 1.0, -1.0),
            Seq(NaN, NaN, NaN, NaN),
            Seq(NaN, 0.3, NaN, NaN),
            Seq(NaN, NaN, NaN, NaN)
          )
        )

        val expectedBack = new Matrix[Double](
          Seq(
            Seq(NaN, NaN, NaN, NaN),
            Seq(NaN, NaN, -0.5, NaN),
            Seq(NaN, NaN, NaN, NaN),
            Seq(1.4, NaN, NaN, NaN)
          )
        )

        val expectedCross = new Matrix[Double](
          Seq(
            Seq(NaN, NaN, NaN, NaN),
            Seq(NaN, NaN, NaN, 0.5),
            Seq(NaN, NaN, NaN, NaN),
            Seq(NaN, NaN, NaN, NaN)
          )
        )

        val result = NeuralNetwork.analiseNetwork(matrix, 0)
        checkTuples(result, (expectedForward, expectedBack, expectedCross))

      }

      "Using INPUT-1" in {

        val expectedForward = new Matrix[Double](
          Seq(
            Seq(NaN, NaN, NaN, NaN),
            Seq(NaN, NaN, -0.5, 0.5),
            Seq(NaN, NaN, NaN, NaN),
            Seq(1.4, NaN, NaN, NaN)
          )
        )

        val expectedBack = new Matrix[Double](
          Seq(
            Seq(NaN, NaN, NaN, -1.0),
            Seq(NaN, NaN, NaN, NaN),
            Seq(NaN, 0.3, NaN, NaN),
            Seq(NaN, NaN, NaN, NaN)
          )
        )

        val expectedCross = new Matrix[Double](
          Seq(
            Seq(NaN, NaN, 1.0, NaN),
            Seq(NaN, NaN, NaN, NaN),
            Seq(NaN, NaN, NaN, NaN),
            Seq(NaN, NaN, NaN, NaN)
          )
        )

        val result = NeuralNetwork.analiseNetwork(matrix, 1)
        checkTuples(result, (expectedForward, expectedBack, expectedCross))

      }
    }

    "Pass a complex test" when {

      val matrix: NetworkMatrix = new NetworkMatrix(
        Seq(
          Seq(1.0, NaN, 1.0, 1.0, 1.0, NaN, NaN, NaN),
          Seq(NaN, NaN, NaN, 1.0, 1.0, NaN, NaN, NaN),
          Seq(NaN, NaN, NaN, NaN, NaN, 1.0, 1.0, NaN),
          Seq(1.0, 1.0, NaN, NaN, NaN, 1.0, 1.0, NaN),
          Seq(NaN, 1.0, NaN, NaN, NaN, NaN, NaN, 1.0),
          Seq(1.0, NaN, NaN, 1.0, NaN, NaN, NaN, NaN),
          Seq(NaN, NaN, NaN, NaN, NaN, NaN, NaN, NaN),
          Seq(NaN, NaN, 1.0, 1.0, NaN, NaN, NaN, NaN),
          Seq(0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0)
        ), Seq(0, 1), Seq(6, 7)
      )

      "Using INPUT-0" in {

        val expectedForward = new Matrix[Double](
          Seq(
            Seq(NaN, NaN, 1.0, 1.0, 1.0, NaN, NaN, NaN),
            Seq(NaN, NaN, NaN, NaN, NaN, NaN, NaN, NaN),
            Seq(NaN, NaN, NaN, NaN, NaN, NaN, NaN, NaN),
            Seq(NaN, NaN, NaN, NaN, NaN, 1.0, 1.0, NaN),
            Seq(NaN, 1.0, NaN, NaN, NaN, NaN, NaN, 1.0),
            Seq(NaN, NaN, NaN, NaN, NaN, NaN, NaN, NaN),
            Seq(NaN, NaN, NaN, NaN, NaN, NaN, NaN, NaN),
            Seq(NaN, NaN, NaN, NaN, NaN, NaN, NaN, NaN)
          )
        )

        val expectedBack = new Matrix[Double](
          Seq(
            Seq(1.0, NaN, NaN, NaN, NaN, NaN, NaN, NaN),
            Seq(NaN, NaN, NaN, NaN, 1.0, NaN, NaN, NaN),
            Seq(NaN, NaN, NaN, NaN, NaN, NaN, NaN, NaN),
            Seq(1.0, 1.0, NaN, NaN, NaN, NaN, NaN, NaN),
            Seq(NaN, NaN, NaN, NaN, NaN, NaN, NaN, NaN),
            Seq(NaN, NaN, NaN, 1.0, NaN, NaN, NaN, NaN),
            Seq(NaN, NaN, NaN, NaN, NaN, NaN, NaN, NaN),
            Seq(NaN, NaN, NaN, NaN, NaN, NaN, NaN, NaN)
          )
        )

        val expectedCross = new Matrix[Double](
          Seq(
            Seq(NaN, NaN, NaN, NaN, NaN, NaN, NaN, NaN),
            Seq(NaN, NaN, NaN, 1.0, NaN, NaN, NaN, NaN),
            Seq(NaN, NaN, NaN, NaN, NaN, 1.0, 1.0, NaN),
            Seq(NaN, NaN, NaN, NaN, NaN, NaN, NaN, NaN),
            Seq(NaN, NaN, NaN, NaN, NaN, NaN, NaN, NaN),
            Seq(1.0, NaN, NaN, NaN, NaN, NaN, NaN, NaN),
            Seq(NaN, NaN, NaN, NaN, NaN, NaN, NaN, NaN),
            Seq(NaN, NaN, 1.0, 1.0, NaN, NaN, NaN, NaN)
          )
        )

        val result = NeuralNetwork.analiseNetwork(matrix, 0)
        checkTuples(result, (expectedForward, expectedBack, expectedCross))

      }

      "Using INPUT-1" in { }

    }
  }
}
