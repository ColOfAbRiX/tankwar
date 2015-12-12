name := "TankWar"

version := "0.2.0"

scalaVersion := "2.11.7"

// LWJGL

libraryDependencies += "org.lwjgl.lwjgl" % "lwjgl_util" % "2.9.0"

libraryDependencies += "org.lwjgl.lwjgl" % "lwjgl-platform" % "2.9.0" classifier "natives-windows" classifier "natives-linux"

// Slick-Util Slick-2D

libraryDependencies += "slick-util" % "slick-util" % "1.0.0" from "http://slick.ninjacave.com/slick-util.jar"

// Uncommon Maths

libraryDependencies += "org.uncommons" % "uncommons-maths" % "1.2"

// The Watchmaker Framework

libraryDependencies +=  "org.uncommons.watchmaker" % "watchmaker-framework" % "0.7.1"

// Scalatest

libraryDependencies += "org.scalatest" % "scalatest_2.11" % "2.2.4" % "test"

// Native libraries extraction - LWJGL has some native libraries provided as JAR files that I have to extract

resourceGenerators in Compile += Def.task {
  val r = "^(\\w+).*".r
  val r(os) = System.getProperty( "os.name" )

  val jars = ( update in Compile ).value
    .select( configurationFilter( "compile" ) )
    .filter( _.name.contains( os.toLowerCase ) )

  jars foreach { jar =>
    println( s"[info] Processing '$jar' and saving to '${unmanagedBase.value}'" )
    IO.unzip(  jar, unmanagedBase.value )
  }

  Seq.empty[File]
}.taskValue
