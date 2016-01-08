#!/bin/bash
#
# This hook is invoked by git commit, and can be bypassed with --no-verify
# option. It takes no parameter, and is invoked before obtaining the
# proposed commit log message and making a commit. Exiting with non-zero
# status from this script causes the git commit to abort.
#
# The default pre-commit hook, when enabled, catches introduction of
# lines with trailing whitespaces and aborts the commit when such a line
# is found.
#
# All the git commit hooks are invoked with the environment variable
# GIT_EDITOR=: if the command will not bring up an editor to modify the
# commit message.
#

enter() {
    echo -e "\e[1;33mGIT - ENFORCING CODE POLICIES\e[0;0m"

    # Stash unstaged changes before running tests
    git add -A :/
    #git stash -q --keep-index
}

exit_success() {
    # Adding all the files and changes
    git add -A :/

    # Reapply stashed changes
    #git stash pop -q

    echo -e "\nThe project successfully completed the code policies checks\n"
    exit 1
}

exit_fail() {
    # Reapply stashed changes
    #git stash pop -q

    echo -e "\nERROR: Cannot commit.\n\nThe project didn't pass a policy check and cannot be committed\nCheck your code and try again."
    exit 1
}

enter

# Compilation check
echo -e "\nCheck code compilation...\n"
sbt clean compile
[ $? -ne 0 ] && exit_fail

# Code style check
echo -e "\nCheck coding style...\n"
sbt scalastyle
[ $? -ne 0 ] && exit_fail

# Unit testing and code coverage
echo -e "\nUnit testing and code coverage...\n"
sbt coverage test
[ $? -ne 0 ] && exit_fail

exit_success
