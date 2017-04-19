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

import scala.annotation.tailrec
import com.colofabrix.scala.geometry.shapes.Box
import com.colofabrix.scala.gfx.Time.TimeState
import com.colofabrix.scala.gfx.drawing.ShapeRenderer
import com.colofabrix.scala.gfx.{ Keyboard, OpenGL, Time }
import com.colofabrix.scala.math.XYVect
import com.colofabrix.scala.tankwar.Configuration.{ Simulation => SimConfig, World => WorldConfig }
import com.colofabrix.scala.tankwar.simulation.World
import com.typesafe.scalalogging.LazyLogging
import org.lwjgl.opengl.Display

/**
  * Simulation manager
  */
object Manager extends LazyLogging {

  sealed
  case class SimulationState(
    timing: TimeState,
    world: World,
    timeStepMultiplier: Double,
    cycleTimeDelta: Double,
    pause: Boolean,
    viewport: Box
  )

  /** Start the simulation. */
  def start(): Unit = {
    val initial_state = SimulationState(
      Time.init(),
      World(),
      SimConfig.timeStepMultiplier,
      0.0,
      false,
      Box(WorldConfig.Arena.width, WorldConfig.Arena.height)
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
  def run(state: SimulationState): SimulationState = {
    if( state.world.iteration <= SimConfig.maxIterations )
      run(state.copy(world = state.world.step(1.0)))
    else
      state
  }

  @tailrec
  private
  def run_gfx(state: SimulationState): SimulationState = {
    logger.info(s"Manager state: $state")

    if( state.timing.totalTime <= SimConfig.maxTotalTime &&
      state.timing.simulationTime <= SimConfig.maxSimulationTime ) {

      if( !Display.isCloseRequested ) {
        val state2 = manageMouse(state)
        val state3 = manageKeyboard(state2)
        val state4 = manageGraphics(state3)
        val state5 = if( !state4.pause ) state4.copy(
          world = state.world.step(state4.timeStepMultiplier * state4.cycleTimeDelta)
        )
        else state4

        run_gfx(state5)
      }
      else {
        state
      }
    }
    else {
      state
    }
  }

  private
  def manageKeyboard(state: SimulationState): SimulationState = {
    import org.lwjgl.input.Keyboard._

    Keyboard.events().foldLeft(state) {
      case (s, Keyboard.KeyPressed(k)) =>
        if( k == KEY_ADD ) {
          logger.info("KEY_ADD pressed: increase simulation speed.")
          s.copy(
            timeStepMultiplier = Math.min(10.0, s.timeStepMultiplier * 1.2)
          )
        }
        else if( k == KEY_SUBTRACT ) {
          logger.info("KEY_SUBTRACT pressed: decrease simulation speed.")
          s.copy(
            timeStepMultiplier = Math.max(0.1, s.timeStepMultiplier * 0.8)
          )
        }
        else if( k == KEY_W ) {
          logger.info("KEY_W pressed: Move viewport up.")
          s.copy(viewport = s.viewport.moveOf(XYVect(0, 10)))
        }
        else if( k == KEY_A ) {
          logger.info("KEY_A pressed: Move viewport left.")
          s.copy(viewport = s.viewport.moveOf(XYVect(-10, 0)))
        }
        else if( k == KEY_S ) {
          logger.info("KEY_S pressed: Move viewport down.")
          s.copy(viewport = s.viewport.moveOf(XYVect(0, -10)))
        }
        else if( k == KEY_D ) {
          logger.info("KEY_D pressed: Move viewport right.")
          s.copy(viewport = s.viewport.moveOf(XYVect(10, 0)))
        }
        else if( k == KEY_Q ) {
          logger.info("KEY_Q pressed: Zoom viewport in.")
          s.copy(
            viewport = Box(s.viewport.center, s.viewport.width * 1.1, s.viewport.height * 1.1)
          )
        }
        else if( k == KEY_E ) {
          logger.info("KEY_E pressed: Zoom viewport out.")
          s.copy(
            viewport = Box(s.viewport.center, s.viewport.width * 0.9, s.viewport.height * 0.9)
          )
        }
        else if( k == KEY_H ) {
          logger.info("KEY_H pressed: Reset viewport.")
          s.copy(
            viewport = Box(WorldConfig.Arena.width, WorldConfig.Arena.height)
          )
        }
        else if( k == KEY_P ) {
          logger.info("KEY_P pressed: Toggle pause.")
          s.copy(pause = !s.pause)
        }
        else s

      case (s, _) => s
    }
  }

  private
  def manageMouse(state: SimulationState): SimulationState = state

  private
  def manageGraphics(state: SimulationState): SimulationState = {
    OpenGL.clear()
    OpenGL.setViewport(state.viewport)
    for( t <- state.world.tanks ) {
      ShapeRenderer.drawShape(t.shape)
    }
    OpenGL.update()
    val (ns, td) = Time.sync(
      SimConfig.fps,
      state.timeStepMultiplier / SimConfig.timeStepMultiplier
    ).run(state.timing)

    state.copy(timing = ns, cycleTimeDelta = td)
  }

}