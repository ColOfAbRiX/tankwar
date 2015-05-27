package com.colofabrix.test.scala.math

import com.colofabrix.scala.math.NetworkMatrix
import org.scalatest.{Matchers, WordSpec}

import scala.Double._

/**
 * Created by Fabrizio on 24/05/2015.
 */
class NetworkMatrixTest extends WordSpec with Matchers {

  "Other operators" must {

    "equality" in {
      val test = new NetworkMatrix(Seq(
        Seq(NaN, NaN, 1.0, -1.0),
        Seq(NaN, NaN, -0.5, 0.5),
        Seq(NaN, NaN, NaN, NaN),
        Seq(NaN, NaN, NaN, NaN),
        Seq(NaN, NaN, NaN, NaN)
      ), Seq(0, 1), Seq(2, 3))

      val isEqual = new NetworkMatrix(Seq(
        Seq(NaN, NaN, 1.0, -1.0),
        Seq(NaN, NaN, -0.5, 0.5),
        Seq(NaN, NaN, NaN, NaN),
        Seq(NaN, NaN, NaN, NaN),
        Seq(NaN, NaN, NaN, NaN)
      ), Seq(0, 1), Seq(2, 3))

      val isNotEqual1 = new NetworkMatrix(Seq(
        Seq(NaN, NaN, 1.0, -1.0),
        Seq(NaN, 0.2, NaN, 0.5),
        Seq(1.3, NaN, NaN, NaN),
        Seq(NaN, NaN, NaN, NaN),
        Seq(NaN, NaN, NaN, NaN)
      ), Seq(0, 1), Seq(2, 3))

      val isNotEqual2 = new NetworkMatrix(Seq(
        Seq(NaN, NaN, 1.0, -1.0),
        Seq(NaN, NaN, -0.5, 0.5),
        Seq(NaN, NaN, NaN, NaN),
        Seq(NaN, NaN, NaN, NaN),
        Seq(NaN, NaN, NaN, NaN)
      ), Seq(0, 1), Seq(1, 2))

      (test equals isEqual) should equal(true)
      (test equals isNotEqual1) should equal(false)
      (test equals isNotEqual2) should equal(false)
    }

    "toNaN" must {
      val test = new NetworkMatrix(Seq(
        Seq(NaN, NaN, 1.0, -1.0),
        Seq(NaN, NaN, -0.5, 0.5),
        Seq(NaN, NaN, NaN, NaN),
        Seq(NaN, NaN, NaN, NaN),
        Seq(NaN, NaN, NaN, NaN)
      ), Seq(0, 1), Seq(2, 3))

      val reference = new NetworkMatrix(Seq(
        Seq(NaN, NaN, NaN, NaN),
        Seq(NaN, NaN, NaN, NaN),
        Seq(NaN, NaN, NaN, NaN),
        Seq(NaN, NaN, NaN, NaN),
        Seq(NaN, NaN, NaN, NaN)
      ), Seq(0, 1), Seq(2, 3))

      (test.toNaN equals reference) should equal(true)
    }

    "isAllNaN" must {

      val test = new NetworkMatrix(Seq(
        Seq(NaN, NaN, NaN, NaN),
        Seq(NaN, NaN, NaN, NaN),
        Seq(NaN, NaN, NaN, NaN),
        Seq(NaN, NaN, NaN, NaN),
        Seq(NaN, NaN, NaN, NaN)
      ), Seq(0, 1), Seq(2, 3))

      test.isAllNaN should equal(true)
    }

  }

}
