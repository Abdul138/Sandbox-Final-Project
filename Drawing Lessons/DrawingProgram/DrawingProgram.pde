import ddf.minim.*;
import ddf.minim.analysis.*;
import ddf.minim.effects.*;
import ddf.minim.signals.*;
import ddf.minim.spi.*;
import ddf.minim.ugens.*;

//Global Variables
float drawingSurfaceX, drawingSurfaceY, drawingSurfaceWidth, drawingSurfaceHeight, drawingDiameter;
float eraserDiameter;
Boolean draw=false;
String tool = "pen"; //Which tool is selected: "pen" or "eraser" (press p or e to switch)
color drawingSurfaceColour = color(255); //White, the colour the eraser paints with
//
void setup()
{
  //Display Checker
  //Display Orientation Checker
  //Display and CANVAS Checker
  size(500, 600); //Landscape (Portrait or Square)
  //
  //Population
  drawingSurfaceX = width*0;
  drawingSurfaceY = height*0;
  drawingSurfaceWidth = width*3/4;
  drawingSurfaceHeight = height*4/5;
  drawingDiameter = width*1/100;
  eraserDiameter = width*1/25; //Bigger than the pen so it is easy to rub things out
  //
  fill(drawingSurfaceColour);
  rect( drawingSurfaceX, drawingSurfaceY, drawingSurfaceWidth, drawingSurfaceHeight );
  //
}//End setup
//
void draw()
{
  //ellipse( mouseX, mouseY, drawingDiameter, drawingDiameter); //Example Circle Drawing Tool
  if ( draw == true && mouseX>drawingSurfaceX && mouseX<drawingSurfaceX+drawingSurfaceWidth && mouseY>drawingSurfaceY && mouseY<drawingSurfaceY+drawingSurfaceHeight) {
    if ( tool.equals("eraser") ) {
      //Eraser: a circle stamp filled with the drawing surface colour and no outline
      //Note: one circle is stamped per frame, so moving the mouse very fast leaves gaps
      pushStyle(); //Remember the pen's colours so the pen still draws black lines afterwards
      clipToDrawingSurface(); //Only paint inside the surface, keep its border
      noStroke();
      fill(drawingSurfaceColour);
      ellipse( mouseX, mouseY, eraserDiameter, eraserDiameter );
      noClip();
      popStyle(); //Put the pen's colours back
    } else {
      //Pen: pmouseX/pmouseY can still be outside the surface (the mouse just came back in),
      //so clip the line too, otherwise it paints over the border and the grey area
      clipToDrawingSurface();
      line (mouseX, mouseY, pmouseX, pmouseY);
      noClip();
    }
  }
}//End draw
//
void clipToDrawingSurface() {
  //Limits drawing to the white surface, without its 1-pixel border (clip uses imageMode, CORNER by default)
  //Remember to call noClip() when finished
  clip( drawingSurfaceX+1, drawingSurfaceY+1, drawingSurfaceWidth-1, drawingSurfaceHeight-1 );
}//End clipToDrawingSurface
//
void keyPressed() {
  //Tool Selection
  if ( key=='e' || key=='E' ) tool = "eraser";
  if ( key=='p' || key=='P' ) tool = "pen";
}//End keyPressed
//
void mousePressed()
{
  if ( mouseX>drawingSurfaceX && mouseX<drawingSurfaceX+drawingSurfaceWidth && mouseY>drawingSurfaceY && mouseY<drawingSurfaceY+drawingSurfaceHeight ) {
    if ( draw == true ) {
      draw=false;
    } else {
      draw = true;
    }
  } //End drawing tools
}//End mousePressed
//
//End MAIN Program
