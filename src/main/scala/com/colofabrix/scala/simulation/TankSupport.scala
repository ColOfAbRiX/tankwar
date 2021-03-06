/*
 * Copyright (C) 2015 Fabrizio Colonna
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

package com.colofabrix.scala.simulation

import com.colofabrix.scala.math.{ RTVect, Vect, XYVect }
import com.colofabrix.scala.neuralnetwork.old.abstracts.{ InputHelper, OutputHelper }
import com.colofabrix.scala.neuralnetwork.old.builders.abstracts.StructureBuilder

/**
  * A TankChromosome contains all the data needed to uniquely identify a Tank from another.
  * Tank with identical chromosomes behave in the same way
  */
final case class TankChromosome(
    biases: Seq[Seq[Double]],
    weights: Seq[Seq[Seq[Double]]],
    rotationRef: Double,
    sightRatio: Double,
    valueRange: Double,
    activationFunction: Seq[String],
    brainBuilder: StructureBuilder
) {

  /**
    * Returns all the data as a sequence that can be used in loops and similar constructs
    *
    * @return A sequence containing all the fields
    */
  def toList: List[Any] = List(
    biases, weights, rotationRef, sightRatio, valueRange, activationFunction, brainBuilder
  )

}

object TankChromosome {

  /**
    * Creates a chromosome starting from a list
    *
    * @param s A list containing the data for a chromosome
    * @return A new chromosome object
    */
  @SuppressWarnings( Array( "org.brianmckenna.wartremover.warts.AsInstanceOf" ) )
  def apply( s: List[Any] ) = {
    new TankChromosome(
      s( 0 ).asInstanceOf[Seq[Seq[Double]]],
      s( 1 ).asInstanceOf[Seq[Seq[Seq[Double]]]],
      s( 2 ).asInstanceOf[Double],
      s( 3 ).asInstanceOf[Double],
      s( 4 ).asInstanceOf[Double],
      s( 5 ).asInstanceOf[Seq[String]],
      s( 6 ).asInstanceOf[StructureBuilder]
    )
  }

}

/**
  * Support class used as an interface between the output of the NN and the `Tank`
  */
object BrainOutputHelper {
  val count = 4
}

/**
  * Support class used as an interface between the output of the NN and the `Tank`
  *
  * It provides a nicer access to the network outputs:
  * - Speed
  * - Rotation
  * - Shoot-or-not
  */
class BrainOutputHelper( outputs: Seq[Double] ) extends OutputHelper[Double]( outputs ) {
  val rotation = outputs( 2 )
  val shoot = outputs( 3 )
  val speed = XYVect( outputs( 0 ), outputs( 1 ) )
}

/**
  * Support class used as an interface between the `Tank` and the input of the NN
  */
object BrainInputHelper {
  val count = 17
}

/**
  * Support class used as an interface between the `Tank` and the input of the NN
  *
  * It provides a nicer access for the network inputs. It makes also sure that the data that is fed to the
  * network is homogeneous between the different inputs or, in other words, that all inputs read data in
  * the range [-1.0, -1.0] (with few exceptions, like the seen bullets and tanks that can have higher values)
  *
  * @param world Reference to the world
  * @param pos Position vector of the tank
  * @param speed Speed vector of the tank
  * @param rot Rotation versor of the tank
  * @param seenTanksPos Position vector of a seen tank
  * @param seenTanksSpeed Speed vector of a seen tank
  * @param seenBulletsPos Position vector of a seen bullet
  * @param seenBulletsSpeed Speed vector of a seen bullet
  */
class BrainInputHelper(
    world: World,
    pos: Vect,
    speed: Vect,
    rot: Vect,
    seenTanksPos: Vect,
    seenTanksSpeed: Vect,
    seenBulletsPos: Vect,
    closerBulletPos: Vect,
    closerBulletSpeed: Vect,
    seenBulletsSpeed: Vect
) extends InputHelper[Double] {

  private val TWO_PI = 2.0 * Math.PI

  override protected val _values = Seq(
    pos.x / world.arena.topRight.x, pos.y / world.arena.topRight.y,
    speed.x / world.max_tank_speed, speed.y / world.max_tank_speed,
    rot.ϑ / TWO_PI,
    seenTanksPos.ρ, seenTanksPos.ϑ / TWO_PI,
    seenTanksSpeed.ρ, seenTanksSpeed.ϑ / TWO_PI,
    seenBulletsPos.ρ, seenBulletsPos.ϑ / TWO_PI,
    closerBulletPos.ρ, closerBulletPos.ϑ / TWO_PI,
    closerBulletSpeed.ρ, closerBulletSpeed.ϑ / TWO_PI,
    seenBulletsSpeed.ρ, seenBulletsSpeed.ϑ / TWO_PI
  )

  /**
    * Constructor that uses a Seq to initialize the instancew
    *
    * @param world Reference to the world
    * @param rawSequence A sequence containing the input data (see class' description for more information on the data)
    * @return A new BrainInputHelper
    */
  def this( world: World, rawSequence: Seq[Double] ) = this(
    world,
    XYVect( rawSequence( 0 ), rawSequence( 1 ) ),
    XYVect( rawSequence( 2 ), rawSequence( 3 ) ),
    RTVect( 1, rawSequence( 4 ) ),
    XYVect( rawSequence( 5 ), rawSequence( 6 ) ),
    XYVect( rawSequence( 7 ), rawSequence( 8 ) ),
    XYVect( rawSequence( 9 ), rawSequence( 10 ) ),
    XYVect( rawSequence( 11 ), rawSequence( 12 ) ),
    XYVect( rawSequence( 13 ), rawSequence( 14 ) ),
    XYVect( rawSequence( 15 ), rawSequence( 16 ) )
  )
}