/*
 * Copyright (C) 2015 Fabrizio Colonna
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

package com.colofabrix.scala.math

import scala.collection.mutable
import scala.reflect.ClassTag
import scala.language.postfixOps

/**
 * Represents an immutable Matrix and the possible operations on it
 *
 * @param matrix A sequence of sequence, the initializer of the matrix.
 * @param n N/A
 * @param m N/A
 * @tparam T Data type of the matrix elements
 */
@SuppressWarnings( Array( "TraversableHead" ) ) // Applied after checking the whole class. This is safe ATM
class Matrix[T]( val matrix: Seq[Seq[T]] )( implicit n: Numeric[T], m: ClassTag[T] ) {

  import n._

  /**
   * Mutable constructor
   *
   * @param matrix A mutable sequence of mutable sequence, the initializer of the matrix
   */
  def this( matrix: mutable.Seq[mutable.Seq[T]] )( implicit n: Numeric[T], m: ClassTag[T] ) {
    this( matrix.toSeq )
  }

  // Constraint for the construction: minimum size greater than zero and that all the rows are of the same length
  require( matrix.nonEmpty || matrix( 0 ).nonEmpty, "The columns or the rows of the matrix must not be empty" )
  require( matrix.forall( _.length == matrix( 0 ).length ), "The length of every row must match between each other" )

  /**
   * Number of rows of the matrix
   */
  lazy val rows = matrix.length

  /**
   * Number of columns of the matrix
   */
  lazy val cols = matrix.head.length

  /**
   * Main diagonal of the matrix
   */
  lazy val diagonal = Seq.tabulate( Math.min( rows, cols ) ) { i ⇒ matrix( i )( i ) }

  /**
   * Transpose of the matrix
   */
  lazy val transpose = new Matrix( this.colSet() )

  /**
   * Gets a row of the matrix
   *
   * @param i The number of the row to return
   * @return The i-th row of the matrix
   */
  def row( i: Int ) = matrix( i )

  /**
   * Gets the matrix as a Seq of rows
   *
   * @return A Seq() containing the rows of the matrix
   */
  def rowSet() = matrix

  /**
   * Gets a subset of the rows of the matrix
   *
   * @param count The number of rows to return starting from row-0
   * @return The first {count} rows of the matrix
   */
  def rowSet( count: Int ) = new Matrix(
    matrix.take( count )
  )

  /**
   * Gets a subset of the rows of the matrix
   *
   * @param start The row from which to start
   * @param count The number of rows to return
   * @return {count} number of rows from the start-th row
   */
  def rowSet( start: Int, count: Int ) = new Matrix(
    matrix.take( start + count ).takeRight( count )
  )

  /**
   * Gets a column of the matrix
   *
   * @param i The number of the column to return
   * @return The i-th column of the matrix
   */
  def col( i: Int ) = matrix map ( _( i ) )

  /**
   * Gets the matrix as a Seq of columns
   *
   * @return A Seq() containing the columns of the matrix
   */
  def colSet() = matrix.transpose

  /**
   * Gets a subset of the rows of the matrix
   *
   * @param count The number of rows to return starting from row-0
   * @return The first {count} rows of the matrix
   */
  def colSet( count: Int ) = new Matrix(
    matrix.transpose.take( count ).transpose
  )

  /**
   * Gets a subset of the rows of the matrix
   *
   * @param start The row from which to start
   * @param count The number of rows to return
   * @return {count} number of rows from the start-th row
   */
  def colSet( start: Int, count: Int ) = new Matrix(
    matrix.transpose.take( start + count ).takeRight( count ).transpose
  )

  /**
   * Maps each element of the matrix in a new element of
   * a new matrix
   *
   * @param f The mapping function that returns a value for the new cells
   * @return A new matrix where each element is a mapping from the original matrix
   */
  def map[U]( f: () ⇒ U )( implicit n: Numeric[U], m: ClassTag[U] ): Matrix[U] =
    this.map( ( _, _, _ ) ⇒ f() )

  /**
   * Maps each element of the matrix in a new element of
   * a new matrix
   *
   * @param f The mapping function that provides the value of the cell and returns the value for the new cell
   * @return A new matrix where each element is a mapping from the original matrix
   */
  def map[U]( f: T ⇒ U )( implicit n: Numeric[U], m: ClassTag[U] ): Matrix[U] =
    this.map( ( x, _, _ ) ⇒ f( x ) )

  /**
   * Maps each element of the matrix in a new element of
   * a new matrix
   *
   * @param f The mapping function that provides the i and j indexes and returns the value for the new cell
   * @return A new matrix where each element is a mapping from the original matrix
   */
  def map[U]( f: ( Int, Int ) ⇒ U )( implicit n: Numeric[U], m: ClassTag[U] ): Matrix[U] =
    this.map( ( _, i, j ) ⇒ f( i, j ) )

  /**
   * Maps each element of the matrix in a new element of
   * a new matrix
   *
   * @param f The mapping function that provides the value of the cell and the i and j indexes and returns the value for the new cell
   * @return A new matrix where each element is a mapping from the original matrix
   */
  def map[U]( f: ( T, Int, Int ) ⇒ U )( implicit n: Numeric[U], m: ClassTag[U] ): Matrix[U] = {
    new Matrix[U](
      Seq.tabulate( rows, cols ) { ( i, j ) ⇒
        f( matrix( i )( j ), i, j )
      }
    )
  }

  /**
   * Returns an element of the matrix
   *
   * @param i A tuple that specify the i and j index of the element to return
   * @return The element at position (i, j)
   */
  def apply( i: ( Int, Int ) ) = matrix( i._1 )( i._2 )

