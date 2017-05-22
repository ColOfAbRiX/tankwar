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
import com.colofabrix.scala.physix.VerletPhysics
import com.colofabrix.scala.physix.concrete.{ VerletPhysics, WorldXZGravity }
import com.colofabrix.scala.tankwar.Configuration.{ Simulation => SimConfig, World => WorldConfig }
import com.colofabrix.scala.tankwar.managers.{ GraphicManager, KeyboardManager, MouseManager, WorldManager }
import com.typesafe.scalalogging.LazyLogging
import org.lwjgl.opengl.Display

/**
  * Simulation manager
  */
object TankWar extends LazyLogging {
  /** MAIN */
  def main(args: Array[String]): Unit = {
    val physics = VerletPhysics()
    val initialState = SimulationState(WorldXZGravity(), physics)
    TankWar.run(initialState)
  }

  /** Start the simulation. */
  def start(initialState: SimulationState): SimulationState = {
    logger.info("Running simulation with graphic interface.")

    OpenGL.init(
      WorldConfig.Arena.width.toInt,
      WorldConfig.Arena.height.toInt
    )

    val finalState = run(initialState)

    OpenGL.destroy()
    logger.info(s"Simulation terminated with status: $finalState.")
    return finalState
  }

  @tailrec
  private
  def run(state: SimulationState): SimulationState = {
    logger.info(s"Manager state: $state")

    // Stop when the simulation time is finished
    if( state.timing.simTime > SimConfig.maxSimulationTime ) {
      logger.info(s"Simulation time exceeded maximum time. Terminating.")
      return state
    }

    // Stop when requested
    if( Display.isCloseRequested ) {
      logger.info(s"Received close request. Terminating.")
      return state
    }

    // Main actions of the simulation
    val actions = for {
      _ <- MouseManager.manage()
      _ <- KeyboardManager.manage()
      _ <- GraphicManager.manage()
      s <- WorldManager.manage()
    } yield s

    // Run and call recursive for next iteration
    return run(actions.run(state)._2)
  }

}