package com.colofabrix.scala.neuralnetwork.abstracts

import com.colofabrix.scala.math.Matrix

/**
 * Abstract implementation of a Stateless Neural Network
 *
 * A stateless neural network is a network that satisfies all of these equivalent statements:
 *  - Has no memory of the past
 *  - There is no feedback between neurons
 *  - The graph is acyclic
 *  - Only the feedforward area of the adjacency matrix can be a numeric value
 *
 * @param inputCount Number of inputs of the Neural Network
 * @param outputCount Number of inputs of the Neural Network
 * @param matrix Defining adjacency matrix
 * @param af Activation function used by the network
 */
abstract class AbstractStatelessNetwork(
  override val inputCount: Int,
  override val outputCount: Int,
  override val matrix: Matrix[Double],
  override val af: ActivationFunction )
extends NeuralNetwork {

  //
  // INITIALIZATION CONSTRAINTS
  //
  require( inputCount > 0, "The number of allowed input must be a positive integer")
  require( outputCount > 0, "The number of allowed input must be a positive integer")
  require( matrix.rows >= inputCount + outputCount + 1, "The adjacency matrix must be define for at least the inputs and the outputs plus biases" )
  require( matrix.cols >= inputCount + outputCount, "The size of weights must match the input/output numbers" )
  require( matrix.row(matrix.rows - 1).forall(!_.isNaN), "The biases must always be numeric" )
  require( this.isStateless, "The adjacency matrix must represent a non-recurrent neural network (the graph must be acyclic)" )

  /**
   * Calculate the output of the Neural Network
   *
   * Given a set of input values it calculates the set of output values
   *
   * @param inputs A sequence of T to feed the NN
   * @return A sequence of T representing the output
   */
  override def output( inputs: Seq[Double] ): Seq[Double] = {
    require( inputs.length == inputCount, "The number of supplied inputs must match the number of inputs defined in the network")
    require( inputs.forall(!_.isNaN), "The inputs must be numeric values" )

    // This vector matches the size of the adjacency matrix and it's useful to generalize the algorithm. The last value, 1.0, is for the biases
    val output = solveNetwork(inputs ++ Seq.fill(matrix.rows - inputCount - 1)(Double.NaN) ++ Seq(1.0))

    // Returns only the output of the last outputCount neurons
    output.takeRight(outputCount + 1).take(outputCount).toList
  }

  /**
   * Calculate the output of the Neural Network
   *
   * Given a set of input values it calculates the set of output values using a recursive algorithm
   * This code is for internal use only as its inputs/outputs don't match the contract with the client nor their meaning
   *
   * @param inputs A sequence of Double that represents the inputs of the adjacency matrix. The length must match the number of neurons plus one
   * @return A sequence of Double representing all the outputs of the matrix for the given input
   */
  protected def solveNetwork( inputs: Seq[Double] ): Seq[Double] = {
    // Outputs of each neurons. By default it's NaN, as a neuron might not have an output value yet
    val outputs = Array.fill(matrix.rows - 1)(Double.NaN)

    // Calculate the sum of the weighted inputs using the output vector
    for( i ← (0 until matrix.rows).par; j ← (0 until matrix.cols).par ) {
      // The inputs are multiplied by the weight of the neuron they feed
      val weightedInput = inputs(i) * matrix(i, j)

      // I have to deal with the NaN values: I process the weightedInput only if it's not NaN
      if (!weightedInput.isNaN) {
        // The output matrix too contains NaN values, that I have to revert if I have to make calculations.
        // Except with the bias weights, as they are always not NaN and would always been added, also for invalid outputs
        if (outputs(j).isNaN && i < matrix.rows - 1) outputs(j) = 0.0
        // Sum the weightedInputs here, to save a for-loop later. The AF is applied later, if needed
        outputs(j) += weightedInput
      }
    }

    // If there's nothing more to process, return the result
    if( outputs.forall(_.isNaN) )
      return inputs

    // Recursive tail call to process the next step of the algorithm
    solveNetwork(
      // I apply the Activation Function only before the recursive call. It's cleaner and more efficient.
      // Don't forget the 1.0 value for the biases
      outputs.map( af(_) ) ++ Seq(1.0)
    )
  }
}