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

package com.colofabrix.scala.tankwar.simulation

import com.colofabrix.scala.gfx.{ OpenGl, Sync }
import com.colofabrix.scala.gfx.OpenGl.GlobalFrame
import com.colofabrix.scala.math._
import com.colofabrix.scala.tankwar.Configuration.{ World ⇒ WorldConfig }
import com.typesafe.scalalogging.LazyLogging
import org.lwjgl.opengl.Display

/**
  * The world where the simulation takes place.
  */
class World private (
    val iteration: Int,
    _tanks: Option[Seq[Tank]],
    _gfx: Option[GlobalFrame]
) extends LazyLogging {

  /* Configuration */

  /** The walls of the arena */
  protected val arena = WorldConfig.Arena()

  /** The force field present on the arena, point by point */
  protected def forceField( position: Vect ) = XYVect( Math.pow( position.x / 10, 2.0 ), -9.81 )

  val renderState: GlobalFrame = _gfx match {
    case None ⇒ OpenGl.initOpenGl( 1000, 800, "Tankwar V.2" )
    case Some( gfx ) ⇒ gfx
  }

  val stepSync: Sync[Unit, Option[World]] = new Sync( 60, step _ )

  /* State */

  /** List of tanks in the World */
  val tanks = _tanks match {
    case Some( ts ) ⇒ ts

    case None ⇒ for ( i ← 0 until WorldConfig.tankCount ) yield {
      new Tank(
        XYVect( 20.0, 20.0 ),
        XYVect( -5.0, 20.0 ),
        initialExternalForce = forceField( Vect.zero )
      )
    }
  }

  /* State change */

  /** Resets the world to the initial state */
  def reset(): World = new World( 0, None, Some( GlobalFrame() ) )

  /** Advances the world of one step until the last allowed iteration */
  def step(): Option[World] = {
    logger.info( "Running step" )
    stepSync.call().getOrElse( Some( this ) )
  }

  def stepActions(): Option[World] = {
    logger.info( s"World iteration #$iteration." )

    if ( iteration >= WorldConfig.rounds ) {
      logger.warn( s"Reached max iteration number of ${WorldConfig.rounds}." )
      return None
    }

    val newTanks = for ( t ← tanks ) yield {
      t.step( arena, Seq(), forceField( t.position ) )
    }

    OpenGl.applyContext( renderState ) {
      for ( t ← tanks ) {
        OpenGl.drawCircle( t.shape )
      }
    }

    OpenGl.frameClearUp()

    Some( new World( iteration + 1, Some( newTanks ), Some( renderState ) ) )
  }

}

object World {
  def apply() = new World( 0, None, None )
}