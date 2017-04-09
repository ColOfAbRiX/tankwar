package com.colofabrix.scala.gfx

/**
  * Sync class allows a method to be called a specific number of times per second without blocking the main thread
  * It does not achieve the rate perfectly but it is good enough for most purposes
  * @param _rate The number of times per second to call the functions
  * @param actions The function to be called of type (Option[I]) => R
  * @tparam I Type of the argument to the function (Wrapped in an option)
  * @tparam R Return type of the function, can be Unit
  */
class Sync[I, R]( private var _rate: Int, val actions: ( Option[I] ) ⇒ R ) {

  /**
    * Secondary constructor for use with a function that does not take an argument
    * @param rate The number of times per second to call the functions
    * @param actions The function to be called, of type () => R
    */
  def this( rate: Int, actions: () ⇒ R ) {
    this( rate, ( x: Option[Any] ) ⇒ actions() )
  }

  def rate = _rate
  def setRate( r: Int ) = {
    _rate = r
    _sloppyModifier = 1
    _callsLastSecond = r
    CALL_SPACING = SECOND_IN_NANO / rate
  }

  private val SECOND_IN_NANO = Math.pow( 10, 9 )
  private var CALL_SPACING = if ( rate == -1 ) 0 else SECOND_IN_NANO / rate

  private var _lastTime: Long = System.currentTimeMillis / 1000000l //System.nanoTime()
  private var _deltaTime: Long = 0l

  private var _lastSecondTime: Long = System.nanoTime()
  private var _callsThisSecond: Int = 0
  private var _callsLastSecond: Int = 0

  private var _sloppyModifier: Double = 1

  /**
    * Call this in a mainloop every time it loops. This will call the function (actions) if it is the correct time. It
    * will limit it to the rate
    * @param argument Argument to the function, defaults to None
    * @return Returns None if function not called otherwise Some(function return value)
    */
  def call( argument: Option[I] = None ): Option[R] = {

    val newTime: Long = System.currentTimeMillis / 1000000l
    println( s"lastTime=$lastTime" )
    println( s"newTime=$newTime" )
    println( s"newTime - lastTime=${newTime - lastTime}" )
    println( s"CALL_SPACING=$CALL_SPACING" )
    println( s"_sloppyModifier=${_sloppyModifier}" )
    println( s"CALL_SPACING * _sloppyModifier=${CALL_SPACING * _sloppyModifier}" )
    println( s"newTime - lastTime >= CALL_SPACING * _sloppyModifier=${newTime - lastTime >= CALL_SPACING * _sloppyModifier}" )
    println( "" )

    if ( newTime - lastTime >= CALL_SPACING * _sloppyModifier || rate == -1 ) {

      _deltaTime = newTime - lastTime
      _lastTime = newTime

      if ( _lastTime - _lastSecondTime <= SECOND_IN_NANO ) _callsThisSecond += 1
      else {
        _callsLastSecond = _callsThisSecond
        _lastSecondTime = _lastTime
        _callsThisSecond = 0
        if ( rate != -1 ) {
          if ( _callsLastSecond < rate )
            _sloppyModifier *= 0.99
          if ( _callsLastSecond > rate )
            _sloppyModifier *= 1.01
        }
        //Log.info("Updates last second: " + _callsLastSecond)
      }

      Some( actions( argument ) )

    }
    else None

  }

  /**
    * Timestamp of the last call of the function
    * @return
    */
  def lastTime: Long = _lastTime

  /**
    * Time since the last call in seconds
    * @return
    */
  def deltaTime: Double = _deltaTime / SECOND_IN_NANO

  /**
    * The number of calls in the last second (can be used as the calls per second)
    * @return
    */
  def callsLastSecond = _callsLastSecond

  def timingModifier = _sloppyModifier

}