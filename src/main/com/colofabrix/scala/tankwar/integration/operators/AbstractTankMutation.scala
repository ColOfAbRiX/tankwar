package com.colofabrix.scala.tankwar.integration.operators

import java.util
import java.util.Random

import com.colofabrix.scala.tankwar.{Tank, TankChromosome}
import org.uncommons.watchmaker.framework.EvolutionaryOperator

import scala.collection.JavaConversions._

/**
 * Abstract operator to mutate a Tank
 *
 * It provides a series of methods that work on the data of a tank and allow the
 * developer to focus on the implementation of the operator logic
 *
 * Created by Fabrizio on 27/01/2015.
 */
abstract class AbstractTankMutation extends EvolutionaryOperator[Tank] {

  override def apply(list: util.List[Tank], random: Random): util.List[Tank] = {
    // Calls mutation rules for each Tank
    list.map { t =>
      val newChromosome = mutateTank(t.chromosome, random)
      t.world.createAndAddTank(newChromosome)
    }
  }

  private def mutateTank(c: TankChromosome, random: Random): TankChromosome = {
    // Spreads the mutation of a Tank in different small tasks
    new TankChromosome(
      mutateBiases(c, random),
      mutateWeights(c, random),
      mutateRotationReference(c, random),
      mutateSightRatio(c, random),
      c.valueRange,
      c.activationFunction,
      c.brainBuilder)
  }

  /**
   * Mutates a Seq[Seq[Double]] following the specified function
   *
   * @param f The function that mutate the original value
   * @param seq Input sequence of number
   * @param random Random Number generator
   * @return A mutated sequences of numbers
   */
  protected def mutate(f: (Double, Random) => Double, seq: Seq[Seq[Double]], random: Random): Seq[Seq[Double]] = {
    for( outer <- seq ) yield {
      for( value <- outer ) yield f(value, random)
    }
  }

  /**
   * Mutate the biases of a Tank
   *
   * @param c The chromosome used to gather information from the Tank
   * @param random Random number generator
   * @return A new set of biases with applied the mutation rules
   */
  protected def mutateBiases(c: TankChromosome, random: Random): Seq[Seq[Double]]

  /**
   * Mutate the weights of a Tank
   *
   * @param c The chromosome used to gather information from the Tank
   * @param random Random number generator
   * @return A new set of weights with applied the mutation rules
   */
  protected def mutateWeights(c: TankChromosome, random: Random): Seq[Seq[Seq[Double]]]

  /**
   * Mutate the reference rotation zero of a Tank
   *
   * @param c The chromosome used to gather information from the Tank
   * @param random Random number generator
   * @return A new rotation zero reference with applied the mutation rules
   */
  protected def mutateRotationReference(c: TankChromosome, random: Random): Double

  /**
   * Mutate the sight ratio of a Tank
   *
   * @param c The chromosome used to gather information from the Tank
   * @param random Random number generator
   * @return A new sight ratio with applied the mutation rules
   */
  protected def mutateSightRatio(c: TankChromosome, random: Random): Double

}
