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
object GraphicManager extends SimManager[SimulationState] with LazyLogging {

  def manage(): SimAction = scalaz.State {
    case state: GraphicSimulation =>
      // Viewport
      OpenGL.clear()
      OpenGL.setCamera(state.viewport)

      // Draw the world force field
      if( state.display.forceField )
        OpenGL.apply(colour = Some(Colour.DARK_GREY)) {
          GenericRender.draw(state.world.forceField _)
        }

      // Draw of the world elements
      for( t <- state.world.tanks ) {
        // Tank shape
        GenericRender.draw(t.shape)

        // Velocity vector
        if( state.display.vectors ) {
          GenericRender.draw(t.velocity, t.position)
        }
      }

      // Draw the boundaries of the arena
      for( b <- WorldConfig.Arena() )
        OpenGL.apply(Some(Colour.RED)) {
          GenericRender.draw(b)
        }

      // Update display
      OpenGL.update()

      // Time synchronization
      val (ns, td) = Timing.sync(
        SimConfig.fps,
        state.tsMultiplier / SimConfig.timeMultiplier
      ).run(state.timing)

      // Update
      returnState(state.copy(timing = ns, cycleDelta = td))

    // Unexpected cases
    case state =>
      logger.info(s"Unexpected state $state. Doing nothing with it.")
      returnState(state)
  }

}
