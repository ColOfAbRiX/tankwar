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

import com.colofabrix.scala.gfx.drawing.ShapeRenderer
import com.colofabrix.scala.gfx.{ OpenGL, Timing }
import com.colofabrix.scala.tankwar.Configuration.{ Simulation => SimConfig }
import com.colofabrix.scala.tankwar.TankWarMain.SimulationState
import com.typesafe.scalalogging.LazyLogging

/**
  * Manages the display of graphics
  */
object GraphicManager extends SimulationManager[SimulationState] with LazyLogging {

  def manage(state: SimulationState): SimulationState = {
    // Viewport
    OpenGL.clear()
    OpenGL.projection(state.viewport)

    // Drawing
    for (t <- state.world.tanks) {
      ShapeRenderer.drawShape(t.shape)
    }
    OpenGL.update()

    // Time synchronization
    val (ns, td) = Timing.sync(
      SimConfig.fps,
      state.tsMultiplier / SimConfig.timeStepMultiplier
    ).run(state.timing)

    // Update
    state.copy(timing = ns, cycleDelta = td)
  }

}
