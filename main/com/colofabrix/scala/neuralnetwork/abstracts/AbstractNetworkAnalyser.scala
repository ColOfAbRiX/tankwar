package com.colofabrix.scala.neuralnetwork.abstracts

import java.io.PrintWriter

import com.colofabrix.scala.tankwar.{BrainInputHelper, World}

/**
 * Provides a way to visualize the behaviour of a NN
 *
 * This class allows to create CSV files to analyse the various outputs varying
 * specific inputs.
 *
 * Created by Fabrizio on 15/02/2015.
 */
abstract class AbstractNetworkAnalyser(val world: World, val network: com.colofabrix.scala.neuralnetworkOld.abstracts.NeuralNetwork) extends NetworkAnalyser {

  /**
   * First line that will be written to the output stream
   */
  def outputHeader: String

  /**
   * Contains the definition of the plots for the network, like the definition of the
   * input values for a specific input. The list if filled by a concrete implementation
   * of the class
   *
   * @return A list of tuples constructed like: (input#, start_value, end_value, points_count)
   */
  def plotDefinitions: List[(Int, Double, Double, Double)]

  /**
   * Contains the definition of the tests that will be performed during the run. The list
   * if filled by a concrete implementation of the class
   *
   * @return A list of functions that run the test, each of which are called once
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
  protected def internalFullAnalysis(inputBase: Seq[Double], plotIndexes: Seq[Int])(writer: PrintWriter): Unit = {
    val currentIndex = plotIndexes.head

    // Extracts the definition of the plot from the list
    val (input, start, end, points) = plotDefinitions(currentIndex)

    // All the X-Values to plot
    val range = start.to(end, (end - start) / points)

    // For every X value
    range.foreach { x =>
      // Modify the value of the current input
      val inputs = inputBase.patch(input, Seq(x), 1)

      if (plotIndexes.length == 1) {
        // If there is only one value to plot, then plot it
        val outputs = network.output( new BrainInputHelper(world, inputs))
        writer.write(s"${inputs.mkString(";") };${outputs.mkString(";") }\n".replace(".", ","))
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
  protected def fullAnalysis(inputBase: Seq[Double], plotIndexes: Int*)(writer: PrintWriter): Unit =
    internalFullAnalysis(inputBase, plotIndexes)(writer)
}
