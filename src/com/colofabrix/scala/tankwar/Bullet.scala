package com.colofabrix.scala.tankwar

import com.colofabrix.scala.tankwar.geometry._

/**
 * Represents a bullet shot by a Tank
 *
 * Created by Fabrizio on 02/01/2015.
 */
class Bullet( override val world: World, val tank: Tank, val proper_speed: Double ) extends  PhysicalObject {
  override var _position: Vector2D = tank.position
  override var _speed = Vector2D.fromRT( proper_speed, tank.rotation ) + tank.speed
  override val boundaries: Shape = Circle(_position, 3)

  override def stepForward(): Unit = {
    _position = _position + _speed
  }

  def hit(tank: Tank): Unit = {}
}