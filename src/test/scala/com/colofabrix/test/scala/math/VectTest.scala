/*
 * Copyright (C) 2016 Fabrizio
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language
 * governing permissions and limitations under the License.
 */

package com.colofabrix.test.scala.math

import com.colofabrix.scala.math.VectUtils.Support
import com.colofabrix.scala.math.{ DoubleWithAlmostEquals, RTVect, Vect, XYVect }
import jdk.nashorn.internal.runtime.regexp.joni.exception.ValueException
import org.scalatest.{ FlatSpec, Matchers }

/**
  * Unit testing for Vect
  */
abstract class VectTest extends FlatSpec with Matchers {
  /* Implicit used to define Double equality */
  implicit val doubleEquality = new org.scalactic.Equality[Double] {
    def areEqual( a: Double, b: Any ): Boolean = b match {
      case d: Double => a ~== d
      case _ => false
    }
  }

  "Operator equals" should "satisfy equality properties" in {
    val vect1: Vect = XYVect( -20, 0 )
    val vect2: Vect = XYVect( -20, 0 )
    val vect3: Vect = XYVect( -20, 0 )

    // Reflexive property
    vect1 shouldEqual vect1
    // Symmetry property
    vect3 shouldEqual vect2
    vect1 shouldEqual vect2
    // Transitive property
    vect2 shouldEqual vect1
  }

  "Operator +" should "satisfy addition properties" in {
    val vect1: Vect = XYVect( 10, 15 )
    val vect2: Vect = XYVect( 20, 25 )
    val vect3: Vect = XYVect( -30, -10 )
    val vect4: Vect = XYVect( -vect1.x, -vect1.y )

    // Commutative property
    vect1 + vect2 shouldEqual vect2 + vect1
    // Associative property
    (vect1 + vect2) + vect3 shouldEqual vect1 + (vect2 + vect3)
    // Identity element
    vect1 + Vect.zero shouldEqual vect1
    // Inverse element
    vect1 + vect4 shouldEqual Vect.zero
  }

  "Operator -" should "be a sum of the opposite element" in {
    val vect1: Vect = XYVect( 10, 15 )
    val vect2: Vect = XYVect( 20, 25 )
    val vect3: Vect = XYVect( -vect2.x, -vect2.y )
    val vect4: Vect = RTVect( vect2.ρ, vect2.ϑ + Math.PI )

    vect1 - vect2 shouldEqual vect1 + vect3
    vect1 - vect2 shouldEqual vect1 + vect4
  }

  "Operator *" should "satisfy the scalar multiplication properties" in {
    val vector1 = XYVect( 15, 25 )
    val vector2 = RTVect( 5, 10 )
    val scalar1 = Math.exp( 1 )
    val scalar2 = Math.sqrt( 2 )

    // Commutative property
    vector1 * scalar1 shouldEqual scalar1 * vector1
    // Additivity in the scalar
    (scalar1 + scalar2) * vector1 shouldEqual scalar1 * vector1 + scalar2 * vector1
    // Additivity in the vector
    scalar1 * (vector1 + vector2) shouldEqual scalar1 * vector1 + scalar1 * vector2
    // Compatibility of product of scalars with scalar multiplication
    (scalar1 * scalar2) * vector1 shouldEqual scalar1 * (scalar2 * vector1)
    // Existence of unit scalar for scalar multiplication
    1.0 * vector1 shouldEqual vector1
    // Existence of zero scalar for scalar multiplication
    0.0 * vector1 shouldEqual Vect.zero
    // Additive inverse
    -1.0 * vector1 shouldEqual XYVect( -vector1.x, -vector1.y )
  }

  "Operator ∙" should "satisfy the scalar product properties" in {
    val vector1 = XYVect( 15, 25 )
    val vector2 = XYVect( 10, 5 )
    val vector3 = RTVect( 10, 1 )
    val vector4 = RTVect( 20, 3 )
    val vector5 = RTVect( 10, vector4.ϑ + Math.PI / 2.0 )
    val scalar1 = Math.exp( 1 )
    val scalar2 = Math.PI

    // Commutative property
    vector1 ∙ vector2 shouldEqual (vector2 ∙ vector1)
    // Distributive over vector addition
    vector1 ∙ (vector2 + vector3) shouldEqual (vector1 ∙ vector2) + (vector1 ∙ vector3)
    // Bilinear
    vector1 ∙ (vector2 * scalar1 + vector3) shouldEqual (vector1 ∙ vector2 * scalar1) + (vector1 ∙ vector3)
    // Scalar multiplication
    (vector1 * scalar1) ∙ (vector2 * scalar2) shouldEqual (scalar1 * scalar2) * (vector1 ∙ vector2)
    // Orthogonal
    vector4 ∙ vector5 shouldEqual 0.0
  }

