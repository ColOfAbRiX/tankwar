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

import com.colofabrix.scala.tankwar.SimulationState

/**
  * Generic manager.
  */
trait Manager[S] {
  type ManagerAction = scalaz.State[S, S]

  protected
  def ret(state: S): (S, S) = (state, state)
}

/**
  * Representation of a manager for the simulation.
  */
trait SimManager extends Manager[SimulationState] {
  type SimAction = ManagerAction

  def apply(): SimAction
}