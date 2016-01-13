package com.nathan;

import java.io.IOException;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.Display;
import com.nathan.cpu.Cpu;
import static org.lwjgl.opengl.GL11.*;
public class Main {
	public static int scale=(int) Math.pow(2, 3);
	public static Cpu Comp = new Cpu();
	public static KeyInput keys=new KeyInput();
	public static void main(String[] Args)
	{
		if(Args.length > 1)Scale = Integer.parseInt(Args[1]);
		OpenGL.setupDisplay(64*scale, 32*scale, false);
		OpenGL.create();
		OpenGL.initOpenGL();
		Comp.loadROM(Args[0]);
		while(!Display.isCloseRequested())
		{
			OpenGL.clearBuffers();
			keys.Update();
			Comp.step();
			Comp.render();
			Display.update();
		}
		Display.destroy();
	}
}
