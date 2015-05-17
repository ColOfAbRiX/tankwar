package com.colofabrix.scala.neuralnetwork.tests

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

  "Constraints" must {
  }

  "Outputs" when {
  }

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

        val result = NeuralNetwork.analiseNetwork(matrix, Seq(0))
        intercept[TestFailedException] {
          checkTuples(result, (matrix, matrix.toNaN, matrix.toNaN))
        }
      }

    }

    "Detect forward edges" when {

      "there is one input" in {
        val matrix = new Matrix[Double](Seq(
          Seq(NaN, 0.1, -0.2),
          Seq(NaN, NaN, NaN),
          Seq(NaN, NaN, NaN)
        ))

        val result = NeuralNetwork.analiseNetwork(matrix, Seq(0))
        checkTuples( result, (matrix, matrix.toNaN, matrix.toNaN) )
      }

      "there are two inputs" in {
        val matrix = new Matrix[Double](Seq(
          Seq(NaN, NaN, 1.0, -1.0),
          Seq(NaN, NaN, -0.5, 0.5),
          Seq(NaN, NaN, NaN, NaN),
          Seq(NaN, NaN, NaN, NaN)
        ))

        val result = NeuralNetwork.analiseNetwork(matrix, Seq(0, 1))
        checkTuples( result, (matrix, matrix.toNaN, matrix.toNaN) )
      }
    }


    "Detect forward+back edges" in {
      val matrix = new Matrix[Double](Seq(
        Seq(NaN, NaN, 1.0, -1.0),
        Seq(NaN, NaN, -0.5, 0.5),
        Seq(NaN, 0.3, NaN, NaN),
        Seq(1.4, NaN, NaN, NaN)
      ))

      val expectedForward = new Matrix[Double](Seq(
        Seq(NaN, NaN, 1.0, -1.0),
        Seq(NaN, NaN, -0.5, 0.5),
        Seq(NaN, NaN, NaN, NaN),
        Seq(NaN, NaN, NaN, NaN)
      ))

      val expectedBack = new Matrix[Double](Seq(
        Seq(NaN, NaN, NaN, NaN),
        Seq(NaN, NaN, NaN, NaN),
        Seq(NaN, 0.3, NaN, NaN),
        Seq(1.4, NaN, NaN, NaN)
      ))

      val result = NeuralNetwork.analiseNetwork(matrix, Seq(0, 1))
      checkTuples( result, (expectedForward, expectedBack, matrix.toNaN) )
    }

    "Detect forward+cross edges" in {
      val matrix = new Matrix[Double](Seq(
        Seq(NaN, NaN, 1.0, -1.0),
        Seq(NaN, NaN, -0.5, 0.5),
        Seq(NaN, 0.3, NaN, NaN),
        Seq(1.4, NaN, NaN, NaN)
      ))

      val expectedForward = new Matrix[Double](Seq(
        Seq(NaN, NaN, 1.0, -1.0),
        Seq(0.1, NaN, -0.5, 0.5),
        Seq(NaN, NaN, NaN, NaN),
        Seq(NaN, NaN, 0.3, NaN)
      ))

      val expectedCross = new Matrix[Double](Seq(
        Seq(NaN, NaN, NaN, NaN),
        Seq(0.1, NaN, NaN, NaN),
        Seq(NaN, NaN, NaN, NaN),
        Seq(NaN, NaN, 0.3, NaN)
      ))

      val result = NeuralNetwork.analiseNetwork(matrix, Seq(0, 1))
      checkTuples( result, (expectedForward, matrix.toNaN, expectedCross) )
    }

    "Detect forward+back+cross edges" in {
      val matrix = new Matrix[Double](Seq(
        Seq(NaN, NaN, 1.0, -1.0),
        Seq(NaN, NaN, -0.5, 0.5),
        Seq(NaN, 0.3, NaN, NaN),
        Seq(1.4, NaN, NaN, NaN)
      ))

      val expectedForward = new Matrix[Double](Seq(
        Seq(NaN, NaN, 1.0, -1.0),
        Seq(0.1, 0.2, -0.5, 0.5),
        Seq(NaN, NaN, NaN, NaN),
        Seq(NaN, NaN, 0.3, NaN)
      ))

      val expectedBack = new Matrix[Double](Seq(
        Seq(NaN, NaN, NaN, NaN),
        Seq(NaN, 0.2, NaN, NaN),
        Seq(NaN, NaN, NaN, NaN),
        Seq(NaN, NaN, NaN, NaN)
      ))

      val expectedCross = new Matrix[Double](Seq(
        Seq(NaN, NaN, NaN, NaN),
        Seq(0.1, NaN, NaN, NaN),
        Seq(NaN, NaN, NaN, NaN),
        Seq(NaN, NaN, 0.3, NaN)
      ))

      val result = NeuralNetwork.analiseNetwork(matrix, Seq(0, 1))
      checkTuples( result, (expectedForward, expectedBack, expectedCross) )
    }
  }
}
