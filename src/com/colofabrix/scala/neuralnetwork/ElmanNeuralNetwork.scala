package com.colofabrix.scala.neuralnetwork

import com.colofabrix.scala.neuralnetwork.layers.{ElmanFeedbackLayer, InputLayer, OutputLayer}

/**
 * Elman Neural Network
 *
 * A type of Recurrent Neural Network
 */
class ElmanNeuralNetwork(
  override val input_layer: InputLayer,
  hidden_layers: Seq[ElmanFeedbackLayer],
  override val output_layer: OutputLayer,
  private var _rememberPast: Boolean = true)
extends FeedforwardNeuralNetwork(
  input_layer,
  hidden_layers,
  output_layer) {

  def rememberPast = _rememberPast
  def rememberPast_=(value: Boolean) {
    _rememberPast = value
    hidden_layers.foreach(_.remember = value)
  }

}