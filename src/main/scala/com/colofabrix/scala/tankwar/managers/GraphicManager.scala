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

import com.colofabrix.scala.drawing.GenericRender
import com.colofabrix.scala.gfx._
import com.colofabrix.scala.tankwar.Configuration.{ Simulation => SimConfig, World => WorldConfig }
import com.typesafe.scalalogging.LazyLogging

/**
  * Manages the display of graphics
  */
object GraphicManager extends SimManager[SimState] with LazyLogging {

  def manage(state: SimState): SimState = {
    // Viewport
    OpenGL.clear()
    OpenGL.projection(state.viewport, WorldConfig.Arena.asBox)

    // Force field
    if( state.displayForceField ) {
      OpenGL.apply(colour = Some(Colour.DARK_GREY)) {
        GenericRender.draw(state.world.forceField _)
      }
    }

    // Drawing of the world elements
    for( t <- state.world.tanks ) {
      // Tank shape
      GenericRender.draw(t.shape)

      // Velocity vector
      if( state.displayVectors ) {
        GenericRender.draw(t.velocity, t.position)
      }
    }

    // Arena boundaries
    for( b <- WorldConfig.Arena() ) {
      OpenGL.apply(Some(Colour.RED)) {
        GenericRender.draw(b)
      }
    }

    // Update display
    OpenGL.update()

    // Time synchronization
    val (ns, td) = Timing.sync(
      SimConfig.fps,
      state.tsMultiplier / SimConfig.timeMultiplier
    ).run(state.timing)

    // Update
    state.copy(timing = ns, cycleDelta = td)
  }

}
