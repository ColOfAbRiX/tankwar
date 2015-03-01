package com.colofabrix.scala.neuralnetwork

import java.io.PrintWriter

import com.colofabrix.scala.neuralnetwork.abstracts.NeuralNetwork

/**
 * Provides a way to visualize the behaviour of a NN
 *
 * This class allows to create CSV files to analyse the various outputs varying
 * specific inputs.
 *
 * Created by Fabrizio on 15/02/2015.
 */
final class NNTester(val network: NeuralNetwork, nInputs: Int, name: String) {

  // Range of the values and number of points (input#, start_value, end_value, points_count)
  val plotDefinitions = List(
    (0, 0.0, 30.0, 250.0),
    (1, 0.0, 30.0, 250.0),
    (2, 0.0, 20.0, 250.0),
    (3, 0.0, 20.0, 250.0),
    (4, 0.0, 2 * Math.PI, 250.0),
    (5, 0.0, 100.0, 250.0),
    (6, 0.0, 100.0, 250.0)
  )

  val testDefinitions: List[(PrintWriter ⇒ Unit)] = List(
    // Behaviour on the x-axis with x-speed of zero
    fullAnalysis(Seq(0, 500, 0, 0, 0, 0, 0), 0, 2)(_)
    //fullAnalysis(Seq(0, 0, 0, 0, 0, 0, 0), 0, 1)(_),
    //fullAnalysis(Seq(0, 0, 0, 0, 0, 0, 0), 0, 1)(_),
    //fullAnalysis(Seq(0, 0, 0, 0, 0, 0, 0), 2, 3)(_)
  )

  private def internalFullAnalysis(inputBase: Seq[Double], plotIndexes: Seq[Int])(writer: PrintWriter): Unit = {
    val currentIndex = plotIndexes.head

    // Extracts the definition of the plot from the list
    val (input, start, end, points) = plotDefinitions(currentIndex)

    // All the X-Values to plot
    val range = start.to(end, (end - start) / points)

    // For every X value
    range.map { x ⇒
      // Modify the value of the current input
      val inputs = inputBase.patch(input, Seq(x), 1)

      if (plotIndexes.length == 1) {
        // If there is only one value to plot, then plot it
        val outputs = network.output(inputs.asInstanceOf[Seq[this.network.T]])
        writer.write(s"${inputs.mkString(";") };${outputs.mkString(";") }\n".replace(".", ","))
      }
      else
        // If there is more than one value to plot, recursively call this function over the remaining indexes
        internalFullAnalysis(inputs, plotIndexes.tail)(writer)
    }
  }

  def fullAnalysis(inputBase: Seq[Double], plotIndexes: Int*)(writer: PrintWriter): Unit =
    internalFullAnalysis(inputBase, plotIndexes)(writer)

  def runTests(): Unit = {

    testDefinitions.zipWithIndex.foreach { test ⇒
      val fileName = s"$name-test${test._2}.csv"

      // Don't do anything if the test has already run
      if( !new java.io.File(fileName).exists() ) {
        val writer = new PrintWriter(fileName)
        writer.println(s"${Seq.range(0, nInputs).mkString("", "-input;", "-input") };Force-X;Force-Y;Rot;Shoot")

        // Run the test
        test._1 { writer }

        writer.close()
      }
    }
  }

}
