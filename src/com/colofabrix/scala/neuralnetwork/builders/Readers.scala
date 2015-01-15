package com.colofabrix.scala.neuralnetwork.builders

import com.colofabrix.scala.neuralnetwork.abstracts.ActivationFunction
import com.colofabrix.scala.neuralnetwork.builders.abstracts.{DataReader, LayerReader}

import scala.util.Random

class RandomReader(scale: Double, nLayers: Int, rng: Random = new Random) extends DataReader with LayerReader {
  private def _nextDouble = rng.nextDouble() * 2 * scale - scale

  override def layerReaders = Seq.fill(nLayers)(this)

  override def neuronBiases(neurons: Int) = {
    Seq.fill(neurons)(_nextDouble)
  }

  override def inputWeights(neurons: Int, inputs: Int) = {
    Seq.fill(neurons, inputs)(_nextDouble)
  }

  override def activationFunction = ActivationFunction("tanh")
}

class SeqReader(biases: Seq[Double], weights: Seq[Seq[Double]]) extends DataReader {
  override def layerReaders: LayerReader = ???
}