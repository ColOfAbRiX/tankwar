package com.colofabrix.scala.neuralnetwork.builders

import com.colofabrix.scala.neuralnetwork.abstracts.NeuralNetwork
import com.colofabrix.scala.neuralnetwork.builders.abstracts.{NeuralNetworkBuilder, TopologyBuilder}
import com.colofabrix.scala.neuralnetwork.layers.ElmanFeedbackLayer
import com.colofabrix.scala.neuralnetwork.{ElmanNeuralNetwork, FeedforwardNeuralNetwork}

class FeedforwardBuilder(topologyBuilder: TopologyBuilder) extends NeuralNetworkBuilder(topologyBuilder) {

  override def build(nInputs: Int, nOutputs: Int): NeuralNetwork =
    new FeedforwardNeuralNetwork(
      topologyBuilder.inputLayer(nInputs),
      topologyBuilder.hiddenLayers(nInputs),
      topologyBuilder.outputLayer(nOutputs)
    )

}

class ElmanBuilder(topologyBuilder: TopologyBuilder) extends NeuralNetworkBuilder(topologyBuilder) {

  override def build(nInputs: Int, nOutputs: Int): NeuralNetwork = {
    val hiddens = topologyBuilder.hiddenLayers(nInputs)
    require(hiddens.length == 1)

    val elmanHidden: ElmanFeedbackLayer = hiddens(0) match {
      case efl: ElmanFeedbackLayer => efl
      case _ => throw new IllegalArgumentException
    }

    new ElmanNeuralNetwork(
      topologyBuilder.inputLayer(nInputs),
      Seq(elmanHidden),
      topologyBuilder.outputLayer(nOutputs)
    )
  }

}
