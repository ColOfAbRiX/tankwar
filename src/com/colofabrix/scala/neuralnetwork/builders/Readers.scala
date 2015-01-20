package com.colofabrix.scala.neuralnetwork.builders

import com.colofabrix.scala.neuralnetwork.abstracts.ActivationFunction
import com.colofabrix.scala.neuralnetwork.builders.abstracts.{DataReader, LayerReader}

import scala.util.Random

/**
 * Creates random number for the initialization of a NN
 *
 * The data provided by this object is non structured so it works
 * both as a Data and a Layer Reader
 *
 * @param scale The random numbers will be picked in the interval ]-scale, +scale[
 * @param nLayers The number of layers of the network
 * @param rng Random Number Generator
 */
class RandomReader(scale: Double, nLayers: Int, rng: Random = new Random) extends DataReader with LayerReader {

  // Function to retrieve the number
  private def _nextDouble = rng.nextDouble() * 2 * scale - scale

  /**
   * Gets the next `LayerReader` for the next layer.
   *
   * Every call to this method advances an internal pointer so that every call returns
   * another reader
   *
   * @return An instance of `LayerReader` in the current position
   */
  override def layerReaders = Seq.fill(nLayers)(this)

  /**
   * Returns the biases of the neurons
   *
   * @param neurons Number of neurons in the layer
   * @return The sequence of biases, one for each neuron
   */
  override def neuronBiases(neurons: Int) = {
    Seq.fill(neurons)(_nextDouble)
  }

  /**
   * Return the weights, for each neuron, associated with the inputs
   *
   * @param neurons Number of neurons in the layer
   * @param inputs Number of inputs for each neuron
   * @return The weights, for each neuron, associated with the inputs
   */
  override def inputWeights(neurons: Int, inputs: Int) = {
    Seq.fill(neurons, inputs)(_nextDouble)
  }

  /**
   * Returns the ActivationFunction associated with the layer
   *
   * @return The ActivationFunction associated with the layer
   */
  override def activationFunction = ActivationFunction("tanh")
}


/**
 * Sequence Data Reader
 *
 * It reads the data of a NN Layer from a Sequence
 *
 * @param biases Biases of the layer
 * @param weights Weights of the layer
 */
class SeqReader(biases: Seq[Double], weights: Seq[Seq[Double]]) extends DataReader {
  override def layerReaders = ???
}