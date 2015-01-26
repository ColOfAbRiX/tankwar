package com.colofabrix.scala.tankwar.integration

import java.util

import com.colofabrix.scala.tankwar.Tank
import org.uncommons.watchmaker.framework.FitnessEvaluator

/**
 * Created by Fabrizio on 18/01/2015.
 */
class TankEvaluator() extends FitnessEvaluator[Tank] {

  override def getFitness(t: Tank, list: util.List[_ <: Tank]): Double = {
    /*
    val sum: Double =
      10.0 * (if (t.isDead) -1.0 else 0.0) +
      30.0 * t.kills / (t.world.tanks.size - 1) +
      10.0 * (t.surviveTime / t.world.max_rounds)
    */

    val sum: Double =
      1.0 * t.kills / (t.world.tanks.size - 1)

    //if( t.isDead ) return 0.0
    Math.max(0.0, sum)
  }

  override def isNatural: Boolean = true

}
