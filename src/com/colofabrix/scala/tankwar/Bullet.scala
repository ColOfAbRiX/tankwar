package com.colofabrix.scala.tankwar

/**
 * Represents a bullet shot by a Tank
 *
 * Created by Fabrizio on 02/01/2015.
 */
class Bullet( override val world: World, val tank: Tank, val proper_speed: Double ) extends  PhysicalObject {
  override var _position: Point = tank.position
  override var _speed = Point.fromPolar( proper_speed, tank.rotation ) + tank.speed
  override val boundary: Shape = Circle(_position, 3)

  override def stepForward(): Unit = {
    _position = _position + _speed
  }
}