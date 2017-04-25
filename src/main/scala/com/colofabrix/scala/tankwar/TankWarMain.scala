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
import com.colofabrix.scala.gfx.OpenGL
import com.colofabrix.scala.tankwar.Configuration.{ Simulation => SimConfig, World => WorldConfig }
import com.colofabrix.scala.tankwar.managers.{ GraphicManager, KeyboardManager, MouseManager, SimState }
import com.typesafe.scalalogging.LazyLogging
import org.lwjgl.opengl.Display

/**
  * Simulation manager
  */
object TankWarMain extends LazyLogging {

  type SimAction = SimState => SimState

  /** Start the simulation. */
  def start(): Unit = {
    if( Configuration.Simulation.gxfEnabled ) {
      OpenGL.init(
        WorldConfig.Arena.width.toInt,
        WorldConfig.Arena.height.toInt
      )

      run_gfx(SimState())

      OpenGL.destroy()
    }
    else run(SimState())
  }

  @tailrec
  private def run(state: SimState): SimState = {
    if( state.world.iteration <= SimConfig.maxIterations )
      run(state.copy(world = state.world.step(1.0)))
    else
      state
  }

  @tailrec
  private def run_gfx(state: SimState): SimState = {
    logger.info(s"Manager state: $state")

    if( state.timing.totalTime <= SimConfig.maxTotalTime && state.timing.simTime <= SimConfig.maxSimulationTime ) {

      if( !Display.isCloseRequested ) {
        val nextState = Seq[SimAction](
          MouseManager.manage,
          KeyboardManager.manage,
          GraphicManager.manage,
          s => {
            if( !s.pause )
              s.copy(world = state.world.step(s.tsMultiplier * s.cycleDelta))
            else
              s
          }
        ).foldLeft(state) {
          (s, manager) => manager(s)
        }

        run_gfx(nextState)
      }
      else state
    }
    else state
  }

}