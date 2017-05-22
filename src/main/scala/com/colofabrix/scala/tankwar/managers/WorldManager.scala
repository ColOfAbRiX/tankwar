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

import com.colofabrix.scala.math.{ XYVect, _ }
import com.colofabrix.scala.physix.PhysixEngine
import com.colofabrix.scala.tankwar.Configuration.{ World => WorldConfig }
import com.colofabrix.scala.tankwar.WorldState
import com.colofabrix.scala.tankwar.entities.Tank
import com.typesafe.scalalogging.LazyLogging

/**
  * Manages world and the progressing of the simulation
  */
object WorldManager extends SimManager with LazyLogging {

  def manage(): SimAction = scalaz.State { state =>
    val worldState = state.worldState

    val newWorldState = worldState.copy(
      bodies = worldState.physix.step(worldState.bodies, WorldConfig.Arena()),
      physix = worldState.physix,
      counter = worldState.counter + 1
    )

    ret(state.copy(worldState = newWorldState))
  }

  /** Creates the World. */
  def apply(physixEngine: PhysixEngine) = WorldState(
    Seq.tabulate(WorldConfig.tankCount) { i =>
      Tank(physics = physixEngine)
    },
    physixEngine
  )

  /** The force field present on the arena, point by point */
  def worldForceField: VectorField = { p =>
    XYVect(
      -75.0 * (p.y / WorldConfig.Arena.height - 0.5),
      -75.0 * (p.x / WorldConfig.Arena.width - 0.5)
    )
  }

  /** The friction present on the arena, point by point */
  def worldFriction: ScalarField = { _ => 0.0 }

}
