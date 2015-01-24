package com.colofabrix.scala.tankwar.integration

import java.util
import java.util.Random

import com.colofabrix.scala.neuralnetwork.builders.SeqDataReader
import com.colofabrix.scala.tankwar.{Tank, TankChromosome, World}
import org.uncommons.maths.random.Probability
import org.uncommons.watchmaker.framework.EvolutionaryOperator

import scala.collection.JavaConversions._

/**
 * Full Mutation means that if a gene has to mutate its value will change
 * completely and it will not be related to the old value. The new value will
 * only be related to a Tank's scale
 */
class TankFullMutation(probability: Probability) extends EvolutionaryOperator[Tank] {

  override def apply(list: util.List[Tank], random: Random): util.List[Tank] =
    list.map { t => mutateTank(t.world, t.chromosome, random) }

  private def mutateTank(world: World, c: TankChromosome, random: Random): Tank = {
    val newBiases = mutateBiases( c, random )
    val newWeights = mutateWeights( c, random )

    val reader = new SeqDataReader(newBiases, newWeights, c.af)

    world.createAndAddDefaultTank(reader)
  }

  private def mutateBiases(c: TankChromosome, random: Random) =
    mutateSeq(c.biases, random, c.valueRange)

  private def mutateWeights(c: TankChromosome, random: Random) =
    for( layer <- c.weights ) yield mutateSeq(layer, random, c.valueRange)

  protected def mutateSeq(seq: Seq[Seq[Double]], rnd: Random, scale: Double): Seq[Seq[Double]] = {
    for( outer <- seq ) yield {
      for( value <- outer ) yield {
        if( rnd.nextDouble <= probability.doubleValue )
          rnd.nextDouble * 2.0 + scale - scale
        else
          value
      }
    }
  }

}
