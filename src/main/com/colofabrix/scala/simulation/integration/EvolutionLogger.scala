package com.colofabrix.scala.simulation.integration

import java.io.PrintWriter

import com.colofabrix.scala.simulation.{Tank, TankBrainTester}
import org.uncommons.watchmaker.framework.{EvolutionObserver, PopulationData}

/**
 * Trivial evolution observer to display information about the population at the end
 * of each generation.
 */
class EvolutionLogger[T <: Tank] extends EvolutionObserver[T] {

  val writer = new PrintWriter("out/population.csv")

  override def populationUpdate(pop: PopulationData[_ <: T]): Unit = {
    val best = pop.getBestCandidate

    val scores = best.world.tanks.map( TankEvaluator.fitness ).toSeq
    val count = scores.length
    val mean = scores.sum / count
    val devs = scores.map(score => (score - mean) * (score - mean))
    val stddev = Math.sqrt(devs.sum / count)

    println( s"Gen #${pop.getGenerationNumber}: Best: ${pop.getBestCandidateFitness}, Mean: $mean, Std Dev: $stddev" )
    println( s"Best - Kills: ${best.kills}, Survival: ${best.surviveTime}" )
    println( s"Counters: ${best.world.counters}" )
    //println( "Best candidate: " + best.definition + " = " + best.brain.toString )
    println( "" )

    writer.println(s"${pop.getGenerationNumber};${pop.getMeanFitness};${pop.getBestCandidateFitness};${best.kills};${best.surviveTime}".replace(".", ","))
    writer.flush()

    new TankBrainTester(best).runTests()
  }

}
