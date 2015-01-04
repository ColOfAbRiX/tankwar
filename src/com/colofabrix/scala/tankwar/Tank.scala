package com.colofabrix.scala.tankwar

import com.colofabrix.scala.neuralnetwork.GenericNeuralNetwork
import com.colofabrix.scala.neuralnetwork.builders._

import scala.collection.mutable.Stack

/**
 * A Tank in the Game
 *
 * Created by Fabrizio on 02/01/2015.
 */
class Tank {
  var position: Point = Point(0, 0)
  var rotation: Double = 0.0
  var isShooting: Boolean = false
  var boundary: Shape = Circle(position, 20)

  private var time: Long = 0
  private val arena: Point = Point(500, 500)

  private var speed = Point(0.0, 0.0)
  private var direction = Point(1.0, 1.0)
  private var shoot_history = Stack(0.0, 0.0, 0.0)
  private def movement (pos: Double, i: Int, outputs: Seq[Double]) = pos + outputs(i) * direction(i) * speed(i)

  /*
   Inputs: 0=Position.x, 1=Position.y, 2=Speed.x, 3=Speed.y, 4=Rotation, 5=Time, 6=Feedback input
   Outputs: 0=Position.x, 1=Position.y, 2=Speed.x, 3=Speed.y, 4=Rotation, 5=Shoot, 6=Feedback output
   */
  val brain: GenericNeuralNetwork = new Random3LNetwork(7, 7, 7, Math.pow(2, -3)).build
  private var previous: Seq[Double] = Seq.fill(brain.n_outputs)(0.0)

  def stepForward() {
    time += 1

    // Calculating outputs
    val outputs = brain.output( Seq(position.x, position.y, speed.x, speed.y, rotation, time.toDouble, previous(6)) )
    previous = outputs

    // Updating direction: if it reaches a boundary I invert the direction on that axis
    val next = position.transform{ movement(_, _, outputs) }
    direction = direction.transform{ (d, i) => if( next(i) < 0 || next(i) > arena(i) ) -1.0 * d else d }

    // Create new speed
    speed = speed + Point( outputs(2), outputs(3) )
    speed = speed.transform( x => Math.max(Math.min(10, x),-10))

    // Updating position and rotation
    position = position.transform{ movement(_, _, outputs) }
    rotation += outputs(4) % 2 * Math.PI

    // It shoots it the corresponding output changes sign
    shoot_history = shoot_history.push(outputs(5)).take(3)
    isShooting = Math.signum(shoot_history(1) - shoot_history(0)) != Math.signum(shoot_history(2) - shoot_history(1))
    //isShooting = Math.abs(shoot_history(1) - shoot_history(0)) > 0.001
  }

  override def toString = s"$time;${position.x};${position.y};$rotation;$isShooting;${speed.x};${speed.y}".replace(".", ",")
}
