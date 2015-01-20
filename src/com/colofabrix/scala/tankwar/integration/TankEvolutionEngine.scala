package com.colofabrix.scala.tankwar.integration

import java.util
import java.util.Random

import com.colofabrix.scala.tankwar.Tank
import org.uncommons.watchmaker.framework.{CandidateFactory, EvolutionaryOperator, FitnessEvaluator, SelectionStrategy}

import scala.collection.JavaConversions._

/**
 * Created by Fabrizio on 20/01/2015.
 */
class TankEvolutionEngine (candidateFactory: CandidateFactory[Tank],
                           evolutionScheme: EvolutionaryOperator[Tank],
                           fitnessEvaluator: FitnessEvaluator[_ >: Tank],
                           selectionStrategy: SelectionStrategy[_ >: Tank],
                           rng: Random)
extends GameEvolutionEngine[Tank] (candidateFactory, evolutionScheme, fitnessEvaluator, selectionStrategy, rng) {

  override protected def runCompetition(population: util.List[Tank]): util.List[Tank] = {

    if(population.size == 0)
      return List()

    // Safest way to reference the world is using one of the Tanks
    val world = population.head.world
    // Clean the world for a new start
    world.resetWorld(population.to)

    println( "Current population: " )
    world.tanks.foreach { t => println(t.brain) }

    val t0 = System.nanoTime()
    world.rounds foreach { _ => world.step() }
    val t1 = System.nanoTime()

    println("Elapsed time: " + (t1 - t0) / 1000000 + "ms")

    world.tanks

  }

}