  "Operator ×" should "satisfy the vector product properties" in {
    val vector1 = XYVect( 15, 25 )
    val vector2 = XYVect( 10, 5 )
    val vector3 = RTVect( 10, 1 )
    val scalar1 = Math.exp( 1 )

    // Anticommutative property
    vector1 × vector2 shouldEqual -1.0 * (vector2 × vector1)
    // Distributive over addition
    (vector1 × (vector2 + vector3)) shouldEqual (vector1 × vector2) + (vector1 × vector3)
    // Compatible with scalar multiplication
    ((vector1 * scalar1) × vector2) shouldEqual (vector1 × (vector2 * scalar1))
    ((vector1 * scalar1) × vector2) shouldEqual ((vector1 × vector2) * scalar1)
  }
}

/**
  * Unit testing for XYVect
  */
class XYVectTest extends VectTest {
  "The constructor" should "create correct cartesian coordinates" in {
    // Test vector 1: t=10u r=90° -> x=0, y=10
    val vect1 = XYVect( 0, 10 )

    vect1.x shouldEqual 0.0
    vect1.y shouldEqual 10.0
  }

  "The constructor" should "convert to polar coordinates" in {
    // Test vector 1: t=10u r=90° -> x=0, y=10
    val vect1 = XYVect( 0, 10 )
    // Test vector 1: t=10u r=180° -> x=-10, y=0
    val vect2 = XYVect( -10, 0 )
    // Test vector 2: t=10u r=-45° -> x=7.071067811865475, y=-7.071067811865475
    val vect3 = XYVect( 7.071067811865475, -7.071067811865475 )

    vect1.ρ shouldEqual 10.0
    vect1.ϑ shouldEqual Math.PI / 2.0

    vect2.ρ shouldEqual 10.0
    vect2.ϑ shouldEqual Math.PI

    vect3.ρ shouldEqual 10.0
    vect3.ϑ shouldEqual 7.0 / 4.0 * Math.PI
  }

  "XYVect.zero" should "return the Origin point" in {
    XYVect.zero.x shouldEqual 0.0
    XYVect.zero.y shouldEqual 0.0
    XYVect.zero shouldEqual XYVect( 0, 0 )
    XYVect.zero shouldEqual RTVect( 0, 0 )
  }


  "Operator equals" should "return true when given an equivalent XYVect" in {
    val vect: Vect = XYVect( -20, 0 )

    // Equality with itself
    vect shouldEqual vect
    // Equality with an equivalent XYVect
    vect shouldEqual XYVect( -20, 0 )
  }

  "Operator equals" should "return true when given an equivalent RTVect" in {
    XYVect( -20, 0 ) shouldEqual RTVect( 20, Math.PI )
  }

  "Operator equals" should "return false with non Vect objects" in {
    XYVect( -20, 0 ) shouldNot equal(2.0)
  }


  "Operator +" should "add a XYVect" in {
    val vect1: Vect = XYVect( 10, 15 )
    val vect2: Vect = XYVect( 20, 25 )

    // Actual computation
    vect1 + vect2 shouldEqual XYVect( 30, 40 )
  }

  "Operator +" should "add a RTVect" in {
    val vect1: Vect = XYVect( 10, 15 )
    val vect2: Vect = RTVect( 20, 0 )

    // Actual computation
    vect1 + vect2 shouldEqual XYVect( 30, 15 )
  }


  "Operator *" should "find the scalar multiplication with a constant" in {
    val vector = XYVect( 15, 25 )
    val scalar = 12.34

    // Actual calculations
    vector * scalar shouldEqual XYVect( 185.1, 308.5 )
  }

  "Operator /" should "find the scalar product with the inverse of a constant" in {
    val vector = XYVect( 15, 25 )
    val scalar = Math.exp( 1 )

    // Actual calculation
    vector / scalar shouldEqual vector * (1.0 / scalar)
  }

