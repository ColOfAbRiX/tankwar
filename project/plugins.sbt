logLevel := Level.Warn

// Code formatting

addSbtPlugin("org.scalastyle" %% "scalastyle-sbt-plugin" % "0.8.0")

addSbtPlugin("org.scalariform" % "sbt-scalariform" % "1.6.0")

// Code Quality plugins

addSbtPlugin("org.brianmckenna" % "sbt-wartremover" % "0.14")

addSbtPlugin("org.scoverage" % "sbt-scoverage" % "1.3.3")

addSbtPlugin("de.johoop" % "cpd4sbt" % "1.1.5")

// Utilities

addSbtPlugin("com.orrsella" % "sbt-stats" % "1.0.5")

addSbtPlugin("com.markatta" % "taglist-plugin" % "1.3.1")

addSbtPlugin("net.virtual-void" % "sbt-dependency-graph" % "0.8.1")

// IDE Integration

addSbtPlugin("com.github.mpeltonen" % "sbt-idea" % "1.6.0")
