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

import java.util
import java.util.Random

import com.colofabrix.scala.simulation.Tank
import org.uncommons.watchmaker.framework.{ CandidateFactory, EvolutionaryOperator, FitnessEvaluator, SelectionStrategy }

import scala.collection.JavaConversions._

/**
 * This `EvolutionEngine` takes care of run a competition between
 * all the Tanks after a new generation is created
 */
class TankEvolutionEngine(
  candidateFactory: CandidateFactory[Tank],
  evolutionScheme: EvolutionaryOperator[Tank],
  fitnessEvaluator: FitnessEvaluator[_ >: Tank],
  selectionStrategy: SelectionStrategy[_ >: Tank],
  rng: Random )
  extends GameEvolutionEngine[Tank]( candidateFactory, evolutionScheme, fitnessEvaluator, selectionStrategy, rng ) {

  /**
   * This method runs the competition between the individuals of the population
   *
   * It first resets the world cleaning everything and the it starts
   * stepping the world until all the rounds have elapsed
   *
   * @param population The population of individuals
   * @return The population of individuals after the competition
   */
  override protected def runCompetition( population: util.List[Tank] ): util.List[Tank] = {

    if( population.size == 0 ) {
      return List( )
    }

    // Safest way to reference the world is using one of the Tanks
    val world = population.head.world

    // Clean the world for a new start
    world.resetWorld( population.to )

    // Runs the competition
    world.rounds foreach { _ => world.step( )}

    // Returns the population
    world.tanks

  }

}