  "Operator ∙" should "find the scalar product of a XYVect and a RTVect" in {
    val vector1 = XYVect( 15, 25 )
    val vector2 = XYVect( 10, 5 )
    val vector3 = RTVect( 10, 1 )

    // Actual calculations
    vector1 ∙ vector2 shouldEqual 275.0
    vector1 ∙ vector3 shouldEqual 291.41309208219513
  }

  "Operator ×" should "find the vector product of a XYVect and a RTVect" in {
    val vector1 = XYVect( 15, 25 )
    val vector2 = XYVect( 10, 5 )
    val vector3 = RTVect( 10, 1 )

    // Actual calculations
    vector1 × vector2 shouldEqual -175.0
    vector1 × vector3 shouldEqual -8.854928745850472
  }


  "Operator →" should "project a vector onto a XYVect" in {
    val vector = XYVect( 100, 100 )
    val axis = XYVect( 10, 0 )

    // Projection between two vectors
    vector → axis shouldEqual XYVect( 100, 0 )
    // Projection of a vector onto itself
    vector → vector shouldEqual vector
  }

  "Operator →" should "project a vector onto a RTVect" in {
    val vector = XYVect( 100, 100 )
    val axis = RTVect( 10, 0 )

    // Projection between two vectors
    vector → axis shouldEqual XYVect( 100, 0 )
  }

  "Operator →" should "project using a Zero vector" in {
    // Projection of the Zero vector
    val vector = XYVect( 100, 100 )

    vector → XYVect.zero shouldEqual Vect.zero
    XYVect.zero → vector shouldEqual Vect.zero
  }


  "Operator ⊣" should "find the CCW perpendicular of the vector" in {
    val vect1 = XYVect( 10, 10 )
    val vect2 = XYVect( 10, 0 )

    vect1.⊣ shouldEqual XYVect( 10, -10 )
    vect2.⊣ shouldEqual XYVect( 0, -10 )
    an[ValueException] should be thrownBy XYVect( 0, 0 ).⊣
  }

  "Operator ⊢" should "find the CW perpendicular of the vector" in {
    val vect1 = XYVect( 10, 10 )
    val vect2 = XYVect( 10, 0 )

    vect1.⊢ shouldEqual XYVect( -10, 10 )
    vect2.⊢ shouldEqual XYVect( 0, 10 )
    an[ValueException] should be thrownBy XYVect( 0, 0 ).⊢
  }

  "Operator v" should "find the versor of the vector" in {
    XYVect( 10, 10 ).v shouldEqual XYVect( 0.7071067811865475, 0.7071067811865475 )
    an[ValueException] should be thrownBy XYVect.zero.v
  }

  "Operator n" should "find the normal of the vector" in {
    XYVect( 10, 10 ).n shouldEqual XYVect( 0.7071067811865475, -0.7071067811865475 )
    an[ValueException] should be thrownBy XYVect.zero.n
  }

  "Operator ↺" should "rotate the vector" in {
    val vector = XYVect( 10, 0 )

    // Positive angle rotation
    vector ↺ (Math.PI / 2.0) shouldEqual XYVect( 0, 10 )
    // Negative angle rotation
    vector ↺ -(Math.PI / 2.0) shouldEqual XYVect( 0, -10 )
    // Zero angle rotation
    vector ↺ 0.0 shouldEqual vector
    // Multi-cycle angle rotation
    vector ↺ (2.0 * Math.PI) shouldEqual vector
  }

  "Operator quadrant" should "find the quadrant of an RTVect" in {
    // Normal quadrants
    XYVect( 10, 10 ).quadrant shouldEqual 1
    XYVect( -10, 10 ).quadrant shouldEqual 2
    XYVect( -10, -10 ).quadrant shouldEqual 3
    XYVect( 10, -10 ).quadrant shouldEqual 4

    // Axes and origin
    XYVect( 10, 0 ).quadrant shouldEqual 0
    XYVect( 0, 10 ).quadrant shouldEqual 0
    XYVect( -10, 0 ).quadrant shouldEqual 0
    XYVect( 0, -10 ).quadrant shouldEqual 0
    XYVect( 0, 0 ).quadrant shouldEqual 0
  }

