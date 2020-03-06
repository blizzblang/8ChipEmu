package com.nathan;






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
public void Update(Window Keyboard)
{

}
public boolean getKeyUp(int key) {
return keyUp[key];
}
public boolean getKeyDown(int key) {
return keyDown[key];
}

public int[] GetMouse(int x,int y)
{
	return new int[]{-x-MousePos[0],-y-MousePos[1]};
}


}
