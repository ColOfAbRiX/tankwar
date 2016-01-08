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

echo -e "\e[1;33mGIT - ENFORCING COMMIT MESSAGE POLICIES\e[0;0m"

#
# Message format
#

CARDS="[A-Z]+-[0-9]+:\s+[^\.]{8,}\."     # TKWAR-10: This is a commit. TKWAR-10: This is another ocmmit.
NOTES="NOTES?:\s+[^\.]{8,}\."            # NOTES: This is a note on the commit.
TESTS="TEST:\s+[^\.]{8,}\."              # TEST: This is a test.
CAVEATS="CAV(|EAT):\s+[^\.]{8,}\."       # CAVEAT: When reading note that something is not perfect.
REGEX="^($CARDS(\s+$CARDS)*|$TESTS)(|\s+$NOTES)(|\s+$CAVEATS)$"

egrep -q "$REGEX" "$1"
if [ $? -ne 0 ]
then
    echo -e "\nERROR: Cannot commit.\n\nThe commit message doesn't comply to the required standard.\n\nThe commit message must respect the regular expression:\n    $REGEX\n\nChange the commit message and try again."
    exit 1
fi

#
# Spell checker
#

ASPELL=$(which aspell)

if [ $? -eq 0 ]; then
    WORDS=$($ASPELL list < "$1")

    if [ -n "$WORDS" ]; then
        echo -e "Possible spelling errors found in commit message. Use git commit --amend to change the message.\n\tPossible mispelled words: $WORDS."
    fi
fi


echo -e "\nThe project successfully completed the commit message policies checks\n"
exit 1
