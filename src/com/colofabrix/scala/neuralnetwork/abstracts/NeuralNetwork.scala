package com.colofabrix.scala.neuralnetwork.abstracts

import com.colofabrix.scala.math.Matrix

import scala.collection.mutable.{ArrayBuffer, Stack}

/**
 * Any Neural Network of any type.
 *
 * A Neural Network has always a set of outputs given a set of inputs
 *
 * Created by Fabrizio on 03/05/2015.
 */
trait NeuralNetwork {

  /**
   * Adjacency matrix that defines the Neural Network
   *
   * See the description of the adjacency matrix (TKWAR-1) for its definition, structure
   * and for the nomenclature and assumptions
   *
   * @return The adjacency matrix that defines the Neural Network
   */
  def matrix: Matrix[Double]

  /**
   * Number of inputs of the Neural Network
   */
  def inputCount: Int

  /**
   * Number of outputs of the Neural Network
   */
  def outputCount: Int

  /**
   * Activation function associated with the Neural Network
   *
   * @return The activation function of the Neural Network
   */
  def af: ActivationFunction

  /**
   * Calculate the output of the Neural Network
   *
   * Given a set of input values it calculates the set of output values
   *
   * @param inputs A sequence of T to feed the NN
   * @return A sequence of T representing the output
   */
  def output( inputs: Seq[Double] ): Seq[Double]

  /**
   * Calculate the output of the Neural Network
   *
   * Given an input value it calculates the set of output values
   *
   * @param input A sequence of T to feed the NN
   * @return A sequence of T representing the output
   */
  def output( input: Double ): Seq[Double] = output( Seq(input) )

  /**
   * Determine if two Neural Network are the same
   *
   * The quality of two neural network is and must be only determined by their respective
   * adjacency matrices, regardless of their implementation.
   * This is not completely true as there are many equivalent matrices that specifies the same
   * neural network
   *
   * @param other The other object to check
   */
  override final def equals( other: Any ): Boolean = other match {
    case that: NeuralNetwork ⇒
      // Speed check with the number of inputs
      if( inputCount != that.inputCount || outputCount != that.outputCount ) return false

      // Checking every element
      for( i ← (0 to matrix.rows).par;
           j ← (0 to matrix.cols).par ) {
        if( matrix(i, j) != that.matrix(i, j)) return false
      }

      true

    case _ ⇒ false
  }

  /**
   * Hashcode of the Neural Network
   *
   * @return A number identifying the network
   */
  override def hashCode: Int = matrix.hashCode()

  /**
   * Gets a string representation of the neural network
   *
   * @return A string containing the representation of weights and biases of the neural network
   */
  override def toString = {
    val text = this.getClass + "(" + matrix.toString() + ")"
    text.replace("class ", "").replace("List", "").replace("com.colofabrix.scala.neuralnetwork.", "")
  }

  /**
   * Tells if the Neural Network is stateless
   */
  lazy val isStateless: Boolean =
    NeuralNetwork.isAcyclic(matrix.rowSet(matrix.rows - 1))
}

object NeuralNetwork {

  /**
   * Determines if an adjacency matrix represents an acyclic graph
   *
   * An element (a-ij) in the power matrix An represents if there is a path of length n between node i and node j
   * Non-zero elements on the diagonal represent cyclic path as they mean there is a path between the same node
   * The algorithm checks powers from from 1 to N to see if there are no non-zero elements on the diagonals of those
   * matrices.
   *
   * The implementation is taken from the following references:
   *  - https://math.stackexchange.com/questions/513288/test-for-acyclic-graph-property-based-on-adjacency-matrix
   *
   * The algorithm runs in O(n^3), n = number of nodes (empirical check)
   *
   * @deprecated Use {analiseNetwork} as it's more efficient in both worse-case and average-case scenario
   * @param a The adjacency matrix that represents the desired. It must be a square matrix
   * @return True if and only if the graph is acyclic
   */
  def isAcyclic(a: Matrix[Double]): Boolean = {
    require( a.rows == a.cols )

    // Transforms the weights matrix into a pure adjacency matrix (only zeroes and ones)
    val wm = a map { (x, _, _) ⇒ if (x.isNaN) 0 else 1 }

    val check = Seq.fill(wm.cols)(0)
    var pow = a map { (_, i, j) ⇒ if (i == j) 1 else 0 }

    // Actual algorithm. Check the documentation to understand it
    for( i ← 1 until wm.cols ) {
      pow = pow * wm
      if (pow.diagonal != check)
        return false
    }

    true
  }

  /**
   * Analyse a network and returns its subset of edges as adjacency matrices
   *
   * The method returns a tuple containing 3 adjacency matrices:
   *  -
   *
   * The algorithm runs in worse-case scenario as O(n^5), n = number of nodes (empirical check)
   *
   * @param matrix Adjacency matrix that represents the network
   * @return A tuple of three adjacency matrices that represents: the forward edges, the back edges and the cross edges
   */
  def analiseNetwork(matrix: Matrix[Double], inputs: Seq[Int] = Seq(0)): (Matrix[Double], Matrix[Double], Matrix[Double]) = {
    require( matrix.cols == matrix.rows, "The input matrix must be square" )

    // NOTE: for speed, this function uses mutable ArrayLists and not the Matrix class

    // Output matrices, all set to Double.NaN at the beginning
    val forward = ArrayBuffer.tabulate(matrix.rows, matrix.rows) { (i, j) ⇒ Double.NaN }
    val back = ArrayBuffer.tabulate(matrix.rows, matrix.rows) { (i, j) ⇒ Double.NaN }
    val cross = ArrayBuffer.tabulate(matrix.rows, matrix.rows) { (i, j) ⇒ Double.NaN }

    // Tree search stack
    val searchStack = new Stack[Int]()
    // List of already discovered nodes
    val visited = ArrayBuffer.fill(matrix.rows)(false)
    // List of the ancestors of the currently explored node
    val ancestors = ArrayBuffer.fill(matrix.rows)(new ArrayBuffer[Int]())

    for( start ← inputs ) {

      // Initial node
      searchStack.push(start)

      // Loop until there are nodes to visit
      while (searchStack.length > 0) {

        // Get the node to explore
        val current = searchStack.pop()

        // Process a node that hasn't been visited yet
        if (!visited(current)) {
          // Mark the node as visited
          visited(current) = true

          // Get the list of nodes that are directly connected to this one by a forward edge, including a self reference
          val adjacentForwardNodes = matrix.row(current)
            .zipWithIndex
            .filter(!_._1.isNaN)

          // Go through the children
          for ((value, child) ← adjacentForwardNodes) {
            ancestors(child) += current

            if (!visited(child)) {
              // Child not been seen before. Add it as a new node to explore and update the forward matrix
              forward(current)(child) = value
              searchStack.push(child)
            }
            else {
              if (!ancestors(current).contains(child) && !inputs.contains(current))
              // The child has been seen before, but it's not an ancestor. Just update the cross matrix
                cross(current)(child) = value

              else
              // The child has been seen before as an ancestor of the current node. Update the back matrix
                back(current)(child) = value
            }
          }
        }
      }
    }

    (new Matrix(forward), new Matrix(back), new Matrix(cross))
  }
}