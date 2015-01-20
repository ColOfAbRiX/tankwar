package com.colofabrix.scala.tankwar.integration

import java.util
import java.util.Random

import com.colofabrix.scala.tankwar.Tank
import org.uncommons.maths.random.Probability
import org.uncommons.watchmaker.framework.operators.AbstractCrossover

import scala.collection.JavaConversions._

/**
 * Created by Fabrizio on 18/01/2015.
 */
class TankCrossover(crossoverPoints: Int, crossoverProbability: Probability)
extends AbstractCrossover[Tank](crossoverPoints, crossoverProbability) {

  override def mate(t: Tank, t1: Tank, i: Int, random: Random): util.List[Tank] = {
    // TODO: Does nothing at the moment. To be completed
    val firstHalf = List.fill(i / 2)(t)
    val secondHalf = List.fill(i / 2)(t1)

    (firstHalf ::: secondHalf ::: Nil).take(i)
  }

}
