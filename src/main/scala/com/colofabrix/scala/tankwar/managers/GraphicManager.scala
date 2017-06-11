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
import com.colofabrix.scala.gfx.{ GenericRender, _ }
import com.colofabrix.scala.tankwar.Configuration.{ Graphics => GfxConfig }
import com.colofabrix.scala.tankwar.SimState
import com.typesafe.scalalogging.LazyLogging

/**
  * Manages the display of graphics
  */
object GraphicManager extends Manager[SimState] with LazyLogging {

  def apply(): ManagerAction = State { state =>
    // Viewport
    OpenGL.clear()
    OpenGL.setCamera(state.display.viewport)

    // Draw the world force field
    if (state.display.forceField)
      OpenGL.apply(colour = Some(Colour.DARK_GREY)) {
        GenericRender.draw(state.world.forceField)
      }

    // Draw of the world elements
    for (t <- state.world.bodies) {
      // Tank shape
      GenericRender.draw(t.shape)

      // Velocity vector
      if (state.display.vectors) {
        GenericRender.draw(t.velocity, t.position)
      }
    }

    // Draw the boundaries of the arena
    for (b <- state.world.walls)
      OpenGL.apply(Some(Colour.RED)) {
        GenericRender.draw(b)
      }

    // Update display
    OpenGL.update()

    // Time synchronization
    val time = for {
      t <- Timing.sync(GfxConfig.fps, state.world.timeDelta)
    } yield t

    // Update and return
    ret(state.copy(timing = time.run(state.timing)._2))
  }

}
