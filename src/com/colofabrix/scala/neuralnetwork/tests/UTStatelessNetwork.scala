package com.colofabrix.scala.neuralnetwork.tests

import com.colofabrix.scala.math.Matrix
import com.colofabrix.scala.neuralnetwork.GenericStatelessNetwork
import com.colofabrix.scala.neuralnetwork.abstracts._
import org.scalatest.{Matchers, WordSpec}

import scala.util.Random

/**
 * Unit testing for Stateless Neural Networks
 *
 * Created by Fabrizio on 06/05/2015.
 */
class UTStatelessNetwork extends WordSpec with Matchers {
  import scala.Double._

  // Range of test values
  private val inputs_range: List[Double] = (-2.0 to (2.0, 0.1)).toList ::: List.fill(20)(Random.nextDouble * 10 - 5)
  private val activation = ActivationFunction("tanh")
  private val tolerance = 1E-05

  private def executeTest( inputs: Seq[Double], expectedOutputs: Seq[Double], matrix: Matrix[Double] ): Unit = {
    val testNetwork = new GenericStatelessNetwork(inputs.length, expectedOutputs.length, matrix, activation)
    val outputs = testNetwork.output(inputs)

    // Check the length of the output
    outputs.length should equal(expectedOutputs.length)

    // Check the values
    outputs.zip(expectedOutputs).foreach {
      case (output, expected) ⇒ output should equal(expected +- tolerance)
    }
  }

  /*
  "Acyclicity performance test" in {
    // Warm up
    NeuralNetwork.isAcyclic( new Matrix[Double](Seq.fill(200, 200)(0.0)) )

    for( i ← (10.to(1001, 10)) ) {
      // Huge matrix using the worst-case with all zeroes
      val matrixToCheck1 = new Matrix[Double](Seq.fill(i, i)(Double.NaN))
      val matrixToCheck2 = new Matrix[Double](Seq.fill(i, i)(1.0))

      val start1 = System.nanoTime()
      NeuralNetwork.isAcyclic(matrixToCheck2) == true
      val end1 = System.nanoTime()

      val start2 = System.nanoTime()
      NeuralNetwork.analiseNetwork(Seq(), matrixToCheck1)._2 == matrixToCheck2.map((_, _, _) ⇒ Double.NaN)
      val end2 = System.nanoTime()

      println(s"$i\t${((end1 - start1) / 1000000)}\t${((end2 - start2) / 1000000)}")
    }
  }
  */

  "temp" in {

    // Matrix with forward edges
    val matrix1 = new Matrix[Double](Seq(
      Seq(NaN, NaN, 1.2, -1.0),
      Seq(NaN, NaN, -0.5, 0.5),
      Seq(NaN, NaN, NaN, NaN),
      Seq(NaN, NaN, NaN, NaN)
    ))

    // Matrix with forward+back edges
    val matrix2 = new Matrix[Double](Seq(
      Seq(NaN, NaN, 1.2, -1.0),
      Seq(NaN, NaN, -0.5, 0.5),
      Seq(NaN, NaN, NaN, NaN),
      Seq(3.2, NaN, NaN, NaN)
    ))

    // Matrix with forward+cross edges
    val matrix3 = new Matrix[Double](Seq(
      Seq(NaN, NaN, 1.2, -1.0),
      Seq(NaN, NaN, -0.5, 0.5),
      Seq(NaN, NaN, NaN, 1.0),
      Seq(NaN, NaN, NaN, NaN)
    ))

    // Matrix with forward+back+cross edges
    val matrix4 = new Matrix[Double](Seq(
      Seq(NaN, NaN, 1.2, -1.0),
      Seq(NaN, NaN, -0.5, 0.5),
      Seq(NaN, NaN, NaN, 1.0),
      Seq(3.2, NaN, NaN, 0.0)
    ))

    println("Matrix with forward edges")
    println(NeuralNetwork.analiseNetwork(matrix1))

    println("\nMatrix with forward+back edges")
    println(NeuralNetwork.analiseNetwork(matrix2))

    println("\nMatrix with forward+cross edges")
    println(NeuralNetwork.analiseNetwork(matrix3))

    println("\nMatrix with forward+back+cross edges")
    println(NeuralNetwork.analiseNetwork(matrix4))
  }

