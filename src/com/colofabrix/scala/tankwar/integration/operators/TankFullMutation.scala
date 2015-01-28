package com.colofabrix.scala.tankwar.integration.operators

import java.util.Random

import com.colofabrix.scala.tankwar.TankChromosome
import org.uncommons.maths.random.Probability

/**
 * Full Mutation means that if a gene has to mutate its value will change
 * completely and it will not be related to the old value. The new value will
 * only be related to a Tank's scale
 */
class TankFullMutation(probability: Probability) extends AbstractTankMutation {

  /**
   * Rules that defines how to mutate
   *
   * This rule works like this: for every value there is a specific `probability` that it mutates.
   * If the value mutates, then generate a completely new random value between the known
   * boundaries of `scale`.
   *
   * @param scale Range of the random values, in [-scale, scale]
   * @param x Value that possibly mutates
   * @param rng Random number generator
   * @return The old value or a new mutated value
   */
  protected def mutationRule(scale: Double)(x: Double, rng: Random) = {
    if( rng.nextDouble <= probability.doubleValue )
      rng.nextDouble * 2.0 * scale - scale
    else x
  }

  /**
   * Mutate the biases of a Tank
   *
   * @param c The chromosome used to gather information from the Tank
   * @param random Random number generator
   * @return A new set of biases with applied the mutation rules
   */
  override def mutateBiases(c: TankChromosome, random: Random) =
    mutate(
      mutationRule(c.valueRange),
      c.biases,
      random)

  /**
   * Mutate the weights of a Tank
   *
   * @param c The chromosome used to gather information from the Tank
   * @param random Random number generator
   * @return A new set of weights with applied the mutation rules
   */
  override def mutateWeights(c: TankChromosome, random: Random) =
    for( layer <- c.weights ) yield
      mutate(
        mutationRule(c.valueRange),
        layer,
        random)

  /**
   * Mutate the sight of a Tank
   *
   * @param c The chromosome used to gather information from the Tank
   * @param random Random number generator
   * @return A new `TankSight` object with applied the mutation rules
   */
  override def mutateSight(c: TankChromosome, random: Random) = c.sight

  /**
   * Mutate the mass of a Tank
   *
   * @param c The chromosome used to gather information from the Tank
   * @param random Random number generator
   * @return A new mass with applied the mutation rules
   */
  override def mutateMass(c: TankChromosome, random: Random) = random.nextDouble * 5.0
}
