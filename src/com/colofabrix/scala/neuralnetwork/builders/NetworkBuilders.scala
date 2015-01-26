package com.colofabrix.scala.neuralnetwork.builders

import com.colofabrix.scala.neuralnetwork.abstracts.NeuralNetwork
import com.colofabrix.scala.neuralnetwork.builders.abstracts.{BehaviourBuilder, DataReader, StructureBuilder}
import com.colofabrix.scala.neuralnetwork.layers.ElmanFeedbackLayer
import com.colofabrix.scala.neuralnetwork.{ElmanNeuralNetwork, FeedforwardNeuralNetwork}

/**
 * Creates a new Feedforward Neural Network
 *
 * @param structure The builder of the internal structure of the NN
 */
class FeedforwardBuilder(structure: StructureBuilder) extends BehaviourBuilder {

  /**
   * Builds a Neural Network
   *
   * @param nInputs The number of input provided to the NN
   * @param nOutputs The number of outputs the NN returns
   * @return A new instance of a Neural Network
   */
  override def build(nInputs: Int, nOutputs: Int, dataReader: DataReader): NeuralNetwork =
    new FeedforwardNeuralNetwork(
      structure.inputLayer(nInputs, dataReader),
      structure.hiddenLayers(nInputs, dataReader),
      structure.outputLayer(nOutputs, dataReader)
    )

}


/**
 * Creates a new Recurrent Neural Network based on Elman model
 *
 * An Elman network is always a three-layer network
 *
 * @param structure The builder of the internal structure of the NN
 */
class ElmanBuilder(structure: StructureBuilder) extends BehaviourBuilder {

  /**
   * Builds a Neural Network
   *
   * @param nInputs The number of input provided to the NN
   * @param nOutputs The number of outputs the NN returns
   * @return A new instance of a Neural Network
   */
  override def build(nInputs: Int, nOutputs: Int, dataReader: DataReader): NeuralNetwork = {
    // Checks that only one hidden layer is provided
    val hidden = structure.hiddenLayers(nInputs, dataReader)
    require(hidden.length == 1)

    // Check the type of the hidden layer. It must be an ElmanFeedbackLayer
    val elmanHidden: ElmanFeedbackLayer = hidden(0) match {
      case efl: ElmanFeedbackLayer => efl
      case _ => throw new IllegalArgumentException("A layer of type ElmanFeedbackLayer must be specified")
    }

    // Build the object
    new ElmanNeuralNetwork(
      structure.inputLayer(nInputs, dataReader),
      Seq(elmanHidden),
      structure.outputLayer(nOutputs, dataReader)
    )
  }

}
