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

import com.colofabrix.scala.geometry.shapes.Canvas
import com.colofabrix.scala.math._
import com.colofabrix.scala.physix._
import com.colofabrix.scala.tankwar.Configuration.{ World ⇒ WorldConfig }
import com.typesafe.scalalogging.LazyLogging


/**
  * The world where the simulation takes place.
  */
class World private(
  val iteration: Int,
  _tanks: Option[Seq[Physix]]
) extends LazyLogging {

  /* Configuration */

  /** Commodity variable that defines the Arena */
  protected val arena = Canvas(WorldConfig.width.toDouble, WorldConfig.height.toDouble)

  /** The force field present on the arena, point by point */
  protected def forceField(position: Vect) = XYVect(0.0, -9.81)

  /* Internal status */

  /** Information about all tanks */
  val tanks: Seq[Physix] = _tanks match {
    case Some(t) ⇒ t
    case None ⇒ initTankList().map { t ⇒
      PosVerletEngine(t, forceField(t.position))
    }
  }

  /** Creates the initial list of Tanks */
  def initTankList(): Seq[Tank] = Seq.tabulate(WorldConfig.tankCount) { _ =>
    Tank(velocity = XYVect(15.0, 15.0))
  }

  /* State change */

  /** Resets the world to the initial state */
  def reset(): World = new World(0, None)

  /** Advances the world of one step until the last allowed iteration */
  def step(): Option[World] = {
    if( iteration == WorldConfig.rounds ) {
      logger.warn(s"Reached max iteration number of ${WorldConfig.rounds }. No more iterations allowed.")
      return None
    }

    val newTanks = tanks.map { t ⇒
      val t2 = t.motion(forceField(t.physicalObject.position))
      val t3 = t2.borders(Seq(arena))
      t3.collision(Seq())
    }

    return Some(new World(iteration + 1, Some(newTanks)))
  }
}

object World {def apply() = new World(0, None)}