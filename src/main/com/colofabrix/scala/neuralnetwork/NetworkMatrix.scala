/*
 * Copyright (C) 2015 Fabrizio Colonna
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language
 * governing permissions and limitations under the License.
 */

package com.colofabrix.scala.neuralnetwork

import com.colofabrix.scala.math.Matrix
import com.colofabrix.scala.neuralnetwork.abstracts.NeuralNetwork

import scala.collection.mutable

/**
 * An adjacency matrix for Neural Networks
 *
 * It is a weighted, asymmetric adjacency matrix with Double.NaN to indicate non-connected vertices. The matrix is composed
 * of two parts, a square matrix that contains the weights of the edges and the last row that contains the biases for
 * each neuron
 *
 * @param matrix A sequence of sequence, the initializer of the matrix
 * @param inputRoots The indexes in the matrix that represent the inputs
 * @param outputRoots The indexes in the matrix that represent the outputs
 */
class NetworkMatrix( override val matrix: Seq[Seq[Double]], val inputRoots: Seq[Int], val outputRoots: Seq[Int] ) extends Matrix[Double]( matrix ) {
  require( this.rows == this.cols + 1, "The adjacency matrix must be square with one additional row for biases" )
  require( inputRoots.nonEmpty && outputRoots.nonEmpty, "Input and output roots must not be empty" )
  require( inputRoots.length + outputRoots.length <= this.cols, "The number of inputs and outputs must be less than the size of the matrix" )
  require( inputRoots.forall( _ < this.cols ), "One or more input refer an index not present in the matrix" )
  require( outputRoots.forall( _ < this.cols ), "One or more output refer an index not present in the matrix" )
  require( inputRoots.distinct.length == inputRoots.length, "The input roots must be distinct" )
  require( outputRoots.distinct.length == outputRoots.length, "The output roots must be distinct" )

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
   */
  val adjacencyOnly = this.rowSet( this.rows - 1 )

  /**
   * The bias row (which is always the last row of the matrix)
   */
  val biases = this.row( this.rows - 1 )

  /**
   * Tells if the Neural Network represented by this NetworkMatrix is stateless (and the graph acyclic)
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
   * Tells if the Neural Network represented by this NetworkMatrix is forward only (this implies the network is also stateless)
   */
  lazy val isForwardOnly: Boolean = {
    if( !isAcyclic )
      // Shortcut, if acyclic is never forward-only
      false

    else {
      // Test the network for all possible starting points (the inputs)

      val result = inputRoots map { i =>
        val (_, _, cross) = NeuralNetwork.analiseNetwork(this, i)
        // Check that there are no back edges nor cross edges
        cross.map(x => if( x.isNaN ) 0.0 else 1.0) == cross.toZero
      }

      // The condition must be true for all the starting points
      result.forall(_ == true)
    }
  }

  /**
   * Determines if two matrices are equals
   *
   * @param obj The other object to compare
   * @return true if the other object is a matrix identical to the current one
   */
  override def equals( obj: Any ): Boolean = obj match {
    case that: NetworkMatrix => this compare that
    case that: Matrix[Double] => this compare that
    case that: Seq[Double] => this compare that
    case _ => super.equals(obj)
  }

  /**
   * Determines if the given adjacency matrix is the same as the current instance
   *
   * This method is useful because it does a correct semantic check for Double.NaN
   * and for the input/output roots
   *
   * @param that The other matrix to check
   * @return true if the two adjacency matrices are equal
   */
  private def compare(that: Matrix[Double]): Boolean = {
    val adMatrix = this.adjacencyOnly

    // Shortcut checking only the number of rows and cols
    if( adMatrix.cols != that.cols || adMatrix.rows != that.rows )
      return false

    // Check element by element that they are the equal also in respect to NaN values
    val values = for( i <- (0 until adMatrix.rows).par; j <- (0 until adMatrix.cols).par ) yield {
      (adMatrix(i, j) == that(i, j)) || (adMatrix(i, j).isNaN && that(i, j).isNaN)
    }

    values.forall(_ == true)
  }

  /**
   * Determines if the given biases are the same as the current instance's ones
   *
   * This method is useful because it does a correct semantic check for Double.NaN
   * and for the input/output roots
   *
   * @param that The other matrix to check
   * @return true if the two adjacency matrices are equal
   */
  private def compare(that: Seq[Double]): Boolean = {
    val biases = this.biases

    // Shortcut checking only the number of elements
    if( biases.length != that.length )
      return false

    // Check element by element that they are the equal also in respect to NaN values
    val values = (biases zip that) map { case (x, y) =>
      (x == y) || (x.isNaN && y.isNaN)
    }

    values.forall(_ == true)
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
  private def compare(that: NetworkMatrix): Boolean = {
    (this compare that.adjacencyOnly) &&
      (this compare that.biases) &&
      (this.inputRoots == that.inputRoots) &&
      (this.outputRoots == that.outputRoots)
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