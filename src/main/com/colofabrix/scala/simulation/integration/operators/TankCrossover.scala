package com.colofabrix.scala.simulation.integration.operators

import java.util
import java.util.Random

import com.colofabrix.scala.simulation.{Tank, TankChromosome}
import org.uncommons.maths.random.Probability
import org.uncommons.watchmaker.framework.operators.AbstractCrossover

import scala.collection.JavaConversions._

/**
 * Object to perform Tank-Tank crossover
 *
 * Created by Fabrizio on 18/01/2015.
 */
class TankCrossover(crossoverPoints: Int, crossoverProbability: Probability)
extends AbstractCrossover[Tank](crossoverPoints, crossoverProbability) {

  override def mate(t0: Tank, t1: Tank, i: Int, random: Random): util.List[Tank] = {
    val ch0 = t0.chromosome.toList
    val ch1 = t1.chromosome.toList

    // Crossover point
    val crossoverPoint = Math.abs(random.nextInt % (ch0.length - 1) + 1)

    val newCh1 = ch0.take(crossoverPoint) ::: ch1.drop(crossoverPoint)
    val newCh2 = ch1.take(crossoverPoint) ::: ch0.drop(crossoverPoint)

    Seq(
      t0.world.createAndAddTank(TankChromosome(newCh1)),
      t0.world.createAndAddTank(TankChromosome(newCh2))
    )
  }

}
