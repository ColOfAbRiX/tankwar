package com.colofabrix.scala.neuralnetwork.old.builders

import com.colofabrix.scala.neuralnetwork.old.FeedforwardNeuralNetwork
import com.colofabrix.scala.neuralnetwork.old.abstracts.NeuralNetwork
import com.colofabrix.scala.neuralnetwork.old.builders.abstracts.{BehaviourBuilder, LayerReader}
import com.colofabrix.scala.neuralnetwork.old.layers.{InputLayer, OutputLayer, HiddenLayer}
import com.colofabrix.scala.neuralnetwork.old.builders.abstracts.LayerReader
import com.colofabrix.scala.neuralnetwork.old.layers.OutputLayer

/**
 * Builder to create the components of a simple FeedForward Neural Network
 */
class FeedforwardBuilder extends BehaviourBuilder {
  /**
   * Builds a Neural Network
   *
   * @param inputLayer Input layer of the new Neural Network
   * @param hiddenLayers Hidden layers of the new Neural Network
   * @param outputLayer Output layer of the new Neural Network
   * @return A new instance of a Neural Network of type Feedforward
   */
  override def buildNetwork(inputLayer: InputLayer, hiddenLayers: Seq[HiddenLayer], outputLayer: OutputLayer): NeuralNetwork =
    new FeedforwardNeuralNetwork(
      inputLayer,
      hiddenLayers,
      outputLayer
    )

  /**
   * Returns a new InputLayer for the specific BehaviourBuilder
   *
   * @param nInputs The number of inputs of the NN
   * @return A new InputLayer for the NN
   */
  override def buildInputLayer(nInputs: Int, dataReader: LayerReader): InputLayer =
    new InputLayer(nInputs)

  /**
   * Returns a new HiddenLayer for the specific BehaviourBuilder
   *
   * @param nInputs The number of inputs of the NN
   * @param nNeurons The number of neurons in the HiddenLayer
   * @return A single HiddenLayer
   */
  override def buildHiddenLayer(nInputs: Int, nNeurons: Int, dataReader: LayerReader): HiddenLayer =
    new HiddenLayer(
      dataReader.activationFunction,
      nInputs,
      nNeurons,
      dataReader.neuronBiases(nNeurons),
      dataReader.inputWeights(nNeurons, nInputs)
    )

  /**
   * Returns a new OutputLayer for the specific BehaviourBuilder
   *
   * @param nInputs The number of inputs of the NN
   * @param nNeurons The number of neurons in the HiddenLayer
   * @return A single OutputLayer
   */
  override def buildOutputLayer(nInputs: Int, nNeurons: Int, dataReader: LayerReader): OutputLayer =
    new OutputLayer(
      dataReader.activationFunction,
      nInputs,
      nNeurons,
      dataReader.neuronBiases(nNeurons),
      dataReader.inputWeights(nNeurons, nInputs)
    )
}
