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

    /** Width of the arena */
    def width: Int = conf.getInt("world.arena_width")

    /** Height of the arena */
    def height: Int = conf.getInt("world.arena_height")
  }

  object Simulation {
    /** dt (time step) for every World step */
    def timeStep: Double = conf.getDouble("simulation.time_step")
  }

  object Tanks {
    /** Default mass of a tank */
    def defaultMass: Double = conf.getDouble("tanks.default_mass")
  }
}
