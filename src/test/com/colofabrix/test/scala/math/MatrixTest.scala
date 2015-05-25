package com.colofabrix.test.scala.math

import com.colofabrix.scala.math.Matrix
import org.scalatest.{Matchers, WordSpec}

/**
 * Unit test for the {Matrix} class
 *
 * Created by Fabrizio on 07/05/2015.
 */
class MatrixTest extends WordSpec with Matchers {

  val test = new Matrix[Int](Seq.tabulate(3, 5)(_ * _))

  "Attributes" when {

    "dimensions" in {
      // Test that the dimensions corresponds to the right ones
      test.rows should equal(3)
      test.cols should equal(5)
    }

    "columns and rows" in {
      // Test that I cat fetch the right column and row
      test.row(2) should equal(Seq(0, 2, 4, 6, 8))
      test.col(3) should equal(Seq(0, 3, 6))
    }

    "transpose" when {
      // Test the transpose of the matrix
      val expected = new Matrix(Seq.tabulate(5, 3)(_ * _))
      test.transpose should equal(expected)
    }

    "diagonal" when {
      // Fetch the main diagonal from the matrix
      val expected = Seq(0, 1, 4)
      test.diagonal should equal(expected)
    }

    "rowset" when {
      val firstRow = new Matrix(Seq(Seq(0, 0, 0, 0, 0)))
      val secondRow = new Matrix(Seq(Seq(0, 1, 2, 3, 4)))

      test.rowSet(1) should equal(firstRow)
      test.rowSet(1, 1) should equal(secondRow)
    }

    "colset" when {
      val firstCol = new Matrix(Seq(Seq(0), Seq(0), Seq(0)))
      val secondCol = new Matrix(Seq(Seq(0), Seq(1), Seq(2)))

      test.colSet(1) should equal(firstCol)
      test.colSet(1, 1) should equal(secondCol)
    }
  }

  "Operators" when {

    "multiplication" must {
      val test2 = test.transpose
      val multiplied = new Matrix(Seq(
        Seq(0, 0,  0,  0,  0),
        Seq(0, 5,  10, 15, 20),
        Seq(0, 10, 20, 30, 40),
        Seq(0, 15, 30, 45, 60),
        Seq(0, 20, 40, 60, 80)
      ))

      // Test the multiplication
      test2 * test should equal(multiplied)
      // Test its non-commutativity
      test * test2 shouldNot equal(multiplied)
    }

    "exponentiation" must {
      val exp_test = new Matrix(Seq.tabulate(3, 3)(_ * _))
      val expected_0 = exp_test.toIdentity
      val expected_1 = exp_test.clone
      val expected_2 = new Matrix(Seq(Seq(0, 0, 0), Seq(0, 5, 10), Seq(0, 10, 20)))

      // Tests the first thee powers of the matrix
      exp_test ** 0 should equal(expected_0)
      exp_test ** 1 should equal(expected_1)
      exp_test ** 2 should equal(expected_2)
    }

    "to-identity" must {
      val expected = new Matrix(Seq.tabulate(3, 3) { (i, j) =>
        if (i == j) 1 else 0
      })

      // Test that I can get an identity matrix of the right dimensions
      test.toIdentity should equal(expected)
    }

    "equality" must {
      // Tests that two matrices are equal by content and not by reference
      val expected = new Matrix(Seq.tabulate(3, 5)(_ * _))
      test should equal(expected)
      test shouldNot be theSameInstanceAs expected
    }

    "clone" must {
      val cloned = test.clone
      // Test that a cloned matrix contains the same content of the original
      cloned should equal(test)
      // And also that it's not the same instance
      cloned shouldNot be theSameInstanceAs test
    }

    "mapping" must {
      val expected = new Matrix[Int](Seq.tabulate(3, 5)(2 * _ * _))

      val map1 = test map { (x, _, _) => 2 * x }
      val map2 = test map { (_, i, j) => 2 * i * j }

      map1 should equal(expected)
      map2 should equal(expected)
    }

    "update" must {
      val expected = new Matrix[Int](Seq(
        Seq(0, 0, 0, 0, 0),
        Seq(0, 123, 2, 3, 4),
        Seq(0, 2, 4, 6, 8)
      ))

      test.update((1, 1), 123) should equal(expected)
    }

    "addition" must {}

    "subtraction" must {}

    "multiplication by scalar" must {}
  }

}
