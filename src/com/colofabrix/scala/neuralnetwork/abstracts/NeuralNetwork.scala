package com.colofabrix.scala.neuralnetwork.abstracts

/**
 * Any Neural Network of any type.
 *
 * A Neural Network has always a set of outputs given a set of inputs
 *
 * Created by Fabrizio on 17/12/2014.
 */
trait NeuralNetwork {

  /**
   * The data type of the Neural Network
   */
  type T <: AnyVal

  /**
   * Number of inputs of this Neural Network
   */
  val n_inputs: Int

  /**
   * Number of outputs of this Neural Network
   */
  val n_outputs: Int

  /**
   * Calculate the output of the Neural Network
   *
   * Given a set of input values it calculates the set of output values
   *
   * @param inputs A sequence of T to feed the NN
   * @return A sequence of T representing the output
   */
  def output( inputs: Seq[T] ): Seq[T]

  /**
   * Calculate the output of the Neural Network
   *
   * Given an input value it calculates the set of output values
   *
   * @param input A sequence of T to feed the NN
   * @return A sequence of T representing the output
   */
  def output( input: T ): Seq[T] = output( Seq(input) )
}

/**
 * Any Neural Network that implements weights for its neurons
 */
trait Weighted[B] extends NeuralNetwork {
  /**
   * Weights for the inputs of the neurons.
   * The first dimension represents the neuron, the second dimension represents an input for that neuron
   * It must be sized as (n_outputs, n_inputs)
   */
  val weights: Seq[Seq[B]]
}

/**
 * Any Neural Network that implements bias for its neurons
 */
trait Biased[B] extends NeuralNetwork {
  /**
   * Bias for the neurons. Every item is the bias for the corresponding neuron
   */
  val biases: Seq[B]
}