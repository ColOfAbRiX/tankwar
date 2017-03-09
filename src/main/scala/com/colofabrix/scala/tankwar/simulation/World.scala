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

package com.colofabrix.scala.tankwar.simulation

import com.colofabrix.scala.tankwar.geometry.shapes.Arena
import com.colofabrix.scala.tankwar.physics.PhysixEngineVerlet.PhysixInfo
import com.typesafe.scalalogging.LazyLogging



/**
  *
  */
class World private(
  val iteration: Int,
  _tanks: Option[Seq[PhysixInfo]]
) extends LazyLogging {

  import com.colofabrix.scala.tankwar.Configuration.{ World ⇒ WorldConfig }

  private val arena = Arena(WorldConfig.width, WorldConfig.height)

  /** Information about all tanks */
  val tanks: Seq[PhysixInfo] = _tanks match {
    case Some(t) ⇒ t
    case None ⇒ initTanks().map(PhysixInfo(_))
  }

  /** Advances the world of one step until the last allowed iteration */
  def step(): World = {
    logger.info("Requeste World's iteration")
    if( iteration == WorldConfig.rounds ) {
      logger.info(s"Reached max iteration number of ${WorldConfig.rounds }. No more iterations allowed.")
      return this
    }

    val newTanks = tanks.map { t ⇒
      val t2 = t.motion()
      val t3 = t2.borders(Seq(arena))
      t3.collision(Seq())
    }

    return new World(iteration + 1, Some(newTanks))
  }

  /** Resets the world to the initial state */
  def reset(): World = new World(0, None)

  /** Creates the initial list of Tanks */
  def initTanks(): Seq[Tank] = Seq.tabulate(WorldConfig.tankCount) { _ =>
    Tank()
  }
}

object World {
  def apply() = new World(0, None)
}