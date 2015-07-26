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

import com.colofabrix.scala.geometry.DummyQuadtree
import com.colofabrix.scala.geometry.shapes.Box
import com.colofabrix.scala.gfx.GFXManager
import com.colofabrix.scala.gfx.abstracts.Renderer
import com.colofabrix.scala.gfx.renderers.{ EnvironmentRenderer, _ }
import com.colofabrix.scala.gfx.ui.UIManager
import com.colofabrix.scala.math.Vector2D
import com.colofabrix.scala.neuralnetwork.old.builders.abstracts.DataReader

import scala.collection.mutable.ArrayBuffer
import scala.util.Random

/**
 * The world of the simulation
 *
 * This object represents the world where Tanks fight and provides a simulation for it. It also enforces rules
 * and limits that the participants must follow
 *
 * @param arena The arena where the tanks play
 * @param max_tank_speed The maximum speed allowed for a tank per step
 * @param max_tank_rotation The maximum angular speed of a tank per step
 * @param max_bullet_speed The maximum proper speed of a bullet per step
 * @param bullet_life The maximum number of steps a bullet can live
 * @param max_sight Maximum allowed sight for a tank
 * @param max_rounds Maximum number of rounds for a generation
 * @param dead_time Percentage of the time when a tank can be dead
 * @param _initialTanks The tanks present in the world
 */
