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

import com.colofabrix.scala.geometry.abstracts.Shape
import com.colofabrix.scala.geometry.shapes.{ Box, Circle }
import com.colofabrix.scala.gfx.abstracts.{ Renderable, Renderer }
import com.colofabrix.scala.gfx.renderers.TankRenderer
import com.colofabrix.scala.math.Vector2D
import com.colofabrix.scala.neuralnetwork.old.abstracts.NeuralNetwork
import com.colofabrix.scala.neuralnetwork.old.builders.abstracts.DataReader
import com.colofabrix.scala.neuralnetwork.old.builders.{ FeedforwardBuilder, RandomReader, SeqDataReader, ThreeLayerNetwork }
import com.colofabrix.scala.simulation.Tank._
import com.colofabrix.scala.simulation.abstracts.{ InteractiveObject, PhysicalObject }
import com.colofabrix.scala.simulation.integration.TankEvaluator

import scala.collection.mutable.ArrayBuffer
import scala.util.Random

/**
 * A Tank that plays in the game
 *
 * A Tank is the main actor of the simulation. Like other objects in the {com.colofabrix.scala.simulation} package it is
 * a stateful and time-depended object.
 * It is made of a defining data part, the {TankChromosome}, a behavioural part which decides how the Tank responds
 * to different situation using the defining data and a simulation part that calculates, step by step, the physical
 * components or actions of the Tank. The behaviour is defined by both a neural network that calculates various outputs
 * given the input values (e.g. where to go in relation of the current position) and by algorithms that take decisions
 * about situation (e.g. if there are multiple targets which one to shoot).
 * Tank's implementations have a certain degree of freedom on how they implement thing, but the {World} makes sure that
 * many constraints are respected (e.g. the maximum allowed speed). Usually a Tank is first informed it is violating
 * a constraint, so that it can take actions like trim its speed, and if the violation persists the Tank is removed from
 * the simulation (check {World} for more information}.
 *
 * NOTE: This object and other in the {com.colofabrix.scala.simulation} package will be the subject of heavy refactoring,
 * so don't rely on their current implementation but check the workstream.
 *
 * @param world Reference to the World. It is uses to calculate parameters, maximums,timings, ...
 * @param initialData The defining data of the Tank in the form of a Chromosome
 * @param dataReader A DataReader. If this is specified, the Brain data of the `initialData` is ignored and re-initialised
 */
