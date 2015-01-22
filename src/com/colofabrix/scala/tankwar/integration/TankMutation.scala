package com.colofabrix.scala.tankwar.integration

import java.util
import java.util.Random

import com.colofabrix.scala.neuralnetwork.builders.abstracts.{BehaviourBuilder, StructureBuilder}
import com.colofabrix.scala.tankwar.Tank
import org.uncommons.watchmaker.framework.EvolutionaryOperator

import scala.collection.JavaConversions._

/**
 * Created by Fabrizio on 18/01/2015.
 */
class TankMutation(mutationProbability: Double, structureBuilder: StructureBuilder, behaviourBuilder: BehaviourBuilder) extends EvolutionaryOperator[Tank] {

  override def apply(list: util.List[Tank], random: Random): util.List[Tank] = {

    list.map { t => mutateTank(t) }

  }

  private def mutateTank(t: Tank): Tank = {
    null
  }

  private def mutateBiases(biases: Seq[Double]): Seq[Double] = {
    null
  }

  private def mutateWeights(biases: Seq[Double]): Seq[Double] = {
    null
  }
}
