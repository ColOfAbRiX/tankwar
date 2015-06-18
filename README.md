#TankWar

##A study on Neuroevolution

> "Neuroevolution, or neuro-evolution, is a form of machine learning that uses evolutionary algorithms to train artificial
neural networks. It is most commonly applied in artificial life, computer games, and evolutionary robotics." � Wikipedia

##The TankWar Project

###A brief description

The project is a simulation of a battlefield where small _"alive"_ (to be intended in [this broad sense][turing-test])
beings, named Tanks, fights between each others for their survival and to transmit their genes to the next generation.

Their movements and their behaviours are not hard coded but determined using a neural network and evolution.

At the beginning of the simulation every tank is "stupid" and does more or less nothing interesting. Generation after
generation the Tanks' network mutate creating new behaviours and interaction with the world. Usually this changes are
useless (for the final score of the Tank), but sometimes, here and there, appear a behaviour that proves to be useful for
the tank increasing its score. Useful behaviours are spread across generations and accumulated so that recognizable 
behaviours that can be seen, like Tanks dodging bullets, following and targeting other Tanks and even close-combat scenes!

[turing-test]: https://en.wikipedia.org/wiki/Turing_test

###History

The project was first inspired reading, years ago, the article [Evolution of Adaptive Behaviour in Robots by Means of
Darwinian Selection][PLOSS-1]. In the article some algorithms were first trained using a combination of neural networks,
to control their behaviour, and evolutionary algorithm, to train the network and then implemented into real robot that
started to fight between each others exposing recognizable behaviours and patterns.

After few years of relative laziness, I took the opportunity with one of my students to develop this idea into a teaching
project. With the oncoming of my interest for the Scala Programming Language I decided to start a side project to that
first one and properly implement my own ideas on the subject and learn the language itself.

[PLOSS-1]: http://journals.plos.org/plosbiology/article?id=10.1371/journal.pbio.1000292

###Goals

TankWar has several different goals.

The first is to learn Artificial Intelligence and related topic, like [Artificial Neural Networks][WIKI-1] and [Genetic
Algorithms][WIKI-2]. And to be honest, to see my little tanks evolve and how they live their artificial lives in the arena.

The second goal is to improve my skills in programming learning a new language, data structures, programming methodologies,
market standards and tools.

The third and last goad is to enter properly the world of Open Source knowing its standards and sharing my creations with
the community.

[WIKI-1]: https://en.wikipedia.org/wiki/Neural_network
[WIKI-2]: https://en.wikipedia.org/wiki/Genetic_algorithm

####Disclaimer

For these reasons many of the tasks and components found in the project could have been done in a different way, probably
in more simple and clever ways or even with the help of external libraries (for instance, there are many libraries for
maths and neural networks like the amazing [Encog][encog] in the wild), but this would be against the ultimate goal of
learning.

Plus, let's admit it, I am a sufferer of the [Not-Invented-Here Syndrome][WIKI-3], mainly because I love to know every
detail of the code and play with it as I like. But I acknowledge others work, though. Anyway I console myself reading
[In Defense of Not-Invented-Here Syndrome][JOEL] omitting, of course, the part where its speaks about _"really good team
with great programmers"_.

This is also fruit of my own research and the knowledge I implement here is not something that come from books or from
a university background. It may lack of precision, it may be messy or otherwise incomplete and not done following the
state-of-the-art. I will be pleased to receive corrections and suggestions from everybody to improve the project.

[ENCOG]: http://www.heatonresearch.com/encog
[WIKI-3]: https://en.wikipedia.org/wiki/Not_invented_here
[JOEL]: http://joelonsoftware.com/articles/fog0000000007.html

##Quick Start

The quick start assumes you are using the [Reccomended Settings][CONTRIBUTING.md]

* Download the project from Github
* Import it in Intellij IDEA making sure that the project references the correct Scala JDK in your system

You can now either build the JAR and simply run the simulation

* Build the JAR from `Build -> Build Artfacts` 
* Open a command line shell
* Locate your JAR file on the system, default in `project output/artifacts/TankWar.jar`
* Run `java -jar <path to the JAR>`

Or you can start it inside Intellij IDEA and work with it.

##Topics touched by the project

###Neural Networks

* Implementation of a Neural Network
* Studies on Graphs
  * Definitions and kinds of graphs
  * Algorithms and operations on graphs using matrices
* Different types of Neural Network
  * Feed-forward Networks
  * Recurrent Networks
  * Topology-evolving networks
* Performance

###Genetic Algorithms

* Evolution strategies
* Selection strategies
* Fitness strategies
* Statistics
* Interaction of the tanks with the world
  * Effects on their fitness
* [Watchmaker Framework][WATCH]

[WATCH]: http://watchmaker.uncommons.org/

###Programming

* Scala programming language
  * Functional programming
  * Design patterns in scala
* Implementation of algorithms and data structures
  * Quadtree for spatial partitioning
  * Matrix multiplication
  * Graph properties
* Performance testings
  * Computational complexity

###Programming Tools and Methodologies

* Managing the workflow with Kanban
* Organise the issues with JIRA
* Use Test-Driven Development
  * Unit testing
* Public repository on Github
  * External Github tools

###Topics of Computer Graphics

* Plane geometry
  * Collision detection
  * Measurement of distance
  * Polygons and algorithms to determine some of their properties
* OpenGL APIs
* Vectors and Matrices implementation and optimizations 

###Topics of Open Source Software

* Open Source Licenses
* De Facto standards for repositories