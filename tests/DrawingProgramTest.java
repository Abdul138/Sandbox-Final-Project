// Automated test for the Drawing Program sketch.
// It pretends to be the user: it moves the "mouse", presses keys,
// then reads pixel colours to check that the pen and eraser behave.
// Run it with tests/run_tests.sh (see tests/README.md).
import processing.core.*;

public class DrawingProgramTest extends DrawingProgram {
  int failures = 0;
  int step = 0;

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
      // Eraser right next to every edge: it must reach the last white pixel inside the border,
      // but never paint over the border itself or the grey area outside the surface
      int right = (int)(drawingSurfaceX + drawingSurfaceWidth);   // border column on the right
      int bottom = (int)(drawingSurfaceY + drawingSurfaceHeight); // border row at the bottom
      int[][] border = { {0, 300}, {right, 300}, {200, 0}, {200, bottom} };
      int[][] outside = { {right + 3, 300}, {200, bottom + 3} };
      int[] borderBefore = new int[border.length];
      int[] outsideBefore = new int[outside.length];
      for (int i = 0; i < border.length; i++) borderBefore[i] = pixelAt(border[i][0], border[i][1]);
      for (int i = 0; i < outside.length; i++) outsideBefore[i] = pixelAt(outside[i][0], outside[i][1]);
      // Pen marks hugging the inside of the right and bottom borders
      pressKey('p');
      moveMouse(right - 1, 250, right - 1, 350);
      super.draw();
      moveMouse(150, bottom - 1, 250, bottom - 1);
      super.draw();
      check(isDark(pixelAt(right - 1, 300)) && isDark(pixelAt(200, bottom - 1)), "pen can draw right next to the border");
      pressKey('e');
      int[][] stamps = { {3, 300}, {right - 3, 300}, {200, 3}, {200, bottom - 3} };
      for (int[] s : stamps) { moveMouse(s[0], s[1], s[0], s[1]); super.draw(); }
      boolean bordersKept = true;
      for (int i = 0; i < border.length; i++) bordersKept &= pixelAt(border[i][0], border[i][1]) == borderBefore[i];
      check(bordersKept, "eraser does not rub out the surface border (all four sides)");
      boolean outsideKept = true;
      for (int i = 0; i < outside.length; i++) outsideKept &= pixelAt(outside[i][0], outside[i][1]) == outsideBefore[i];
      check(outsideKept, "eraser does not paint outside the surface");
      check(isWhite(pixelAt(right - 1, 300)) && isWhite(pixelAt(200, bottom - 1)), "eraser reaches the last pixel next to the right and bottom borders");
      check(isWhite(pixelAt(1, 300)) && isWhite(pixelAt(200, 1)), "eraser reaches the first pixel next to the left and top borders");
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
      // The mouse comes back into the surface from the grey area while drawing:
      // the pen line starts outside (pmouse), but only the part inside the surface may be painted
      int right = (int)(drawingSurfaceX + drawingSurfaceWidth);
      int bottom = (int)(drawingSurfaceY + drawingSurfaceHeight);
      int greyRight = pixelAt(right + 10, 400);
      int greyBelow = pixelAt(100, bottom + 10);
      check(tool.equals("pen"), "pen is selected for the re-entry checks");
      moveMouse(right + 20, 400, right - 20, 400); // came in through the right side
      super.draw();
      moveMouse(100, bottom + 20, 100, bottom - 20); // came in through the bottom
      super.draw();
      check(isDark(pixelAt(right - 10, 400)) && isDark(pixelAt(100, bottom - 10)), "pen still draws the part of the line inside the surface");
      check(pixelAt(right + 10, 400) == greyRight && pixelAt(100, bottom + 10) == greyBelow, "pen does not paint outside the surface when the mouse comes back in");
    } else if (step == 6) {
      System.out.println(failures == 0 ? "ALL TESTS PASSED" : failures + " TEST(S) FAILED");
      System.exit(failures == 0 ? 0 : 1);
    }
  }
}
