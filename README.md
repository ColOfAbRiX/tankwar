#TankWar

##A study on Neuroevolution

> "Neuroevolution, or neuro-evolution, is a form of machine learning that uses evolutionary algorithms to train artificial
neural networks. It is most commonly applied in artificial life, computer games, and evolutionary robotics." � Wikipedia

##The TankWar Project

###A brief description

The project is a simulation of a battlefield where small _"alive"_ beings (to be intended in [this broad sense][turing-test]),
named Tanks, fight between each others for their survival and to transmit their genes to the next generation.

Their movements and their behaviours are not hard coded but determined using a neural network, evolution and rules.

At the beginning of the simulation every tank is "stupid" and does more or less nothing interesting. Generation after
generation the Tanks' network mutates creating new behaviours and new interactions with the world. Usually these changes
are useless for the final score -fitness- of the Tank but sometimes, here and there, a behaviour appears that proves to
be useful for the Tank, increasing its score. Useful behaviours are spread across generations and accumulated on individuals
so that recognizable behaviours can be seen, like Tanks dodging bullets, following and targeting other Tanks and even
close-combat scenes!

This is an optimization process aimed at maximising a "life function".

[turing-test]: https://en.wikipedia.org/wiki/Turing_test

###History

The project was first inspired reading, years ago, the article [Evolution of Adaptive Behaviour in Robots by Means of
Darwinian Selection][PLOSS-1]. In the article some algorithms were first trained on a computer, using a combination of
neural networks to control their behaviour, and evolutionary algorithm to train the network themselves. The resulting 
data was then implemented into real robot that started to fight between each others exposing recognizable behaviours and
patterns.

After few years of relative laziness, I took the opportunity with one of my students to develop this idea into a teaching
project. With the oncoming of my interest for the Scala Programming Language I decided to start myself a side project to
that first one and properly implement my own ideas on the subject and learn the language itself.

[PLOSS-1]: http://journals.plos.org/plosbiology/article?id=10.1371/journal.pbio.1000292

###Goals

TankWar has several different goals. And all the goals are related to learning and personal growth.

Tankwar is mainly an educational project.

The first is to learn the basics of Artificial Intelligence and related topic, like [Artificial Neural Networks][WIKI-1]
and [Genetic Algorithms][WIKI-2]. And to be honest, to see my little tanks evolve and how they live their artificial
lives in the arena.

The second goal is to improve my skills in programming and to learn a new language (Scala), data structures and related
algorithms, programming methodologies, market standards and tools.

The third and last goad is to enter properly the world of Open Source learning its standards and sharing my creations with
the community.

[WIKI-1]: https://en.wikipedia.org/wiki/Neural_network
[WIKI-2]: https://en.wikipedia.org/wiki/Genetic_algorithm

####Disclaimer

For these reasons many of the tasks and components found in the project could have been done in a different way, probably
in more simple and clever ways or even with the help of external libraries (for instance, there are many libraries for
maths and neural networks in the wild, like the amazing [Encog][encog]), but this would be against the ultimate goal of
learning.

Plus, let's admit it, I am a sufferer of the [Not-Invented-Here Syndrome][WIKI-3], mainly because I love to know every
detail of the code and play with it as I like. But I acknowledge others work, though. Anyway I console myself reading
[In Defense of Not-Invented-Here Syndrome][JOEL].

This is also fruit of my own research and the knowledge I implement here is not something that comes from books or from
a university background. It may lack of precision, it may be messy or otherwise incomplete and not done following the
state-of-the-art practices. I will be pleased to receive corrections and suggestions from everybody to improve the project.

[ENCOG]: http://www.heatonresearch.com/encog
[WIKI-3]: https://en.wikipedia.org/wiki/Not_invented_here
[JOEL]: http://joelonsoftware.com/articles/fog0000000007.html

##Quick Start

For quick start read the [Getting Started](CONTRIBUTING.md) section in the CONTRIBUTING.md file

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
  * SBT
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
* Static code analysis
* Public repository on Github
  * External Github tools
* GIT

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