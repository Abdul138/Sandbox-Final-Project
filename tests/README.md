# Tests

`run_tests.sh` checks the Drawing Program (`Drawing Lessons/DrawingProgram`) automatically.
It acts like a user (clicks, moves the mouse, presses `e` / `p`) and then checks pixel colours, for example:
the pen draws a dark line, the eraser paints white, the eraser never paints over the border or outside the surface,
the pen still draws after the eraser was used, and the pen does not paint outside the surface when the mouse comes back in.

## How to run
You need Java, Python 3 and Processing's `core.jar` (it comes with Processing, in `core/library/core.jar`). On a machine without a screen (a server) you also need `xvfb-run`.

```sh
PROCESSING_CORE=/path/to/processing/core/library/core.jar tests/run_tests.sh
```

It prints `PASS` / `FAIL` for each check and `ALL TESTS PASSED` at the end.

## How it works
- `preprocess.py` turns the `.pde` sketch into a normal Java class (a small version of what the Processing editor does).
- `DrawingProgramTest.java` extends that class and runs the checks inside `draw()`.
