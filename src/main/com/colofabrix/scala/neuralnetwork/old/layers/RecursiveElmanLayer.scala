package com.colofabrix.scala.neuralnetwork.old.layers

import com.colofabrix.scala.neuralnetwork.old.abstracts.ActivationFunction

import scala.collection.mutable.ListBuffer

/**
 * Hidden Layer with feedback
 *
 * Every time the function output is called, its return values are memorized and
 * fed into the feedback neurons to be used at the next call.
 *
 * @param activation The activation function used by the neurons
 * @param n_inputs The number of inputs for each neuron
 * @param n_outputs The number of outputs, which equals the number of neurons
 * @param biases The set of bias values, one for each neuron
 * @param weights The set of input weights. Every neuron has n_inputs weights.
 * @param remember Determines if the layer has to remember the output of every call
 * @param contextWeights The weights for the feedback. It is structured as `weights` and the inputs are as `n_output`
 */
class RecursiveElmanLayer (
  activation: ActivationFunction,
  n_inputs: Int,
  n_outputs: Int,
  biases: Seq[Double],
  weights: Seq[Seq[Double]],
  contextWeights: Seq[Seq[Double]],
  var remember: Boolean = true)
extends HiddenLayer(activation, n_inputs, n_outputs, biases, weights) {

  // Check that every sequence of feedback weights associated with each neuron is the same size of the inputs of that neuron
  require( contextWeights.length == n_outputs && contextWeights.forall( _.length == n_outputs ), "The size of context weights must match n_output" )

  /**
   * Contains the values used as inputs for the last feedback
   *
   * @return
   */
  def lastFeedback =_memory.toList

  // This memory contains the outputs of the previous call of output
  private val _memory: ListBuffer[Double] = ListBuffer.fill(n_outputs)(0.0)

  // This is actually done because want to trick the HiddenLayer and give it modified parameters instead of create
  // a new type of layer from scratch. I just inherit from HiddenLayer to have an external interface.
  // The internal layer is configured to accept the normal inputs plus the feedback inputs
  private val internalLayer = new HiddenLayer(
    activation,
    n_inputs + n_outputs,
    n_outputs,
    biases,
    mixInputs(weights, contextWeights)
  )

  private def mixInputs( inputs1: Seq[Seq[Double]], inputs2: Seq[Seq[Double]] ) = (inputs1 zip inputs2) map { case (i1, i2) => i1 ++ i2 }

  override def output( inputs: Seq[Double] ): Seq[Double] = {
    val outputs = internalLayer.output(inputs ++ _memory)

    if( remember ) {
      _memory.clear()
      _memory ++= outputs
    }

    outputs
  }
}
