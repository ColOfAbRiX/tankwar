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

import scalaz.effect._
import com.colofabrix.scala.geometry.Shape
import com.colofabrix.scala.geometry.shapes.Line
import com.colofabrix.scala.math.XYVect
import com.typesafe.config.ConfigFactory

/**
  * Global Tankwar Configuration
  */
object Configuration {

  private val conf = IO(ConfigFactory.load).unsafePerformIO()

  object World {
    /** Number of tanks in the World */
    def tankCount: Int = conf.getInt("world.tank_count")

    /** Number of steps the World will run for */
    def rounds: Int = conf.getInt("world.rounds")

    /**
      * Builder of the arena
      */
    object Arena {

      /** Width of the arena */
      def width: Double = conf.getDouble("world.arena_width")

      /** Height of the arena */
      def height: Double = conf.getDouble("world.arena_height")

      def apply(): Seq[Shape] = Seq(
        // Top side
        Line(XYVect(0, -1), height),
        // Left side
        Line(XYVect(1, 0), 0.0),

        // Bottom side
        Line(XYVect(0, 1), 0.0),

        // Right side
        Line(XYVect(-1, 0), height)
      )

    }

  }

  object Simulation {
    /** dt (time step) for every World step */
    def timeStep: Double = conf.getDouble("simulation.time_step")

    /** Target Framerate */
    def fps: Double = conf.getDouble("simulation.fps")
  }

  object Tanks {
    /** Default mass of a tank */
    def defaultMass: Double = conf.getDouble("tanks.default_mass")
  }

}