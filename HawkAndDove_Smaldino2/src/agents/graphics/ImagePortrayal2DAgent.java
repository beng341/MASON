/*
  Copyright 2006 by Sean Luke and George Mason University
  Licensed under the Academic Free License version 3.0
  See the file "LICENSE" for more information
*/

package agents.graphics;
import sim.portrayal.*;
import sim.portrayal.simple.*;
import java.awt.*;

/**
   A simple portrayal for 2D visualization of images. It extends the SimplePortrayal2D and
   it manages the drawing and hit-testing for rectangular shapes containing images/pictures.
   
   <p>ImagePortrayal2D draws an image centered on the Portrayal's origin.  
   Images are not stretched to fill the info.draw.height x info.draw.width rectangle.  Instead, if the image
   is taller than it is wide, then the width of the image will be info.draw.width * scale and the height
   will stay in proportion; else the height of the image will be info.draw.height * scale and the
   width will stay in proportion.
*/

// if you want something more sophisticated than this (such as responding
// to the color values in a useful fashion) you may need to use Java2D's
// drawImage(BufferedImage img, BufferedImageOp op,  int x, int y) method,
// and that would be expensive.  :-)

// if you want more exact control over the scaling method for the image
// (nearest neighbor vs. bilinear), you can set the KEY_INTERPOLATION
// rendering hint in Java2D.

public class ImagePortrayal2DAgent extends ImagePortrayal2D
    {
    public Image image;
    public double xmin, xmax, ymin, ymax;
    
    public ImagePortrayal2DAgent(Image image)  
    { this(image,1.0); }
    
    public ImagePortrayal2DAgent(Image image, double scale)  
        { 
        super(image,scale);  // don't bother with color
        this.image = image;
        this.scale = scale;
        }
    
    public ImagePortrayal2DAgent(Image image, double scale, double xmin, double xmax, double ymin, double ymax)  
    { 
    super(image,scale);  // don't bother with color
    this.image = image;
    this.scale = scale;
    this.xmin = xmin;
    this.xmax = xmax;
    this.ymin = ymin;
    this.ymax = ymax;
    }
        
    // assumes the graphics already has its color set
    public void draw(Object object, Graphics2D graphics, DrawInfo2D info)
        {
        if (image==null) return;
        // in this example we ALWAYS draw the image, even if the color is set to 0 alpha...

        final int iw = image.getWidth(null);
        final int ih = image.getHeight(null);
        double width;
        double height;
        
        if (ih > iw)
            {
            width = info.draw.width*scale;
            height = (ih*width)/iw;  // ih/iw = height / width
            }
        else
            {
            height = info.draw.height * scale;
            width = (iw * height)/ih;  // iw/ih = width/height
            }

        int x = (int)(info.draw.x - width / 2.0);
        if(x < xmin)
        	x = (int)(xmin /*+ width / 2.0*/);
        else if(x > (xmax - width))
        	x = (int)(xmax - width);
        
        int y = (int)(info.draw.y - height / 2.0);
       // System.out.println("y " + y +" " +" ymax " + ymax);
        if(y < ymin)
        	y = (int)(ymin);
        else if(y > (ymax - height))
        	y = (int)(ymax - height);
        
        final int w = (int)(width);
        final int h = (int)(height);

        // draw centered on the origin
        graphics.drawImage(image,x,y,w,h,null);
        }
        
    }
