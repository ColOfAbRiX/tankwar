package com.colofabrix.scala.geometry.abstracts

/**
 * Represents a Shape container
 *
 * A container is used as a faster and simpler way to obtain
 * information or do some actions on a Shape that would otherwise
 * require more computation.
 * Ideally the computation should take O(n) or less. This trait
 * should be applied to shapes that guarantee this fast computation
 */
trait Container
