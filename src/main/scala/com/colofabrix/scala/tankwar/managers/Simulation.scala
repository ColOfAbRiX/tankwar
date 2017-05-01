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
import com.colofabrix.scala.tankwar.Configuration.{ Simulation => SimConfig, World => WorldConfig }
import com.colofabrix.scala.tankwar.entities.World

/**
  * Representation of a manager for the simulation
  */
trait SimManager[S <: SimulationState] {
  type SimAction = scalaz.State[S, S]

  def manage(): SimAction

  def returnState(state: S): (S, S) = (state, state)
}

/**
  * Status of the simulation.
  */
sealed trait SimulationState

/**
  * Background simulation, without graphics.
  */
final case class BackgroundSimulation(world: World = World()) extends SimulationState

/**
  * Simulation that includes graphics.
  */
final case class GraphicSimulation(
  world: World = World(),
  viewport: Box = Box(WorldConfig.Arena.width, WorldConfig.Arena.height),
  timing: TimeState = Timing.init(),
  display: DisplayOptions = DisplayOptions(),
  tsMultiplier: Double = SimConfig.timeMultiplier,
  cycleDelta: Double = 0.0,
  pause: Boolean = false
) extends SimulationState

/**
  * Display options.
  */
case class DisplayOptions(
  forceField: Boolean = false,
  vectors: Boolean = false
)