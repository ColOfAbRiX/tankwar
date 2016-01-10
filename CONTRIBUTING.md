# Contributing

It's nice to have people that helps you. It enriches the project with new ideas and it speeds up the development.

Please, follow these guidelines to make sure a certain quality of the code is met and to allow people to work smoothly
together.

###Reccomended settings

_NOT UPDATED - Jan 2016_

The project was developed using:

* Intellij IDEA 14
* the Scala Plugin
* Oracle JDK 1.8
* On a Windows platform (OpenGL libraries are for Windows)

To maximise the compatibility and to have a quick start, that setup is recommended but in no way I want to enforce your
coding habits.

## Getting Started

_NOT UPDATED - Jan 2016_

* Make sure you have a [GitHub account](https://github.com/signup/free)
* Submit a ticket for your issue, assuming one does not already exist.
  * Clearly describe the issue including steps to reproduce when it is a bug.
  * Make sure you fill in the earliest version that you know has the issue.
* Fork the repository on GitHub then clone it `git clone git@github.com:your-username/tankwar.git`

Add the project to your preferred IDE and start coding! As simple as that.

## Making Changes

* Create a topic branch from where you want to base your work.
  * This is usually the master branch.
  * Only target release branches if you are certain your fix must be on that branch.
  * To quickly create a topic branch based on master; `git checkout -b fix/master/my_contribution master`. Please avoid
    working directly on the `master` branch.
* Make commits of logical units.
* Check for unnecessary whitespace with `git diff --check` before committing.
* Make sure your commit messages are in the proper format.
* Make sure you have added the necessary tests for your changes.
* Run _all_ the tests to assure nothing else was accidentally broken.

###Workload organisation

A private installation of JIRA is used to organise the workload and the tasks are not visible to the public.
At the moment there is no plan to make this public and only accepted and recognized members of the project will be given
access to the JIRA project.

Public contributor can use the Github Issue service to submit and discuss new issues, tasks and improvements.

###Code Style

The ultimate goal of a good and consistent code styling is readability and understandability of the code. Code is itself
the main documentation but a bad style can really create [obscure code][IOCCC]. In addition, if more people contribute
to a codebase it's easy to end up to a nice confusion.

For these reasons, an more, the code style settings of Intellij IDEA are provided and can be imported from the IDE itself
with:

    File -> Import Settings -> project-folder/.idea/code_style.jar

In general, when something is not clear, follow the [Scala Style Guide][scala-style] and you'll never be wrong.

[IOCCC]: http://www.ioccc.org/
[scala-style]: http://docs.scala-lang.org/style/scaladoc.html

##Submitting the code

Push to your fork and submit a pull request from Github.

At this point you're waiting on me. I like to comment on pull requests as soon as possible and I may suggest
some changes or improvements or alternatives. The final decision to merge or not the changes on the master
branch shall only be mine.

To be accepted a pull request, from any contributor, must at least and without reserve:

* Have the new code or changes tested and _all_ (new and existing) tests must pass.
* Have a good and detailed documentation and [comments][comments].

Some things that will increase the chance that your pull request is accepted:

* Follow the style guide.
* Write a [good commit message][commit].

[style]: https://github.com/thoughtbot/guides/tree/master/style
[commit]: http://tbaggery.com/2008/04/19/a-note-about-git-commit-messages.html
[comments]: http://www.hongkiat.com/blog/source-code-comment-styling-tips/

#Thanks

Thanks to [Puppetlab][puppet] and to [Thoughtbot][thoughtbot] for ideas on the CONTRIBUTING file.

[puppet]: https://github.com/puppetlabs/puppet/blob/master/CONTRIBUTING.md
[thoughtbot]: https://github.com/thoughtbot/factory_girl_rails/blob/master/CONTRIBUTING.md