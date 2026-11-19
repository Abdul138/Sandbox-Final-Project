#!/usr/bin/env bash
# Runs the Drawing Program test. Needs Java, Python 3 and Processing's core.jar.
# Usage: PROCESSING_CORE=/path/to/processing/core/library/core.jar tests/run_tests.sh
set -euo pipefail
: "${PROCESSING_CORE:?Set PROCESSING_CORE to the path of the Processing core.jar file}"
here="$(cd "$(dirname "$0")" && pwd)"
build="$(mktemp -d)"
trap 'rm -rf "$build"' EXIT
python3 "$here/preprocess.py" "$here/../Drawing Lessons/DrawingProgram/DrawingProgram.pde" "$build/DrawingProgram.java"
cp "$here/DrawingProgramTest.java" "$build/"
javac -nowarn -cp "$PROCESSING_CORE" -d "$build" "$build/DrawingProgram.java" "$build/DrawingProgramTest.java"
# xvfb-run gives the sketch a pretend screen on machines without a display (e.g. servers)
runner=""
if [ -z "${DISPLAY:-}" ] && command -v xvfb-run >/dev/null; then runner="xvfb-run -a"; fi
$runner java -cp "$PROCESSING_CORE:$build" DrawingProgramTest
