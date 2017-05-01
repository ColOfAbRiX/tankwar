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

import scala.annotation.tailrec
import com.colofabrix.scala.gfx.OpenGL
import com.colofabrix.scala.tankwar.Configuration
import com.colofabrix.scala.tankwar.Configuration.{ Simulation => SimConfig, World => WorldConfig }
import com.typesafe.scalalogging.LazyLogging
import org.lwjgl.opengl.Display

/**
  * Simulation manager
  */
object GlobalManager extends LazyLogging {

  /** Start the simulation. */
  def start(): Unit = {
    if( Configuration.Simulation.gxfEnabled ) {
      logger.info("Running simulation with graphic interface.")

      OpenGL.init(
        WorldConfig.Arena.width.toInt,
        WorldConfig.Arena.height.toInt
      )

      val finalState = runSimulation(GraphicSimulation())

      OpenGL.destroy()

      logger.info(s"Simulation terminated with status: $finalState.")
    }
    else {
      logger.info("Running simulation without graphics.")

      val finalState = runSimulation(BackgroundSimulation())

      logger.info(s"Simulation terminated with status: $finalState.")
    }
  }

  @tailrec
  private def runSimulation(state: SimulationState): SimulationState = state match {
    // Simulation with graphics
    case s: GraphicSimulation =>
      logger.info(s"Manager state: $s")

      // Stop when the simulation time is finished
      if( s.timing.simTime > SimConfig.maxSimulationTime ) {
        logger.info(s"Simulation time exceeded maximum time. Terminating.")
        return s
      }

      // Stop when requested
      if( Display.isCloseRequested ) {
        logger.info(s"Received close request. Terminating.")
        return s
      }

      // Main actions of the simulation
      val managers = for {
        s1 <- MouseManager.manage()
        s2 <- KeyboardManager.manage()
        s3 <- GraphicManager.manage()
        s4 <- WorldManager.manage()
      } yield s4

      // Run and call recursive for next iteration
      return runSimulation(managers.run(s)._2)

    // Background simulation, no graphics
    case s: BackgroundSimulation =>
      // Stop when reached maximum iterations
      if( s.world.iteration > SimConfig.maxIterations )
        return s

      return runSimulation(s.copy(world = s.world.step(1.0)))

    // Unexpected cases
    case s: SimulationState =>
      logger.info(s"Unexpected state $s. Doing nothing with it.")
      return s
  }

}