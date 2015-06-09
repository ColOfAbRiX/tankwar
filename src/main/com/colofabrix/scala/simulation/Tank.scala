package com.colofabrix.scala.simulation

import com.colofabrix.scala.geometry.abstracts.Shape
import com.colofabrix.scala.geometry.shapes.Circle
import com.colofabrix.scala.math.Vector2D
import com.colofabrix.scala.neuralnetwork.old.abstracts.NeuralNetwork
import com.colofabrix.scala.neuralnetwork.old.builders.abstracts.DataReader
import com.colofabrix.scala.neuralnetwork.old.builders.{FeedforwardBuilder, RandomReader, SeqDataReader, ThreeLayerNetwork}
import com.colofabrix.scala.simulation.abstracts.{InteractiveObject, PhysicalObject}
import com.colofabrix.scala.simulation.integration.TankEvaluator

import scala.collection.mutable.ArrayBuffer
import scala.util.Random

/**
 * A Tank that plays the game
 *
 * @param world Reference to the World. It is uses to calculate parameters, maximums,timings, ...
 * @param initialData The defining data of the Tank in the form of a Chromosome
 * @param dataReader A DataReader. If this is specified, the Brain data of the `initialData` is ignored and re-initialised
 */
class Tank private (override val world: World, initialData: TankChromosome, dataReader: Option[DataReader] = Option.empty)
extends PhysicalObject with InteractiveObject {

  import java.lang.Math._

  private var _direction = Vector2D.new_xy(1, 1)
  private val _rotReference = initialData.rotationRef

  private var _seenTanks: ArrayBuffer[(Tank, Vector2D, Vector2D)] = ArrayBuffer()
  private var _seenBullets: ArrayBuffer[(Bullet, Vector2D, Vector2D)] = ArrayBuffer()

  private var _isShooting: Boolean = false
  private var _shoot = 0.0

  private var _isDead = false
  private var _killsCount: Int = 0
  private var _surviveTime: Long = 0

  /**
   * The list of tanks in the sight of the current instance of tank
   *
   * @return A Seq of tuples where the first entry is the position of the tank and the second its velocity, both relative to the center of the Tank
   */
  def seenTank = _seenTanks.toSeq

  /**
   * The list of bullets in the sight of the current instance of tank
   *
   * @return A Seq of tuples where the first entry is the position of the bullets and the second its velocity, both relative to the center of the Tank
   */
  def seenBullet = _seenBullets.toSeq

  /**
   * Physical boundary of the PhysicalObject located in the space
   */
  override def objectShape: Shape = Circle(_position, 10)

  /**
   * Indicates if the tank is dead
   */
  def isDead = _isDead

  /**
   * Brain of the tank
   */
  val brain: NeuralNetwork =
    initialData.brainBuilder.buildNetwork(
      BrainInputHelper.count,
      BrainOutputHelper.count,
      if( dataReader == Option.empty )
        new SeqDataReader(initialData.biases, initialData.weights, initialData.activationFunction)
      else
        dataReader.get
    )

  /**
   * Number of other tanks killed by the current one
   */
  def kills: Int = _killsCount

  /**
   * Number of cycles the Tank has survived
   */
  def surviveTime = _surviveTime

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
  _speed = Vector2D.new_xy(0.0, 0.0)

  /**
   * Indicates if the tanks is shooting at current time
   *
   * @return true if the tank is shooting a bullet
   */
  def isShooting = _isShooting

  /**
   * The chromosome contains all the data needed to identify uniquely this Tank.
   *
   * It is created collecting the main information of the tank in one single place. So, the information
   * can be obtained in other ways too.
   * Note that it doesn't contain any status information like the current position or speed
   */
  val chromosome = new TankChromosome(
    brain.biases.asInstanceOf[Seq[Seq[Double]]],
    brain.weights.asInstanceOf[Seq[Seq[Seq[Double]]]],
    initialData.rotationRef,
    initialData.sightRatio,
    initialData.valueRange,
    brain.activationFunction.asInstanceOf[Seq[String]],
    initialData.brainBuilder
  )

  private def calculateBulletVision: (Vector2D, Vector2D) = {
    if( _seenBullets.isEmpty )
      return (Vector2D.zero, Vector2D.zero)

    // FIXME: In the past here an exception appeared, multiple times
    val posVector = _seenBullets.foldLeft(Vector2D.zero)(_ + _._2)
    val speedVector = _seenBullets.foldLeft(Vector2D.zero)(_ + _._3)

    (posVector, speedVector)
  }

  private def calculateTankVision: (Vector2D, Vector2D) = {
    if( _seenTanks.isEmpty )
      return (Vector2D.zero, Vector2D.zero)

    // FIXME: In the past here an exception appeared, multiple times
    val selectedTank = _seenTanks.sortBy( t => TankEvaluator.fitness(t._1) ).head
    (selectedTank._2, selectedTank._3)
  }

  /**
   * Moves the PhysicalObject one step into the future
   */
  override def stepForward(): Unit = {
    val seenTank = calculateTankVision
    val seenBullet = calculateBulletVision

    // Calculating outputs
    val output = new BrainOutputHelper(
      brain.output(
        new BrainInputHelper(world, _position, _speed := _direction, _rotation, seenTank._1, seenTank._2, seenBullet._1, seenBullet._2)
      )
    )

    // The output of the NN is the speed (mapped to the max allowed speed)
    _speed = (output.force * world.max_tank_speed) := _direction
    _position = _position + _speed

    // The rotation is found using directly the output of the NN (mapped to a circle). World's maximum is applied
    val newAngle = output.rotation * PI * 2.0 + _rotReference
    _angularSpeed = min(newAngle - _rotation.t, world.max_tank_rotation)
    _rotation = Vector2D.new_rt(1, _rotation.t + _angularSpeed)

    // It shoots when the function is increasing
    _isShooting = output.shoot - _shoot > 0.02
    if (_isShooting) world.on_tankShot(this)
    _shoot = output.shoot

    // Update the survive time at each tick
    _surviveTime += 1

    // Reset the vision every step
    _seenTanks.clear()
    _seenBullets.clear()
  }

  /**
   * Record identifying the step of the Tank
   *
   * @return A string in the format of a CSV
   */
  override def record = super.record + s",${rotation.t },${_shoot },$isShooting"

  /**
   * A text of the definition of the Tank
   *
   * A definition is the minimum set of data that uniquely identfy a Tank
   *
   * @return A string that identifies the Tank
   */
  def definition = id + ": " + chromosome

  /**
   * A text representation of the Tank
   *
   * @return A string containing the
   */
  override def toString = id

  /**
   * Callback function used to signal the Tank that it has hit a wall (or it has gone beyond it)
   */
  override def on_hitsWalls(): Unit = {
    // Invert the speed on the axis of impact (used when the output is considered to be the speed
    _direction = _direction := { (x, i) =>
      if (_position(i) < 0 || _position(i) > world.arena.topRight(i)) -1.0 * x else x
    }

    // Trim the position to the boundary of the arena if the tank is outside
    _position = _position := ((x, i) => max(min(world.arena.topRight(i), x), world.arena.bottomLeft(i)))
  }

  /**
   * Callback function used to signal the Tank that is moving faster than the maximum allowed speed
   */
  override def on_maxSpeedReached(maxSpeed: Double): Unit = {
    //_speed = _speed := { x => min(max(x, -world.max_tank_speed), world.max_tank_speed) }
  }

  /**
   * Callback function used to signal the Tank that is revolving faster than the maximum allowed angular speed
   */
  override def on_maxAngularSpeedReached(maxAngularSpeed: Double): Unit = {}

  /**
   * Callback function used to signal the Tank that it will be respawned in the next step
   */
  override def on_respawn(): Unit = {
    _speed = Vector2D.new_xy(0, 0)
    _position = world.arena.topRight := { _ * Random.nextDouble() }
    _isDead = false
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
    if( that == classOf[Tank] )
      return new Circle(_position,
        (1.0 - initialData.sightRatio) * sqrt(world.max_sight / PI)
      )

    if( that == classOf[Bullet] )
      return new Circle(_position,
        initialData.sightRatio * sqrt(world.max_sight / PI)
      )

    return null
  }

  /**
   * Callback function used to signal the Tank that a bullet is on its sight
   *
   * @param that The object that it's in the sight of the current one
   */
  override def on_objectOnSight(that: PhysicalObject): Unit = {
    val direction = that.position - this.position
    val speed = _speed - that.speed

    that match {
      case t: Tank =>
        // Memorize the direction of all the targets (one at a time)
        _seenTanks += ((t, direction, speed))

      case b: Bullet =>
        // Memorize the direction of all the threats (one at a time)
        _seenBullets += ((b, direction, speed))
    }
  }

  /**
   * Callback function used to signal the Tank that it has been hit by a bullet
   *
   * @param that The object that hit the current instance
   */
  override def on_isHit(that: PhysicalObject): Unit = that match {
    case t: Tank =>
      // No actions for Tank-Tank collision

    case b: Bullet =>
      // Kill myself and lower my fitness
      _isDead = true
      _killsCount = max(_killsCount - 1, 0)
  }

  /**
   * Callback function used to signal the object that it has hit another object
   *
   * @param that The object that is being hit
   */
  override def on_hits(that: PhysicalObject): Unit = that match {
    case t: Tank =>
      // No actions for Tank-Tank collision

    case b: Bullet =>
      // Making up points for the fitness
      _killsCount += 1 + b.tank.kills
  }

  /**
   * Callback function used to signal the object that its sight is exceeding the limits
   */
  override def on_sightExceedingMax( maxAllowedArea: Double ): Unit = {}
}


/**
 * Structural configuration of a tank (usually used at first-time creation)
 */
object Tank {

  // Mass of the tank
  val defaultMass = 1.0

  // Range of the inputs (the purpose is to utilize all the range of the activation function)
  val defaultRange = 4.0

  val defaultActivationFunction = Seq.fill(3)("sin")

  val defaultHiddenNeurons = (BrainInputHelper.count + BrainOutputHelper.count) / 2

  val defaultSightRatio = 0.5

  val defaultBrainBuilder =
    //new ThreeLayerNetwork(new ElmanBuilder, defaultHiddenNeurons)
    new ThreeLayerNetwork(new FeedforwardBuilder, defaultHiddenNeurons)

  def defaultRandomReader(rng: Random) =
    new RandomReader(
      defaultBrainBuilder.hiddenLayersCount,
      rng,
      defaultRange / 30,
      defaultActivationFunction(0))

  def apply(world: World, chromosome: TankChromosome): Tank = new Tank(world, chromosome)

  def apply(world: World, chromosome: TankChromosome, reader: DataReader) = new Tank(world, chromosome, Option(reader))

}
