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
   * Type of data evaluated by the neurons
   */
  @specialized(Double)
  type T

  /**
   * Type of data used to expose externally biases and weights
   */
  @specialized(Double)
  type U

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

  /**
   * Calculate the output of the Neural Network
   *
   * Given an input value it calculates the set of output values
   *
   * @param inputHelper A sequence of T to feed the NN
   * @return A sequence of T representing the output
   */
  def output( inputHelper: InputHelper[T] ): Seq[T] = {
    output( inputHelper.getValues )
  }

  /**
   * Weights for the inputs of the neurons.
   * The first dimension represents the neuron, the second dimension represents an input for that neuron
   * It must be sized as (n_outputs, n_inputs)
   */
  val weights: Seq[Seq[U]]

  /**
   * Bias for the neurons. Every item is the bias for the corresponding neuron
   */
  val biases: Seq[U]
}