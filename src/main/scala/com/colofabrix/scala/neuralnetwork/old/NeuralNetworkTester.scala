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

package com.colofabrix.scala.neuralnetwork.old

import java.io.PrintWriter

import com.colofabrix.scala.neuralnetwork.old.abstracts.{ NeuralNetwork, Tester }

/**
 * Provides a way to visualize the behaviour of a NN
 *
 * This class allows to create CSV files to analyse the various outputs varying
 * specific inputs.
 *
 * Created by Fabrizio on 15/02/2015.
 */
@deprecated( "Deprecated", "0.1.0" )
abstract class NeuralNetworkTester( val network: NeuralNetwork, nInputs: Int ) extends Tester {

  // Range of the values and number of points (input#, start_value, end_value, points_count)
  def plotDefinitions: List[( Int, Double, Double, Double )]

  def testDefinitions: List[( PrintWriter ⇒ Unit )]

  protected def evaluateNetwork(): Unit

  protected def internalFullAnalysis( inputBase: Seq[Double], plotIndexes: Seq[Int] )( writer: PrintWriter ): Unit = {
    val currentIndex = plotIndexes.head

    // Extracts the definition of the plot from the list
    val ( input, start, end, points ) = plotDefinitions( currentIndex )

    // All the X-Values to plot
    val range = start.to( end, ( end - start ) / points )

    // For every X value
    range.foreach { x ⇒
      // Modify the value of the current input
      val inputs = inputBase.patch( input, Seq( x ), 1 )

      if ( plotIndexes.length == 1 ) {
        // If there is only one value to plot, then plot it
        val outputs = network.output( inputs )
        writer.write( s"${inputs.mkString( ";" )};${outputs.mkString( ";" )}\n".replace( ".", "," ) )
      }
      else {
        // If there is more than one value to plot, recursively call this function over the remaining indexes {
        internalFullAnalysis( inputs, plotIndexes.tail )( writer )
      }
    }
  }

  protected def fullAnalysis( inputBase: Seq[Double], plotIndexes: Int* )( writer: PrintWriter ): Unit =
    internalFullAnalysis( inputBase, plotIndexes )( writer )

  def runTests(): Unit = {

    testDefinitions.zipWithIndex.foreach { test ⇒
      val fileName = s"${network.hashCode}test${test._2}.csv"

      // Don't do anything if the test has already run
      if ( !new java.io.File( fileName ).exists() ) {
        val writer = new PrintWriter( fileName )
        writer.println( s"${Seq.range( 0, nInputs ).mkString( "", "-input;", "-input" )};Force-X;Force-Y;Rot;Shoot" )

        // Run the test
        test._1 { writer }

        writer.close()
      }
    }
  }
}