  "Constraints" must {

    "on creation must interrupt" when {

      "the number of inputs is invalid" in {

        intercept[IllegalArgumentException] {
          val matrix = new Matrix[Double]( Seq(
            Seq(NaN, 1.0),
            Seq(NaN, NaN),
            Seq(0.0, 0.0)
          ))
          new GenericStatelessNetwork(0, 1, matrix, activation)
        }

      }

      "the number of outputs is invalid" in {

        intercept[IllegalArgumentException] {
          val matrix = new Matrix[Double]( Seq(
            Seq(NaN, 1.0),
            Seq(NaN, NaN),
            Seq(0.0, 0.0)
          ))
          new GenericStatelessNetwork(1, 0, matrix, activation)
        }

      }

      "the adjacency matrix doesn't respect the minimum size" in {

        intercept[IllegalArgumentException] {
          val matrix = new Matrix[Double]( Seq(
            Seq(NaN, 1.0),
            Seq(NaN, NaN),
            Seq(0.0, 0.0)
          ))
          new GenericStatelessNetwork(2, 2, matrix, activation)
        }

      }

      "the biases are not numeric" in {

        intercept[IllegalArgumentException] {
          val matrix = new Matrix[Double](Seq(
            Seq(NaN, 1.0),
            Seq(NaN, NaN),
            Seq(NaN, NaN)
          ))
          new GenericStatelessNetwork(1, 1, matrix, activation)
        }

      }

      "the matrix is not for a stateless network" in {

        val matrix1 = new Matrix[Double](Seq(
          Seq(NaN, NaN, 1.0, -1.0),
          Seq(NaN, NaN, -0.5, 0.5),
          Seq(NaN, NaN, NaN, NaN),
          Seq(NaN, NaN, NaN, NaN),
          Seq(0.0, 0.0, 0.0, 0.0)
        ))

        new GenericStatelessNetwork(2, 2, matrix1, activation)

        val matrix2 = new Matrix[Double](Seq(
          Seq(NaN, NaN, 1.0, -1.0),
          Seq(NaN, NaN, -0.5, 0.5),
          Seq(NaN, NaN, NaN, 1.0),
          Seq(NaN, 1.0, NaN, NaN),
          Seq(0.0, 0.0, 0.0, 0.0)
        ))

        intercept[IllegalArgumentException] {
          new GenericStatelessNetwork(2, 2, matrix2, activation)
        }

        // Adjacency matrix
        val matrix3 = new Matrix[Double](Seq(
          Seq(NaN, NaN, 1.0, -1.0),
          Seq(NaN, NaN, -0.5, 0.5),
          Seq(NaN, NaN, 1.0, NaN),
          Seq(NaN, NaN, NaN, NaN),
          Seq(0.0, 0.0, 0.0, 0.0)
        ))

        intercept[IllegalArgumentException] {
          new GenericStatelessNetwork(2, 2, matrix3, activation)
        }
      }

    }

    "on evaluation must interrupt" when {

      "the input vector doesn't match the input count" in {
        val matrix = new Matrix[Double](Seq(
          Seq(NaN, NaN, 1.0,  -1.0),
          Seq(NaN, NaN, -0.5, 0.5),
          Seq(NaN, NaN, NaN,  NaN),
          Seq(NaN, NaN, NaN,  NaN),
          Seq(0.0, 0.0, 0.0,  0.0)
        ))

        val network = new GenericStatelessNetwork(2, 2, matrix, activation)

        intercept[IllegalArgumentException] {
          network.output( Seq(1.0, 2.0, 3.0) )
        }
      }

      "one or more inputs are not numeric" in {
        val matrix = new Matrix[Double](Seq(
          Seq(NaN, NaN, 1.0,  -1.0),
          Seq(NaN, NaN, -0.5, 0.5),
          Seq(NaN, NaN, NaN,  NaN),
          Seq(NaN, NaN, NaN,  NaN),
          Seq(0.0, 0.0, 0.0,  0.0)
        ))

        val network = new GenericStatelessNetwork(2, 2, matrix, activation)

        intercept[IllegalArgumentException] {
          network.output( Seq(1.0, NaN) )
        }
      }

    }

  }

