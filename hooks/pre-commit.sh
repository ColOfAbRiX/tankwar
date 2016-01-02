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
# The hook should exit with non-zero status after issuing an appropriate
# message if it wants to stop the commit.
#

fail_commit() {
    # Reapply stashed changes
    #git stash pop -q
    echo "\nERROR: Cannot commit.\n\nThe project didn't pass a policy check and cannot be committed\nCheck your code and try again."
    exit 1
}

echo "GIT - ENFORCING CODE POLICIES"

# Stash unstaged changes before running tests
#git stash -q --keep-index


# Cleaning
sbt clean

# Compilation check
echo "\nCheck code compilation...\n"
sbt compile
[ $? -ne 0 ] && fail_commit

# Code style check
echo "\nCheck coding style...\n"
sbt scalastyle
[ $? -ne 0 ] && fail_commit

# Unit testing and code coverage
echo "\nUnit testing and code coverage...\n"
sbt coverage test
[ $? -ne 0 ] && fail_commit


# Reapply stashed changes
#git stash pop -q

echo "\nThe project successfully completed the policy checks\n"
exit 1