  "Operator unit" should "create a unitary vector" in {
    XYVect.unit( Math.PI / 2.0 ) shouldEqual XYVect( 0, 1 )
  }


  "Method toString" should "represent the XYVect" in {
    XYVect(1, 1).toString shouldEqual "Vec(x: 1.0, y: 1.0)"
  }
}

/**
  * Unit testing for RTVect
  */
class RTVectTest extends VectTest {
  "The constructor" should "create correct polar coordinates" in {
    // Test vector 1: t=10u r=90° -> x=0, y=10
    val vect1 = RTVect( 10, Math.PI / 2.0 )

    vect1.ρ shouldEqual 10.0
    vect1.ϑ shouldEqual Math.PI / 2.0
  }

  "The constructor" should "convert to cartesian coordinates" in {
    // Test vector 1: t=10u r=90° -> x=0, y=10
    val vect1 = RTVect( 10, Math.PI / 2 )
    // Test vector 1: t=10u r=180° -> x=-10, y=0
    val vect2 = RTVect( 10, Math.PI )
    // Test vector 2: t=10u r=-45° -> x=7.0710678, y=-7.0710678
    val vect3 = RTVect( 10, -Math.PI / 4 )

    vect1.x shouldEqual 0.0
    vect1.y shouldEqual 10.0

    vect2.x shouldEqual -10.0
    vect2.y shouldEqual 0.0

    vect3.x shouldEqual 7.071067811865475
    vect3.y shouldEqual -7.071067811865475
  }

  "The constructor" should "restrict angles 0 <= α < 2π" in {}

  "The constructor" should "fail with negative ρ" in {
    an[IllegalArgumentException] should be thrownBy RTVect( -1, 1 )
  }

  "RTVect.zero" should "return the Origin point" in {
    RTVect.zero.ρ shouldEqual 0.0
    RTVect.zero.ϑ shouldEqual 0.0
    RTVect.zero shouldEqual RTVect( 0, 0 )
    RTVect.zero shouldEqual XYVect( 0, 0 )
  }


  "Operator equals" should "return true when given an equivalent RTVect" in {
    val vect: Vect = RTVect( 20, 5 * Math.PI )

    // Equality with itself
    vect shouldEqual vect
    // Equality with an equivalent RTVect
    vect shouldEqual RTVect( 20, Math.PI )
  }

  "Operator equals" should "return true when given an equivalent XYVect" in {
    RTVect( 20, Math.PI ) shouldEqual XYVect( -20, 0 )
  }

  "Operator equals" should "return false with non Vect objects" in {
    RTVect( 20, 0 ) shouldNot equal(2.0)
  }


  "Operator +" should "add a RTVect" in {
    val vect1: Vect = RTVect( 10, 0 )
    val vect2: Vect = RTVect( 20, Math.PI / 2.0 )

    // Actual computation
    vect1 + vect2 shouldEqual XYVect( 10, 20 )
  }

  "Operator +" should "add a XYVect" in {
    val vect1: Vect = RTVect( 10, 0 )
    val vect2: Vect = XYVect( 0, 20 )

    // Actual computation
    vect1 + vect2 shouldEqual XYVect( 10, 20 )
  }


  "Operator *" should "find the scalar multiplication with a constant" in {
    val vector = RTVect( 5, 3 )
    val scalar = 12.34

    // Actual calculations
    vector * scalar shouldEqual RTVect( 61.7, 3 )
  }

  "Operator /" should "find the scalar product with the inverse of a constant" in {
    val vector = RTVect( 15, 2 )
    val scalar = Math.exp( 1 )

    // Actual calculation
    vector / scalar shouldEqual vector * (1.0 / scalar)
  }

  "Operator ∙" should "find the scalar product of a RTVect and a XYVect" in {
    val vector1 = RTVect( 10, 1 )
    val vector2 = RTVect( 20, 3 )
    val vector3 = XYVect( 15, 25 )

    // Actual calculations
    vector1 ∙ vector2 shouldEqual -83.22936730942848
    vector1 ∙ vector3 shouldEqual 291.4130920821951
  }

  "Operator ×" should "find the vector product of a RTVect and a XYVect" in {
    val vector1 = RTVect( 10, 1 )
    val vector2 = RTVect( 20, 3 )
    val vector3 = XYVect( 15, 25 )

    // Actual calculations
    vector1 × vector2 shouldEqual 181.85948536513635
    vector1 × vector3 shouldEqual 8.854928745850465
  }


