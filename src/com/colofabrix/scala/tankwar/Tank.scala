package com.colofabrix.scala.tankwar

import com.colofabrix.scala.neuralnetwork.GenericNeuralNetwork
import com.colofabrix.scala.neuralnetwork.builders._

/**
 * A Tank in the Game
 *
 * Created by Fabrizio on 02/01/2015.
 */
class Tank( override val world: World ) extends PhysicalObject {
  override var _position: Point = Point(0, 0)
  override var _speed = Point(0.0, 0.0)
  override val boundary: Shape = Circle(_position, 20)

  private var _rotation: Double = 0.0
  def rotation = _rotation

  private var _isShooting: Boolean = false
  def isShooting = _isShooting

  val brain_net: GenericNeuralNetwork = new Random3LNetwork(InputMapping.count, 5, OutputMapping.count, Math.pow(2, -2)).build
  val feedb_net = new Random3LNetwork( brain_net.n_outputs, 10, brain_net.n_inputs, Math.pow(2, -2), "linear").build

  private var shoot = 0.0
  private var feedback: Seq[Double] = Seq.fill(brain_net.n_inputs)(0.0)
  private var hit_count: Int = 0

  def stepForward() {
    // Calculating outputs
    val output_tmp = brain_net.output( InputMapping( _position, _speed, _rotation, world.time, feedback ) )

    val output = OutputMapping( output_tmp )

    // Updating feedback
    feedback = feedb_net.output(output_tmp)

    // Update spatial values
    _speed = _speed + output.acceleration
    _position = _position + _speed

    // Check arena boundary
    if( !world.arena.isInside(_position) ) {
      _speed = _speed.transform{ (x, i) => if( _position(i) < 0 || _position(i) > world.arena.topRight(i) ) -1.0 * x else x }
      _position = world.arena trimInside _position
    }

    // Check speed boundary
    _speed = _speed transform { x => Math.min(Math.max(x, -world.max_speed), world.max_speed) }

    // Update rotation
    _rotation += output.rotation % Math.PI

    // It shoots when the function changes tone
    _isShooting = output.shoot - shoot > 0
    if( _isShooting ) world.shot(this)
    shoot = output.shoot
  }

  def hit(bullet: Bullet): Unit = {
    hit_count += 1
  }
}

private case class OutputMapping( acceleration: Point, rotation: Double, shoot: Double )

private object OutputMapping {
  val count = 4
  def apply( output: Seq[Double] ) =
    new OutputMapping(
      Point(output(0), output(1) ),
      output(2),
      output(3)
    )
}

private case class InputMapping( inputs: Seq[Double] )

private object InputMapping {
  val count = 6
  def apply( pos: Point, speed: Point, rot: Double, time: Long, feedback: Seq[Double] ) =
    Seq( pos.x - Math.signum(pos.x) * Math.abs(feedback(0)),
      pos.y - Math.signum(pos.y) * Math.abs(feedback(1)),
      speed.x - Math.signum(speed.x) * Math.abs(feedback(3)),
      speed.y - Math.signum(speed.y) * Math.abs(feedback(4)),
      rot - Math.signum(rot) * Math.abs(feedback(5)),
      time.toDouble
    )
}
