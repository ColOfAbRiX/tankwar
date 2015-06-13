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

package com.colofabrix.scala.neuralnetwork.abstracts

import java.io.PrintWriter

import com.colofabrix.scala.simulation.{BrainInputHelper, World}

/**
 * Provides a way to visualize the behaviour of a NN
 *
 * This class allows the creation of CSV files to analyse the various outputs varying specific inputs. It feeds a network
 * with input values and writes the outputs to a stream. It also provides a basic structure to define different types
 * of tests (for different inputs and combinations of them) for different ranges of input values.
 *
 * @param world Reference to the World.
 * @param network The neural network that you want to analyse
 */
// FIXME: Remove the usage of {world}. It is there only to provide data for a badly designed {BrainInputHelper}
abstract class AbstractNetworkAnalyser( val world: World, val network: com.colofabrix.scala.neuralnetwork.old.abstracts.NeuralNetwork ) extends NetworkAnalyser {

  /**
   * First line that will be written to the output stream, usually the header of the CSV file
   *
   * @return A text containing a comma-separated values as a header for the CSV file
   */
  def outputHeader: String

  /**
   * Contains the definition of the plots for the network, like the definition of the input range for a
   * specific input. The list will be filled by a concrete implementation of the class. Every item of the list
   * is a tuple containing:
   *
   * (input#, start_value, end_value, points_count)
   * - input#: Reference to the ordinal number of the input to use
   * - start_value: The initial value to start to feed
   * - end_value: The final value to feed
   * - points_count: How many intermediate values (between start_value and end_value) feed to the network
   *
   * @return A list of tuples constructed like:
   */
  def plotDefinitions: List[(Int, Double, Double, Double)]

  /**
   * Contains the definition of the tests that will be performed during the run. The list will be filled
   * by a concrete implementation of the class. The list simply contains functions that will be called one
   * after the other and that accept a writer. The writer is used to write the data to the output stream
   *
   * @return A list of functions that run the test, each of which are called in the sequence found in the list
   */
  def testDefinitions: List[(PrintWriter => Unit)]

  /**
   * Performs the numerical analysis of the network
   *
   * Give the plot definition to use, it scan through the values and calculates the output of the
   * network that then writes in an outputs stream
   *
   * @param inputBase The initial vector to feed the network. Used to define custom values for the indexes not in the range
   * @param plotIndexes Sequence containing the iIndexes of the input vector to scan through, applying the plot definition
   * @param writer Writer used to write the output
   */
  protected def internalFullAnalysis( inputBase: Seq[Double], plotIndexes: Seq[Int] )( writer: PrintWriter ): Unit = {
    val currentIndex = plotIndexes.head

    // Extracts the definition of the plot from the list
    val (input, start, end, points) = plotDefinitions(currentIndex)

    // The list of the input values to plot
    val range = start.to(end, (end - start) / points)

    range.foreach { x =>
      // Modify the value of the current input
      val inputs = inputBase.patch(input, Seq(x), 1)

      if( plotIndexes.length == 1 ) {
        // If there is only one value to plot, then plot it
        val outputs = network.output(new BrainInputHelper(world, inputs))
        writer.write(s"${inputs.mkString(";")};${outputs.mkString(";")}\n".replace(".", ","))
      }
      else {
        // If there is more than one value to plot, recursively call this function over the remaining indexes { {
        internalFullAnalysis(inputs, plotIndexes.tail)(writer)
      }
    }
  }

  /**
   * Performs the numerical analysis of the network
   *
   * Give the plot definition to use, it scan through the values and calculates the output of the
   * network that then writes in an outputs stream
   *
   * @param inputBase The initial vector to feed the network. Used to define custom values for the indexes not in the range
   * @param plotIndexes Indexes of the input vector to scan through, applying the plot definition
   * @param writer Writer used to write the output
   */
  protected def fullAnalysis( inputBase: Seq[Double], plotIndexes: Int* )( writer: PrintWriter ): Unit =
    internalFullAnalysis(inputBase, plotIndexes)(writer)
}