class World(
  val arena: Box = Box( Vector2D.new_xy( 0, 0 ), Vector2D.new_xy( 1280, 800 ) ),
  val max_tank_speed: Double = 5,
  val max_tank_rotation: Double = 10 * 2.0 * Math.PI / 360,
  val max_bullet_speed: Double = 4,
  val bullet_life: Int = 25,
  val max_sight: Double = 62831.853071795864, // Area for a total radius of 100
  val max_rounds: Int = 1000,
  val dead_time: Double = 0.2,
  _initialTanks: List[Tank] = List( ) ) {

  require( arena.width > 0 && arena.height > 0, "The arena must not be a point" )
  require( max_tank_rotation > 0, "The maximum angular speed must be positive" )
  require( max_tank_speed > 0, "The maximum tank speed must be positive" )
  require( max_bullet_speed > 0, "The maximum bullet speed must be positive" )
  require( bullet_life > 0, "The maximum bullet lifespan must be positive" )
  require( max_sight > 0, "The sight value must be positive" )
  require( max_rounds > 0, "The number of rounds must be positive" )
  require( dead_time >= 0 && dead_time <= 1.0, "The dead time percentage must be between 0 and 1" )

  private val _counters = collection.mutable.HashMap(
    "hits" -> 0,
    "shots" -> 0,
    "bannedForPosition" -> 0,
    "bannedForSpeed" -> 0,
    "bannedForSpeed" -> 0,
    "seenTanks" -> 0,
    "seenBullets" -> 0
  )
  private val _envRenderer: EnvironmentRenderer = new EnvironmentRenderer( this, _tanks )
  private var _bullets = DummyQuadtree[Bullet]( arena, List( ) )
  private var _tanks = DummyQuadtree[Tank]( arena, _initialTanks )
  private var _time: Long = 0
  /**
   * Graphics manager of the simulation
   */
  val GFXManager = new GFXManager( this, "Tank War", new BGRenderer( arena ) )
  /**
   * User interaction manager of the simulation
   */
  val UIManager = new UIManager( this )
  /**
   * Sequence of all rounds in the world
   */
  val rounds = 1 to max_rounds
  /**
   * Penalty applied to each tank by the rules of the world
   */
  val tanksPenalty = ArrayBuffer.fill( tanks.length )( 0.0 )

  /**
   * Counters for the statistics of the world
   */
  def counters = _counters.toMap

  /**
   * Creates and add a new Tank to the world
   *
   * NOTE: To be refactored
   *
   * @return The newly created Tank
   */
  def createAndAddDefaultTank( reader: DataReader ): Tank = {
    val chromosome = new TankChromosome(
      Seq( ),
      Seq( ),
      2.0 * Math.PI * new Random( ).nextDouble( ),
      Tank.defaultSightRatio,
      Tank.defaultRange,
      Tank.defaultActivationFunction,
      Tank.defaultBrainBuilder
    )

    createAndAddTank( chromosome, reader )
  }

  /**
   * Creates and add a new Tank to the world
   *
   * NOTE: To be refactored
   *
   * @param chromosome The chromosome defining the Tank
   * @param reader N/A
   * @return
   */
  def createAndAddTank( chromosome: TankChromosome, reader: DataReader = null ): Tank = {
    val tank = Tank( this, chromosome, reader )
    _tanks = _tanks + tank
    tank
  }

  /**
   * A tank requests to shot a bullet
   *
   * @param tank The tank that requested to shot
   */
  def on_tankShot( tank: Tank ) {
    _bullets = _bullets + new Bullet( this, tank, max_bullet_speed )
    incCounter( "shots" )
  }

  /**
   * Collects the renderers to draw the objects in the world, like tanks and bullets
   *
   * @return All the renderers for the objects in the simulation worlds
   */
  def renderers: Seq[Renderer] = _envRenderer +: tanks.filter( !_.isDead ).map( _.renderer ) ++: bullets.map( _.renderer )

  /**
   * List of tanks present in the world
   */
  def tanks = _tanks.toList

  /**
   * List of bullets running through the world
   */
  def bullets = _bullets.toList

  /**
   * Resets the world to a known, initial states
   *
   * NOTE: Might be refactored
   *
   * @param tankList Current population of the world
   */
  def resetWorld( tankList: List[Tank] ): Unit = {
    // Reset time
    _time = 0

    // Reinitialize the Tanks
    _tanks = _tanks.clear( )
    for( t <- tankList ) {
      t.clear( )
      _tanks = _tanks + t
    }

    // Clear all bullets
    _bullets = _bullets.clear( )

    // Reset dei counter
    _counters.foreach { case (k, v) => resCounter( k ) }
  }

  /**
   * Reset a specific counter
   *
   * @param counter The counter to reset
   */
  private def resCounter( counter: String ): Unit = {
    _counters += (counter -> 0)
  }

  /**
   * Moves the world one step forward.
   *
   * It first moves tanks and thus manages tanks firing. It then
   * handles bullets and collision between bullets and tanks
   */
  def step( ): Unit = {
    _time += 1

    _tanks = _tanks.refresh( )
    _bullets = _bullets.refresh( )

    // Managing tanks
    for( tank <- tanks.par ) {
      if( !tank.isDead ) {
        manageAliveTank( tank )
      }
      else {
        manageDeadTank( tank )
      }
    }

    // Managing bullets
    bullets.par.foreach( manageBullet )

    // Update the graphic object and render everything
    UIManager.update( )
    GFXManager.render( )
  }

  /**
   * Handles any dead tank in the world
   *
   * The method performs the following actions:
   * - After a specific amount of time it respawns the tank
   *
   * @param tank The tank to manage
   */
  private def manageDeadTank( tank: Tank ): Unit = {
    if( tank.surviveTime + max_rounds * dead_time < _time ) {
      tank.on_respawn( )
    }
  }

  /**
   * Handles any alive tank in the world
   *
   * The method performs the following actions:
   * - Moves the tank on step forward
   * - Checks that it respects the limits
   * - Checks for tank-tank and tank-bullet interactions
   *
   * @param tank The tank to manage
   */
  private def manageAliveTank( tank: Tank ): Unit = {
    tank.step( )

    // Arena boundary check
    check_limit(
      ( ) => arena.contains( tank.position ),
      ( ) => tank.on_hitsWalls( ),
      ( ) => {
        _tanks = _tanks - tank
        incCounter( "bannedForPosition" )
      }
    )

    // Speed limit check
    check_limit(
      ( ) => tank.speed.x <= max_tank_speed || tank.speed.y <= max_tank_speed,
      ( ) => tank.on_maxSpeedReached( max_tank_speed ),
      ( ) => {
        _tanks = _tanks - tank
        incCounter( "bannedForSpeed" )
      }
    )

    // Angular speed limit check
    check_limit(
      ( ) => tank.angularSpeed <= max_tank_rotation,
      ( ) => tank.on_maxAngularSpeedReached( max_tank_rotation ),
      ( ) => {
        _tanks = _tanks - tank
        incCounter( "bannedForAngularSpeed" )
      }
    )

    // Maximum sight boundary
    check_limit(
      ( ) => tank.sight( classOf[Tank] ).area + tank.sight( classOf[Bullet] ).area <= max_sight,
      ( ) => tank.on_sightExceedingMax( max_sight ),
      ( ) => {
        _tanks = _tanks - tank
        incCounter( "bannedForSight" )
      }
    )

    // Tank/Tank sight (when a tank crosses the vision area of the current tank)
    _tanks
      .lookAround( tank.sight( classOf[Tank] ) )
      .filter( t => !t.isDead && t != tank )
      .foreach {
      otherTank =>
        interactionTankTank( tank, otherTank )
    }

    // Tank/Bullet sight (when a bullet crosses the vision area of the current tank)
    _bullets
      .lookAround( tank.sight( classOf[Bullet] ) )
      .foreach {
      bullet =>
        interactionTankBullet( tank, bullet )
    }
  }

  /**
   * Manages the interaction between a tank and a bullet
   *
   * @param tank The reference tank
   * @param bullet The bullet to check agains the tank
   */
  private def interactionTankBullet( tank: Tank, bullet: Bullet ): Unit = {
    // If a bullet overlaps a Tank's sight (and it's not one of the bullets fired by the Tank itself) then I inform the Tank
    if( tank.sight( classOf[Bullet] ).intersects( bullet.objectShape ) && bullet.tank != tank && !bullet.tank.isDead ) {
      tank.on_objectOnSight( bullet )
      incCounter( "seenBullets" )
    }

    if( bullet.touches( tank ) && bullet.tank != tank ) {
      this.on_tankHit( tank, bullet )
    }
  }

  /**
   * A tank is hit by a bullet
   *
   * @param tank The tank hit by the bullet
   * @param bullet The bullet that hits the tank
   */
  def on_tankHit( tank: Tank, bullet: Bullet ) {
    // Prevent a dead tank to kill another tank
    if( !bullet.tank.isDead ) {

      // Inform the hit tank
      tank.on_isHit( bullet )
      // Inform the tank that shot the bullet
      bullet.tank.on_hits( bullet )

      _bullets = _bullets - bullet
      incCounter( "hits" )
    }
  }

  /**
   * Manages the interaction between two tanks
   *
   * @param tank The reference tank
   * @param otherTank The other tank to check against the first one
   */
  private def interactionTankTank( tank: Tank, otherTank: Tank ): Unit = {
    // If a tank overlaps a Tank's sight then I inform the Tank
    if( tank.sight( classOf[Tank] ).intersects( otherTank.objectShape ) ) {
      tank.on_objectOnSight( otherTank )
      incCounter( "seenTanks" )
    }

    if( otherTank.touches( tank ) ) {
      tank.on_hits( otherTank )
      otherTank.on_isHit( tank )
    }
  }

  /**
   * Increments a specific counter by 1
   *
   * @param counter The counter to increment
   */
  private def incCounter( counter: String ): Unit = {
    _counters += (counter -> (_counters( counter ) + 1))
  }

  /**
   * Check if a limit is respected. If not it first notifies an entity and
   * if the check is still not respected it takes a different action
   *
   * @param check What to check. If true no actions will be taken
   * @param notify Function to call to notify an entity the first time check is found to be equals to false
   * @param action The action to take if check equals false for a second time
   */
  private def check_limit( check: () => Boolean, notify: () => Unit, action: () => Unit ) {
    if( !check( ) ) {
      notify( )
      if( !check( ) ) {
        action( )
      }
    }
  }

  /**
   * Handles any bullet in the world
   *
   * The method performs the following actions:
   * - Moves the bullet on step forward
   * - Checks that it respects the limits
   * - Removes it when he has to die
   *
   * @param bullet The bullet to manage
   */
  private def manageBullet( bullet: Bullet ): Unit = {
    bullet.step( )

    // Arena boundary check
    check_limit(
      ( ) => arena.contains( bullet.position ),
      ( ) => bullet.on_hitsWalls( ),
      ( ) => {
        _bullets = _bullets - bullet
      }
    )

    // Check the lifespan of a bullet
    if( bullet.life >= bullet_life ) {
      _bullets = _bullets - bullet
    }
  }

  /**
   * Global execution time
   *
   * @return The number of steps taken from the beginning
   */
  def time = _time
}
