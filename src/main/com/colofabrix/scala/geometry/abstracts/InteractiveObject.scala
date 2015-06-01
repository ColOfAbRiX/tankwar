package com.colofabrix.scala.geometry.abstracts

import com.colofabrix.scala.simulation.World

/**
 * An interactive object is an object that interacts, like it can see, it can hit and be hit, a {PhysicalObject}
 */
trait InteractiveObject {

  /**
   * Reference to the world
   */
  def world: World

  /**
   * The sight of the object in relation of a specific object
   *
   * It returns a shape that represents the sight that the current instance has towards object of another type
   *
   * @param that The class type of the object that we are interested in
   * @tparam T N/A
   * @return A {Shape} that represents the sight towards the object type of {that}
   */
  def sight[T <: PhysicalObject]( that: Class[T] ): Shape

  /**
   * Callback function used to signal the object that it has hit another object
   *
   * @param that The object that is being hit
   */
  def on_hits( that: PhysicalObject )

  /**
   * Callback function used to signal the Tank that it has been hit by a bullet
   *
   * @param that The object that hit the current instance
   */
  def on_isHit( that: PhysicalObject )

  /**
   * Callback function used to signal the Tank that a bullet is on its sight
   *
   * @param that The object that it's in the sight of the current one
   */
  def on_objectOnSight( that: PhysicalObject )

  /**
   * Callback function used to signal the object that its sight is exceeding the limits
   */
  def on_sightExceedingMax( maxAllowedArea: Double ): Unit
}