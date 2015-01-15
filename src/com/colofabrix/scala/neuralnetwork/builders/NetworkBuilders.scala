package com.colofabrix.scala.neuralnetwork.builders

import com.colofabrix.scala.neuralnetwork.abstracts.NeuralNetwork
import com.colofabrix.scala.neuralnetwork.builders.abstracts.{BehaviourBuilder, StructureBuilder}
import com.colofabrix.scala.neuralnetwork.layers.ElmanFeedbackLayer
import com.colofabrix.scala.neuralnetwork.{ElmanNeuralNetwork, FeedforwardNeuralNetwork}

class FeedforwardBuilder(structure: StructureBuilder) extends BehaviourBuilder {

  override def build(nInputs: Int, nOutputs: Int): NeuralNetwork =
    new FeedforwardNeuralNetwork(
      structure.inputLayer(nInputs),
      structure.hiddenLayers(nInputs),
      structure.outputLayer(nOutputs)
    )

}

class ElmanBuilder(structure: StructureBuilder) extends BehaviourBuilder {

  override def build(nInputs: Int, nOutputs: Int): NeuralNetwork = {
    val hidden = structure.hiddenLayers(nInputs)
    require(hidden.length == 1)

    val elmanHidden: ElmanFeedbackLayer = hidden(0) match {
      case efl: ElmanFeedbackLayer => efl
      case _ => throw new IllegalArgumentException
    }

    new ElmanNeuralNetwork(
      structure.inputLayer(nInputs),
      Seq(elmanHidden),
      structure.outputLayer(nOutputs)
    )
  }

}
