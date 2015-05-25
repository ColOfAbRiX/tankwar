package com.colofabrix.scala.tankwar.integration

import java.util

import com.colofabrix.scala.tankwar.{Tank, World}
import org.uncommons.watchmaker.framework.FitnessEvaluator

/**
 * Evaluates the fitness of a Tank
 */
class TankEvaluator() extends FitnessEvaluator[Tank] {

  /**
   * Given a Tank returns its fitness
   *
   * @param t The tank to evaluate
   * @param list The list of all Tanks. It may be useful for comparison purposes
   * @return A number representing the fitness of the Tank
   */
  override def getFitness(t: Tank, list: util.List[_ <: Tank]): Double =
    TankEvaluator.fitness(t)

  override def isNatural: Boolean = true

}

object TankEvaluator {

  /**
   * Returns the fitness of a tank
   *
   * @param t The tank being evaluated
   * @return The score of the tank representing its fitness
   */
  def fitness(t: Tank): Double = {
    val (w, ts) = (t.world, t.world.tanks)
    3.0 * t.kills.toDouble / (ts.length - 1).toDouble +
      3.0 * t.surviveTime.toDouble / w.max_rounds.toDouble +
      1.0 * ts.count(_.isDead).toDouble / (ts.length - 1).toDouble
  }

  /**
   * Returns the highest possible fitness value
   *
   * @param world The world of the simulation
   * @return The highest possible value
   */
  def higherFitness(world: World): Double = {
    val fittest = world.tanks.maxBy(fitness)
    fitness(fittest)
  }
}