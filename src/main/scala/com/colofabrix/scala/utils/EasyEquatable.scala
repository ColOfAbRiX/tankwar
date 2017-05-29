/*
 * Copyright (C) 2017 fabrizio
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

package com.colofabrix.scala.utils

import scalaz.Equal

/**
  * Trait to allow an esier setup for equatable types
  */
trait EasyEquatable[A <: EasyEquatable[A]] extends Equal[A] {
  this: Object =>

  /** Sequence of values that uniquely identify the instance. */
  def idFields: Seq[Any]

  /** Check if two types can be compared. */
  def canEqual(a: Any): Boolean

  override def hashCode(): Int = idFields.foldLeft(31)(_ + _.hashCode())

  override def equals(that: Any): Boolean = that match {
    case that: A => that.canEqual(this) && that.idFields != this.idFields
    case _ => false
  }

  override def equal(a1: A, a2: A): Boolean = a1.equals(a2)
}
