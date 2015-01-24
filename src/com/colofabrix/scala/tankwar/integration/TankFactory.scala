package com.colofabrix.scala.tankwar.integration

import java.util
import java.util.Random

import com.colofabrix.scala.tankwar.{Tank, World}
import org.uncommons.watchmaker.framework.factories.AbstractCandidateFactory

import scala.collection.JavaConversions._

/**
 * Created by Fabrizio on 18/01/2015.
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
