/*
 * Copyright (C) 2015 Fabrizio Colonna
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language
 * governing permissions and limitations under the License.
 */

package com.colofabrix.scala.simulation.integration.operators

import java.util.Random

import com.colofabrix.scala.simulation.TankChromosome
import org.uncommons.maths.random.Probability

/**
 * Full Mutation means that if a gene has to mutate its value will change
 * completely and it will not be related to the old value. The new value will
 * only be related to a Tank's scale
 */
class TankFullMutation( probability: Probability ) extends AbstractTankMutation {

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
  protected def mutationRule( scale: Double )( x: Double, rng: Random ) = {
    if( rng.nextDouble <= probability.doubleValue ) {
      rng.nextDouble * 2.0 * scale - scale
    }
    else {
      x
    }
  }

  /**
   * Mutate the biases of a Tank
   *
   * @param c The chromosome used to gather information from the Tank
   * @param random Random number generator
   * @return A new set of biases with applied the mutation rules
   */
  override def mutateBiases( c: TankChromosome, random: Random ) =
    mutate(
      mutationRule( c.valueRange ),
      c.biases,
      random
    )

  /**
   * Mutate the weights of a Tank
   *
   * @param c The chromosome used to gather information from the Tank
   * @param random Random number generator
   * @return A new set of weights with applied the mutation rules
   */
  override def mutateWeights( c: TankChromosome, random: Random ) =
    for( layer <- c.weights ) yield
    mutate(
      mutationRule( c.valueRange ),
      layer,
      random
    )

  /**
   * Mutate the reference rotation zero of a Tank
   *
   * @param c The chromosome used to gather information from the Tank
   * @param random Random number generator
   * @return A new mass with applied the mutation rules
   */
  override def mutateRotationReference( c: TankChromosome, random: Random ) = mutationRule( c.valueRange )( c.rotationRef, random )

  /**
   * Mutate the sight ratio of a Tank
   *
   * @param c The chromosome used to gather information from the Tank
   * @param random Random number generator
   * @return A new sight ratio with applied the mutation rules
   */
  protected def mutateSightRatio( c: TankChromosome, random: Random ) = {
    // The sight ration doesn't use {mutationRule} because its value is not centered in zero
    if( random.nextDouble <= probability.doubleValue ) {
      random.nextDouble * (1.0 - 2.0 * extremityDistance) + extremityDistance
    }
    else {
      c.sightRatio
    }
  }
}