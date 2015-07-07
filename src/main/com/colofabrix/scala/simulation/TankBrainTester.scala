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

package com.colofabrix.scala.simulation

import java.io.PrintWriter

import com.colofabrix.scala.neuralnetwork.abstracts.AbstractNetworkAnalyser

/**
 * Provides a way to visualize the behaviour of a NN
 *
 * This class allows to create CSV files to analyse the various outputs varying specific inputs.
 */
final class TankBrainTester( val tank: Tank ) extends AbstractNetworkAnalyser( tank.world, tank.brain ) {

  /**
   * First line that will be written to the output stream
   */
  override def outputHeader: String = s"${Seq.range( 0, network.n_inputs ).mkString( "", "-input;", "-input" ) };Force-X;Force-Y;Rot;Shoot"

  /**
   * Contains the definition of the plots for the network, like the definition of the
   * input values for a specific input.
   *
   * @return A list of tuples constructed like: (input#, start_value, end_value, points_count)
   */
  override val plotDefinitions = List(
    (0, 0.0, tank.world.arena.topRight.x, 100.0), // Range for the x-position
    (1, 0.0, tank.world.arena.topRight.y, 100.0), // Range for the y-position
    (2, 0.0, tank.world.max_tank_speed, 100.0), // Range for the x-speed
    (3, 0.0, tank.world.max_tank_speed, 100.0), // Range for the y-speed
    (4, 0.0, 2 * Math.PI, 100.0), // Range for the angle of rotation
    (5, 0.0, tank.world.max_sight, 100.0), // Range for the distance of the seen target
    (6, 0.0, 2 * Math.PI, 100.0) // Range for the rotation of the seen target
  )

  /**
   * Contains the definition of the tests that will be performed during the run.
   *
   * @return A list of functions that run the test, each of which are called once
   */
  override val testDefinitions: List[(PrintWriter => Unit)] = List(
    fullAnalysis( Seq.fill( BrainInputHelper.count )( 0.0 ), 5, 6 )( _ )
  )

  def runTests( ): Unit = {
    // Run through all the tests
    testDefinitions.zipWithIndex.foreach { test =>

      // Different name of files for different networks and tests
      val fileName = s"out/NN-${Math.abs( network.hashCode ) }_test-${test._2 }.csv"

      // Don't do anything if the test has already run (speedup a lot)
      if( !new java.io.File( fileName ).exists( ) ) {
        val writer = new PrintWriter( fileName )

        // Write the header
        writer.println( outputHeader )

        // Run the test
        test._1 {writer}

        writer.close( )
      }
    }
  }
}