  /**
   * Updates an element of the matrix
   *
   * @param k A tuple that specify the i and j index of the element to return
   * @param value The new value to set
   * @return A new matrix with the changed value
   */
  def update( k: ( Int, Int ), value: T ) = {
    val before: Seq[Seq[T]] = matrix.take( k._1 )
    val row: Seq[T] = matrix( k._1 ).updated( k._2, value )
    val after: Seq[Seq[T]] = matrix.takeRight( this.rows - k._1 - 1 )
    new Matrix( before ++ Seq( row ) ++ after )
  }

  /**
   * Clones the matrix in a new one
   *
   * @return A new matrix with the same values as the current one
   */
  override def clone = new Matrix( Seq.tabulate( rows, cols ) { ( i, j ) ⇒ matrix( i )( j ) } )

  /**
   * Determines if two matrices are equals
   *
   * @param obj The other object to compare
   * @return true if the other object is a matrix identical to the current one
   */
  override def equals( obj: Any ): Boolean = {
    // Check if the two objects can be compared
    if ( !( this canEqual obj ) ) return false

    obj match {
      case that: Matrix[T] ⇒

        // Fast check with the number of inputs
        if ( rows != that.rows || cols != that.cols ) return false

        // Checking every element
        for ( i ← matrix.indices.par; j ← matrix( i ).indices.par ) {
          if ( matrix( i )( j ) != that( ( i, j ) ) ) return false
        }

        true

      case _ ⇒ false
    }
  }

  /**
   * It is called from equals to make sure that the objects are comparable both ways
   *
   * @see http://www.artima.com/pins1ed/object-equality.html
   * @param other The other object to check
   * @return The method should return true if the other object is an instance of the class in which canEqual is (re)defined, false otherwise.
   */
  protected def canEqual( other: Any ): Boolean = other match {
    case _: Matrix[T] ⇒ true
    case _ ⇒ false
  }

  override def hashCode = matrix.foldLeft( 0 ) { ( res, row ) ⇒
    41 * res +
      row.foldLeft( 41 ) { ( res2, x ) ⇒
        41 * res2 + x.hashCode()
      }
  }

  /**
   * Matrix multiplication
   *
   * @see http://rosettacode.org/wiki/Matrix_multiplication#Scala
   * @param other The second element of the multiplication
   * @return A new matrix that is the result of a matrix multiplication between the current one and {that}
   */
  def *( other: Matrix[T] ): Matrix[T] = new Matrix(
    {
      val c = other.transpose.toSeq
      for ( row ← matrix )
        yield for ( col ← c )
        yield ( row.zip( col ) map { Function.tupled( _ * _ ) } ).sum
    }
  )

  /**
   * Exponentiation of the matrix
   *
   * Valid only for non-negative powers and for square matrices
   *
   * Implementation of the "exponentiation by squaring" algorithm
   * @see http://www.programminglogic.com/fast-exponentiation-algorithms/
   *      Ref: https://stackoverflow.com/questions/12311869/fast-matrix-exponentiation
   *
   * @param p The exponent to raise the matrix, a non-negative integer
   * @return The current matrix multiplied by itself p times
   */
  @SuppressWarnings( Array( "org.brianmckenna.wartremover.warts.Var" ) )
  def **( p: Int ) = {
    require( p >= 0 )
    require( rows == cols )

    var result = toIdentity
    var n = p
    var b = this

    while ( n > 0 ) {
      if ( n % 2 == 0 ) {
        b = b * b
        n /= 2
      }
      else {
        result = result * b
        n -= 1
      }
    }

    result
  }

  /**
   * Performs a scalar multiplication of the matrix
   *
   * @param other The scalar to multiply with the matrix
   * @return A new matrix that is the original matrix multiplied by the supplied scalar
   */
  def *( other: T ) = this map { x: T ⇒ x * other }

  /**
   * Returns a square identity matrix of the same size of the current matrix
   *
   * Note that an identity matrix can only be square and thus this function will return an identity
   * matrix as large as the smallest size of the matrix
   *
   * @return An new, identity matrix of the same size of the current matrix
   */
  def toIdentity: Matrix[T] = Matrix.identity[T]( Math.min( rows, cols ) )

  /**
   * Returns a zero matrix of the same size of the current matrix
   *
   * @return A new, zero matrix of the same size of the current matrix
   */
  def toZero: Matrix[T] = this map { _ ⇒ n.zero }

  /**
   * String representation of the matrix
   *
   * @return A string displaying graphically the matrix
   */
  override def toString = "\n" + matrix.map( _.mkString( "[", ", ", "]" ) ).mkString( "\n" ) + "\n"

  /**
   * The current matrix as Seq[Seq[T]]
   *
   * @return The current matrix as an immutable Seq
   */
  def toSeq = this.matrix

  /**
   * The current matrix as Buffer[Buffer[T]]
   *
   * @return The current matrix as a Buffer
   */
  def toBuffer = this.matrix map { _.toBuffer } toBuffer
}

object Matrix {

  /**
   * Creates an Identity Matrix of the specified size
   *
   * Remember that identity matrices are always square
   *
   * @param size The size of the matrix
   * @param n N/A
   * @param m N/A
   * @tparam T The type of the matrix
   * @return A new identity matrix, with ones on the main diagonal an zeroes elsewhere
   */
  def identity[T]( size: Int )( implicit n: Numeric[T], m: ClassTag[T] ) =
    new Matrix(
      Seq.tabulate( size, size ) { ( i, j ) ⇒
        if ( i == j ) n.one else n.zero
      }
    )

}