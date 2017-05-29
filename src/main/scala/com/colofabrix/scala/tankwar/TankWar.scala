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

import java.util.Random

import scala.annotation.tailrec
import com.colofabrix.scala.gfx.OpenGL
import com.colofabrix.scala.math.VectUtils.RichVect
import com.colofabrix.scala.math._
import com.colofabrix.scala.physix._
import com.colofabrix.scala.physix.concrete._
import com.colofabrix.scala.tankwar.Configuration.{ Simulation => SimConfig, World => WorldConfig }
import com.colofabrix.scala.tankwar.entities.Tank
import com.colofabrix.scala.tankwar.managers._
import com.typesafe.scalalogging.LazyLogging
import org.lwjgl.opengl.Display

/**
  * Simulation manager
  */
object TankWar extends LazyLogging {

  /** MAIN */
  def main(args: Array[String]): Unit = {
    logger.info("TankWar started.")

    val verlet: PhysixEngine = VerletPhysics()
    val tanks: Seq[RigidBody] = Seq.fill(WorldConfig.tankCount) {
      Tank(
        mass = new Random().nextDouble() * 5.0,
        position = WorldConfig.Arena.asBox.opposite.xyRand(),
        velocity = XYVect(0.0, 5.0).xyRand()
      )
    }
    val world: World = WorldXZGravity(tanks, SimConfig.timeDelta)

    OpenGL.init(
      WorldConfig.Arena.width.toInt,
      WorldConfig.Arena.height.toInt
    )

    val finalState = TankWar.run(SimulationState(verlet, world))

    OpenGL.destroy()
    logger.info(s"Simulation terminated with status: $finalState.")
  }

  @tailrec
  private def run(state: SimulationState): SimulationState = {
    logger.info(s"Simulation step.")
    logger.info(s"Manager state: $state")

    // Stop when the simulation time is finished
    if (state.timing.simulationTime > SimConfig.maxSimulationTime) {
      logger.info(s"Simulation time (${state.timing.simulationTime.eng("s")}) exceeded maximum time. Terminating.")
      return state
    }

    // Stop when requested
    if (Display.isCloseRequested) {
      logger.info(s"Received close request. Terminating.")
      return state
    }

    // Main actions of the simulation
    val actions = for {
      _ <- MouseManager()
      _ <- KeyboardManager()
      _ <- GraphicManager()
      s <- WorldManager()
    } yield s

    // Run and call recursive for next iteration
    return run(actions.run(state)._2)
  }

}