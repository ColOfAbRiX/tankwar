package com.colofabrix.test.scala.neuralnetwork

import com.colofabrix.scala.math.{Matrix, NetworkMatrix}
import com.colofabrix.scala.neuralnetwork.abstracts._
import org.scalatest.exceptions.TestFailedException
import org.scalatest.{Matchers, WordSpec}

import scala.Double._

/**
 * Unit testing for the trait {NeuralNetwork}
 *
 * Created by Fabrizio on 06/05/2015.
 */
class NeuralNetworkTest extends WordSpec with Matchers {

  "Network analysis" must {

    def checkTuples(result: (Matrix[Double], Matrix[Double], Matrix[Double]), expected: (NetworkMatrix, NetworkMatrix, NetworkMatrix) ): Unit = {

      result._1.map( x => if (x.isNaN) 0.0 else 1.0 ) should equal(expected._1.map( x => if (x.isNaN) 0.0 else 1.0 ))
      result._2.map( x => if (x.isNaN) 0.0 else 1.0 ) should equal(expected._2.map( x => if (x.isNaN) 0.0 else 1.0 ))
      result._3.map( x => if (x.isNaN) 0.0 else 1.0 ) should equal(expected._3.map( x => if (x.isNaN) 0.0 else 1.0 ))

    }

    "Check constraints" when {

      "not enough roots are provided" in {
        val matrix = new NetworkMatrix(Seq(
          Seq(NaN, NaN, 1.0, -1.0),
          Seq(NaN, NaN, -0.5, 0.5),
          Seq(NaN, NaN, NaN, NaN),
          Seq(NaN, NaN, NaN, NaN),
          Seq(0.0, 0.0, 0.0, 0.0)
        ), Seq(0, 1), Seq(2, 3))

        val result = NeuralNetwork.analiseNetwork(matrix, 0)
        intercept[TestFailedException] {
          checkTuples(result, (matrix, matrix.toNaN, matrix.toNaN))
        }
      }

      "matrix is empty" in {
        intercept[IllegalArgumentException] {
          new NetworkMatrix(Seq(Seq()), Seq(), Seq())
        }
      }

      "matrix is not square-plus-one" in {
        intercept[IllegalArgumentException] {
          new NetworkMatrix(Seq(
            Seq(NaN, NaN, 1.0, -1.0),
            Seq(NaN, NaN, -0.5, 0.5),
            Seq(NaN, NaN, NaN, NaN),
            Seq(NaN, NaN, NaN, NaN)
          ), Seq(0, 1), Seq(2, 3))
        }

        intercept[IllegalArgumentException] {
          new NetworkMatrix(Seq(
            Seq(NaN, NaN, 1.0, -1.0),
            Seq(NaN, NaN, -0.5, 0.5),
            Seq(NaN, NaN, NaN, NaN),
            Seq(NaN, NaN, NaN, NaN),
            Seq(NaN, NaN, NaN, NaN),
            Seq(NaN, NaN, NaN, NaN)
          ), Seq(0, 1), Seq(2, 3))
        }
      }

    }

    "Detect forward edges" in {
      val matrix = new NetworkMatrix(Seq(
        Seq(NaN, NaN, 1.0, -1.0),
        Seq(NaN, NaN, -0.5, 0.5),
        Seq(NaN, NaN, NaN, NaN),
        Seq(NaN, NaN, NaN, NaN),
        Seq(0.0, 0.0, 0.0, 0.0)
      ), Seq(0, 1), Seq(2, 3))

      // Inputs must be checked one at the time...
      val result1 = NeuralNetwork.analiseNetwork(matrix, 0)
      checkTuples( result1, (matrix, matrix.toNaN, matrix.toNaN) )

      val result2 = NeuralNetwork.analiseNetwork(matrix, 1)
      checkTuples( result2, (matrix, matrix.toNaN, matrix.toNaN) )
    }

    "Detect forward+back edges" in {
      val matrix = new NetworkMatrix(Seq(
        Seq(0.2, NaN, 1.0, -1.0),
        Seq(NaN, 1.0, -0.5, 0.5),
        Seq(NaN, NaN, NaN, NaN),
        Seq(NaN, NaN, NaN, NaN),
        Seq(0.0, 0.0, 0.0, 0.0)
      ), Seq(0, 1), Seq(2, 3))

      //
      // FOR INPUT-0
      //
      val expectedForward0 = new NetworkMatrix(Seq(
        Seq(NaN, NaN, 1.0, -1.0),
        Seq(NaN, NaN, NaN, NaN),
        Seq(NaN, NaN, NaN, NaN),
        Seq(NaN, NaN, NaN, NaN),
        Seq(0.0, 0.0, 0.0, 0.0)
      ), Seq(0, 1), Seq(2, 3))

      val expectedBack0 = new NetworkMatrix(Seq(
        Seq(0.2, NaN, NaN, NaN),
        Seq(NaN, NaN, NaN, NaN),
        Seq(NaN, NaN, NaN, NaN),
        Seq(NaN, NaN, NaN, NaN),
        Seq(0.0, 0.0, 0.0, 0.0)
      ), Seq(0, 1), Seq(2, 3))

      val result0 = NeuralNetwork.analiseNetwork(matrix, 0)
      checkTuples( result0, (expectedForward0, expectedBack0, matrix.toNaN) )

      //
      // FOR INPUT-1 (the network results in one forward edge for this input)
      //
      val expectedForward1 = new NetworkMatrix(Seq(
        Seq(NaN, NaN, NaN, NaN),
        Seq(NaN, NaN, -0.5, 0.5),
        Seq(NaN, NaN, NaN, NaN),
        Seq(NaN, NaN, NaN, NaN),
        Seq(0.0, 0.0, 0.0, 0.0)
      ), Seq(0, 1), Seq(2, 3))

      val expectedBack1 = new NetworkMatrix(Seq(
        Seq(NaN, NaN, NaN, NaN),
        Seq(NaN, 1.0, NaN, NaN),
        Seq(NaN, NaN, NaN, NaN),
        Seq(NaN, NaN, NaN, NaN),
        Seq(0.0, 0.0, 0.0, 0.0)
      ), Seq(0, 1), Seq(2, 3))

      val result1 = NeuralNetwork.analiseNetwork(matrix, 1)
      checkTuples( result1, (expectedForward1, expectedBack1, matrix.toNaN) )
    }

    "Detect forward+cross edges" in {
      val matrix = new NetworkMatrix(Seq(
        Seq(NaN, NaN, 1.0, -1.0),
        Seq(NaN, NaN, -0.5, 0.5),
        Seq(0.3, NaN, NaN, NaN),
        Seq(1.4, NaN, NaN, NaN),
        Seq(0.0, 0.0, 0.0, 0.0)
      ), Seq(0, 1), Seq(2, 3))

      //
      // FOR INPUT-0 (no cross edges from this root)
      //
      val expectedForward0 = new NetworkMatrix(Seq(
        Seq(NaN, NaN, 1.0, -1.0),
        Seq(NaN, NaN, NaN, NaN),
        Seq(NaN, NaN, NaN, NaN),
        Seq(NaN, NaN, NaN, NaN),
        Seq(0.0, 0.0, 0.0, 0.0)
      ), Seq(0, 1), Seq(2, 3))

      val expectedBack0 = new NetworkMatrix(Seq(
        Seq(NaN, NaN, NaN, NaN),
        Seq(NaN, NaN, NaN, NaN),
        Seq(0.3, NaN, NaN, NaN),
        Seq(1.4, NaN, NaN, NaN),
        Seq(0.0, 0.0, 0.0, 0.0)
      ), Seq(0, 1), Seq(2, 3))

      val result0 = NeuralNetwork.analiseNetwork(matrix, 0)
      checkTuples( result0, (expectedForward0, expectedBack0, matrix.toNaN) )

      //
      // FOR INPUT-1 (the network results in one forward edge for this input)
      //
      val expectedForward1 = new NetworkMatrix(Seq(
        Seq(NaN, NaN, NaN, NaN),
        Seq(NaN, NaN, -0.5, 0.5),
        Seq(NaN, NaN, NaN, NaN),
        Seq(1.4, NaN, NaN, NaN),
        Seq(0.0, 0.0, 0.0, 0.0)
      ), Seq(0, 1), Seq(2, 3))

      val expectedBack1 = new NetworkMatrix(Seq(
        Seq(NaN, NaN, NaN, -1.0),
        Seq(NaN, NaN, NaN, NaN),
        Seq(0.3, NaN, NaN, NaN),
        Seq(NaN, NaN, NaN, NaN),
        Seq(0.0, 0.0, 0.0, 0.0)
      ), Seq(0, 1), Seq(2, 3))

      val expectedCross1 = new NetworkMatrix(Seq(
        Seq(NaN, NaN, 1.0, NaN),
        Seq(NaN, NaN, NaN, NaN),
        Seq(NaN, NaN, NaN, NaN),
        Seq(NaN, NaN, NaN, NaN),
        Seq(0.0, 0.0, 0.0, 0.0)
      ), Seq(0, 1), Seq(2, 3))

      val result1 = NeuralNetwork.analiseNetwork(matrix, 1)
      checkTuples( result1, (expectedForward1, expectedBack1, expectedCross1) )
    }

    "Detect forward+back+cross edges" in {
      val matrix = new NetworkMatrix(Seq(
        Seq(NaN, NaN, 1.0, -1.0),
        Seq(NaN, NaN, -0.5, 0.5),
        Seq(NaN, 0.3, NaN, NaN),
        Seq(1.4, NaN, NaN, NaN),
        Seq(0.0, 0.0, 0.0, 0.0)
      ), Seq(0, 1), Seq(2, 3))

      //
      // INPUT-0
      //
      val expectedForward0 = new NetworkMatrix(Seq(
        Seq(NaN, NaN, 1.0, -1.0),
        Seq(NaN, NaN, NaN, NaN),
        Seq(NaN, 0.3, NaN, NaN),
        Seq(NaN, NaN, NaN, NaN),
        Seq(0.0, 0.0, 0.0, 0.0)
      ), Seq(0, 1), Seq(2, 3))

      val expectedBack0 = new NetworkMatrix(Seq(
        Seq(NaN, NaN, NaN, NaN),
        Seq(NaN, NaN, -0.5, NaN),
        Seq(NaN, NaN, NaN, NaN),
        Seq(1.4, NaN, NaN, NaN),
        Seq(0.0, 0.0, 0.0, 0.0)
      ), Seq(0, 1), Seq(2, 3))

      val expectedCross0 = new NetworkMatrix(Seq(
        Seq(NaN, NaN, NaN, NaN),
        Seq(NaN, NaN, NaN, 0.5),
        Seq(NaN, NaN, NaN, NaN),
        Seq(NaN, NaN, NaN, NaN),
        Seq(0.0, 0.0, 0.0, 0.0)
      ), Seq(0, 1), Seq(2, 3))

      val result0 = NeuralNetwork.analiseNetwork(matrix, 0)
      checkTuples( result0, (expectedForward0, expectedBack0, expectedCross0) )

      //
      // INPUT-1
      //
      val expectedForward1 = new NetworkMatrix(Seq(
        Seq(NaN, NaN, NaN, NaN),
        Seq(NaN, NaN, -0.5, 0.5),
        Seq(NaN, NaN, NaN, NaN),
        Seq(1.4, NaN, NaN, NaN),
        Seq(0.0, 0.0, 0.0, 0.0)
      ), Seq(0, 1), Seq(2, 3))

      val expectedBack1 = new NetworkMatrix(Seq(
        Seq(NaN, NaN, NaN, -1.0),
        Seq(NaN, NaN, NaN, NaN),
        Seq(NaN, 0.3, NaN, NaN),
        Seq(NaN, NaN, NaN, NaN),
        Seq(0.0, 0.0, 0.0, 0.0)
      ), Seq(0, 1), Seq(2, 3))

      val expectedCross1 = new NetworkMatrix(Seq(
        Seq(NaN, NaN, 1.0, NaN),
        Seq(NaN, NaN, NaN, NaN),
        Seq(NaN, NaN, NaN, NaN),
        Seq(NaN, NaN, NaN, NaN),
        Seq(0.0, 0.0, 0.0, 0.0)
      ), Seq(0, 1), Seq(2, 3))

      val result1 = NeuralNetwork.analiseNetwork(matrix, 1)
      checkTuples( result1, (expectedForward1, expectedBack1, expectedCross1) )
    }

    "Complex test" in {

      val matrix: NetworkMatrix = new NetworkMatrix(Seq(
        Seq(1.0, NaN, 1.0, 1.0, 1.0, NaN, NaN, NaN),
        Seq(NaN, NaN, NaN, 1.0, 1.0, NaN, NaN, NaN),
        Seq(NaN, NaN, NaN, NaN, NaN, 1.0, 1.0, NaN),
        Seq(1.0, 1.0, NaN, NaN, NaN, 1.0, 1.0, NaN),
        Seq(NaN, 1.0, NaN, NaN, NaN, NaN, NaN, 1.0),
        Seq(1.0, NaN, NaN, 1.0, NaN, NaN, NaN, NaN),
        Seq(NaN, NaN, NaN, NaN, NaN, NaN, NaN, NaN),
        Seq(NaN, NaN, 1.0, 1.0, NaN, NaN, NaN, NaN),
        Seq(0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0)
      ), Seq(0, 1), Seq(6, 7))

      //
      // INPUT-0
      //
      val expectedForward0 = new NetworkMatrix(Seq(
        Seq(NaN, NaN, 1.0, 1.0, 1.0, NaN, NaN, NaN),
        Seq(NaN, NaN, NaN, NaN, NaN, NaN, NaN, NaN),
        Seq(NaN, NaN, NaN, NaN, NaN, NaN, NaN, NaN),
        Seq(NaN, NaN, NaN, NaN, NaN, 1.0, 1.0, NaN),
        Seq(NaN, 1.0, NaN, NaN, NaN, NaN, NaN, 1.0),
        Seq(NaN, NaN, NaN, NaN, NaN, NaN, NaN, NaN),
        Seq(NaN, NaN, NaN, NaN, NaN, NaN, NaN, NaN),
        Seq(NaN, NaN, NaN, NaN, NaN, NaN, NaN, NaN),
        Seq(0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0)
      ), Seq(0, 1), Seq(6, 7))

      val expectedBack0 = new NetworkMatrix(Seq(
        Seq(1.0, NaN, NaN, NaN, NaN, NaN, NaN, NaN),
        Seq(NaN, NaN, NaN, NaN, 1.0, NaN, NaN, NaN),
        Seq(NaN, NaN, NaN, NaN, NaN, NaN, NaN, NaN),
        Seq(1.0, 1.0, NaN, NaN, NaN, NaN, NaN, NaN),
        Seq(NaN, NaN, NaN, NaN, NaN, NaN, NaN, NaN),
        Seq(NaN, NaN, NaN, 1.0, NaN, NaN, NaN, NaN),
        Seq(NaN, NaN, NaN, NaN, NaN, NaN, NaN, NaN),
        Seq(NaN, NaN, NaN, NaN, NaN, NaN, NaN, NaN),
        Seq(0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0)
      ), Seq(0, 1), Seq(6, 7))

      val expectedCross0 = new NetworkMatrix(Seq(
        Seq(NaN, NaN, NaN, NaN, NaN, NaN, NaN, NaN),
        Seq(NaN, NaN, NaN, 1.0, NaN, NaN, NaN, NaN),
        Seq(NaN, NaN, NaN, NaN, NaN, 1.0, 1.0, NaN),
        Seq(NaN, NaN, NaN, NaN, NaN, NaN, NaN, NaN),
        Seq(NaN, NaN, NaN, NaN, NaN, NaN, NaN, NaN),
        Seq(1.0, NaN, NaN, NaN, NaN, NaN, NaN, NaN),
        Seq(NaN, NaN, NaN, NaN, NaN, NaN, NaN, NaN),
        Seq(NaN, NaN, 1.0, 1.0, NaN, NaN, NaN, NaN),
        Seq(0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0)
      ), Seq(0, 1), Seq(6, 7))

      val result0 = NeuralNetwork.analiseNetwork(matrix, 0)
      checkTuples( result0, (expectedForward0, expectedBack0, expectedCross0) )

      //
      // INPUT-1
      //
      // ...

    }
  }
}
