package com.colofabrix.scala.neuralnetwork.tests

import com.colofabrix.scala.math.Matrix
import com.colofabrix.scala.neuralnetwork.GenericStatelessNetwork
import com.colofabrix.scala.neuralnetwork.abstracts._
import org.scalatest.{Matchers, WordSpec}

import scala.util.Random

/**
 * Unit testing for the class {AbstractStatelessNetwork}
 *
 * Created by Fabrizio on 06/05/2015.
 */
class UTGenericStatelessNetwork extends WordSpec with Matchers {
  import scala.Double._

  // Range of test values
  private val inputs_range: List[Double] = (-2.0 to (2.0, 0.2)).toList ::: List.fill(10)(Random.nextDouble * 10 - 5)
  private val activation = ActivationFunction("tanh")

  /**
   * This function tests all the inputs of a NN
   *
   * It runs the NN and checks against a given function that the results are correct
   *
   * @param nI The number of inputs
   * @param nO The number of outputs
   * @param matrix The matrix that defines the NN
   * @param expectedOutputs A function that manually calculate the output of {matrix}
   */
  private def executeTest( nI: Int, nO: Int, matrix: Matrix[Double], expectedOutputs: Seq[Double] ⇒ Seq[Double] ): Unit = {

    val testNetwork = new GenericStatelessNetwork(nI, nO, matrix, activation)

    def innerExecute( inputsBase: Seq[Double], index: Int ) {
      inputs_range.foreach { x ⇒

        // Modify the value of the current input
        val inputs = inputsBase.patch(index, Seq(x), 1)

        if (index == inputsBase.length - 1) {
          // If there is only one value to check, then check it
          val outputs = testNetwork.output(inputs)

          // Check length
          outputs.length == testNetwork.outputCount

          // Check the values
          outputs should equal( expectedOutputs(inputs) )
        }
        else
        // If there is more than one value to plot, recursively call this function over the remaining indexes { {
          innerExecute(inputs, index + 1)
      }
    }

    innerExecute( Seq.fill(testNetwork.inputCount)(inputs_range(0)), 0 )
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

        // This is valid
        val matrix1 = new Matrix[Double](Seq(
          Seq(NaN, NaN, 1.0, -1.0),
          Seq(NaN, NaN, -0.5, 0.5),
          Seq(NaN, NaN, NaN, NaN),
          Seq(NaN, NaN, NaN, NaN),
          Seq(0.0, 0.0, 0.0, 0.0)
        ))

        new GenericStatelessNetwork(2, 2, matrix1, activation)

        // This has loops
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

        // This has self-loops
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

      def expected(inputs: Seq[Double]): Seq[Double] =
        Seq(activation(inputs(0) * 1.0))

      executeTest( 1, 1, matrix, expected )
    }

    "there are multiple inputs and multiple outputs, no hidden neurons" in {
      // Adjacency matrix
      val matrix = new Matrix[Double]( Seq(
        //   1    2    3    4      //     Neuron #
        Seq(NaN, NaN, 0.1, -0.2),  // 1 - Input neuron 1
        Seq(NaN, NaN, 0.3, -0.4),  // 2 - Input neuron 2
        Seq(NaN, NaN, NaN, NaN ),  // 3 - Output neuron 1
        Seq(NaN, NaN, NaN, NaN ),  // 4 - Output neuron 2
        Seq(0.0, 0.0, 0.0, 0.0 )   // 5 - Biases
        //   1    2    3    4      //     Neuron #
      ) )

      def expected(inputs: Seq[Double]): Seq[Double] =
        Seq(activation(inputs(0) * 0.1 + inputs(1) * 0.3), activation(inputs(0) * (-0.2) + inputs(1) * (-0.4)))

      executeTest( 2, 2, matrix, expected )
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

      // Calculates manually the outputs of the network
      def expected(inputs: Seq[Double]): Seq[Double] = {
        val inLayerOutputs = Seq(
          activation(inputs(0) * 0.1 + inputs(1) * (-0.2)),
          activation(inputs(0) * (-0.1) + inputs(1) * 0.3),
          activation(inputs(0) * 0.2 + inputs(1) * (-0.3))
        )
        val hdLayerOutputs = Seq(
          activation(inLayerOutputs(0) * 0.4 + inLayerOutputs(1) * 0.5 + inLayerOutputs(2) * 0.6),
          activation(inLayerOutputs(0) * (-0.4) + inLayerOutputs(1) * (-0.5) + inLayerOutputs(2) * (-0.6))
        )
        Seq(hdLayerOutputs(0), hdLayerOutputs(1))
      }

      executeTest( 2, 2, matrix, expected )
    }

    "the biases affect correctly a multi-neuron network" in {
      // Calculates manually the outputs of the network
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

      // Calculates manually the outputs of the network
      def expected(inputs: Seq[Double]): Seq[Double] = {
        val inLayerOutputs = Seq(
          activation(inputs(0) * 0.1 + inputs(1) * (-0.2) + 0.2),
          activation(inputs(0) * (-0.1) + inputs(1) * 0.3 - 0.2),
          activation(inputs(0) * 0.2 + inputs(1) * (-0.3) + 0.3)
        )
        val hdLayerOutputs = Seq(
          activation(inLayerOutputs(0) * 0.4 + inLayerOutputs(1) * 0.5 + inLayerOutputs(2) * 0.6 - 0.3),
          activation(inLayerOutputs(0) * (-0.4) + inLayerOutputs(1) * (-0.5) + inLayerOutputs(2) * (-0.6) + 0.4)
        )
        Seq(hdLayerOutputs(0), hdLayerOutputs(1))
      }

      executeTest( 2, 2, matrix, expected )
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

      // Calculates manually the outputs of the network
      def expected(inputs: Seq[Double]): Seq[Double] = {
        val inLayerOutputs = Seq(
          activation(inputs(0) * 0.1 + inputs(1) * (-0.2) + 0.2),
          activation(inputs(0) * 1.0 + inputs(0) * (-0.1) + inputs(1) * 0.3 - 0.2),
          activation(inputs(0) * 0.2 + inputs(1) * (-0.3) + 0.3)
        )
        val hdLayerOutputs = Seq(
          activation(inLayerOutputs(0) * 0.4 + inLayerOutputs(1) * 0.5 + inLayerOutputs(2) * 0.6 - 0.3),
          activation(inLayerOutputs(0) * (-0.4) + inLayerOutputs(1) * (-0.5) + inLayerOutputs(2) * (-0.6) + 0.4)
        )
        Seq(hdLayerOutputs(0), hdLayerOutputs(1))
      }

      // FIXME: This test it's not possible, as there are multiple ways through the network due to the cross-edge. See TKWAR-19
      //executeTest( 2, 2, matrix, expected )
    }

  }
}
