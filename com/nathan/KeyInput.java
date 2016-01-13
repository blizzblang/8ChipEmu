package com.nathan;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;





public class KeyInput {
private boolean[] keyDown = new boolean[208+1];
private boolean[] keyUp = new boolean[208+1];
private boolean[] keyUpHelper = new boolean[208+1];
int[] MousePos;
int YMouseangle=90;
double Rotation=0;
double dif=0;
//int[] caps = new int[]{90-30,90+30};
int[] caps = new int[]{0,360};
public KeyInput()
{
	MousePos = new int[]{0,0};
	Rotation = 0;
}
public void Update()
{
	for(int x=0;x<keyUp.length;x++)
	{
		if(keyUpHelper[x])keyUp[x]=false;
	}
	while(Keyboard.next())
	{
		for(int x=0;x<keyUp.length;x++)
		{
			
			if(Keyboard.getEventKey() == x)
			{
				

				if(Keyboard.getEventKeyState())
				{
				keyDown[x] = true;
				keyUp[x] = false;
				}
				else
				{
				keyDown[x] = false;
				keyUp[x] = true;
				keyUpHelper[x]=true;
				}
				
			}
			
		}
	}
	
	int dy = Mouse.getDY();
	int dx = Mouse.getDX();
	MousePos[0] -= dx;
	MousePos[1] -= dy;
	
}
public boolean getKeyUp(int key) {
return keyUp[key];
}
public boolean getKeyDown(int key) {
return keyDown[key];
}
public int[] GetMouseFromCenter()
{
	return GetMouse(OpenGL.dims[0]/2,OpenGL.dims[1]/2);
}
public int[] GetMouse(int x,int y)
{
	return new int[]{-x-MousePos[0],-y-MousePos[1]};
}
public double getAngle()
{
//return dif;	
	return Math.toDegrees(Math.atan2(GetMouseFromCenter()[1], GetMouseFromCenter()[0]));
}

}
