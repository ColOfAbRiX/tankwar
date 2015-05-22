package com.colofabrix.scala.tankwar

import com.colofabrix.scala.geometry.abstracts.{PhysicalObject, Shape}
import com.colofabrix.scala.geometry.shapes.{Circle, Polygon}
import com.colofabrix.scala.math.Vector2D
import com.colofabrix.scala.neuralnetworkOld.abstracts.NeuralNetwork
import com.colofabrix.scala.neuralnetworkOld.builders._
import com.colofabrix.scala.neuralnetworkOld.builders.abstracts.DataReader

import scala.util.Random

/**
 * A Tank that plays the game
 *
 * @param world Reference to the World. It is uses to calculate parameters, maximums,timings, ...
 * @param initialData The defining data of the Tank in the form of a Chromosome
 * @param dataReader A DataReader. If this is specified, the Brain data of the `initialData` is ignored and re-initialised
 */
class Tank private (override val world: World, initialData: TankChromosome, dataReader: Option[DataReader] = Option.empty) extends PhysicalObject {

  import java.lang.Math._

  private var _isDead = false
  private var _shoot = 0.0
  private var _killsCount: Int = 0
  private var _surviveTime: Long = 0
  private var _rotation: Vector2D = Vector2D.new_rt(1, 0)
  private var _isShooting: Boolean = false
  private var _seenTank: Vector2D = Vector2D.origin
  private var _seenBullet = Vector2D.origin
  private var _direction = Vector2D.new_xy(1, 1)

  _mass = initialData.mass

  def seenTank = _seenTank
  def seenBullet = _seenBullet

  /**
   * Physical boundary of the PhysicalObject located in the space
   */
  override def boundary: Shape = Circle(_position, 10)

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
   *
   * @return The point on the world where is the center of the PhysicalObject
   */
  _position = world.arena.topRight := { _ * Random.nextDouble() }

  /**
   * Speed of the object relative to the arena
   *
   * At creation time it is always zero
   *
   * @return The current step speed
   */
  _speed = Vector2D.new_xy(0.0, 0.0)

  /**
   * Rotation of the Tank's main axis
   *
   * @return A versor indicating the angle formed by the Tank's main axis and the X axis in radians
   */
  def rotation = _rotation

  /**
   * Indicates if the tanks is shooting at current time
   *
   * @return true if the tank is shooting a bullet
   */
  def isShooting = _isShooting

