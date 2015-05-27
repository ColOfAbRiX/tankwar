package com.colofabrix.scala.neuralnetwork.old.builders

import com.colofabrix.scala.neuralnetwork.old.ElmanNeuralNetwork
import com.colofabrix.scala.neuralnetwork.old.abstracts.NeuralNetwork
import com.colofabrix.scala.neuralnetwork.old.builders.abstracts.{BehaviourBuilder, LayerReader}
import com.colofabrix.scala.neuralnetwork.old.layers.{ElmanFeedbackLayer, HiddenLayer, InputLayer, OutputLayer}

/**
 * Builder to create the components of a simple Elman Recurrent Neural Network
 */
class ElmanBuilder extends BehaviourBuilder {
  /**
   * Builds a Neural Network
   *
   * @param inputLayer Input layer of the new Neural Network
   * @param hiddenLayers Hidden layers of the new Neural Network
   * @param outputLayer Output layer of the new Neural Network
   * @return A new instance of a Neural Network
   */
  override def buildNetwork(inputLayer: InputLayer, hiddenLayers: Seq[HiddenLayer], outputLayer: OutputLayer): NeuralNetwork = {
    // Checks that only one hidden layer is provided
    require(hiddenLayers.length == 1)

    // Check the type of the hidden layer. It must be an ElmanFeedbackLayer
    val elmanHidden: ElmanFeedbackLayer = hiddenLayers(0) match {
      case efl: ElmanFeedbackLayer => efl
      case _ => throw new IllegalArgumentException("A layer of type ElmanFeedbackLayer must be specified")
    }

    // Build the object
    new ElmanNeuralNetwork(inputLayer, Seq(elmanHidden), outputLayer)
  }

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
    new ElmanFeedbackLayer(
      dataReader.activationFunction,
      nInputs,
      nNeurons,
      dataReader.neuronBiases(nNeurons),
      dataReader.inputWeights(nNeurons, nInputs),
      dataReader.inputWeights(nNeurons, nNeurons)
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
