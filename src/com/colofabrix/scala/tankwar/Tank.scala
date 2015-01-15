package com.colofabrix.scala.tankwar

import com.colofabrix.scala.geometry._
import com.colofabrix.scala.geometry.abstracts.{PhysicalObject, Shape}
import com.colofabrix.scala.geometry.shapes.Circle
import com.colofabrix.scala.neuralnetwork.abstracts.{InputHelper, NeuralNetwork, OutputHelper}
import com.colofabrix.scala.neuralnetwork.builders.abstracts.NeuralNetworkBuilder

import scala.util.Random

/**
 * A Tank in the Game
 *
 * Created by Fabrizio on 02/01/2015.
 */
class Tank(override val world: World, brainBuilder: NeuralNetworkBuilder) extends PhysicalObject {

  import java.lang.Math._

  /**
   * Support class used as an interface between the output of the NN and the `Tank`
   */
  object BrainOutputHelper {
    val count = 4
  }

  class BrainOutputHelper(outputs: Seq[Double]) extends OutputHelper[Double](outputs) {
    val acceleration = Vector2D.fromXY(outputs(0), outputs(1))
    val rotation = outputs(2)
    val shoot = outputs(3)
  }

  /**
   * Support class used as an interface between the `Tank` and the input of the NN
   */
  object BrainInputHelper {
    val count = 6
  }

  class BrainInputHelper(pos: Vector2D, speed: Vector2D, rot: Double, time: Long) extends InputHelper[Double] {
    override protected val _values = Seq(
      pos.x, pos.y,
      speed.x, speed.y,
      rot,
      time.toDouble
    )
  }

  override def boundaries: Shape = Circle(_position, 50)

  /**
   * Indicates if the tank is dead
   */
  var isDead = false

  /**
   * Brain of the tank
   */
  val brain: NeuralNetwork = brainBuilder.build(BrainInputHelper.count, BrainOutputHelper.count)

  // Tracks last shoot output
  private var _shoot = 0.0

  // Number of kills
  private var _killsCount: Int = 0

  override var _position: Vector2D = world.arena.topRight := {
    _ * Random.nextDouble()
  }

  override var _speed = Vector2D.fromXY(0.0, 0.0)

  /**
   * Rotation of the Tank's main axis
   *
   * @return The angle formed by the Tank's main axis and the X axis
   */
  def rotation = _rotation

  private var _rotation: Double = 0.0

  /**
   * Indicates if the tanks is shooting at current time
   *
   * @return true if the tank is shooting a bullet
   */
  def isShooting = _isShooting

  private var _isShooting: Boolean = false

  override def stepForward(): Unit = {
    // Calculating outputs
    val output = new BrainOutputHelper(
      brain.output(
        new BrainInputHelper(_position, _speed, _rotation, world.time)
      )
    )

    // Update spatial values
    _speed = _speed + output.acceleration
    _position = _position + _speed

    // Check arena boundary
    if (!world.arena.overlaps(_position)) {
      _speed = _speed := { (x, i) =>
        if (_position(i) < 0 || _position(i) > world.arena.topRight(i)) -1.0 * x else x
      }
      _position = _position := ((x, i) => min(world.arena.topRight(i), x))
    }

    // Check speed boundary
    _speed = _speed := { x => min(max(x, -world.max_speed), world.max_speed)}

    // Update rotation
    _rotation += output.rotation % PI

    // It shoots when the function changes tone
    _isShooting = output.shoot - _shoot > 0
    if (_isShooting) world.shot(this)
    _shoot = output.shoot
  }

  /**
   * Called when a tank is hit by a bullet
   *
   * @param bullet The bullet that hits the tank
   */
  def on_isHit(bullet: Bullet) {
    _killsCount += 1
  }

  /**
   * Called when a tank hits another tank with a bullet
   *
   * @param bullet The bullet that has hit a tank
   * @param tank The tank that is hit
   */
  def on_hits(bullet: Bullet, tank: Tank) {
  }

  /**
   * Record identifying the step of the Tank
   *
   * @return A string in the format of a CSV
   */
  override def record = super.record + s";$rotation;${_shoot};$isShooting".replace(".", ",")

  override def toString = id
}