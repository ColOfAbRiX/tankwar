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
import com.typesafe.scalalogging.LazyLogging
import org.lwjgl.input.Keyboard._
import org.lwjgl.opengl.Display

/**
  * Simulation manager
  */
object Manager extends LazyLogging {

  sealed
  case class SimulationState(
    timing: SyncState,
    world: Option[World],
    fps: Int,
    timeDelta: Double
  )

  /** Start the simulation. */
  def start(): Unit = {
    val initial_state = SimulationState(
      Sync.init(),
      Some(World()),
      SimConfig.fps,
      0.0
    )

    if( Configuration.Simulation.gxfEnabled ) {
      // Run with graphics
      OpenGL.init(800, 600)
      run_gfx(initial_state)
      OpenGL.destroy()
    }
    else {
      // Quick run without graphics
      run(initial_state)
    }
  }

  @tailrec
  private
  def run(state: SimulationState): SimulationState =
    state.world match {
      case Some(w) => run(state.copy(world = w.step(1.0)))
      case _ => state.copy(world = None)
    }

  @tailrec
  private
  def run_gfx(state: SimulationState): SimulationState =
    state.world match {
      case Some(w) => if( !Display.isCloseRequested ) {
        val state2 = manageMouse(state)
        val state3 = manageKeyboard(state2)
        val state4 = manageGraphics(state3)
        val state5 = state4.copy(
          world = w.step(state4.timeDelta)
        )
        run_gfx(state5)
      }
      else {
        state.copy(world = None)
      }

      case _ => state.copy(world = None)
    }

  private
  def manageKeyboard(state: SimulationState): SimulationState = {
    Keyboard.events().foldLeft(state) {
      case (s, Keyboard.KeyPressed(k)) =>
        if( k == KEY_P )
          s.copy(fps = s.fps + 5)
        else if( k == KEY_Q )
          s.copy(fps = Math.max(1, s.fps - 5))
        else
          s

      case (s, _) => s
    }
  }

  private
  def manageMouse(state: SimulationState): SimulationState = state match {
    case _ => state
  }

  private
  def manageGraphics(state: SimulationState): SimulationState = state match {
    case SimulationState(_, Some(w), _, _) =>
      OpenGL.clear()
      for( t <- w.tanks ) {
        Drawing.drawCircle(t.shape.center, t.shape.radius)
      }
      OpenGL.update()
      val (ns, td) = Sync.sync(state.fps).run(state.timing)

      state.copy(timing = ns, timeDelta = td)

    case _ => state
  }

}