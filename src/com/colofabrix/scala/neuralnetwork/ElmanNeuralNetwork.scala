package com.colofabrix.scala.neuralnetwork

import com.colofabrix.scala.neuralnetwork.layers.{HiddenLayer, InputLayer, OutputLayer}

/**
 * Elman Neural Network
 *
 * A type of Recurrent Neural Network
 *
 * Created by Fabrizio on 09/01/2015.
 */
class ElmanNeuralNetwork(
  override val input_layer: InputLayer,
  hidden_layers: HiddenLayer,
  override val output_layer: OutputLayer,
  val feedback_layer: HiddenLayer )
extends FeedforwardNeuralNetwork(
  input_layer,
  Seq(hidden_layers),
  output_layer) {

}