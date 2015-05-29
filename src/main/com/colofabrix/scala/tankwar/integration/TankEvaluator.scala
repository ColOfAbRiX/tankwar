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
   * The current algorithm count the number of kills relative to the total number of Tanks
   *
   * @param t The tank being evaluated
   * @return The score of the tank representing its fitness
   */
  def fitness(t: Tank): Double = {
    val (w, ts) = (t.world, t.world.tanks)

    2.0 * t.kills.toDouble / ts.length.toDouble +
      3.0 * t.surviveTime.toDouble / w.max_rounds.toDouble
  }

  /**
   * Returns the highest possible fitness value
   *
   * @param world The world of the simulation
   * @return The highest possible value
   */
  def higherFitness(world: World): Double = {
    //fitness(world.tanks.maxBy(fitness))
    5.0
  }

}