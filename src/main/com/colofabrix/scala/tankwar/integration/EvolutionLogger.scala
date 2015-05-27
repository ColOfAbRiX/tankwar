package com.colofabrix.scala.tankwar.integration

import java.io.PrintWriter

import com.colofabrix.scala.tankwar.{Tank, TankBrainTester}
import org.uncommons.watchmaker.framework.{EvolutionObserver, PopulationData}

/**
 * Trivial evolution observer to display information about the population at the end
 * of each generation.
 */
class EvolutionLogger[T <: Tank] extends EvolutionObserver[T] {

  val writer = new PrintWriter("out/population.csv")

  override def populationUpdate(pop: PopulationData[_ <: T]): Unit = {
    val best = pop.getBestCandidate

    println( s"Gen #${pop.getGenerationNumber}: ${pop.getBestCandidateFitness}/${pop.getMeanFitness} - ${best.kills}/${best.surviveTime}/${best.world.tanks.count(!_.isDead)}, " )
    println( "Best candidate: " + best.definition + " = " + best.brain.toString )
    println( "Counters: " + best.world.counters )
    println( "" )

    writer.println(s"${pop.getGenerationNumber};${pop.getMeanFitness};${pop.getBestCandidateFitness};${best.kills};${best.surviveTime}".replace(".", ","))
    writer.flush()

    new TankBrainTester(best).runTests()
  }

}
