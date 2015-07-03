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

package com.colofabrix.scala.simulation.integration

import java.io.PrintWriter

import com.colofabrix.scala.simulation.{ Tank, TankBrainTester }
import org.uncommons.watchmaker.framework.{ EvolutionObserver, PopulationData }

/**
 * Trivial evolution observer to display information about the population at the end
 * of each generation.
 */
class EvolutionLogger[T <: Tank] extends EvolutionObserver[T] {

  val writer = new PrintWriter( "out/population.csv" )

  override def populationUpdate( pop: PopulationData[_ <: T] ): Unit = {
    // Best candidate
    val best = pop.getBestCandidate

    // Calculates various stats about the population
    val scores = best.world.tanks.map( TankEvaluator.fitness ).toSeq
    val count = scores.length
    val mean = scores.sum / count
    val devs = scores.map( score => (score - mean) * (score - mean) )
    val stddev = Math.sqrt( devs.sum / count )

    // Print on screen
    println( s"Gen #${pop.getGenerationNumber}: Best: ${pop.getBestCandidateFitness}, Mean: $mean, Std Dev: $stddev" )
    println( s"Best - Points: ${best.kills}, Survival: ${best.surviveTime}" )
    println( s"Counters: ${best.world.counters}" )
    println( "" )

    // Print on file
    writer.println( s"${pop.getGenerationNumber};${pop.getMeanFitness};${pop.getBestCandidateFitness};${best.kills};${best.surviveTime}".replace( ".", "," ) )
    writer.flush( )

    // Run the network analysis of the fittest candidate
    new TankBrainTester( best ).runTests( )
  }

}