  "Outputs" when {

    "there is only one input and one output, no hidden neurons" in {
      // Adjacency matrix
      val matrix = new Matrix[Double]( Seq(
        //   1    2     //     Neuron #
        Seq(NaN, 1.0),  // 1 - Input neuron 1
        Seq(NaN, NaN),  // 2 - Output neuron 1
        Seq(0.0, 0.0)   // 3 - Biases
        //   1    2     //     Neuron #
      ))

      val inputs = Seq(2.0)
      val expected = Seq(activation(2.0 * 1.0))  // Seq(.9640275800758169)

      executeTest( inputs, expected, matrix)
    }

    "there are multiple inputs and multiple outputs, no hidden neurons" in {
      // Adjacency matrix
      val matrix = new Matrix[Double]( Seq(
        //   1    2    3    4      //     Neuron #
        Seq(NaN, NaN, 1.0, -1.0),  // 1 - Input neuron 1
        Seq(NaN, NaN, -0.5, 0.5),  // 2 - Input neuron 2
        Seq(NaN, NaN, NaN, NaN ),  // 3 - Output neuron 1
        Seq(NaN, NaN, NaN, NaN ),  // 4 - Output neuron 2
        Seq(0.0, 0.0, 0.0, 0.0 )   // 5 - Biases
        //   1    2    3    4      //     Neuron #
      ) )

      val inputs = Seq(0.5, -0.5)
      val expected = Seq(activation(0.5 * 1.0 + (-0.5) * (-0.5)), activation(0.5 * (-1.0) + (-0.5) * 0.5))  // Seq(0.6351489523872873, -0.6351489523872873

      executeTest( inputs, expected, matrix)
    }

    "there is an hidden layer" in {
      // Adjacency matrix
      val matrix = new Matrix[Double]( Seq(
        //   1     2     3     4     5     6     7      //     Neuron #
        Seq(NaN,  NaN,  0.1,  -0.1, 0.2,  NaN,  NaN ),  // 1 - Input neuron 1
        Seq(NaN,  NaN,  -0.2, 0.3,  -0.3, NaN,  NaN ),  // 2 - Input neuron 2
        Seq(NaN,  NaN,  NaN,  NaN,  NaN,  0.4,  -0.4),  // 3 - Hidden neuron 1
        Seq(NaN,  NaN,  NaN,  NaN,  NaN,  0.5,  -0.5),  // 4 - Hidden neuron 2
        Seq(NaN,  NaN,  NaN,  NaN,  NaN,  0.6,  -0.6),  // 5 - Hidden neuron 3
        Seq(NaN,  NaN,  NaN,  NaN,  NaN,  NaN,  NaN ),  // 6 - Output neuron 1
        Seq(NaN,  NaN,  NaN,  NaN,  NaN,  NaN,  NaN ),  // 7 - Output neuron 1
        Seq(0.0,  0.0,  0.0,  0.0,  0.0,  0.0,  0.0 )   // 8 - Biases
        //   1     2     3     4     5     6     7      //     Neuron #
      ))
    }

    "the biases affect correctly a multi-neuron network" in {
      // Adjacency matrix
      val matrix = new Matrix[Double]( Seq(
        //   1     2     3     4     5     6     7      //     Neuron #
        Seq(NaN,  NaN,  0.1,  -0.1, 0.2,  NaN,  NaN ),  // 1 - Input neuron 1
        Seq(NaN,  NaN,  -0.2, 0.3,  -0.3, NaN,  NaN ),  // 2 - Input neuron 2
        Seq(NaN,  NaN,  NaN,  NaN,  NaN,  0.4,  -0.4),  // 3 - Hidden neuron 1
        Seq(NaN,  NaN,  NaN,  NaN,  NaN,  0.5,  -0.5),  // 4 - Hidden neuron 2
        Seq(NaN,  NaN,  NaN,  NaN,  NaN,  0.6,  -0.6),  // 5 - Hidden neuron 3
        Seq(NaN,  NaN,  NaN,  NaN,  NaN,  NaN,  NaN ),  // 6 - Output neuron 1
        Seq(NaN,  NaN,  NaN,  NaN,  NaN,  NaN,  NaN ),  // 7 - Output neuron 1
        Seq(0.1,  -0.1, 0.2,  -0.2, 0.3,  -0.3, 0.4 )   // 8 - Biases
        //   1     2     3     4     5     6     7      //     Neuron #
      ))

      val inputs = Seq(2.0, 3.0)
      val expected = Seq(-0.26019424076983344, 0.350765812191313)

      executeTest( inputs, expected, matrix)
    }

    "there are interactions between neurons of the same layer" in {
      // Adjacency matrix
      val matrix = new Matrix[Double]( Seq(
        //   1     2     3     4     5     6     7      //     Neuron #
        Seq(NaN,  1.0,  0.1,  -0.1, 0.2,  NaN,  NaN ),  // 1 - Input neuron 1
        Seq(NaN,  NaN,  -0.2, 0.3,  -0.3, NaN,  NaN ),  // 2 - Input neuron 2
        Seq(NaN,  NaN,  NaN,  NaN,  NaN,  0.4,  -0.4),  // 3 - Hidden neuron 1
        Seq(NaN,  NaN,  NaN,  NaN,  NaN,  0.5,  -0.5),  // 4 - Hidden neuron 2
        Seq(NaN,  NaN,  NaN,  NaN,  NaN,  0.6,  -0.6),  // 5 - Hidden neuron 3
        Seq(NaN,  NaN,  NaN,  NaN,  NaN,  NaN,  NaN ),  // 6 - Output neuron 1
        Seq(NaN,  NaN,  NaN,  NaN,  NaN,  NaN,  NaN ),  // 7 - Output neuron 1
        Seq(0.1,  -0.1, 0.2,  -0.2, 0.3,  -0.3, 0.4 )   // 8 - Biases
        //   1     2     3     4     5     6     7      //     Neuron #
      ))

      val inputs = Seq(2.0, 3.0)
      val expected = Seq(-0.2404913857132186, 0.3321968561804623)

      executeTest(inputs, expected, matrix)
    }

  }

}
