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
import com.colofabrix.scala.tankwar.Configuration.{ World => WorldConfig }
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
  //protected val arena = Canvas(WorldConfig.width.toDouble, WorldConfig.height.toDouble)
  protected val arena = Seq(
    (XYVect(1, 0), 1.0),
    (XYVect(0, -1), 1.0),
    (XYVect(-1, 0), 1.0),
    (XYVect(1, 1), 1.0)
  )


  /** The force field present on the arena, point by point */
  protected def forceField(position: Vect) = XYVect(0.0, -9.81)

  /* Internal status */

  /** Information about all tanks */
  val tanks: Seq[Physix] = _tanks match {
    case Some(t) => t
    case None => initTankList().map { t =>
      PosVerletEngine(t, forceField(t.position))
    }
  }

  /** Creates the initial list of Tanks */
  def initTankList(): Seq[Tank] = Seq.tabulate(WorldConfig.tankCount) { _ =>
    Tank(position = XYVect(20.0, 0.0), velocity = XYVect(20.0, 20.0))
  }

  /* State change */

  /** Resets the world to the initial state */
  def reset(): World = new World(0, None)

  /** Advances the world of one step until the last allowed iteration */
  def step(): Option[World] = {
    logger.info(s"World iteration #$iteration.")

    if( iteration == WorldConfig.rounds ) {
      logger.warn(s"Reached max iteration number of ${WorldConfig.rounds }.")
      return None
    }

    // Resolve collisions
    val step1 = for {
      t <- tanks
    } yield  {
      //val distance = (t.body.position ∙ p._1) + p._2
      t
    }

    // Integrate
    val step2 = for {
      t <- step1
    } yield {
      t.integrate(forceField(t.body.position))
    }

    return Some(new World(iteration + 1, Some(step2)))
  }
}

object World {def apply() = new World(0, None)}