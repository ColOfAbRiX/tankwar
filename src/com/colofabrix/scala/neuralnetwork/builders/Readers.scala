package com.colofabrix.scala.neuralnetwork.builders

import com.colofabrix.scala.neuralnetwork.abstracts.ActivationFunction
import com.colofabrix.scala.neuralnetwork.builders.abstracts.{DataReader, LayerReader}

import scala.util.Random

class RandomReader(scale: Double, rng: Random = new Random) extends DataReader with LayerReader {
  override def nextLayerReader = this

  override def neuronBiases(neurons: Int) = Seq.fill(neurons)(rng.nextDouble() * 2 * scale - scale)

  override def inputWeights(neurons: Int, inputs: Int) = Seq.fill(neurons, inputs)(rng.nextDouble() * 2 * scale - scale)

  override def activationFunction = ActivationFunction("tanh")
}

class SeqReader(biases: Seq[Double], weights: Seq[Seq[Double]]) extends DataReader {
  override def nextLayerReader: LayerReader = ???
}