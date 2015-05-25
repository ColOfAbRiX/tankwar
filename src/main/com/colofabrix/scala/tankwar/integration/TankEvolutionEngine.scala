package com.colofabrix.scala.tankwar.integration

import java.util
import java.util.Random

import com.colofabrix.scala.tankwar.Tank
import org.uncommons.watchmaker.framework.{CandidateFactory, EvolutionaryOperator, FitnessEvaluator, SelectionStrategy}

import scala.collection.JavaConversions._

/**
 * This `EvolutionEngine` takes care of run a competition between
 * all the Tanks after a new generation is created
 */
class TankEvolutionEngine (
  candidateFactory: CandidateFactory[Tank],
  evolutionScheme: EvolutionaryOperator[Tank],
  fitnessEvaluator: FitnessEvaluator[_ >: Tank],
  selectionStrategy: SelectionStrategy[_ >: Tank],
  rng: Random)
extends GameEvolutionEngine[Tank] (candidateFactory, evolutionScheme, fitnessEvaluator, selectionStrategy, rng) {

  /**
   * This method runs the competition between the individuals of the population
   *
   * It first resets the world cleaning everything and the it starts
   * stepping the world until all the rounds have elapsed
   *
   * @param population The population of individuals
   * @return The population of individuals after the competition
   */
  override protected def runCompetition(population: util.List[Tank]): util.List[Tank] = {

    if(population.size == 0)
      return List()

    // Safest way to reference the world is using one of the Tanks
    val world = population.head.world

    // Clean the world for a new start
    world.resetWorld(population.to)

    // Runs the competition
    world.rounds foreach { _ => world.step() }

    world.tanks

  }

}
