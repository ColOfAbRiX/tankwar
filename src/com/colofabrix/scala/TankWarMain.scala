package com.colofabrix.scala

import java.util
import java.util.Random

import com.colofabrix.scala.neuralnetwork.builders.RandomReader
import com.colofabrix.scala.tankwar.{Tank, World}
import org.uncommons.maths.random.{MersenneTwisterRNG, Probability}
import org.uncommons.watchmaker.framework._
import org.uncommons.watchmaker.framework.factories.AbstractCandidateFactory
import org.uncommons.watchmaker.framework.operators.{AbstractCrossover, EvolutionPipeline}
import org.uncommons.watchmaker.framework.selection.RouletteWheelSelection

import scala.collection.JavaConversions._

/**
 * Main game class
 *
 * Created by Fabrizio on 29/09/2014.
 */
object TankWarMain {
  def main( args: Array[String] ) {

    val pipeline = new EvolutionPipeline[Tank](List(
      new TankMutation(),
      new TankCrossover(1, null)
    ))

    val engine = new GenerationalEvolutionEngine[Tank](
      new TankFactory(),
      pipeline,
      new TankEvaluator(),
      new RouletteWheelSelection(),
      new MersenneTwisterRNG()
    )

    val world = new World( tanks_count = 5 )
    (1 to 500) foreach { _ => world.step() }

  }
}

class TankFactory() extends AbstractCandidateFactory[Tank] {
  override def generateInitialPopulation(i: Int, random: Random): util.List[Tank] = {
    val reader = new RandomReader(1.0, random)
    null
  }

  override def generateInitialPopulation(i: Int, collection: util.Collection[Tank], random: Random): util.List[Tank] = ???

  override def generateRandomCandidate(random: Random): Tank = ???
}

class TankMutation extends EvolutionaryOperator[Tank] {
  override def apply(list: util.List[Tank], random: Random): util.List[Tank] = ???
}

class TankCrossover(crossoverPoints: Int, crossoverProbability: Probability)
extends AbstractCrossover[Tank](crossoverPoints, crossoverProbability) {
  override def mate(t: Tank, t1: Tank, i: Int, random: Random): util.List[Tank] = ???
}

class TankEvaluator() extends FitnessEvaluator[Tank] {
  override def getFitness(t: Tank, list: util.List[_ <: Tank]): Double = ???

  override def isNatural: Boolean = ???
}
