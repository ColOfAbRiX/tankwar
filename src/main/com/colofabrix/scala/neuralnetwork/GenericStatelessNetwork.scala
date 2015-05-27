package com.colofabrix.scala.neuralnetwork

import com.colofabrix.scala.math.NetworkMatrix
import com.colofabrix.scala.neuralnetwork.abstracts.{AbstractStatelessNetwork, ActivationFunction}

/**
 * A direct implementation of {StatelessNetwork}
 *
 * @param inputCount Number of inputs of the Neural Network
 * @param outputCount Number of inputs of the Neural Network
 * @param matrix Defining adjacency matrix
 * @param af Activation function used by the network
 */
class GenericStatelessNetwork(inputCount: Int, outputCount: Int, matrix: NetworkMatrix, af: ActivationFunction)
  extends AbstractStatelessNetwork(inputCount, outputCount, matrix, af)
