#!/bin/bash
#
# This hook is invoked by git commit, and can be bypassed with --no-verify
# option. It takes a single parameter, the name of the file that holds the
# proposed commit log message. Exiting with non-zero status causes the git
# commit to abort.
#
# The hook is allowed to edit the message file in place, and can be used to
# normalize the message into some project standard format (if the project
# has one). It can also be used to refuse the commit after inspecting the
# message file.
#
