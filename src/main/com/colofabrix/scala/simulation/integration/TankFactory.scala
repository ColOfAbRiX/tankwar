package com.colofabrix.scala.simulation.integration

import java.util
import java.util.Random

import com.colofabrix.scala.simulation.{Tank, World}
import org.uncommons.watchmaker.framework.factories.AbstractCandidateFactory

import scala.collection.JavaConversions._

/**
 * Factory to generate the initial Tank population.
 *
 * Required by Watchmaker library
 */
class TankFactory(world: World) extends AbstractCandidateFactory[Tank] {

  override def generateInitialPopulation(i: Int, random: Random): util.List[Tank] = {
    (0 until i).foreach( _ => generateRandomCandidate(random) )
    world.tanks
  }

  override def generateInitialPopulation(i: Int, collection: util.Collection[Tank], random: Random): util.List[Tank] = {
    generateInitialPopulation(i, random)
  }

  override def generateRandomCandidate(random: Random): Tank = {
    world.createAndAddDefaultTank(Tank.defaultRandomReader(random))
  }

}
