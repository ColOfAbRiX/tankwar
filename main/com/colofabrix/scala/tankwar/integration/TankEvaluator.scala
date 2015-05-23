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
  override def getFitness(t: Tank, list: util.List[_ <: Tank]): Double = {
    val (w, ts) = (t.world, t.world.tanks)
    3.0 * t.kills.toDouble / (ts.length - 1).toDouble +
      2.0 * t.surviveTime.toDouble / w.max_rounds.toDouble +
      1.0 * ts.count(_.isDead).toDouble / (ts.length - 1).toDouble
  }

  override def isNatural: Boolean = true

}

object TankEvaluator {
  def higherFitness(world: World): Double = {
    3.0 + 2.0 + 1.0
  }
}