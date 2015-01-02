package com.colofabrix.scala.neuralnetwork.abstracts

/**
 * A neural network layer
 *
 * A layer can be thought as a neural network itself as it has its own inputs
 * and outputs the same was a NN does.
 * A layer is defined by its neurons, their inputs and inputs weights, the
 * activation function used and a bias for every neuron.
 *
 * @param activation The activation function used by the neurons
 * @param n_outputs The number of neurons in the layer
 * @param n_inputs The number of inputs for each neuron
 * @param biases The set of bias values, one for each neuron
 * @param weights The set of input weights. Every neuron has n_inputs weights.
 */
abstract class NeuronLayer (
  val activation: Double => Double,
  override val n_inputs: Int,
  override val n_outputs: Int,
  override val biases: Seq[Double],
  override val weights: Seq[Seq[Double]] )
extends Weighted[Double] with Biased[Double]
{
  // An activation function must be provided
  require( activation != null )
  // There must be at least a neuron
  require( n_outputs > 0, "The number of outputs must be positive" )
  // Every neuron must have at least one input
  require( n_inputs > 0, "The number of inputs must be positive" )
  // Every neuron has a bias, so bias.length must match neuron_count
  require( biases.length == n_outputs, "The number of biases must match the number of outputs" )
  // Check that every sequence of weights associated with each neuron is the same size of the inputs of that neuron
  require( weights.length == n_outputs && weights.forall( _.length == n_inputs ), "The size of weights must match the input/output numbers" )

  override type T = Double

  override def equals( other: Any ) = other match {
    case that: NeuronLayer =>
      this.canEqual(that) &&
      this.biases == that.biases &&
      this.weights == that.weights
    case _ => false
  }

  override def hashCode: Int = {
    41 * (
      41 * (
        41 + this.activation.hashCode
      ) + this.biases.hashCode
    ) + this.weights.hashCode
  }

  def canEqual( other: Any ): Boolean =
    other.isInstanceOf[NeuronLayer]

  /**
   * Calculate the output of the layer
   *
   * Given a set of input values it calculates the set of output values
   *
   * @param inputs A sequence of double to feed the layer
   * @return A sequence of double representing the output
   */
  def output( inputs: Seq[T] ): Seq[T] = {
    // The given input values must match the NN configuration
    require( inputs.length == n_inputs, "The actual inputs must match the number of set inputs for the layer" )

    for( o <- 0 until n_outputs ) yield
      (inputs zip weights(o) map { case (i, w) => i * w } sum ) + biases(o)
  }
}