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
import java.util.Random

import com.colofabrix.scala.simulation.{Tank, World}
import org.uncommons.watchmaker.framework.factories.AbstractCandidateFactory

import scala.collection.JavaConversions._

/**
 * Factory to generate the initial Tank population.
 *
 * Required by Watchmaker library
 */
class TankFactory(world: World) extends AbstractCandidateFactory[Tank] {

  override def generateInitialPopulation(i: Int, random: Random): util.List[Tank] = {
    (0 until i).foreach( _ => generateRandomCandidate(random) )
    world.tanks
  }

  override def generateInitialPopulation(i: Int, collection: util.Collection[Tank], random: Random): util.List[Tank] = {
    generateInitialPopulation(i, random)
  }

  override def generateRandomCandidate(random: Random): Tank = {
    world.createAndAddDefaultTank(Tank.defaultRandomReader(random))
  }

}
