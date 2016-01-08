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

import java.util
import java.util.Random

import com.colofabrix.scala.simulation.{ Tank, TankChromosome }
import org.uncommons.watchmaker.framework.EvolutionaryOperator

import scala.collection.JavaConverters._
import scala.language.postfixOps

/**
 * Abstract operator to mutate a Tank
 *
 * It provides a series of methods that work on the data of a tank and allow the
 * developer to focus on the implementation of the operator logic
 *
 * Created by Fabrizio on 27/01/2015.
 */
abstract class AbstractTankMutation extends EvolutionaryOperator[Tank] {
  /**
   * Constant used for those ranges that don't include the extremities
   */
  val extremityDistance = 1E-6

  @SuppressWarnings( Array("JavaConverters") )
  override def apply( list: util.List[Tank], random: Random ): util.List[Tank] = {
    // Calls mutation rules for each Tank
    list.asScala.map { t ⇒
      val newChromosome = mutateTank( t.chromosome, random )
      t.world.createAndAddTank( newChromosome )
    } asJava
  }

  private def mutateTank( c: TankChromosome, random: Random ): TankChromosome = {
    // Spreads the mutation of a Tank in different small tasks
    new TankChromosome(
      mutateBiases( c, random ),
      mutateWeights( c, random ),
      mutateRotationReference( c, random ),
      mutateSightRatio( c, random ),
      c.valueRange,
      c.activationFunction,
      c.brainBuilder
    )
  }

  /**
   * Mutates a Seq[Seq[Double]] following the specified function
   *
   * @param f The function that mutate the original value
   * @param seq Input sequence of number
   * @param random Random Number generator
   * @return A mutated sequences of numbers
   */
  protected def mutate( f: ( Double, Random ) ⇒ Double, seq: Seq[Seq[Double]], random: Random ): Seq[Seq[Double]] = {
    for ( outer ← seq ) yield {
      for ( value ← outer ) yield f( value, random )
    }
  }

  /**
   * Mutate the biases of a Tank
   *
   * @param c The chromosome used to gather information from the Tank
   * @param random Random number generator
   * @return A new set of biases with applied the mutation rules
   */
  protected def mutateBiases( c: TankChromosome, random: Random ): Seq[Seq[Double]]

  /**
   * Mutate the weights of a Tank
   *
   * @param c The chromosome used to gather information from the Tank
   * @param random Random number generator
   * @return A new set of weights with applied the mutation rules
   */
  protected def mutateWeights( c: TankChromosome, random: Random ): Seq[Seq[Seq[Double]]]

  /**
   * Mutate the reference rotation zero of a Tank
   *
   * @param c The chromosome used to gather information from the Tank
   * @param random Random number generator
   * @return A new rotation zero reference with applied the mutation rules
   */
  protected def mutateRotationReference( c: TankChromosome, random: Random ): Double

  /**
   * Mutate the sight ratio of a Tank
   *
   * @param c The chromosome used to gather information from the Tank
   * @param random Random number generator
   * @return A new sight ratio with applied the mutation rules
   */
  protected def mutateSightRatio( c: TankChromosome, random: Random ): Double

}