  "Operator →" should "project a vector onto a RTVect" in {
    val vector = RTVect( 10, Math.PI / 2.0 )
    val axis = RTVect( 10, Math.PI / 4.0 )

    // Projection between two vectors
    vector → axis shouldEqual XYVect( 5, 5 )
    // Projection of a vector onto itself
    vector → vector shouldEqual vector
  }

  "Operator →" should "project a vector onto a XYVect" in {
    val vector = RTVect( 10, Math.PI / 2.0 )
    val axis = XYVect( 100, 100 )

    // Projection between two vectors
    vector → axis shouldEqual XYVect( 5, 5 )
  }

  "Operator →" should "project using a Zero vector" in {
    // Projection of the Zero vector
    val vector = RTVect( 100, 1 )

    vector → RTVect.zero shouldEqual Vect.zero
    RTVect.zero → vector shouldEqual Vect.zero
  }


  "Operator ⊣" should "find the CCW perpendicular of the vector" in {
    val vect1 = RTVect( 10, Math.PI / 4.0 )
    val vect2 = RTVect( 10, 0 )

    vect1.⊣ shouldEqual RTVect( 10, 3.0 * Math.PI / 4.0 )
    vect2.⊣ shouldEqual RTVect( 10, Math.PI / 2.0 )
    an[ValueException] should be thrownBy RTVect( 0, 0 ).⊣
  }

  "Operator ⊢" should "find the CW perpendicular of the vector" in {
    val vect1 = RTVect( 10, Math.PI / 4.0 )
    val vect2 = RTVect( 10, 0 )

    vect1.⊢ shouldEqual RTVect( 10, -Math.PI / 4.0 )
    vect2.⊢ shouldEqual RTVect( 10, -Math.PI / 2.0 )
    an[ValueException] should be thrownBy RTVect( 0, 0 ).⊢
  }

  "Operator v" should "find the versor of the vector" in {
    RTVect( 10, 1 ).v shouldEqual RTVect( 1, 1 )
    an[ValueException] should be thrownBy RTVect.zero.v
  }

  "Operator n" should "find the normal of the vector" in {
    RTVect( 10, Math.PI / 4.0 ).n shouldEqual RTVect( 1, 3.0 * Math.PI / 4.0 )
    an[ValueException] should be thrownBy RTVect.zero.n
  }

  "Operator ↺" should "rotate the vector" in {
    val vector = RTVect( 10, Math.PI / 4.0 )

    // Positive angle rotation
    vector ↺ (Math.PI / 2.0) shouldEqual RTVect( 10, 3.0 * Math.PI / 4.0 )
    // Negative angle rotation
    vector ↺ -Math.PI shouldEqual RTVect( 10, 5.0 * Math.PI / 4.0 )
    // Zero angle rotation
    vector ↺ 0.0 shouldEqual vector
    // Multi-cycle angle rotation
    vector ↺ (2.0 * Math.PI) shouldEqual vector
  }

  "Operator quadrant" should "find the quadrant of an RTVect" in {
    // Normal quadrants
    RTVect( 10, Math.PI / 4.0 ).quadrant shouldEqual 1
    RTVect( 10, 3.0 * Math.PI / 4.0 ).quadrant shouldEqual 2
    RTVect( 10, 5.0 * Math.PI / 4.0 ).quadrant shouldEqual 3
    RTVect( 10, 7.0 * Math.PI / 4.0 ).quadrant shouldEqual 4

    // Axes and origin
    RTVect( 10, 0 ).quadrant shouldEqual 0
    RTVect( 10, Math.PI / 2.0 ).quadrant shouldEqual 0
    RTVect( 10, Math.PI ).quadrant shouldEqual 0
    RTVect( 10, 3.0 * Math.PI / 2.0 ).quadrant shouldEqual 0
    RTVect( 0, 0 ).quadrant shouldEqual 0
  }

  "Operator unit" should "create a unitary vector" in {
    RTVect.unit( 3 ) shouldEqual RTVect( 1, 3 )
  }


  "Method toString" should "represent the RTVect" in {
    RTVect(1, 1).toString shouldEqual "Vec(ρ: 1.0, ϑ: 1.0)"
  }
}
