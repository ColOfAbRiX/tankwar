package com.colofabrix.scala

import com.colofabrix.scala.tankwar.integration._
import com.colofabrix.scala.tankwar.{Tank, World}
import org.uncommons.maths.random.{MersenneTwisterRNG, Probability}
import org.uncommons.watchmaker.framework.operators.EvolutionPipeline
import org.uncommons.watchmaker.framework.selection.RouletteWheelSelection
import org.uncommons.watchmaker.framework.termination.GenerationCount

import scala.collection.JavaConversions._

/**
 * Main game class
 *
 * Created by Fabrizio on 29/09/2014.
 */
object TankWarMain {
  def main( args: Array[String] ) {

    // Create a new world where to run the Tanks
    val world = new World()

    // Mutation pipeline
    val pipeline = new EvolutionPipeline[Tank](
      List(
        new TankFullMutation(new Probability(0.2))//,
        //new TankCrossover(1, new Probability(0.1))
      )
    )

    // Evolutionary engine
    val engine = new TankEvolutionEngine(
      new TankFactory(world),
      pipeline,
      new TankEvaluator(),
      new RouletteWheelSelection(),
      new MersenneTwisterRNG()
    )

    // Run the simulation
    engine.evolve(10, 0, new GenerationCount(2))
  }
}

