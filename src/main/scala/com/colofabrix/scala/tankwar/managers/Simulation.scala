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

import com.colofabrix.scala.geometry.shapes.Box
import com.colofabrix.scala.gfx.Timing
import com.colofabrix.scala.gfx.Timing.TimeState
import com.colofabrix.scala.math._
import com.colofabrix.scala.tankwar.Configuration.{ Simulation => SimConfig, World => WorldConfig }
import com.colofabrix.scala.tankwar.entities.World


/**
  * Representation of a manager for the simulation
  */
trait SimManager[S] {
  def manage(state: S): S
}

/**
  * Status of the simulation.
  */
final case class SimState(

  viewport: Box = Box(WorldConfig.Arena.width, WorldConfig.Arena.height),
  pause: Boolean = false,
  world: World = World(),
  timing: TimeState = Timing.init(),
  tsMultiplier: Double = SimConfig.timeMultiplier,
  cycleDelta: Double = 0.0,
  displayForceField: Boolean = false,
  displayVectors: Boolean = false

) {

  override def toString: String = {
    s"viewport=$viewport, " +
      s"pause=$pause, " +
      s"timing=$timing, " +
      s"tsMultiplier=${tsMultiplier.sig() }, " +
      s"cycleDelta=${cycleDelta.sig() }" +
      s"displayForceField=$displayForceField" +
      s"displayForceField=$displayVectors"
  }

}
