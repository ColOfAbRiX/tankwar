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

import scalaz.State
import com.colofabrix.scala.math._
import com.colofabrix.scala.tankwar.Configuration.{ World => WorldConfig }
import com.colofabrix.scala.tankwar.SimState
import com.typesafe.scalalogging.LazyLogging

/**
  * Manages world and the progressing of the simulation
  */
object WorldManager extends Manager[SimState] with LazyLogging {

  def apply(): ManagerAction = State { state =>
    val actions = for {
      nextBodies <- state.physixEngine.step()
    } yield nextBodies

    val result = actions.run(state.world)

    ret(state.copy(world = result._1))
  }

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
