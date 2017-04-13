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

package com.colofabrix.scala.tankwar.simulation

import scala.annotation.tailrec
import com.colofabrix.scala.gfx.opengl.Sync.SyncState
import com.colofabrix.scala.gfx.opengl._
import com.colofabrix.scala.tankwar.Configuration
import com.colofabrix.scala.tankwar.Configuration.{ Simulation => SimConfig }
import org.lwjgl.opengl.Display

final
case class SimulationState(
  timing: SyncState,
  openGl: Boolean,
  world: World,
  fps: Double
)

/**
  * Simulation manager
  */
object Manager {

  /** Start the simulation. */
  def start(state: SimulationState): Unit = {
    val state = SimulationState(
      Sync.init(),
      Configuration.Simulation.gxfEnabled,
      World(),
      SimConfig.fps
    )

    if( Configuration.Simulation.gxfEnabled ) {
      OpenGL.init(800, 600)
    }
  }

  @tailrec
  private
  def run(ow: Option[World]): Unit = ow match {
    case Some(w) => run(w.step())
    case _ =>
  }

  @tailrec
  private
  def run_gfx(ow: Option[World], state: SyncState): Unit = ow match {
    case Some(w) => if( !Display.isCloseRequested ) {
      OpenGL.clear()

      OpenGL.apply(Frame(colour = Colour.RED)) {
        for( t <- w.tanks ) {
          Drawing.drawCircle(t.shape.center, t.shape.radius)
        }
      }

      OpenGL.update()
      val (nextState, timeDelta) = Sync.sync(30).run(state)
      run_gfx(w.step(), nextState)
    }

    case _ =>
  }

}