/*
 * Copyright (C) 2017 Fabrizio
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

import com.typesafe.config.ConfigFactory

import scalaz.effect._

object Configuration {
  private val conf = IO(ConfigFactory.load).unsafePerformIO()

  object World {
    def tankCount: Int = conf.getInt("world.tank_count")

    def rounds: Int = conf.getInt("world.rounds")

    def width: Int = conf.getInt("world.arena_width")

    def height: Int = conf.getInt("world.arena_height")
  }

  object Simulation {
    def timeStep: Double = conf.getDouble("simulation.time_step")
  }

}
