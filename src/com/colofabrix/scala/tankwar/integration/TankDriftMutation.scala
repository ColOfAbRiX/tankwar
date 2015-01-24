package com.colofabrix.scala.tankwar.integration

import java.util.Random

import org.uncommons.maths.number.NumberGenerator
import org.uncommons.maths.random.Probability

/**
 * A drift mutation means that if a value has to change the new value is
 * a modification of the old one, a drift from it, by a random value generated
 * by a proper distribution generator
 */
class TankDriftMutation(probability: Probability, generator: NumberGenerator[java.lang.Double]) extends TankFullMutation(probability) {

  override def mutateSeq(seq: Seq[Seq[Double]], rnd: Random, scale: Double): Seq[Seq[Double]] = {
    for( outer <- seq ) yield {
      for( value <- outer ) yield {
        if( rnd.nextDouble <= probability.doubleValue )
          value + generator.nextValue * 2.0 + scale - scale
        else
          value
      }
    }
  }

}
