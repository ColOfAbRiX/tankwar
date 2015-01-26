package com.colofabrix.scala

import java.io.{File, PrintWriter}

import com.colofabrix.scala.tankwar.integration._
import com.colofabrix.scala.tankwar.{Tank, World}
import org.uncommons.maths.random.{GaussianGenerator, MersenneTwisterRNG, Probability}
import org.uncommons.watchmaker.framework.operators.EvolutionPipeline
import org.uncommons.watchmaker.framework.selection.SigmaScaling
import org.uncommons.watchmaker.framework.termination.GenerationCount
import org.uncommons.watchmaker.framework.{EvolutionObserver, PopulationData}

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
        new TankFullMutation(new Probability(0.05)),
        new TankDriftMutation(new Probability(0.4), new GaussianGenerator(0, 0.05, new MersenneTwisterRNG())),
        new TankCrossover(2, new Probability(0.3))
      )
    )

    // Evolutionary engine
    val engine = new TankEvolutionEngine(
      new TankFactory(world),
      pipeline,
      new TankEvaluator(),
      new SigmaScaling(),
      new MersenneTwisterRNG()
    )

    engine.addEvolutionObserver(new EvolutionLogger)

    // Run the simulation
    engine.evolve(50, 2, new GenerationCount(2000))
  }
}

/**
 * Trivial evolution observer for displaying information at the end
 * of each generation.
 */
class EvolutionLogger[T <: Tank] extends EvolutionObserver[T] {
  val writer = new PrintWriter(new File("""out/population.csv"""))

  override def populationUpdate(populationData: PopulationData[_ <: T]): Unit = {
    println( s"Generation ${populationData.getGenerationNumber}: ${populationData.getBestCandidateFitness}, ${populationData.getMeanFitness}" )
    println( "Best candidate: " + populationData.getBestCandidate.brain.toString )

    writer.println(s"${populationData.getMeanFitness},${populationData.getBestCandidateFitness}")
    writer.flush
  }
}