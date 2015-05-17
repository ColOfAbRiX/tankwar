package com.colofabrix.scala.neuralnetwork

import com.colofabrix.scala.math.Matrix
import com.colofabrix.scala.neuralnetwork.abstracts.{ActivationFunction, StatelessNetwork}

/**
 * A direct implementation of {StatelessNetwork}
 *
 * @param inputCount Number of inputs of the Neural Network
 * @param outputCount Number of inputs of the Neural Network
 * @param matrix Defining adjacency matrix
 * @param af Activation function used by the network
 */
class GenericStatelessNetwork(inputCount: Int, outputCount: Int, matrix: Matrix[Double], af: ActivationFunction)
  extends StatelessNetwork(inputCount, outputCount, matrix, af)
