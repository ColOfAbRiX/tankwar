package com.colofabrix.scala.math

import scala.reflect.ClassTag

class Matrix[T]( val matrix: Seq[Seq[T]] )( implicit n: Numeric[T], m: ClassTag[T] )
{
  import n._

  require( matrix.length > 0 )
  require( matrix(0).length > 0 )
  require( matrix.forall(_.length == matrix(0).length) )

  /**
   * Number of rows of the matrix
   */
  val rows = matrix.length

  /**
   * Number of columns of the matrix
   */
  val cols = matrix(0).length

  /**
   * Main diagonal of the matrix
   */
  lazy val diagonal = Seq.tabulate(Math.min(rows, cols)) { i ⇒ matrix(i)(i) }

  /**
   * Transpose of the matrix
   */
  lazy val transpose = new Matrix(matrix.transpose)

  /**
   * Gets a row of the matrix
   *
   * @param i The number of the row to return
   * @return The i-th row of the matrix
   */
  def row(i: Int) = matrix(i)

  /**
   * Gets a subset of the rows of the matrix
   *
   * @param count The number of rows to return starting from row-0
   * @return The first {count} rows of the matrix
   */
  def rowSet(count: Int) = new Matrix(
    matrix.take(count)
  )

  /**
   * Gets a subset of the rows of the matrix
   *
   * @param start The row from which to start
   * @param count The number of rows to return
   * @return {count} number of rows from the start-th row
   * @return The first {count} rows of the matrix
   */
  def rowSet(start: Int, count: Int) = new Matrix(
    matrix.take(start + count).takeRight(count)
  )

  /**
   * Gets a column of the matrix
   *
   * @param i The number of the column to return
   * @return The i-th column of the matrix
   */
  def col(i: Int) = matrix map (_(i))

  /**
   * Gets a subset of the rows of the matrix
   *
   * @param count The number of rows to return starting from row-0
   * @return The first {count} rows of the matrix
   */
  def colSet(count: Int) = new Matrix(
    matrix.transpose.take(count).transpose
  )

  /**
   * Gets a subset of the rows of the matrix
   *
   * @param start The row from which to start
   * @param count The number of rows to return
   * @return {count} number of rows from the start-th row
   * @return The first {count} rows of the matrix
   */
  def colSet(start: Int, count: Int) = new Matrix(
    matrix.transpose.take(start + count).takeRight(count).transpose
  )

  /**
   * Maps each element of the matrix in a new element of
   * a new matrix
   *
   * @param f The mapping function
   * @return A new matrix where each element is a mapping from the original matrix
   */
  def map[U](f: (T, Int, Int) ⇒ U)(implicit n: Numeric[U], m: ClassTag[U]) = {
    new Matrix[U](
      Seq.tabulate(rows, cols){ (i, j) ⇒
        f(matrix(i)(j), i, j)
      }
    )
  }

  /**
   * Returns an element of the matrix
   *
   * @param i A tuple that specify the i and j index of the element to return
   * @return The element at position (i, j)
   */
  def apply(i: (Int, Int)) = matrix(i._1)(i._2)

  /**
   * Clones the matrix in a new one
   *
   * @return A new matrix with the same values as the current one
   */
  override def clone = new Matrix( Seq.tabulate(rows, cols) {(i, j) ⇒ matrix(i)(j)} )

  /**
   * Determines if two matrices are equals
   *
   * @param obj The other object to compare
   * @return true if the other object is a matrix identical to the current one
   */
  override final def equals( obj: Any ): Boolean = obj match {
    case that: Matrix[T] ⇒
      // Speed check with the number of inputs
      if( rows != that.rows || cols != that.cols ) return false

      // Checking every element
      for( i ← (0 until matrix.length).par;
           j ← (0 until matrix(i).length).par ) {
        if( matrix(i)(j) != that(i, j)) return false
      }

      true

    case _ ⇒ false
  }

  /**
   * Matrix multiplication
   *
   * Ref: http://rosettacode.org/wiki/Matrix_multiplication#Scala
   */
  def *( other: Matrix[T] ): Matrix[T] = new Matrix( {
    val c = other.transpose.toList
    for (row <- matrix)
      yield for (col <- c)
        yield row.zip(col) map { Function.tupled(_ * _) } reduceLeft (_ + _)
  } )

  /**
   * Exponentiation of the matrix
   *
   * Valid only for non-negative powers and for square matrices
   *
   * Implementation of the "exponentiation by squaring" algorithm
   * Ref: http://www.programminglogic.com/fast-exponentiation-algorithms/
   * Ref: https://stackoverflow.com/questions/12311869/fast-matrix-exponentiation
   *
   * @param p The exponent to raise the matrix, a non-negative integer
   * @return The current matrix multiplied by itself p times
   */
  def **( p: Int ) = {
    require( p >= 0 )
    require( rows == cols )

    var result = toIdentity
    var n = p
    var b = this

    while( n > 0 ) {
      if( n % 2 == 0 ) {
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
  def *( other: T ) = this map { (x, _, _) ⇒ x * other }

  def +( other: Matrix[T] ): Matrix[T] = ???

  def -( other: Matrix[T] ): Matrix[T] = ???

  /**
   * Returns a square identity matrix of the same size of the current matrix
   *
   * Please note that an identity matrix can only be square and thus this
   * function will return an identity matrix as large as the smallest size of
   * the matrix
   *
   * @return An identity matrix of the same size of the current matrix
   */
  def toIdentity = Matrix.identity[T](Math.min(rows, cols))

  /**
   * String representation of the matrix
   *
   * @return A string displaying graphically the matrix
   */
  override def toString = "\n" + matrix.map(_.mkString("[", ", ", "]")).mkString("\n") + "\n"

  /**
   * The current matrix as Seq[Seq[T]]
   *
   * @return The current matrix as a sequence
   */
  def toList = this.matrix
}

object Matrix {

  def identity[T]( size: Int )( implicit n: Numeric[T], m: ClassTag[T] ) =
    new Matrix( Seq.tabulate(size, size) { (i, j) => if (i == j) n.one else n.zero } )

}