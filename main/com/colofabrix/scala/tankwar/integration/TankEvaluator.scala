package com.colofabrix.scala.tankwar.integration

import java.util

import com.colofabrix.scala.tankwar.{Tank, World}
import org.uncommons.watchmaker.framework.FitnessEvaluator

/**
 * Evaluates the fitness of a Tank
 */
class TankEvaluator() extends FitnessEvaluator[Tank] {
  import java.lang.Math._

  /**
   * Given a Tank returns its fitness
   *
   * @param t The tank to evaluate
   * @param list The list of all Tanks. It may be useful for comparison purposes
   * @return A number representing the fitness of the Tank
   */
  override def getFitness(t: Tank, list: util.List[_ <: Tank]): Double = {
    max(0.0, t.kills)
  }

  override def isNatural: Boolean = true

}

object TankEvaluator {
  def higherFitness(world: World): Double = {
    world.tanks.maxBy( t => t.kills ).kills
  }
}