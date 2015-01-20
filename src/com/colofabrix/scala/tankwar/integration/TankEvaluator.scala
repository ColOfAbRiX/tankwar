package com.colofabrix.scala.tankwar.integration

import java.util

import com.colofabrix.scala.tankwar.Tank
import org.uncommons.watchmaker.framework.FitnessEvaluator

/**
 * Created by Fabrizio on 18/01/2015.
 */
class TankEvaluator() extends FitnessEvaluator[Tank] {

  override def getFitness(t: Tank, list: util.List[_ <: Tank]): Double = {
    // By convention, every entity must contribute with a
    // modifier in the range [1.0, -1.0]
    val sum =
      if (t.isDead) -1.0 else 0.0 +
      1.0 * t.kills / (t.world.tanks.size - 1) +
      1.0 * (t.surviveTime / t.world.max_rounds)

    Math.max(0.0, sum)
  }

  override def isNatural: Boolean = true

}
