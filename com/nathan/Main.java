package com.nathan;
import com.nathan.cpu.Cpu;

public class Main {
	public static int scale=(int) Math.pow(2, 3);
	public static Cpu Comp = new Cpu();
	public static KeyInput keys=new KeyInput();
	private static Window GameWindow  = new Window("Window", 64*scale, 32*scale, true);;
	public static void main(String[] Args)
	{
		if(Args.length > 1)scale = Integer.parseInt(Args[1]);
		GameWindow.init();
		Comp.loadROM(Args[0]);
		while(!GameWindow.windowShouldClose())
		{
			GameWindow.update();
			keys.Update(GameWindow);
			Comp.step();
			Comp.render();
		}
	}
}
