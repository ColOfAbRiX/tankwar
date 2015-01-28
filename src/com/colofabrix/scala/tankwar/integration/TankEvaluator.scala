package com.colofabrix.scala.tankwar.integration

import java.util

import com.colofabrix.scala.tankwar.Tank
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
  override def getFitness(t: Tank, list: util.List[_ <: Tank]): Double = {
    // This value if meant to optimize:
    //   - Kills: the more a Tank kills the more it is fit
    //   - Survival: A tank that doesn't kill a lot but survives a lot is equally valued
    1.0 * t.kills / (t.world.tanks.size - 1) +
    1.0 * (t.surviveTime / t.world.max_rounds)
  }

  override def isNatural: Boolean = true

}
