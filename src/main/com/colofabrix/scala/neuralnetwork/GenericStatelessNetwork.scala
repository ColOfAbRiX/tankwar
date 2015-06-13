package com.colofabrix.scala.neuralnetwork

import com.colofabrix.scala.neuralnetwork.abstracts.{AbstractStatelessNetwork, ActivationFunction}

/**
 * A direct implementation of {StatelessNetwork}
 *
 * @param matrix Defining adjacency matrix
 * @param af Activation function used by the network
 */
class GenericStatelessNetwork(matrix: NetworkMatrix, af: ActivationFunction)
  extends AbstractStatelessNetwork(matrix, af)
