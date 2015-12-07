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
import org.uncommons.maths.number.NumberGenerator
import org.uncommons.maths.random.Probability

/**
 * A drift mutation means that if a value has to change the new value is
 * a modification of the old one, a drift from it, by a random value generated
 * by a proper distribution generator
 */
class TankDriftMutation( probability: Probability, generator: NumberGenerator[java.lang.Double] )
  extends TankFullMutation( probability ) {

  /**
   * Rules that defines how to mutate
   *
   * This rule works like this: for every value there is a specific `probability` that it mutates.
   * If the value mutates, then the new value a drift from the old value and there are no random
   * possibilities. Usually the new value should have higher probability to fall near the old value
   * with a Gaussian distribution, but this is up to the developer
   *
   * @param scale Range of the random values, in [-scale, scale]
   * @param x Value that possibly mutates
   * @param rng Random number generator
   * @return The old value or a new mutated value
   */
  override def mutationRule( scale: Double )( x: Double, rng: Random ) = {
    if( rng.nextDouble <= probability.doubleValue ) {
      x + generator.nextValue
    }
    else {
      x
    }
  }

  /**
   * Mutate the sight ratio of a Tank
   *
   * @param c The chromosome used to gather information from the Tank
   * @param random Random number generator
   * @return A new sight ratio with applied the mutation rules
   */
  override def mutateSightRatio( c: TankChromosome, random: Random ) = {
    Math.max( Math.min( mutationRule( 1.0 )( c.sightRatio, random ), 1.0 - extremityDistance ), extremityDistance )
  }

}