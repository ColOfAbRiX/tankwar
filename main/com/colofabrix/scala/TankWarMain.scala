package com.colofabrix.scala

import java.io.{File, PrintWriter}

import com.colofabrix.scala.geometry.shapes.Box
import com.colofabrix.scala.math.Vector2D
import com.colofabrix.scala.tankwar.integration._
import com.colofabrix.scala.tankwar.integration.operators.{TankCrossover, TankDriftMutation, TankFullMutation}
import com.colofabrix.scala.tankwar.{Tank, TankBrainTester, World}
import org.uncommons.maths.random.{GaussianGenerator, MersenneTwisterRNG, Probability}
import org.uncommons.watchmaker.framework.operators.EvolutionPipeline
import org.uncommons.watchmaker.framework.selection.SigmaScaling
import org.uncommons.watchmaker.framework.termination._
import org.uncommons.watchmaker.framework.{EvolutionObserver, PopulationData}

import scala.collection.JavaConversions._

/**
 * Main game class
 *
 * Created by Fabrizio on 29/09/2014.
 */
object TankWarMain {
  def main( args: Array[String] ) {

    // Clean analysis files
    for {
      files <- Option(new File("./out/").listFiles)
      file <- files if file.getName.endsWith(".csv")
    } file.delete()

    // Create a new world where to run the Tanks
    val world = new World( max_rounds = 1000, arena = Box( Vector2D.new_xy(0, 0), Vector2D.new_xy(1900, 900) ))

    // Mutation pipeline
    val pipeline = new EvolutionPipeline[Tank](
      List(
        // A very small mutation from the current values is applied frequently
        //new TankDriftMutation(new Probability(0.2), new GaussianGenerator(0, 1.0 / (2.96 * 10), new MersenneTwisterRNG())),
        // A less small drift is applied less frequently
        new TankDriftMutation(new Probability(0.02), new GaussianGenerator(0, 1.0 / 2.96, new MersenneTwisterRNG())),
        // Every so and then a value is changed completely
        new TankFullMutation(new Probability(0.01)),
        // Crossover between tanks
        new TankCrossover(1, new Probability(0.002))
      )
    )

    // Evolutionary engine
    val engine = new TankEvolutionEngine(
      new TankFactory(world),
      pipeline,
      new TankEvaluator(),
      //new TournamentSelection(new Probability(0.85)),
      //new RouletteWheelSelection(),
      new SigmaScaling(),
      new MersenneTwisterRNG()
    )

    engine.addEvolutionObserver(new EvolutionLogger)

    // Run the simulation and stop for stagnation
    engine.evolve(16, 0, new Stagnation(1000, true))
  }
}

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
    println( "" )

    writer.println(s"${pop.getGenerationNumber};${pop.getMeanFitness};${pop.getBestCandidateFitness};${best.kills};${best.surviveTime}".replace(".", ","))
    writer.flush()

    new TankBrainTester(best).runTests()
  }

}