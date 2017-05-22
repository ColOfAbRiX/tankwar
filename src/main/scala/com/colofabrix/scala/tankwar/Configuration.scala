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

import com.colofabrix.scala.geometry.shapes.Box
import com.typesafe.config.ConfigFactory

/**
  * Global Tankwar Configuration
  */
object Configuration {

  private val conf = ConfigFactory.load

  object World {
    /** Number of tanks in the World */
    val tankCount: Int = conf.getInt("world.tank_count")

    object Arena {

      /** Width of the arena */
      val width: Double = conf.getDouble("world.arena_width")

      /** Height of the arena */
      val height: Double = conf.getDouble("world.arena_height")

      /** The Arena as a simplified Box. */
      val asBox: Box = Box(width, height)
    }

  }

  object Simulation {
    /** Number of steps the World will run for. */
    val maxSimulationTime: Int = conf.getInt("simulation.max_simulation_time")

    /** Initial dt (time step) for every World step. */
    val timeDelta: Double = conf.getDouble("simulation.time_delta")
  }

  object Graphics {
    /** Target Framerate. */
    val fps: Int = conf.getInt("graphics.fps")

    /** If to display graphics or not. */
    val enabled: Boolean = conf.getBoolean("graphics.enabled")

    /** If to display vectors. */
    val showVectors: Boolean = conf.getBoolean("graphics.show_vectors")

    /** If to display the world force field. */
    val showForceField: Boolean = conf.getBoolean("graphics.show_force_field")
  }

  object Tanks {
    /** Default mass of a tank. */
    val mass: Double = conf.getDouble("tanks.default_mass")

    /** Default friction of a tank. */
    val friction: Double = conf.getDouble("tanks.default_friction")

    /** Default elasticity of a tank. */
    val elasticity: Double = conf.getDouble("tanks.default_elasticity")
  }

}