@SuppressWarnings(
  Array(
    "org.brianmckenna.wartremover.warts.MutableDataStructures",
    "org.brianmckenna.wartremover.warts.Null",
    "org.brianmckenna.wartremover.warts.Var"
  )
)
class Tank private (
    override val world: World,
    initialData: TankChromosome,
    dataReader: Option[DataReader] = Option.empty[DataReader]
) extends PhysicalObject with InteractiveObject with Renderable {

  import java.lang.Math._

  private val _maxSight = Circle.fromArea( Vector2D.origin, world.max_sight )
  private val _rotReference = initialData.rotationRef
  private var _direction = Vector2D.new_xy( 1, 1 )
  private var _isDead = false
  private var _isShooting: Boolean = false
  private var _points: Int = 0
  private var _seenBullets: ArrayBuffer[( Bullet, Vector2D, Vector2D )] = ArrayBuffer()
  private var _seenTanks: ArrayBuffer[( Tank, Vector2D, Vector2D )] = ArrayBuffer()
  private var _shoot = 0.0
  private var _surviveTime: Long = 0
  /**
   * Brain of the tank
   */
  val brain: NeuralNetwork =
    initialData.brainBuilder.buildNetwork(
      BrainInputHelper.count,
      BrainOutputHelper.count,
      dataReader.getOrElse(
        new SeqDataReader( initialData.biases, initialData.weights, initialData.activationFunction )
      )
    )
  /**
   * The chromosome contains all the data needed to identify uniquely this Tank.
   *
   * It is created collecting the main information of the tank in one single place. So, the information
   * can be obtained in other ways too.
   * Note that it doesn't contain any status information like the current position or speed
   */
  @SuppressWarnings( Array( "org.brianmckenna.wartremover.warts.AsInstanceOf" ) )
  def chromosome = new TankChromosome(
    brain.biases.asInstanceOf[Seq[Seq[Double]]],
    brain.weights.asInstanceOf[Seq[Seq[Seq[Double]]]],
    initialData.rotationRef,
    initialData.sightRatio,
    initialData.valueRange,
    brain.activationFunction.asInstanceOf[Seq[String]],
    initialData.brainBuilder
  )
  val renderer: Renderer = new TankRenderer( this )

  /**
   * Reset the status of a Tank to the initial values
   */
  override def clear(): Unit = {
    _direction = Vector2D.new_xy( 1, 1 )

    _seenTanks = ArrayBuffer()
    _seenBullets = ArrayBuffer()

    _isShooting = false
    _shoot = 0.0

    _isDead = false
    _points = 0
    _surviveTime = 0
  }

  /**
   * A text of the definition of the Tank
   *
   * A definition is the minimum set of data that uniquely identify a Tank
   *
   * @return A string that identifies the Tank
   */
  def definition = id + ": " + chromosome

  /**
   * Indicates if the tank is dead
   */
  def isDead = _isDead

  /**
   * Physical boundary of the PhysicalObject located in the space
   */
  override def objectShape: Shape = Circle( _position, 10 )

  /**
   * Position of the center of the PhysicalObject
   *
   * At creation time it is initialized as a random value inside the arena
   */
  _position = world.arena.topRight := { _ * Random.nextDouble() }

  /**
   * Speed of the object relative to the arena
   *
   * At creation time it is always zero
   */
  _speed = Vector2D.new_xy( 0.0, 0.0 )

  /**
   * Callback function used to signal the object that it has hit another object
   *
   * @param that The object that is being hit
   */
  override def on_hits( that: PhysicalObject ): Unit = that match {
    case t: Tank ⇒
    // No actions for Tank-Tank collision

    case c: Causality[_] ⇒ c.initial match {
      case t: Tank ⇒
        // Making up points for the fitness
        _points += 1 + min( t.points.toDouble, _points * Tank.maxGainK ).toInt
    }

    case b: Bullet ⇒
    // Actually this situation never arises
  }

  /**
   * Number of other tanks killed by the current one
   */
  def points: Int = _points

  /**
   * Callback function used to signal the Tank that it has hit a wall (or it has gone beyond it)
   *
   * When a Tank hits a wall it is bounced back (using a direction vector)
   */
  override def on_hitsWalls(): Unit = {
    // Invert the speed on the axis of impact (used when the output is considered to be the speed
    _direction = _direction := { ( x, i ) ⇒
      if ( _position( i ) < 0 || _position( i ) > world.arena.topRight( i ) ) -1.0 * x else x
    }

    // Trim the position to the boundary of the arena if the tank is outside
    _position = _position := ( ( x, i ) ⇒ max( min( world.arena.topRight( i ), x ), world.arena.bottomLeft( i ) ) )
  }

  /**
   * Callback function used to signal the Tank that it has been hit by a bullet
   *
   * @param that The object that hit the current instance
   */
  override def on_isHit( that: PhysicalObject ): Unit = that match {
    case t: Tank ⇒
      // Small penalty if you hit another tank
      _points = ceil( max( _points * Tank.tankTankPenalty, 0 ) ).toInt

    case b: Bullet ⇒
      // Kill myself and lower my fitness
      _isDead = true
      _points = max( _points * Tank.tankBulletPenalty, 0 ).toInt
  }

  /**
   * Callback function used to signal the Tank that is revolving faster than the maximum allowed angular speed
   */
  override def on_maxAngularSpeedReached( maxAngularSpeed: Double ): Unit = {}

  /**
   * Callback function used to signal the Tank that is moving faster than the maximum allowed speed
   *
   * When maximum speed is reached, it is trimmed to the maximum
   */
  override def on_maxSpeedReached( maxSpeed: Double ): Unit = {
    _speed = _speed := { x ⇒ min( max( x, -world.max_tank_speed ), world.max_tank_speed ) }
  }

  /**
   * Callback function used to signal the Tank that a bullet is on its sight
   *
   * @param that The object that it's in the sight of the current one
   */
  @SuppressWarnings( Array( "NullParameter" ) )
  override def on_objectOnSight( that: PhysicalObject ): Unit = {
    if ( that == null ) return

    val direction = that.position - this.position
    val speed = _speed - that.speed

    // We don't like objects that precisely overlaps the tank... Infinities can happen
    if ( direction == Vector2D.zero ) return

    // Memorize the direction of all the targets or threats (one at a time)
    that match {
      case t: Tank ⇒
        _seenTanks += ( ( t, direction, speed ) )

      case b: Bullet ⇒
        _seenBullets += ( ( b, direction, speed ) )
    }

    return
  }

  /**
   * Callback function used to signal the Tank that it will be respawned in the next step
   */
  override def on_respawn(): Unit = {
    // Set speed to zero
    _speed = Vector2D.new_xy( 0, 0 )
    // Choose a random place in the arena (so I don't appear in front of the tank that killed me and that's still shooting)
    _position = world.arena.topRight := { _ * Random.nextDouble() }
    // I'm not dead anymore!
    _isDead = false
  }

  /**
   * Callback function used to signal the object that its sight is exceeding the limits
   */
  override def on_sightExceedingMax( maxAllowedArea: Double ): Unit = {}

  /**
   * Record identifying the step of the Tank
   *
   * @return A string in the format of a CSV
   */
  override def record = super.record + s",${rotation.t},${_shoot},$isShooting"

  /**
   * Indicates if the tanks is shooting at current time
   *
   * @return true if the tank is shooting a bullet
   */
  def isShooting = _isShooting

  /**
   * The list of bullets in the sight of the current instance of tank
   *
   * @return A Seq of tuples where the first entry is the position of the bullets and the second its velocity, both relative to the center of the Tank
   */
  def seenBullet = _seenBullets.toSeq

  /**
   * The list of tanks in the sight of the current instance of tank
   *
   * @return A Seq of tuples where the first entry is the position of the tank and the second its velocity, both relative to the center of the Tank
   */
  def seenTank = _seenTanks.toSeq

  /**
   * Moves the Tank one step into the future.
   *
   * In other words it calculates the position, speed and shooting condition for the current step
   */
  override def step(): Unit = {
    // Data related to the vision
    val seenTank = calculateTankVision
    val seenBullet = calculateBulletVision
    val closerBullet = calculateClosestBulletVision

    // Calculating outputs
    val output = new BrainOutputHelper(
      brain.output(
        new BrainInputHelper(
          world, _position, _speed := _direction, _rotation, seenTank._1, seenTank._2, seenBullet._1, closerBullet._1, closerBullet._2, seenBullet._2
        )
      )
    )

    // The output of the NN is the speed (mapped to the max allowed speed)
    //_speed = (output.speed * world.max_tank_speed) := _direction
    val newSpeed = output.speed * world.max_tank_speed
    _speed = Vector2D.new_rt( newSpeed.r, min( newSpeed.t, _speed.t + world.max_tank_rotation ) )
    _position = _position + ( _speed := _direction )
    _speed = _speed := _direction

    // The rotation is found using directly the output of the NN (mapped to a circle). World's maximum is applied
    val newAngle = output.rotation * PI * 2.0 + _rotReference
    _angularSpeed = min( newAngle - _rotation.t, world.max_tank_rotation )
    _rotation = Vector2D.new_rt( 1, _rotation.t + _angularSpeed )

    // It shoots when the function is increasing
    _isShooting = output.shoot - _shoot > 0.02
    if ( _isShooting ) world.on_tankShot( this )
    _shoot = output.shoot

    // Update the survive time at each tick
    _surviveTime += 1

    // Every step the sight buffers are reset and filled again by the world
    _seenTanks.clear()
    _seenBullets.clear()
  }

  /**
   * Calculates the data needed to feed the inputs of the {brain} in relation of the bullet vision (a "threat")
   *
   * The current implementation is to do a vector-sum of all the threats (their positions and speed). Then the resulting
   * position vector is used as "seen bullet" and the resulting speed is first projected onto the radial
   *
   * @return A tuple containing 1) the position vector of a threat and 2) the speed vector of the threat
   */
  def calculateClosestBulletVision: ( Vector2D, Vector2D ) = {
    val sightDistance = sight( classOf[Bullet] ) match {
      case c: Circle ⇒ c.radius
      case b: Box ⇒ max( b.width, b.height )
      case _ ⇒ throw new IllegalArgumentException( "Cannot use something different than a Box or Circle here and now" )
    }

    if ( _seenBullets.isEmpty ) {
      return ( Vector2D.zero, Vector2D.zero )
    }

    val closestBullet = _seenBullets.minBy( _._2.r <= sightDistance / 2.0 )
    val closestBulletPosition = Vector2D.new_rt(
      max( 1.0 - closestBullet._2.r / _maxSight.radius, 0 ),
      closestBullet._2.t
    )
    val closestBulleSpeed = closestBullet._3

    // Final position seen by the tank
    ( closestBulletPosition, closestBulleSpeed )
  }

  /**
   * The sight of the object in relation of a specific object
   *
   * It returns a shape that represents the sight that the current instance has towards object of another type
   *
   * @param that The class type of the object that we are interested in
   * @tparam T N/A
   * @return A {Shape} that represents the sight towards the object type of {that}
   */
  override def sight[T <: PhysicalObject]( that: Class[T] ): Shape = {
    if ( that == classOf[Tank] ) {
      // Tank->Tank sight
      return new Circle(
        _position,
        ( 1.0 - initialData.sightRatio ) * sqrt( world.max_sight / PI )
      )
    }

    if ( that == classOf[Bullet] ) {
      // Tank->Bullet sight
      return new Circle(
        _position,
        initialData.sightRatio * sqrt( world.max_sight / PI )
      )
    }

    return null
  }

  /**
   * Calculates the data needed to feed the inputs of the {brain} in relation of the bullet vision (a "threat")
   *
   * The current implementation is to do a vector-sum of all the threats (their positions and speed). Then the resulting
   * position vector is used as "seen bullet" and the resulting speed is first projected onto the radial
   *
   * @return A tuple containing 1) the position vector of a threat and 2) the speed vector of the threat
   */
  def calculateBulletVision: ( Vector2D, Vector2D ) = {
    val sightDistance = sight( classOf[Bullet] ) match {
      case c: Circle ⇒ c.radius
      case b: Box ⇒ max( b.width, b.height )
      case _ ⇒ throw new IllegalArgumentException( "Cannot use something different than a Box or Circle here and now" )
    }

    if ( _seenBullets.isEmpty ) {
      return ( Vector2D.zero, Vector2D.zero )
    }

    // For some (unknown) reasons it can happen that the array contains null values
    _seenBullets = _seenBullets.filter( _ != null )

    // Average position and speed of all the seen bullets
    val bulletsPositionsSum = _seenBullets.foldLeft( Vector2D.zero )( _ + _._2 ) / _seenBullets.size.toDouble
    val bulletsSpeedsSum = _seenBullets.foldLeft( Vector2D.zero )( _ + _._3 ) / _seenBullets.size.toDouble

    // Final position seen by the tank
    val seenPosition = Vector2D.new_rt(
      sqrt( _seenBullets.size.toDouble ) * max( 1.0 - bulletsPositionsSum.r / _maxSight.radius, 0 ),
      bulletsPositionsSum.t
    )

    // Final speed seen by the tank
    val seenSpeed = ( bulletsSpeedsSum / world.max_bullet_speed ) → seenPosition.v

    ( seenPosition, seenSpeed )
  }

  /**
   * Calculates the data needed to feed the inputs of the {brain} in relation of the tank vision (a "target")
   *
   * The code allows for different targeting policies: first match, strongest tank, weakest tank
   *
   * @return A tuple containing 1) the position vector of a target and 2) the speed vector of the target, both with components normalized to 1.0
   */
  @SuppressWarnings( Array( "TraversableHead" ) )
  def calculateTankVision: ( Vector2D, Vector2D ) = {
    if ( _seenTanks.isEmpty ) {
      return ( Vector2D.zero, Vector2D.zero )
    }

    // For some (unknown) reasons it can happen that the array contains null values
    _seenTanks = _seenTanks.filter( _ != null )

    val selectedTank = Tank.defaultTargetType match {

      case FirstTarget ⇒
        // I target the same tank not caring about new tanks on sight (for consistency)
        _seenTanks.sortBy( t ⇒ t._1.id ).head

      case FittestTarget ⇒
        // I target the fittest tank on sight (hoping to gain more points)
        _seenTanks.maxBy( t ⇒ TankEvaluator.fitness( t._1 ) )

      case LessFitTarget ⇒
        // I target the weaker tank on sight (for an easy kill)
        _seenTanks.minBy( t ⇒ TankEvaluator.fitness( t._1 ) )

      case HighPointsTarget ⇒
        // I target the tank with the highest points (hoping to gain more points myself)
        _seenTanks.maxBy( t ⇒ t._1.points )

      case LowPointsTarget ⇒
        // I target the tank with the lowest points (for an easy kill)
        _seenTanks.minBy( t ⇒ t._1.points )

      case SlowestTarget ⇒
        // I target the slowest tank on sight (for an easy kill)
        _seenTanks.minBy( t ⇒ t._3.r )
    }

    // Final position seen by the tank
    // A tank in sight means possible fitness increase. High values at the border means more reaction
    val seenTankPosition = selectedTank._2 / _maxSight.radius

    // Final position seen by the tank, normalized to 1.0
    val seenTankSpeed = selectedTank._3 / world.max_tank_speed

    ( seenTankPosition, seenTankSpeed )
  }

  /**
   * Number of cycles the Tank has survived
   */
  def surviveTime = _surviveTime

  /**
   * A text representation of the Tank
   *
   * @return A string containing the
   */
  override def toString = id
}

