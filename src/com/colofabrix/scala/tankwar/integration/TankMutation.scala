package com.colofabrix.scala.tankwar.integration

import java.util
import java.util.Random

import com.colofabrix.scala.tankwar.Tank
import org.uncommons.watchmaker.framework.EvolutionaryOperator

/**
 * Created by Fabrizio on 18/01/2015.
 */
class TankMutation extends EvolutionaryOperator[Tank] {

  override def apply(list: util.List[Tank], random: Random): util.List[Tank] = {
    // TODO: Does nothing at the moment. To be completed
    val output = new util.ArrayList[Tank](list)
    output
  }

}
