package com.colofabrix.scala.tankwar

import com.colofabrix.scala.geometry._
import com.colofabrix.scala.geometry.abstracts.{PhysicalObject, Shape}
import com.colofabrix.scala.geometry.shapes.Circle
import com.colofabrix.scala.neuralnetwork.abstracts.{InputHelper, NeuralNetwork, OutputHelper}
import com.colofabrix.scala.neuralnetwork.builders._

import scala.util.Random

/**
 * A Tank in the Game
 *
 * Created by Fabrizio on 02/01/2015.
 */
class Tank(override val world: World, initialData: TankCreationData) extends PhysicalObject {

  import java.lang.Math._

  /**
   * Support class used as an interface between the output of the NN and the `Tank`
   */
  object BrainOutputHelper {
    val count = 4
  }

  class BrainOutputHelper(outputs: Seq[brain.T]) extends OutputHelper[brain.T](outputs) {
    val force = Vector2D.new_xy(outputs(0).asInstanceOf[Double], outputs(1).asInstanceOf[Double])
    val rotation = outputs(2).asInstanceOf[Double]
    val shoot = outputs(3).asInstanceOf[Double]
  }

  /**
   * Support class used as an interface between the `Tank` and the input of the NN
   */
  object BrainInputHelper {
    val count = 6
  }

  //class BrainInputHelper(pos: Vector2D, speed: Vector2D, rot: Vector2D, tankOnSight: Double, time: Long) extends InputHelper[brain.T] {
  class BrainInputHelper(pos: Vector2D, speed: Vector2D, rot: Vector2D, tankOnSight: Double) extends InputHelper[brain.T] {
    override protected val _values = Seq(
      pos.x, pos.y,
      speed.x, speed.y,
      rot.t,
      tankOnSight
    ).asInstanceOf[Seq[brain.T]]
  }

  override protected var _mass: Double = initialData.mass

  /**
   * Physical boundary of the PhysicalObject located in the space
   */
  override def boundaries: Shape = Circle(_position, 50)

  /**
   * Indicates if the tank is dead
   */
  def isDead = _isDead
  private var _isDead = false

  /**
   * Brain of the tank
   */
  val brain: NeuralNetwork =
    initialData.brainBuilder.build(
      BrainInputHelper.count,
      BrainOutputHelper.count,
      initialData.dataReader)

  // Tracks last shoot output
  private var _shoot = 0.0

  /**
   * Number of other tanks killed by the current one
   */
  def kills: Int = _killsCount
  private var _killsCount: Int = 0

  /**
   * Time when the tank has died
   */
  def surviveTime = _surviveTime
  private var _surviveTime: Long = 0

  /**
   * Position of the center of the PhysicalObject
   *
   * @return The point on the world where is the center of the PhysicalObject
   */
  override var _position: Vector2D = world.arena.topRight := {
    _ * Random.nextDouble()
  }

  /**
   * Speed of the object relative to the arena
   *
   * @return The current step speed
   */
  override var _speed = Vector2D.new_xy(0.0, 0.0)

  /**
   * Rotation of the Tank's main axis
   *
   * @return The angle formed by the Tank's main axis and the X axis
   */
  def rotation = _rotation
  private var _rotation: Vector2D = Vector2D.new_rt(1, 0)

  /**
   * Indicates if the tanks is shooting at current time
   *
   * @return true if the tank is shooting a bullet
   */
  def isShooting = _isShooting

  private var _isShooting: Boolean = false

  private var _tankOnSight = 0.0

  /**
   * The chromosome contains all the data needed to identify uniquely this Tank
   */
  val chromosome = new TankChromosome(
    brain.biases.asInstanceOf[Seq[Seq[Double]]],
    brain.weights.asInstanceOf[Seq[Seq[Seq[Double]]]],
    brain.activationFunction.asInstanceOf[Seq[String]],
    initialData.valueRange,
    initialData.brainBuilder,
    _mass
  )
  
  /**
   * Moves the PhysicalObject one step into the future
   */
  override def stepForward(): Unit = {
    // Calculating outputs
    val output = new BrainOutputHelper(
      brain.output(
        //new BrainInputHelper(_position, _speed, _rotation, _tankOnSight, world.time)
        new BrainInputHelper(_position, _speed, _rotation, _tankOnSight)
      )
    )

    // Update spatial values
    _speed = _speed + (output.force / _mass)
    _position = _position + _speed
    _rotation = _rotation ¬ output.rotation

    // It shoots when the function changes tone
    _isShooting = output.shoot - _shoot > 0
    if (_isShooting) world.on_tankShot(this)
    _shoot = output.shoot

    _surviveTime = world.time

    _tankOnSight = 0.0
  }

  /**
   * Called when a tank is hit by a bullet
   *
   * @param bullet The bullet that hits the tank
   */
  def on_isHit(bullet: Bullet): Unit = {
    _isDead = true
  }

  /**
   * Called when a tank hits another tank with a bullet
   *
   * @param bullet The bullet that has hit a tank
   * @param tank The tank that is hit
   */
  def on_hits(bullet: Bullet, tank: Tank) {
    _killsCount += 1
  }

  /**
   * If the tank hit a wall (or it goes beyond it), it is bounced back
   */
  override def on_hitsWalls: Unit = {
    // Invert the speed on the axis of impact
    _speed = _speed := { (x, i) =>
      if (_position(i) < 0 || _position(i) > world.arena.topRight(i)) -1.0 * x else x
    }

    _position = _position := ((x, i) => max(min(world.arena.topRight(i), x), world.arena.bottomLeft(i)))
  }

  /**
   * Called when the objects is moving faster than the allowed speed
   */
  override def on_maxSpeedReached: Unit = {
    _speed = _speed := { x => min(max(x, -world.max_speed), world.max_speed)}
  }

  def on_tankOnSight(t: Tank): Unit = {
    _tankOnSight = (t.position - position).r
  }

  /**
   * Record identifying the step of the Tank
   *
   * @return A string in the format of a CSV
   */
  override def record = super.record + s",$rotation,${_shoot},$isShooting"

  override def toString = id

}

object Tank {

  val defaultMass = 1.0

  val defaultRange = 1.0

  val defaultActivationFunction = "tanh"

  val defaultHiddenNeurons = 5

  val defaultBrainBuilder =
    new FeedforwardBuilder(new ThreeLayerNetwork(defaultHiddenNeurons, defaultActivationFunction))

  def defaultRandomReader(rng: Random) = new RandomReader(3, rng, defaultRange, defaultActivationFunction)

  def apply(world: World, chromosome: TankChromosome): Tank = {

    val reader = new SeqDataReader(
      chromosome.biases,
      chromosome.weights,
      chromosome.activationFunction
    )

    val data = new TankCreationData(
      chromosome.brainBuilder,
      reader,
      chromosome.valueRange,
      chromosome.mass
    )

    new Tank(world, data)

  }

  def apply(world: World, data: TankCreationData): Tank = {

    new Tank(world, data)

  }

}