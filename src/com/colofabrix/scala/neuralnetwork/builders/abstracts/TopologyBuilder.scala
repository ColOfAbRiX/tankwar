package com.colofabrix.scala.neuralnetwork.builders.abstracts

import com.colofabrix.scala.neuralnetwork.layers.{HiddenLayer, InputLayer, OutputLayer}

/**
 * Created by Fabrizio on 14/01/2015.
 */
trait TopologyBuilder {
  def inputLayer(nInputs: Int): InputLayer
  def hiddenLayers(nInitialInputs: Int): Seq[HiddenLayer]
  def outputLayer(nOutputs: Int): OutputLayer
}
