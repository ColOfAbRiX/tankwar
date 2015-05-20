package com.colofabrix.scala.neuralnetwork.tests

import com.colofabrix.scala.math
import com.colofabrix.scala.math.Matrix
import com.colofabrix.scala.neuralnetwork.abstracts._
import org.scalatest.exceptions.TestFailedException
import org.scalatest.{Matchers, WordSpec}

import scala.Double._

/**
 * Unit testing for the trait {NeuralNetwork}
 *
 * Created by Fabrizio on 06/05/2015.
 */
class UTNeuralNetwork extends WordSpec with Matchers {

  "Network analysis" must {

    def checkTuples(result: (Matrix[Double], Matrix[Double], Matrix[Double]), expected: (Matrix[Double], Matrix[Double], Matrix[Double]) ): Unit = {

      result._1.map( x ⇒ if (x.isNaN) 0.0 else 1.0 ) should equal(expected._1.map( x ⇒ if (x.isNaN) 0.0 else 1.0 ))
      result._2.map( x ⇒ if (x.isNaN) 0.0 else 1.0 ) should equal(expected._2.map( x ⇒ if (x.isNaN) 0.0 else 1.0 ))
      result._3.map( x ⇒ if (x.isNaN) 0.0 else 1.0 ) should equal(expected._3.map( x ⇒ if (x.isNaN) 0.0 else 1.0 ))

    }

    "Check constraints" when {

      "not enough roots are provided" in {
        val matrix = new Matrix[Double](Seq(
          Seq(NaN, NaN, 1.0, -1.0),
          Seq(NaN, NaN, -0.5, 0.5),
          Seq(NaN, NaN, NaN, NaN),
          Seq(NaN, NaN, NaN, NaN)
        ))

        val result = NeuralNetwork.analiseNetwork(matrix, 0)
        intercept[TestFailedException] {
          checkTuples(result, (matrix, matrix.toNaN, matrix.toNaN))
        }
      }

      "matrix is empty" in {
        val matrix = new Matrix[Double](Seq(Seq()))

        val result = NeuralNetwork.analiseNetwork(matrix, 0)
        intercept[TestFailedException] {
          checkTuples(result, (matrix, matrix.toNaN, matrix.toNaN))
        }
      }

      "matrix is not square" in {
        val matrix = new Matrix[Double](Seq(
          Seq(NaN, NaN, 1.0, -1.0),
          Seq(NaN, NaN, -0.5, 0.5),
          Seq(NaN, NaN, NaN, NaN),
          Seq(NaN, NaN, NaN, NaN),
          Seq(NaN, NaN, NaN, NaN)
        ))

        val result = NeuralNetwork.analiseNetwork(matrix, 0)
        intercept[TestFailedException] {
          checkTuples(result, (matrix, matrix.toNaN, matrix.toNaN))
        }
      }

    }

    "Detect forward edges" in {
      val matrix = new Matrix[Double](Seq(
        Seq(NaN, NaN, 1.0, -1.0),
        Seq(NaN, NaN, -0.5, 0.5),
        Seq(NaN, NaN, NaN, NaN),
        Seq(NaN, NaN, NaN, NaN)
      ))

      // Inputs must be checked one at the time...
      val result1 = NeuralNetwork.analiseNetwork(matrix, 0)
      checkTuples( result1, (matrix, matrix.toNaN, matrix.toNaN) )

      val result2 = NeuralNetwork.analiseNetwork(matrix, 1)
      checkTuples( result2, (matrix, matrix.toNaN, matrix.toNaN) )
    }

    "Detect forward+back edges" in {
      val matrix = new Matrix[Double](Seq(
        Seq(0.2, NaN, 1.0, -1.0),
        Seq(NaN, 1.0, -0.5, 0.5),
        Seq(NaN, NaN, NaN, NaN),
        Seq(NaN, NaN, NaN, NaN)
      ))

      //
      // FOR INPUT-0
      //
      val expectedForward0 = new Matrix[Double](Seq(
        Seq(NaN, NaN, 1.0, -1.0),
        Seq(NaN, NaN, NaN, NaN),
        Seq(NaN, NaN, NaN, NaN),
        Seq(NaN, NaN, NaN, NaN)
      ))

      val expectedBack0 = new Matrix[Double](Seq(
        Seq(0.2, NaN, NaN, NaN),
        Seq(NaN, NaN, NaN, NaN),
        Seq(NaN, NaN, NaN, NaN),
        Seq(NaN, NaN, NaN, NaN)
      ))

      val result0 = NeuralNetwork.analiseNetwork(matrix, 0)
      checkTuples( result0, (expectedForward0, expectedBack0, matrix.toNaN) )

      //
      // FOR INPUT-1 (the network results in one forward edge for this input)
      //
      val expectedForward1 = new Matrix[Double](Seq(
        Seq(NaN, NaN, NaN, NaN),
        Seq(NaN, NaN, -0.5, 0.5),
        Seq(NaN, NaN, NaN, NaN),
        Seq(NaN, NaN, NaN, NaN)
      ))

      val expectedBack1 = new Matrix[Double](Seq(
        Seq(NaN, NaN, NaN, NaN),
        Seq(NaN, 1.0, NaN, NaN),
        Seq(NaN, NaN, NaN, NaN),
        Seq(NaN, NaN, NaN, NaN)
      ))

      val result1 = NeuralNetwork.analiseNetwork(matrix, 1)
      checkTuples( result1, (expectedForward1, expectedBack1, matrix.toNaN) )
    }

    "Detect forward+cross edges" in {
      val matrix = new Matrix[Double](Seq(
        Seq(NaN, NaN, 1.0, -1.0),
        Seq(NaN, NaN, -0.5, 0.5),
        Seq(0.3, NaN, NaN, NaN),
        Seq(1.4, NaN, NaN, NaN)
      ))

      //
      // FOR INPUT-0 (no cross edges from this root)
      //
      val expectedForward0 = new Matrix[Double](Seq(
        Seq(NaN, NaN, 1.0, -1.0),
        Seq(NaN, NaN, NaN, NaN),
        Seq(NaN, NaN, NaN, NaN),
        Seq(NaN, NaN, NaN, NaN)
      ))

      val expectedBack0 = new Matrix[Double](Seq(
        Seq(NaN, NaN, NaN, NaN),
        Seq(NaN, NaN, NaN, NaN),
        Seq(0.3, NaN, NaN, NaN),
        Seq(1.4, NaN, NaN, NaN)
      ))

      val result0 = NeuralNetwork.analiseNetwork(matrix, 0)
      checkTuples( result0, (expectedForward0, expectedBack0, matrix.toNaN) )

      //
      // FOR INPUT-1 (the network results in one forward edge for this input)
      //
      val expectedForward1 = new Matrix[Double](Seq(
        Seq(NaN, NaN, NaN, NaN),
        Seq(NaN, NaN, -0.5, 0.5),
        Seq(NaN, NaN, NaN, NaN),
        Seq(1.4, NaN, NaN, NaN)
      ))

      val expectedBack1 = new Matrix[Double](Seq(
        Seq(NaN, NaN, NaN, -1.0),
        Seq(NaN, NaN, NaN, NaN),
        Seq(0.3, NaN, NaN, NaN),
        Seq(NaN, NaN, NaN, NaN)
      ))

      val expectedCross1 = new Matrix[Double](Seq(
        Seq(NaN, NaN, 1.0, NaN),
        Seq(NaN, NaN, NaN, NaN),
        Seq(NaN, NaN, NaN, NaN),
        Seq(NaN, NaN, NaN, NaN)
      ))

      val result1 = NeuralNetwork.analiseNetwork(matrix, 1)
      checkTuples( result1, (expectedForward1, expectedBack1, expectedCross1) )
    }

    "Detect forward+back+cross edges" in {
      val matrix = new Matrix[Double](Seq(
        Seq(NaN, NaN, 1.0, -1.0),
        Seq(NaN, NaN, -0.5, 0.5),
        Seq(NaN, 0.3, NaN, NaN),
        Seq(1.4, NaN, NaN, NaN)
      ))

      //
      // INPUT-0
      //
      val expectedForward0 = new Matrix[Double](Seq(
        Seq(NaN, NaN, 1.0, -1.0),
        Seq(NaN, NaN, NaN, NaN),
        Seq(NaN, 0.3, NaN, NaN),
        Seq(NaN, NaN, NaN, NaN)
      ))

      val expectedBack0 = new Matrix[Double](Seq(
        Seq(NaN, NaN, NaN, NaN),
        Seq(NaN, NaN, -0.5, NaN),
        Seq(NaN, NaN, NaN, NaN),
        Seq(1.4, NaN, NaN, NaN)
      ))

      val expectedCross0 = new Matrix[Double](Seq(
        Seq(NaN, NaN, NaN, NaN),
        Seq(NaN, NaN, NaN, 0.5),
        Seq(NaN, NaN, NaN, NaN),
        Seq(NaN, NaN, NaN, NaN)
      ))

      val result0 = NeuralNetwork.analiseNetwork(matrix, 0)
      checkTuples( result0, (expectedForward0, expectedBack0, expectedCross0) )

      //
      // INPUT-1
      //
      val expectedForward1 = new Matrix[Double](Seq(
        Seq(NaN, NaN, NaN, NaN),
        Seq(NaN, NaN, -0.5, 0.5),
        Seq(NaN, NaN, NaN, NaN),
        Seq(1.4, NaN, NaN, NaN)
      ))

      val expectedBack1 = new Matrix[Double](Seq(
        Seq(NaN, NaN, NaN, -1.0),
        Seq(NaN, NaN, NaN, NaN),
        Seq(NaN, 0.3, NaN, NaN),
        Seq(NaN, NaN, NaN, NaN)
      ))

      val expectedCross1 = new Matrix[Double](Seq(
        Seq(NaN, NaN, 1.0, NaN),
        Seq(NaN, NaN, NaN, NaN),
        Seq(NaN, NaN, NaN, NaN),
        Seq(NaN, NaN, NaN, NaN)
      ))

      val result1 = NeuralNetwork.analiseNetwork(matrix, 1)
      checkTuples( result1, (expectedForward1, expectedBack1, expectedCross1) )
    }

    "Complex test" in {

      val matrix: math.Matrix[Double] = new math.Matrix(Seq(
        Seq(1.0, NaN, 1.0, 1.0, 1.0, NaN, NaN, NaN),
        Seq(NaN, NaN, NaN, 1.0, 1.0, NaN, NaN, NaN),
        Seq(NaN, NaN, NaN, NaN, NaN, 1.0, 1.0, NaN),
        Seq(1.0, 1.0, NaN, NaN, NaN, 1.0, 1.0, NaN),
        Seq(NaN, 1.0, NaN, NaN, NaN, NaN, NaN, 1.0),
        Seq(1.0, NaN, NaN, 1.0, NaN, NaN, NaN, NaN),
        Seq(NaN, NaN, NaN, NaN, NaN, NaN, NaN, NaN),
        Seq(NaN, NaN, 1.0, 1.0, NaN, NaN, NaN, NaN)
      ))

      //
      // INPUT-0
      //
      val expectedForward0 = new Matrix[Double](Seq(
        Seq(NaN, NaN, 1.0, 1.0, 1.0, NaN, NaN, NaN),
        Seq(NaN, NaN, NaN, NaN, NaN, NaN, NaN, NaN),
        Seq(NaN, NaN, NaN, NaN, NaN, NaN, NaN, NaN),
        Seq(NaN, NaN, NaN, NaN, NaN, 1.0, 1.0, NaN),
        Seq(NaN, 1.0, NaN, NaN, NaN, NaN, NaN, 1.0),
        Seq(NaN, NaN, NaN, NaN, NaN, NaN, NaN, NaN),
        Seq(NaN, NaN, NaN, NaN, NaN, NaN, NaN, NaN),
        Seq(NaN, NaN, NaN, NaN, NaN, NaN, NaN, NaN)
      ))

      val expectedBack0 = new Matrix[Double](Seq(
        Seq(1.0, NaN, NaN, NaN, NaN, NaN, NaN, NaN),
        Seq(NaN, NaN, NaN, NaN, 1.0, NaN, NaN, NaN),
        Seq(NaN, NaN, NaN, NaN, NaN, NaN, NaN, NaN),
        Seq(1.0, 1.0, NaN, NaN, NaN, NaN, NaN, NaN),
        Seq(NaN, NaN, NaN, NaN, NaN, NaN, NaN, NaN),
        Seq(NaN, NaN, NaN, 1.0, NaN, NaN, NaN, NaN),
        Seq(NaN, NaN, NaN, NaN, NaN, NaN, NaN, NaN),
        Seq(NaN, NaN, NaN, NaN, NaN, NaN, NaN, NaN)
      ))

      val expectedCross0 = new Matrix[Double](Seq(
        Seq(NaN, NaN, NaN, NaN, NaN, NaN, NaN, NaN),
        Seq(NaN, NaN, NaN, 1.0, NaN, NaN, NaN, NaN),
        Seq(NaN, NaN, NaN, NaN, NaN, 1.0, 1.0, NaN),
        Seq(NaN, NaN, NaN, NaN, NaN, NaN, NaN, NaN),
        Seq(NaN, NaN, NaN, NaN, NaN, NaN, NaN, NaN),
        Seq(1.0, NaN, NaN, NaN, NaN, NaN, NaN, NaN),
        Seq(NaN, NaN, NaN, NaN, NaN, NaN, NaN, NaN),
        Seq(NaN, NaN, 1.0, 1.0, NaN, NaN, NaN, NaN)
      ))

      val result0 = NeuralNetwork.analiseNetwork(matrix, 0)
      checkTuples( result0, (expectedForward0, expectedBack0, expectedCross0) )

      //
      // INPUT-1
      //
      // ...

    }
  }
}
