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

package com.colofabrix.scala.simulation.integration

import java.util

import com.colofabrix.scala.simulation.{ Tank, World }
import org.uncommons.watchmaker.framework.FitnessEvaluator

/**
 * Evaluates the fitness of a Tank
 */
class TankEvaluator() extends FitnessEvaluator[Tank] {

  /**
   * Given a Tank returns its fitness
   *
   * @param t The tank to evaluate
   * @param list The list of all Tanks. It may be useful for comparison purposes
   * @return A number representing the fitness of the Tank
   */
  override def getFitness( t: Tank, list: util.List[_ <: Tank] ): Double = TankEvaluator.fitness( t )

  override def isNatural: Boolean = true

}

object TankEvaluator {

  import Math._

  /**
   * Returns the fitness of a tank
   *
   * The current algorithm count the number of kills relative to the total number of Tanks
   *
   * @param t The tank being evaluated
   * @return The score of the tank representing its fitness
   */
  def fitness( t: Tank ): Double = {
    val ( w, ts ) = ( t.world, t.world.tanks )
    max( t.points, 0.0 )
  }

  /**
   * Returns the highest possible fitness value
   *
   * @param world The world of the simulation
   * @return The highest possible value
   */
  def higherFitness( world: World ): Double = {
    world.tanks.filter( !_.isDead ).maxBy( _.points ).points
  }

}