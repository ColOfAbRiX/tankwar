package com.colofabrix.scala

import java.io.PrintWriter

import com.colofabrix.scala.geometry.shapes.Box
import com.colofabrix.scala.math.Vector2D
import com.colofabrix.scala.tankwar.integration._
import com.colofabrix.scala.tankwar.integration.operators.TankFullMutation
import com.colofabrix.scala.tankwar.{BrainInputHelper, Tank, TankBrainTester, World}
import org.uncommons.maths.random.{MersenneTwisterRNG, Probability}
import org.uncommons.watchmaker.framework.operators.EvolutionPipeline
import org.uncommons.watchmaker.framework.selection.TournamentSelection
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

    // Create a new world where to run the Tanks
    val world = new World(max_rounds = 1000, arena = Box( Vector2D.new_xy(0, 0), Vector2D.new_xy(1900, 900) ))

    // Mutation pipeline
    val pipeline = new EvolutionPipeline[Tank](
      List(
        // A very small mutation from the current values is applied frequently
        //new TankDriftMutation(new Probability(0.2), new GaussianGenerator(0, 1.0 / (2.96 * 10), new MersenneTwisterRNG())),
        // A less small drift is applied less frequently
        //new TankDriftMutation(new Probability(0.1), new GaussianGenerator(0, 1.0 / 2.96, new MersenneTwisterRNG())),
        // Every so and then a value is changed completely
        new TankFullMutation(new Probability(0.25))
        // Crossover between tanks
        //new TankCrossover(1, new Probability(0.1))
      )
    )

    // Evolutionary engine
    val engine = new TankEvolutionEngine(
      new TankFactory(world),
      pipeline,
      new TankEvaluator(),
      new TournamentSelection(new Probability(0.85)),
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

  val writer = new PrintWriter("population.csv")

  override def populationUpdate(populationData: PopulationData[_ <: T]): Unit = {
    val bestTank = populationData.getBestCandidate

    println( s"Generation ${populationData.getGenerationNumber}: ${populationData.getBestCandidateFitness}(${bestTank.kills}+${bestTank.surviveTime}), ${populationData.getMeanFitness}" )
    println( "Best candidate: " + bestTank.definition + " = " + bestTank.brain.toString )
    println( "" )

    writer.println(s"${populationData.getGenerationNumber};${populationData.getMeanFitness};${populationData.getBestCandidateFitness};${bestTank.kills};${bestTank.surviveTime}".replace(".", ","))
    writer.flush()

    new TankBrainTester(bestTank, BrainInputHelper.count).runTests()
  }

}