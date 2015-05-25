package com.colofabrix.scala.math

import com.colofabrix.scala.neuralnetwork.abstracts.NeuralNetwork

import scala.collection.mutable

/**
 * An adjacency matrix for Neural Networks
 *
 * It is a weighted, asymmetric adjacency matrix with Double.NaN to indicate non-connected vertices
 *
 * @param matrix A sequence of sequence, the initializer of the matrix
 * @param inputRoots The neurons in the matrix that represent the inputs
 * @param outputRoots The neurons in the matrix that represent the outputs
 *
 *                    Created by Fabrizio on 20/05/2015.
 */
class NetworkMatrix( override val matrix: Seq[Seq[Double]], val inputRoots: Seq[Int], val outputRoots: Seq[Int] ) extends Matrix[Double]( matrix ) {
  require( this.rows == this.cols + 1, "The adjacency matrix must be square with one additional row for biases" )
  require( inputRoots.length + outputRoots.length <= this.cols, "The number of inputs and outputs must be less than the size of the matrix" )
  require( inputRoots.forall( _ < this.cols ), "One or more input refer an index not present in the matrix" )
  require( outputRoots.forall( _ < this.cols ), "One or more output refer an index not present in the matrix" )

  /**
   * Mutable constructor
   *
   * @param matrix A mutable sequence of sequence, the initializer of the matrix
   * @param inputRoots The neurons in the matrix that represent the inputs
   * @param outputRoots The neurons in the matrix that represent the outputs
   */
  def this( matrix: mutable.Seq[mutable.Seq[Double]], inputRoots: Seq[Int], outputRoots: Seq[Int] ) {
    this( matrix.toSeq, inputRoots, outputRoots )
  }

  /**
   * Matrix constructor
   *
   * @param matrix The adjacency matrix
   * @param inputRoots The neurons in the matrix that represent the inputs
   * @param outputRoots The neurons in the matrix that represent the outputs
   */
  def this( matrix: Matrix[Double], inputRoots: Seq[Int], outputRoots: Seq[Int] ) {
    this( matrix.toSeq, inputRoots, outputRoots )
  }

  /**
   * The subset of the matrix that contains the adjacency weights
   * @return
   */
  val adjacencyOnly = this.rowSet( this.rows - 1 )

  /**
   * The bias row
   */
  val biases = this.row( this.rows - 1 )

  /**
   * Tells if the Neural Network is stateless (and the graph acyclic)
   */
  lazy val isAcyclic: Boolean = {
    // Test the network for all possible starting points (the inputs)
    val result = inputRoots map { i =>
      val (_, back, _) = NeuralNetwork.analiseNetwork( this, i )
      // Check that there are no back edges
      back.map( x => if (x.isNaN) 0.0 else 1.0 ) == back.toZero
    }

    // The condition must be true for all the starting points
    result.forall( _ == true )
  }

  /**
   * Tells if the Neural Network is forward only (this implies the network is also stateless)
   */
  lazy val isForwardOnly: Boolean = {
    // Test the network for all possible starting points (the inputs)
    val result = inputRoots map { i =>
      val (_, back, cross) = NeuralNetwork.analiseNetwork( this, i )
      // Check that there are no back edges nor cross edges
      back.map( x => if (x.isNaN) 0.0 else 1.0 ) == back.toZero && cross.map( x => if (x.isNaN) 0.0 else 1.0 ) == cross.toZero
    }

    // The condition must be true for all the starting points
    result.forall( _ == true )
  }

  /**
   * Determines if two NetworkMatrices are the same network.
   *
   * This method is useful because it does a correct semantic check for Double.NaN
   * and for the input/output roots
   *
   * @param that The other matrix to check
   * @return true if the two matrices are equal
   */
  def equals(that: NetworkMatrix): Boolean = {
    val values = for( i <- matrix.indices.par; j <- matrix.head.indices.par ) yield {
      // The value Not-a-Number must be checked in a different way
      if( (this(i, j) == that(i, j)) || (this(i, j).isNaN && that(i, j).isNaN) )
        true
      else
        false
    }

    values.forall(_ == true) && this.inputRoots == that.inputRoots && this.outputRoots == that.outputRoots
  }

  /**
   * true if the matrix contains only null values
   */
  lazy val isAllNaN = this equals this.toNaN

  /**
   * A Double.NaN matrix of the same size of the current matrix
   */
  def toNaN: NetworkMatrix = new NetworkMatrix(this map { _ => Double.NaN }, inputRoots, outputRoots)
}

object NetworkMatrix {

  /**
   * A Double.NaN matrix of the same size of the matrix given as parameter
   *
   * @return A new matrix of the size of the given matrix and containing only Double.NaN
   */
  def toNaN(matrix: Matrix[Double]): Matrix[Double] = new Matrix(matrix map { _ => Double.NaN } toSeq)

}