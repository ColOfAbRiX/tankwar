/*
 * Copyright (C) 2017 Fabrizio Colonna
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

package com.colofabrix.scala.tankwar.managers

import com.typesafe.scalalogging.LazyLogging

/**
  * Manages world and the progressing of the simulation
  */
object WorldManager extends SimManager[SimulationState] with LazyLogging {

  def manage(): SimAction = scalaz.State {
    // Running simulation
    case state: GraphicSimulation =>
      val stepTimeDelta = state.tsMultiplier * state.cycleDelta

      val nextWorld = if( !state.pause )
        state.world.step(stepTimeDelta)
      else
        state.world

      returnState(state.copy(world = nextWorld))

    // Unexpected cases
    case state =>
      logger.info(s"Unexpected state $state. Doing nothing with it.")
      returnState(state)
  }

}
