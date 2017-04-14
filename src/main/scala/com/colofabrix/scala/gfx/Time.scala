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

package com.colofabrix.scala.gfx

import scalaz.State
import org.lwjgl.Sys
import org.lwjgl.opengl.Display

/**
  * OpenGL timing and synchronization
  */
object Time {
  final case class TimeState(last: Double, simulationTime: Double, totalTime: Double)

  /** Get the time in seconds */
  def time(): Double = Sys.getTime.toDouble / Sys.getTimerResolution.toDouble

  /** Initial state */
  def init() = TimeState(time(), 0.0, 0.0)

  /** Synchronizes the FPS to the specified rate and returns the delta time */
  def sync(fps: Int, stepMultiplier: Double): State[TimeState, Double] = State { s ⇒
    Display.sync(fps)

    val now = time()
    val delta = now - s.last

    (TimeState(now, s.simulationTime + delta * stepMultiplier, s.totalTime + delta), delta)
  }
}