/**
 * Structural configuration of a tank (usually used at first-time creation)
 */
object Tank {

  sealed trait TargetType

  case object FirstTarget extends TargetType
  case object FittestTarget extends TargetType
  case object LessFitTarget extends TargetType
  case object HighPointsTarget extends TargetType
  case object LowPointsTarget extends TargetType
  case object SlowestTarget extends TargetType

  /** Default activation function */
  val defaultActivationFunction = Seq.fill( 3 )( "tanh" )
  /** Default number of hidden neurons. It is the average between input and output neurons */
  val defaultHiddenNeurons = Math.ceil( ( BrainInputHelper.count + BrainOutputHelper.count ) / 2.0 ).toInt
  /** Default type of neural network */
  val defaultBrainBuilder = new ThreeLayerNetwork( new FeedforwardBuilder, defaultHiddenNeurons )
  /** Default mass of the tank at initial creation */
  val defaultMass = 1.0
  /** Default range of the inputs (the purpose is to utilize all the range of the activation function) at initial creation */
  val defaultRange = 4.0
  /** Default sight ration. */
  val defaultSightRatio = 0.5
  /** Default targeting strategy for targets */
  val defaultTargetType: TargetType = HighPointsTarget
  /** Max amount af point that a Tank can gain */
  val maxGainK = 2.0
  //val maxGainK = 10.0
  /** Penalty that applies when there is a Tank-Bullet collision (a Tank is hit) */
  val tankBulletPenalty = 0.8
  //val tankBulletPenalty = 0.5
  /** Penalty that applies when there is a Tank-Tank collision */
  val tankTankPenalty = 0.95

  // Mess below here. Refactoring planned.

  def apply( world: World, chromosome: TankChromosome ): Tank = new Tank( world, chromosome )

  def apply( world: World, chromosome: TankChromosome, reader: DataReader ) = new Tank( world, chromosome, Option( reader ) )

  def defaultRandomReader( rng: Random ) =
    new RandomReader(
      defaultBrainBuilder.hiddenLayersCount,
      rng,
      defaultRange / 40.0,
      defaultActivationFunction( 0 )
    )

}