  /**
   * Shape that defines the sight of the Tank.
   */
  // TODO: Remove this code and put the default value externally. To do this Tank must be construted only with a Chromosome
  val sightShape: TankSight = initialData.sight

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
    sightShape,
    _mass,
    initialData.valueRange,
    brain.activationFunction.asInstanceOf[Seq[String]],
    initialData.brainBuilder
  )

  /**
   * Moves the PhysicalObject one step into the future
   */
  override def stepForward(): Unit = {
    // Calculating outputs
    val output = new BrainOutputHelper(
      brain.output(
        new BrainInputHelper(world, _position, _speed, _rotation, _seenTank, _seenBullet)
      )
    )

    // SPEED SECTION
    {
      // The output is considered to be a force that changes the speed
      //_speed = _speed + (output.force / _mass)

      // The output of the NN is the speed (mapped to the max allowed speed) but limited to a certain amount of change over time
      //_speed = _speed := { x => min(max(x, -world.max_tank_speed), world.max_tank_speed) }

      // The output of the NN is the speed (mapped to the max allowed speed)
      _speed = (output.force * world.max_tank_speed) := _direction
    }

    _position = _position + _speed

    // ROTATION SECTION
    {
      // The rotation is found changing the previous angle with the output of the NN but limited to a certain amount of change over time
      _rotation = _rotation ¬ min(max(output.rotation, 10.0 * Math.PI / 180.0), 10.0 * Math.PI / 180.0)

      // The rotation is found changing the previous angle with the output of the NN
      //_rotation = _rotation ¬ output.rotation

      // The rotation is found using directly the output of the NN (mapped to a circle)
      //_rotation = Vector2D.new_rt(1, output.rotation * Math.PI * 2.0)
    }

    // SHOOTING SECTION
    {
      // It shoots when the function changes tone
      _isShooting = output.shoot - _shoot > 0
      if (_isShooting) world.on_tankShot(this)
      _shoot = output.shoot
    }

    // Update the survive time at each tick
    _surviveTime = world.time

    // Reset the vision every tick
    _seenTank = Vector2D.origin
    _seenBullet = Vector2D.origin
  }

  /**
   * Called when a tank is hit by a bullet
   *
   * @param bullet The bullet that hits the tank
   */
  def on_isHit(bullet: Bullet): Unit = {
     _isDead = true
    //_killsCount /= 2
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
  override def on_hitsWalls(): Unit = {
    // Invert the speed on the axis of impact (used when the output is considered to be a force
    //_speed = _speed := { (x, i) =>
    //  if (_position(i) < 0 || _position(i) > world.arena.topRight(i)) -1.0 * x else x
    //}

    // Invert the speed on the axis of impact (used when the output is considered to be the speed
    _direction = _direction := { (x, i) =>
      if (_position(i) < 0 || _position(i) > world.arena.topRight(i)) -1.0 * x else x
    }

    // Trim the position to the boundary of the arena if the tank is outside
    _position = _position := ((x, i) => max(min(world.arena.topRight(i), x), world.arena.bottomLeft(i)))
  }

  /**
   * Called when the objects is moving faster than the allowed speed
   */
  override def on_maxSpeedReached(): Unit = {
    _speed = _speed := { x => min(max(x, -world.max_tank_speed), world.max_tank_speed) }
  }

  /**
   * Called when a tank is on the sight of the current one
   *
   * @param t Tank which has been seen
   * @param direction Direction where the tank is seen, relative to `Tank.position`
   */
  def on_tankOnSight(t: Tank, direction: Vector2D): Unit = {
    // Memorize the direction of the target
    _seenTank = direction
  }

  /**
   * Called when a bullet is on the sight of the tank
   *
   * @param b Bullet which has been seen
   * @param direction Direction where the tank is seen, relative to `Tank.position`
   */
  def on_bulletOnSight(b: Bullet, direction: Vector2D): Unit = {
    // Memorize the direction of the threat
    _seenBullet = direction
  }

  /**
   * Record identifying the step of the Tank
   *
   * @return A string in the format of a CSV
   */
  override def record = super.record + s",${rotation.t },${_shoot },$isShooting"

  def definition = id + ": " + chromosome

  override def toString = id
}


/**
 * Structural configuration of a tank (usually used at first-time creation)
 */
object Tank {

  // Mass of the tank
  val defaultMass = 1.0

  // Range of the inputs (the purpose is to utilize all the range of the activation function)
  val defaultRange = 4.0

  val defaultActivationFunction = Seq.fill(3)("tanh")

  val defaultHiddenNeurons = BrainInputHelper.count

  val defaultBrainBuilder =
    //new ThreeLayerNetwork(new ElmanBuilder, defaultHiddenNeurons)
    new ThreeLayerNetwork(new FeedforwardBuilder, defaultHiddenNeurons)

  val defaultSight = TankSight(
    new Polygon(Seq(
      Vector2D.new_xy(0, 5),
      Vector2D.new_xy(100, 0),
      Vector2D.new_xy(0, -5)
    )),
    Vector2D.new_xy(0, 0)
  )

  def defaultRandomReader(rng: Random) =
    new RandomReader(
      defaultBrainBuilder.hiddenLayersCount,
      rng,
      defaultRange,
      defaultActivationFunction(0))

  def apply(world: World, chromosome: TankChromosome): Tank = new Tank(world, chromosome)

  def apply(world: World, chromosome: TankChromosome, reader: DataReader) = new Tank(world, chromosome, Option(reader))

}
