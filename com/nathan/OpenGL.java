package com.nathan;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;

import javax.imageio.ImageIO;

import org.lwjgl.LWJGLException;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.PixelFormat;
import static org.lwjgl.opengl.GL11.*;
import org.lwjgl.opengl.ContextAttribs;
import org.newdawn.slick.opengl.ImageIOImageData;





public class OpenGL {
	public static int[] dims;
	
	public static void initOpenGL()
	{

		System.out.print("\nInitializing OpenGL...");
	    GL11.glMatrixMode(GL11.GL_PROJECTION);
	    GL11.glLoadIdentity();
	    GL11.glOrtho(0,dims[0],dims[1],0, 1, -1);
	    GL11.glMatrixMode(GL11.GL_MODELVIEW);

    
		System.out.print("done \n");
	}
        
	public static void setupDisplay(int[] d,boolean full){setupDisplay(d[0],d[1],full);}
	public static void setupDisplay(int w,int h,boolean full)
	{
		System.out.print("\nInitializing display...");
		dims = new int[]{w,h};
		DisplayMode[] modes = null;
		Display.setLocation(0, 0);
		if(full)
		{
			
			try {
			modes = Display.getAvailableDisplayModes();
			} catch (LWJGLException e) {e.printStackTrace();}

			for (int i=0;i<modes.length;i++) {
			    DisplayMode current = modes[i];
			    System.out.println("Res: "+current.getWidth() + " by " + current.getHeight() + " at " +
			                        current.getBitsPerPixel() + " BPP with " + current.getFrequency() + "Hz"+" : "+i);
			}
			try {
				
				Display.setDisplayMode(modes[10]);
				Display.setFullscreen(full);
				Display.setLocation(100, 0);
				
			} catch (LWJGLException e) {e.printStackTrace();}
		}
		else
		{
			
			try {
				Display.setDisplayMode(new DisplayMode(w, h));
			} catch (LWJGLException e) {e.printStackTrace();}
		}
		System.out.print("done");
	}
	public static void setDisplayTitle(String t)
	{
		Display.setTitle(t);
	}

	public static void create()
	{
		System.out.print("\nCreating display...");
		try {
			Display.create();
			} catch (LWJGLException e) {e.printStackTrace();}
		System.out.print("done");
	}
	public static void clearBuffers() {
		 glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
		glLoadIdentity(); 
	}
	public static void CenterScreen(int x,int y){CenterScreen(x,y,1);}
	public static void CenterScreen(int[] p){CenterScreen(p,1);}
	public static void CenterScreen(int[] p,double scale){CenterScreen(p[0],p[1],scale);}
	public static void CenterScreen(int x,int y,double scale)
	{
		double[] n = new double[]{
				(-x+dims[0]/2/scale),
				(-y+dims[1]/2/scale),
		};
		glScaled(scale,scale,1);
		glTranslated(n[0],n[1], 1);
	}
	public static void push() {glPushMatrix();}
	public static void pop() {glPopMatrix();}
	
	public static int[] getMousePos(){return getMousePos(dims[0]/2,dims[1]/2);}
	public static int[] getMousePos(int x,int y)
	{return new int[]{Mouse.getX()-x,Mouse.getY()-y};}
	public static void en(int g) {
		
		glEnable(g);
	}
	public static void di(int g) {
		glDisable(g);
	}
	public static double getMouseAngle() {
		return Math.atan2(getMousePos()[1], getMousePos()[0]);
		
	}
}
