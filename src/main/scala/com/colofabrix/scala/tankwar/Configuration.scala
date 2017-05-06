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

import com.colofabrix.scala.geometry.Shape
import com.colofabrix.scala.geometry.shapes.{ Box, Line }
import com.colofabrix.scala.math.XYVect
import com.typesafe.config.ConfigFactory

/**
  * Global Tankwar Configuration
  */
object Configuration {

  private val conf = ConfigFactory.load

  object World {
    /** Number of tanks in the World */
    val tankCount: Int = conf.getInt("world.tank_count")

    /**
      * Builder of the arena
      */
    object Arena {

      /** Width of the arena */
      val width: Double = conf.getDouble("world.arena_width")

      /** Height of the arena */
      val height: Double = conf.getDouble("world.arena_height")

      /** The Arena as a simplified Box. */
      val asBox: Box = Box(width, height)

      def apply(): Seq[Shape] = Seq(
        // Top side
        Line(XYVect(0.0, -1.0), height),
        // Left side
        Line(XYVect(1.0, 0.0), 0.0),
        // Bottom side
        Line(XYVect(0.0, 1.0), 0.0),
        // Right side
        Line(XYVect(-1.0, 0.0), width)
      )

    }

  }

  object Simulation {
    /** Number of steps the World will run for */
    val maxIterations: Int = conf.getInt("simulation.max_iterations")

    /** Number of steps the World will run for */
    val maxSimulationTime: Int = conf.getInt("simulation.max_simulation_time")

    /** dt (time step) for every World step */
    val timeMultiplier: Double = conf.getDouble("simulation.time_multiplier")

    /** Target Framerate */
    val fps: Int = conf.getInt("simulation.fps")

    /** If to display graphics or not */
    val gxfEnabled: Boolean = conf.getBoolean("simulation.gfx_enabled")
  }

  object Tanks {
    /** Default mass of a tank */
    val defaultMass: Double = conf.getDouble("tanks.default_mass")
  }

}