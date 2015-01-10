package com.colofabrix.scala.tankwar

import com.colofabrix.scala.neuralnetwork.abstracts.{InputHelper, OutputHelper}
import com.colofabrix.scala.neuralnetwork.builders._
import com.colofabrix.scala.tankwar.geometry._
import com.colofabrix.scala.tankwar.geometry.abstracts.{PhysicalObject, Shape}

import scala.util.Random

/**
 * A Tank in the Game
 *
 * Created by Fabrizio on 02/01/2015.
 */
class Tank( override val world: World ) extends PhysicalObject {
  import java.lang.Math._

  object BrainOutputHelper { val count = 4 }

  class BrainOutputHelper(outputs: Seq[Double])
    extends OutputHelper[Double]( outputs ) {

    val acceleration = Vector2D.fromXY(outputs(0), outputs(1))

    val rotation = outputs(2)

    val shoot = outputs(3)

  }

  object BrainInputHelper { val count = 6 }

  class BrainInputHelper( pos: Vector2D, speed: Vector2D, rot: Double, time: Long, feedback: Seq[Double] )
    extends InputHelper[Double] {
    import java.lang.Math._

    override protected val _values = {
      val out_seq = Seq(
        pos.x,   pos.y,
        speed.x, speed.y,
        rot,
        time.toDouble
      )

      // Apply feedback
      (out_seq zip (feedback :+ 0.0)) map {
        case (o, f) =>
          o - signum(o) * abs(o) * abs(f)
      }
    }
  }

  override def boundaries: Shape = Circle(_position, 20)

  /**
   * Brain of the tank, a feed-forward 3-layer neural network
   */
  val brain_net = new Random3LNetwork(BrainInputHelper.count, 10, BrainOutputHelper.count, pow(2, -4)).build

  /**
   * Feedback network, a feed-forward 3-layer neural network for T - 1 memory
   */
  val feedback_net = new Random3LNetwork(brain_net.n_outputs, 4, brain_net.n_inputs - 1, 0.5, "clipped").build

  // Feedback values of T - 1 time
  private var _feedback: Seq[Double] = Seq.fill(brain_net.n_inputs)(0.0)

  // Tracks last shoot output
  private var _shoot = 0.0

  // Number of kills
  private var _killsCount: Int = 0

  override var _position: Vector2D = world.arena.topRight := { _ * Random.nextDouble() }

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

  override def stepForward() {
    // Calculating outputs
    val output = new BrainOutputHelper(
      brain_net.output(
        new BrainInputHelper( _position, _speed, _rotation, world.time, _feedback )
      )
    )

    // Updating feedback
    _feedback = feedback_net.output(output.raw)

    // Update spatial values
    _speed = _speed + output.acceleration
    _position = _position + _speed

    // Check arena boundary
    if( !world.arena.overlaps(_position) ) {
      _speed = _speed := { (x, i) =>
        if( _position(i) < 0 || _position(i) > world.arena.topRight(i) ) -1.0 * x else x
      }
      _position = world.arena trimInside _position
    }

    // Check speed boundary
    _speed = _speed := { x => min(max(x, -world.max_speed), world.max_speed) }

    // Update rotation
    _rotation += output.rotation % PI

    // It shoots when the function changes tone
    _isShooting = output.shoot - _shoot > 0
    if( _isShooting ) world.shot(this)
    _shoot = output.shoot
  }

  /**
   * Called when a tank is hit by a bullet
   *
   * @param bullet The bullet that hits the tank
   */
  def on_isHit(bullet: Bullet): Unit = {
    _killsCount += 1
  }

  /**
   * Called when a tank hits another tank with a bullet
   *
   * @param bullet The bullet that has hit a tank
   * @param tank The tank that is hit
   */
  def on_hits(bullet: Bullet, tank: Tank): Unit = {
  }

  /**
   * Record identifying the step of the Tank
   *
   * @return A string in the format of a CSV
   */
  override def record = super.record +  s";$rotation;${_shoot};$isShooting".replace(".", ",")

  override def toString = id
}