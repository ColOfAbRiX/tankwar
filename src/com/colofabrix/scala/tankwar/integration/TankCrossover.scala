package com.colofabrix.scala.tankwar.integration

import java.util
import java.util.Random

import com.colofabrix.scala.neuralnetwork.builders.SeqDataReader
import com.colofabrix.scala.tankwar.Tank
import org.uncommons.maths.random.Probability
import org.uncommons.watchmaker.framework.operators.AbstractCrossover

import scala.collection.JavaConversions._

/**
 * Created by Fabrizio on 18/01/2015.
 */
class TankCrossover(crossoverPoints: Int, crossoverProbability: Probability)
extends AbstractCrossover[Tank](crossoverPoints, crossoverProbability) {

  override def mate(t0: Tank, t1: Tank, i: Int, random: Random): util.List[Tank] = {
    val reader0 = new SeqDataReader(t0.chromosome.biases, t1.chromosome.weights, t0.chromosome.activationFunction)
    val reader1 = new SeqDataReader(t1.chromosome.biases, t0.chromosome.weights, t0.chromosome.activationFunction)

    Seq(
      t0.world.createAndAddDefaultTank(reader0),
      t0.world.createAndAddDefaultTank(reader1)
    )
  }

}
