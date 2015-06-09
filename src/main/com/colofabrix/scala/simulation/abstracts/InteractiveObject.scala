package com.colofabrix.scala.simulation.abstracts

import com.colofabrix.scala.geometry.abstracts.Shape
import com.colofabrix.scala.simulation.World

/**
 * An interactive object is an object that actively interacts, like it can see, it can hit and be hit, any kind of
 * {PhysicalObject}. From this point of view {PhysicalObject} can be thought as a passive interactive object
 *
 * This trait provides some callbacks that have to be implemented by the concrete class. These callback functions are
 * a common entry point and, usually, in the implementation, a specific match/case is used to distinguish the various
 * cases.
 */
trait InteractiveObject {

  /**
   * Reference to the world.
   *
   * The world can be used to obtain information about the simulation, the other objects or the limits of the world
   */
  def world: World

  /**
   * The sight of the object in relation of a specific object
   *
   * It returns a shape that represents the sight that the current instance has towards object of another type. For
   * instance, an {InteractiveObject} can see better, with a circular shape, a class of {PhysicalObject} but worse
   * another. This information is bound to the class type of the object being seen and not to a specific instance.
   *
   * @param that The class type of the object that we are interested in
   * @tparam T N/A
   * @return A {Shape} that represents the sight towards the object type of {that}
   */
  def sight[T <: PhysicalObject]( that: Class[T] ): Shape

  /**
   * Callback function used to signal the object that it has hit another object
   *
   * The meaning of "hit" is that one objects touches or overlaps the other.
   * This callback function should be called on the object that hits after the complementary function {on_isHit} on the
   * target objects.
   *
   * @param that The object that is being hit
   */
  def on_hits( that: PhysicalObject ): Unit

  /**
   * Callback function used to signal the Tank that it has been hit by a another object
   *
   * The meaning of "hit" is that one objects touches or overlaps the other.
   * This callback function should be called on the object that is hit after the complementary function {on_hits} on the
   * objects that does the action.
   *
   * @param that The object that hit the current instance
   */
  def on_isHit( that: PhysicalObject ): Unit

  /**
   * Callback function used to signal that a {PhysicalObject} is on its sight
   *
   * @param that The object that it's in the sight of the current one
   */
  def on_objectOnSight( that: PhysicalObject ): Unit

  /**
   * Callback function used to signal the object that its sight is exceeding the limits.
   *
   * Objects that, after calling this method, still don't respect the limit, are usually removed by the simulation
   */
  def on_sightExceedingMax( maxAllowedArea: Double ): Unit
}
