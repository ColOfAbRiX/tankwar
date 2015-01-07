package com.colofabrix.scala.tankwar

import com.colofabrix.scala.neuralnetwork.builders._
import com.colofabrix.scala.tankwar.geometry._

/**
 * A Tank in the Game
 *
 * Created by Fabrizio on 02/01/2015.
 */
class Tank( override val world: World ) extends PhysicalObject {
  import java.lang.Math._

  override var _position: Vector2D = Vector2D.fromXY(0, 0)
  override var _speed = Vector2D.fromXY(0.0, 0.0)
  override val boundaries: Shape = Circle(_position, 20)

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

  /**
   * Brain of the tank, a feed-forward 3-layer neural network
   */
  val brain_net = new Random3LNetwork(InputMapping.count, 10, OutputMapping.count, pow(2, -3)).build

  /**
   * Feedback network, a feed-forward 3-layer neural network for T - 1 memory
   */
  val feedb_net = new Random3LNetwork(brain_net.n_outputs, 10, brain_net.n_inputs - 1, 1).build

  // Feedback values of T - 1 time
  private var feedback: Seq[Double] = Seq.fill(brain_net.n_inputs)(0.0)

  // Tracks last shoot output
  private var shoot = 0.0

  // Number of kills
  private var kills_count: Int = 0

  override def stepForward() {
    // Calculating outputs
    val output_tmp = brain_net.output(
      InputMapping( _position, _speed, _rotation, world.time, feedback )
    )

    val output = OutputMapping( output_tmp )

    // Updating feedback
    feedback = feedb_net.output(output_tmp)

    // Update spatial values
    _speed = _speed + output.acceleration
    _position = _position + _speed

    // Check arena boundary
    if( !world.arena.isInside(_position) ) {
      _speed = _speed.transform{ (x, i) =>
        if( _position(i) < 0 || _position(i) > world.arena.topRight(i) ) -1.0 * x else x
      }
      _position = world.arena trimInside _position
    }

    // Check speed boundary
    _speed = _speed transform { x => min(max(x, -world.max_speed), world.max_speed) }

    // Update rotation
    _rotation += output.rotation % PI

    // It shoots when the function changes tone
    _isShooting = abs(output.shoot - shoot) > 0.05
    if( _isShooting ) world.shot(this)
    shoot = output.shoot
  }

  /**
   * Called when a tank is hit by a bullet
   *
   * @param bullet The bullet that hits the tank
   */
  def on_isHit(bullet: Bullet): Unit = {
    kills_count += 1
  }

  /**
   * Called when a tank hits another tank with a bullet
   *
   * @param bullet The bullet that has hit a tank
   * @param tank The tank that is hit
   */
  def on_hits(bullet: Bullet, tank: Tank): Unit = {
  }
}

private case class OutputMapping( acceleration: Vector2D, rotation: Double, shoot: Double )

private object OutputMapping {
  val count = 4
  def apply( output: Seq[Double] ) =
    new OutputMapping(
      Vector2D.fromXY(output(0), output(1) ),
      output(2),
      output(3)
    )
}

private case class InputMapping( inputs: Seq[Double] )

private object InputMapping {
  import java.lang.Math._

  val count = 6
  def apply( pos: Vector2D, speed: Vector2D, rot: Double, time: Long, feedback: Seq[Double] ) = {
    val out_seq = Seq(
      pos.x,
      pos.y,
      speed.x,
      speed.y,
      rot,
      time.toDouble
    )

    // Apply feedback
    apply_feedback(out_seq, feedback)
  }

  private def apply_feedback( input: Seq[Double], feedback: Seq[Double] ) =
    (input zip (feedback :+ 0.0)) map {
      case (o, f) =>
        o - signum(o) * abs(o) * abs(f)
    }
}
