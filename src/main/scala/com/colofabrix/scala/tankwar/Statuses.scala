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

package com.colofabrix.scala.tankwar

import com.colofabrix.scala.geometry.shapes.Box
import com.colofabrix.scala.gfx.Timing
import com.colofabrix.scala.gfx.Timing.TimeState
import com.colofabrix.scala.physix.{ PhysixEngine, World }
import com.colofabrix.scala.tankwar.Configuration.{ Graphics => GfxConfig, World => WorldConfig }

/**
  * Status of the simulation.
  */
final case class SimulationState(
  physixEngine: PhysixEngine,
  world: World,
  timing: TimeState = Timing(),
  display: DisplayOptions = DisplayOptions(),
  pause: Boolean = false
)

/**
  * Display options.
  */
final case class DisplayOptions(
  viewport: Box = WorldConfig.Arena.asBox,
  forceField: Boolean = GfxConfig.showForceField,
  vectors: Boolean = GfxConfig.showVectors,
  useGraphics: Boolean = GfxConfig.enabled
)
