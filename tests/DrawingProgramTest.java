// Automated test for the Drawing Program sketch.
// It pretends to be the user: it moves the "mouse", presses keys,
// then reads pixel colours to check that the pen and eraser behave.
// Run it with tests/run_tests.sh (see tests/README.md).
import processing.core.*;

public class DrawingProgramTest extends DrawingProgram {
  int failures = 0;
  int step = 0;
  int borderBefore, outsideBefore;

  public static void main(String[] args) {
    PApplet.main("DrawingProgramTest");
  }

  void check(boolean ok, String message) {
    System.out.println((ok ? "PASS: " : "FAIL: ") + message);
    if (!ok) failures++;
  }

  int pixelAt(int x, int y) { loadPixels(); return pixels[y*width + x]; }
  boolean isWhite(int c) { return red(c) > 250 && green(c) > 250 && blue(c) > 250; }
  boolean isDark(int c) { return brightness(c) < 128; }

  void moveMouse(int fromX, int fromY, int toX, int toY) {
    pmouseX = fromX; pmouseY = fromY; mouseX = toX; mouseY = toY;
  }

  void pressKey(char k) { key = k; keyPressed(); }

  public void draw() {
    step++;
    if (step == 1) {
      check(tool.equals("pen"), "pen is the default tool");
      moveMouse(100, 100, 100, 100);
      mousePressed(); // click inside the surface to start drawing
      check(draw == true, "clicking inside the surface turns drawing on");
      moveMouse(100, 100, 200, 100);
      super.draw();
      check(isDark(pixelAt(150, 100)), "pen draws a dark line");
    } else if (step == 2) {
      pressKey('e');
      check(tool.equals("eraser"), "pressing e selects the eraser");
      moveMouse(150, 100, 150, 100);
      super.draw();
      check(isWhite(pixelAt(150, 100)), "eraser paints the line white where it touches");
      check(isDark(pixelAt(120, 100)), "eraser leaves the line alone outside its circle");
    } else if (step == 3) {
      // Eraser right next to the edges: must not paint over the border or the grey area
      int rightEdge = (int)(drawingSurfaceX + drawingSurfaceWidth);
      borderBefore = pixelAt(0, 300);
      outsideBefore = pixelAt(rightEdge + 3, 300);
      moveMouse(3, 300, 3, 300);
      super.draw();
      moveMouse(rightEdge - 3, 300, rightEdge - 3, 300);
      super.draw();
      check(pixelAt(0, 300) == borderBefore, "eraser does not rub out the surface border");
      check(pixelAt(rightEdge + 3, 300) == outsideBefore, "eraser does not paint outside the surface");
      check(isWhite(pixelAt(rightEdge - 4, 300)), "eraser still works close to the edge");
    } else if (step == 4) {
      pressKey('p');
      check(tool.equals("pen"), "pressing p selects the pen again");
      moveMouse(100, 200, 200, 200);
      super.draw();
      check(isDark(pixelAt(150, 200)), "pen draws dark lines again after using the eraser");
      pressKey('E');
      check(tool.equals("eraser"), "capital E also selects the eraser");
      pressKey('P');
      check(tool.equals("pen"), "capital P also selects the pen");
      pressKey('x');
      check(tool.equals("pen"), "other keys do not change the tool");
    } else if (step == 5) {
      System.out.println(failures == 0 ? "ALL TESTS PASSED" : failures + " TEST(S) FAILED");
      System.exit(failures == 0 ? 0 : 1);
    }
  }
}
