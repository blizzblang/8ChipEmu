package com.nathan.cpu;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Random;

import org.lwjgl.opengl.GL11;

import com.nathan.Main;
import com.nathan.OpenGL;
import com.nathan.UnsignedByte;
import com.nathan.UnsignedShort;

public class Cpu {
	InputStream RomFile;
	public int[][] Screen = new int[64][32];
	public UnsignedByte[] Memory = new UnsignedByte[4096]; 		 // About 4 Kilobytes
	public UnsignedByte[] Register = new UnsignedByte[16]; 		 // 16 Bytes
	public UnsignedByte[] Keyboard = new UnsignedByte[16];
	public UnsignedByte[] Sound_and_Delay = new UnsignedByte[2]; // 2 Bytes
	public UnsignedShort[] Program_Stack=new UnsignedShort[16];  // 32 bytes for the stack , each 2 is used for a single memory pointer
	
	public UnsignedByte Stack_Pointer= new UnsignedByte(0x00);
	public UnsignedShort Progran_counter=new UnsignedShort(0x200);
	public UnsignedShort  I= new UnsignedShort(0x0000);
	public UnsignedShort opcode = new UnsignedShort (0x0000);
	boolean debug=false;
	public Cpu()
	{

		for(int i=0;i<Memory.length;i++){Memory[i] = new UnsignedByte(0x00);};
		for(int i=0;i<Register.length;i++){Register[i] = new UnsignedByte(0x00);};
		for(int i=0;i<Sound_and_Delay.length;i++){Sound_and_Delay[i] = new UnsignedByte(0x00);};
		for(int i=0;i<Program_Stack.length;i++){Program_Stack[i] = new UnsignedShort(0x00);};
        int[] hexChars = new int[]{
                        0xF0, 0x90, 0x90, 0x90, 0xF0, // 0
                        0x20, 0x60, 0x20, 0x20, 0x70, // 1
                        0xF0, 0x10, 0xF0, 0x80, 0xF0, // 2
                        0xF0, 0x10, 0xF0, 0x10, 0xF0, // 3
                        0x90, 0x90, 0xF0, 0x10, 0x10, // 4
                        0xF0, 0x80, 0xF0, 0x10, 0xF0, // 5
                        0xF0, 0x80, 0xF0, 0x90, 0xF0, // 6
                        0xF0, 0x10, 0x20, 0x40, 0x40, // 7
                        0xF0, 0x90, 0xF0, 0x90, 0xF0, // 8
                        0xF0, 0x90, 0xF0, 0x10, 0xF0, // 9
                        0xF0, 0x90, 0xF0, 0x90, 0x90, // A
                        0xE0, 0x90, 0xE0, 0x90, 0xE0, // B
                        0xF0, 0x80, 0x80, 0x80, 0xF0, // C
                        0xE0, 0x90, 0x90, 0x90, 0xE0, // D
                        0xF0, 0x80, 0xF0, 0x80, 0xF0, // E
                        0xF0, 0x80, 0xF0, 0x80, 0x80 // F
        };
        for(int i=0;i<hexChars.length;i++){Memory[i+0x050]=new UnsignedByte(hexChars[i]);}
	}
	public void OperationCodes(UnsignedShort code)
	{
		int A = (code.ReturnOpcode() & 0xf000)>>12;
		int B = (code.ReturnOpcode() & 0x0f00)>>8;
		int C = (code.ReturnOpcode() & 0x00f0)>>4;
		int D = (code.ReturnOpcode() & 0x000f)>>0;
		int BC = (B<<4)|C;
		int BCD = ((B<<8) | (C<<4)) | (D);
		int CD = (C<<4 | D);
		switch(A)
		{
		case 0x0:
			switch(D)
			{
			case 0x0:
			if(debug)
			System.out.println("Clearing the screen");
			OpenGL.clearBuffers();
			break;
			
			case 0xE:
			if(debug)
			System.out.println("Returning from subroutine to "+Integer.toHexString(Program_Stack[Stack_Pointer.ReturnOpcode()].ReturnOpcode()));
			Progran_counter = new UnsignedShort(Program_Stack[Stack_Pointer.ReturnOpcode()].ReturnOpcode());
			Stack_Pointer = new UnsignedByte(Stack_Pointer.ReturnOpcode() -1);
		//	Progran_counter.minus(0x2);
			break;
			}
		break;
		case 0x1:
			UnsignedShort t1 = new UnsignedShort(BCD);
			if(debug)
			System.out.println("Jumping to address "+Integer.toHexString(t1.ReturnOpcode()));
			Progran_counter = t1;
			Progran_counter.minus(0x2);
		break;
		case 0x2:
			UnsignedShort t2 = new UnsignedShort(BCD);
			if(debug)
			System.out.println("Jumping to subroutine "+Integer.toHexString(t2.ReturnOpcode())+ " from "+Integer.toHexString(Progran_counter.ReturnOpcode()));
			Stack_Pointer = new UnsignedByte(Stack_Pointer.ReturnOpcode() + 1);
			Program_Stack[Stack_Pointer.ReturnOpcode()] = new UnsignedShort(Progran_counter.ReturnOpcode());
			Progran_counter = t2;
			Progran_counter.minus(0x2);
			
		break;
		case 0x3:
		if(debug)
		System.out.println("Checking if register "+Integer.toHexString(B)+" ( "+Integer.toHexString(Register[B].ReturnOpcode())+" ) is equal to "+Integer.toHexString(CD));
		if(Register[B].ReturnOpcode() == CD)
		Progran_counter = new UnsignedShort(Progran_counter.ReturnOpcode()+2);
		break;
		case 0x4:
		if(debug)
		System.out.println("Checking if register "+Integer.toHexString(B)+" ( "+Integer.toHexString(Register[B].ReturnOpcode())+" ) is not equal to "+Integer.toHexString(CD));
		if(Register[B].ReturnOpcode() != CD)
		Progran_counter = new UnsignedShort(Progran_counter.ReturnOpcode()+2);
		break;
		case 0x5:
		if(debug)
		System.out.println("Checking if register "+Integer.toHexString(B)+" ( "+Integer.toHexString(Register[B].ReturnOpcode())+" ) is equal to register "+Integer.toHexString(C)+" ( "+Integer.toHexString(Register[C].ReturnOpcode())+" )");
		if(Register[B].ReturnOpcode() == Register[C].ReturnOpcode())
		Progran_counter = new UnsignedShort(Progran_counter.ReturnOpcode()+2);
		break;
		case 0x6:
		if(debug)
		System.out.println("Setting register "+Integer.toHexString(B)+" to "+Integer.toHexString(CD));
		Register[B] = new UnsignedByte(CD);
		break;
		case 0x7:
		if(debug)
		System.out.println("Setting register "+getHex(B)+" to "+getHex(Register[B].ReturnOpcode())+ " + "+getHex(CD)+" , it's "+getHex(Register[B].ReturnOpcode()+CD));
		Register[B] = new UnsignedByte(Register[B].ReturnOpcode()+CD);
		break;
		case 0x8:
			switch(D)
			{
			case 0:
			if(debug)
			System.out.println("Setting register "+Integer.toHexString(B)+" ( "+Integer.toHexString(Register[B].ReturnOpcode())+" ) equal to register "+Integer.toHexString(C)+" ( "+Integer.toHexString(Register[C].ReturnOpcode())+" ) ");
			Register[B] = new UnsignedByte(Register[C].ReturnOpcode());
			break;
			case 1:
			if(debug)
			System.out.println("Register "+getHex(B)+" set to "+Integer.toBinaryString(Register[B].ReturnOpcode())+" or "+Integer.toBinaryString(Register[C].ReturnOpcode())+" from register "+Integer.toHexString(C));
			Register[B] = new UnsignedByte(Register[B].ReturnOpcode() | Register[C].ReturnOpcode());
			break;
			case 2:
			if(debug)
			System.out.println("Register "+getHex(B)+" set to "+Integer.toBinaryString(Register[B].ReturnOpcode())+" and "+Integer.toBinaryString(Register[C].ReturnOpcode())+" from register "+Integer.toHexString(C));
			Register[B] = new UnsignedByte(Register[B].ReturnOpcode() & Register[C].ReturnOpcode());
			break;
			case 3:
			if(debug)
			System.out.println("Register "+getHex(B)+" set to "+Integer.toBinaryString(Register[B].ReturnOpcode())+" XOR "+Integer.toBinaryString(Register[C].ReturnOpcode())+" from register "+Integer.toHexString(B)+" & "+Integer.toHexString(C));
			Register[B] = new UnsignedByte(Register[B].ReturnOpcode() ^ Register[C].ReturnOpcode());
			break;
			case 4:
			if(debug)
			System.out.println("Adding register "+getHex(B)+" and "+getHex(C)+" it's "+getHex((Register[B].ReturnOpcode()+Register[C].ReturnOpcode())&0xff));
			Register[B] = new UnsignedByte(Register[B].ReturnOpcode()+Register[C].ReturnOpcode());
			if(Register[B].ReturnOpcode() + Register[C].ReturnOpcode() > 0xFF)
				Register[0xF] = new UnsignedByte(0x1);
			else
				Register[0xF] = new UnsignedByte(0x0);
			break;
			case 5:
			if(debug)
			System.out.print("subtracting register "+getHex(B)+" and "+getHex(C)+" it's "+getHex((Register[B].ReturnOpcode()-Register[C].ReturnOpcode())&0xff));
			Register[B] = new UnsignedByte(Register[B].ReturnOpcode()-Register[C].ReturnOpcode());
			if(Register[B].ReturnOpcode() > Register[C].ReturnOpcode())
				Register[0xF] = new UnsignedByte(0x1);
			else
				Register[0xF] = new UnsignedByte(0x0);

			break;
			case 6:
			if(debug)
			System.out.println("If the least signifigant digit of register "+getHex(B)+" ( "+Integer.toBinaryString(Register[B].ReturnOpcode())+" ) is 0" );
			if(Integer.toBinaryString(Register[B].ReturnOpcode()).endsWith("1"))
				Register[0xf] = new UnsignedByte(0x1);
			else
				Register[0xf] = new UnsignedByte(0x0);
			break;
			case 7:
				if(debug)
					System.out.print("subtracting register "+getHex(C)+" and "+getHex(B)+" it's "+getHex((Register[C].ReturnOpcode()-Register[B].ReturnOpcode())&0xff));
					Register[B] = new UnsignedByte(Register[C].ReturnOpcode()-Register[D].ReturnOpcode());
					if(Register[C].ReturnOpcode() > Register[B].ReturnOpcode())
						Register[0xF] = new UnsignedByte(0x1);
					else
						Register[0xF] = new UnsignedByte(0x0);

			break;
			case 0xe:
				if(debug)
					System.out.println("If the most signifigant digit of register "+getHex(B)+" ( "+Integer.toBinaryString(Register[B].ReturnOpcode())+" ) is 0" );
					if(Integer.toBinaryString(Register[B].ReturnOpcode()).startsWith("1"))
						Register[0xf] = new UnsignedByte(0x1);
					else
						Register[0xf] = new UnsignedByte(0x0);
			break;
			default:
			System.exit(-1);
			break;
			}
		break;
		case 9:
		if(debug)
		System.out.println("Checking if register "+getHex(B)+" ( "+getHex(Register[B].ReturnOpcode())+" ) != register "+getHex(C)+" ( "+getHex(Register[C].ReturnOpcode())+" ) ");
		if(Register[B].ReturnOpcode() != Register[C].ReturnOpcode())
		Progran_counter = new UnsignedShort(Progran_counter.ReturnOpcode()+2);
		break;
		case 0xA:
		if(debug)
		System.out.println("Setting I to "+getHex(BCD));
		I = new UnsignedShort(BCD);	
		break;
		case 0xB:
		if(debug)
		System.out.println("Setting I to "+getHex(BCD)+" + register 0 ( "+getHex(Register[0].ReturnOpcode())+" ) ");
		I = new UnsignedShort(BCD+Register[0].ReturnOpcode());
		break;
		case 0xC:
			if(debug)
				System.out.println("Setting Register "+getHex(B)+" to "+getHex(CD)+" and a random number ");
		Register[B] = new UnsignedByte(CD&new Random().nextInt(0xFF));
		break;
		case 0xD:
		
		int dx = Register[B].ReturnOpcode();
		int dy = Register[C].ReturnOpcode();
		Register[0xF] = new UnsignedByte(0x0);
		if(debug)
		System.out.println("Drawing at "+dx+" , "+dy+" - "+D);
		for(int x=0;x<8;x++)
			for(int y=0;y<D;y++)
			{
				int pre = Screen[(dx+x)%64][(dy+y)%32]+0;
				Screen[(dx+x)%64][(dy+y)%32] = Screen[(dx+x)%64][(dy+y)%32]  ^ Memory[I.ReturnOpcode()+y].getBit(x);
				int post = Screen[(dx+x)%64][(dy+y)%32]+0;
				if(pre==1&&post==0)Register[0xF] = new UnsignedByte(0x1);
				
			}
		break;
		case 0xE:
		if(D==0xE)
		{
			int key = Register[B].ReturnOpcode();
			if(debug)
			System.out.println("Skipping if key "+Integer.toHexString(key)+" is pressed");
			if(Keyboard[key].ReturnOpcode()==0x1)
			{
				Progran_counter = new UnsignedShort(Progran_counter.ReturnOpcode()+0x2);
			}
		}
		else
		if(D==0x1)
		{
			int key = Register[B].ReturnOpcode();
			if(debug)
			System.out.println("Skipping if key "+Integer.toHexString(key)+" isn't pressed");
			if(Keyboard[key].ReturnOpcode()==0x0)
			{
				Progran_counter = new UnsignedShort(Progran_counter.ReturnOpcode()+0x2);
			}
		}
		break;
		case 0xF:
			switch(CD)
			{
			case 0x07:
				if(debug)
				System.out.println("Setting register "+getHex(B)+ " to delay timer: "+Sound_and_Delay[1].ReturnOpcode());
				Register[B] = new UnsignedByte(Sound_and_Delay[1].ReturnOpcode());
				break;
			case 0x0a:
				//TODO
				break;
			case 0x15:
				if(debug)
				System.out.println("Setting Delay timer to "+getHex(B));
				Sound_and_Delay[1] = new UnsignedByte(Register[B].ReturnOpcode());
				break;
			case 0x18:
				if(debug)
				System.out.println("Setting Sound timer to "+getHex(B));
				Sound_and_Delay[0] = new UnsignedByte(Register[B].ReturnOpcode());
				break;
			case 0x1E:
				I = new UnsignedShort(I.ReturnOpcode()+Register[B].ReturnOpcode());
				break;
			case 0x29:
				if(debug)
				System.out.println("Returning memory for number "+getHex(Register[B].ReturnOpcode()));
				I = new UnsignedShort(0x50+Register[B].ReturnOpcode()*5);
				break;
			case 0x33:
				int Reg = Register[B].ReturnOpcode();
				String num = Integer.toString(Reg);
				if(debug)
				System.out.println("Saving code "+getHex(Reg)+" from register "+getHex(B)+" at "+Integer.toHexString(I.ReturnOpcode()));
				if(num.length() == 0)
				{
					Memory[I.ReturnOpcode()]=new UnsignedByte(0);
					Memory[I.ReturnOpcode()+1]=new UnsignedByte(0);
					Memory[I.ReturnOpcode()+2]=new UnsignedByte(0);
				}
				if(num.length() == 1)
				{
					Memory[I.ReturnOpcode()+2]=new UnsignedByte(Integer.parseInt(num));
					Memory[I.ReturnOpcode()+1]=new UnsignedByte(0);
					Memory[I.ReturnOpcode()+0]=new UnsignedByte(0);
				}
				if(num.length() == 2)
				{
					Memory[I.ReturnOpcode()+1]=new UnsignedByte(Integer.parseInt(num.substring(0, 1)));
					Memory[I.ReturnOpcode()+0]=new UnsignedByte(Integer.parseInt(num.substring(1, 2)));
					Memory[I.ReturnOpcode()+0]=new UnsignedByte(0);
				}
				if(num.length() == 3)
				{
					Memory[I.ReturnOpcode()+2]=new UnsignedByte(Integer.parseInt(num.substring(0, 1)));
					Memory[I.ReturnOpcode()+1]=new UnsignedByte(Integer.parseInt(num.substring(1, 2)));
					Memory[I.ReturnOpcode()+0]=new UnsignedByte(Integer.parseInt(num.substring(2, 3)));
				}
				break;
			case 0x55:
				if(debug)
				System.out.println("Reading from registter 0-"+getHex(B)+" into memory "+getHex(I.ReturnOpcode()));
				for(int x=0;x<B;x++)
				{
					Memory[I.ReturnOpcode()+x]= new UnsignedByte(Register[x].ReturnOpcode());
				}
				break;
			case 0x65:
				if(debug)
				System.out.println("Reading from memory "+getHex(I.ReturnOpcode())+" into regsiter 0-"+getHex(B));
				for(int x=0;x<B;x++)
				{
					Register[x] = new UnsignedByte(Memory[I.ReturnOpcode()+x].ReturnOpcode());
				}
				break;
			}
		break;
		}
	}
	public void loadROM(String fie) 
	{
		File file = new File(fie);
		FileInputStream fin = null;
		try {
			// create FileInputStream object
			fin = new FileInputStream(file);

			byte fileContent[] = new byte[(int)file.length()];
			
			// Reads up to certain bytes of data from this input stream into an array of bytes.
			fin.read(fileContent);
			
			//create string from byte array
			String s = new String(fileContent);
			System.out.println("File content: " + fileContent.length);
			for(int i=0;i<fileContent.length;i+=2)
			{
			//	System.out.println(Integer.toHexString(new UnsignedByte(fileContent[i]).ReturnOpcode())+Integer.toHexString(new UnsignedByte(fileContent[i+1]).ReturnOpcode()));
				Memory[512+i] = new UnsignedByte(fileContent[i]);
				Memory[512+i+1] = new UnsignedByte(fileContent[i+1]);
			}
		}
		catch (FileNotFoundException e) {
			System.out.println("File not found" + e);
		}
		catch (IOException ioe) {
			System.out.println("Exception while reading file " + ioe);
		}
		finally {
			// close the streams using close method
			try {
				if (fin != null) {
					fin.close();
				}
			}
			catch (IOException ioe) {
				System.out.println("Error while closing stream: " + ioe);
			}
		}
		
	}
	public void step()
	{
		updateKeys();
		//System.out.println("START: "+Integer.toHexString(Progran_counter.ReturnOpcode()));
		int PC = Progran_counter.ReturnOpcode();
		UnsignedByte now = new UnsignedByte(getMem(Progran_counter.ReturnOpcode()).ReturnOpcode());
		UnsignedByte then= new UnsignedByte(getMem(Progran_counter.ReturnOpcode()+1).ReturnOpcode());
		UnsignedShort ocode = new UnsignedShort(((now.ReturnOpcode() << 8) | then.ReturnOpcode()));
		
		System.out.println("-- Opcode: "+Integer.toHexString(ocode.ReturnOpcode())+ " at "+Integer.toHexString(PC-0x200));
		this.OperationCodes(ocode);
		
		Progran_counter = new UnsignedShort(Progran_counter.ReturnOpcode()+0x2);
		for(int i=0;i<2;i++)
		{
			if(Sound_and_Delay[i].ReturnOpcode() - 1 >= 0)
			Sound_and_Delay[i] = new UnsignedByte(Sound_and_Delay[i].ReturnOpcode()-0x1);
		}
	}
	public UnsignedByte getMem(int pointer)
	{

		return Memory[pointer];
	}
	public void render() {
		
		for(int x=0;x<this.Screen.length;x++)
			for(int y=0;y<this.Screen[0].length;y++)
			{

				int S=Main.scale;
				GL11.glBegin(GL11.GL_QUADS);
				if(Screen[x][y] ==1)
				{
				
					GL11.glColor3f(1f, 1f, 1f);
				}
				else
					GL11.glColor3f(0f, 0f, 0f);
				GL11.glVertex2i(x*S, y*S);
				GL11.glVertex2i(x*S+S, y*S);
				GL11.glVertex2i(x*S+S, y*S+S);
				GL11.glVertex2i(x*S, y*S+S);
				GL11.glEnd();
			}
		
		
	}
	public void updateKeys()
	{
		for(int i=0;i<Keyboard.length;i++)
		{
			Keyboard[i] = new UnsignedByte(0x0);
		}
		if(Main.keys.getKeyDown(org.lwjgl.input.Keyboard.KEY_X)){Keyboard[0x0]=new UnsignedByte(0x1);}
		if(Main.keys.getKeyDown(org.lwjgl.input.Keyboard.KEY_1)){Keyboard[0x1]=new UnsignedByte(0x1);}
		if(Main.keys.getKeyDown(org.lwjgl.input.Keyboard.KEY_2)){Keyboard[0x2]=new UnsignedByte(0x1);}
		if(Main.keys.getKeyDown(org.lwjgl.input.Keyboard.KEY_3)){Keyboard[0x3]=new UnsignedByte(0x1);}
		
		if(Main.keys.getKeyDown(org.lwjgl.input.Keyboard.KEY_Q)){Keyboard[0x4]=new UnsignedByte(0x1);}
		if(Main.keys.getKeyDown(org.lwjgl.input.Keyboard.KEY_W)){Keyboard[0x5]=new UnsignedByte(0x1);}
		if(Main.keys.getKeyDown(org.lwjgl.input.Keyboard.KEY_E)){Keyboard[0x6]=new UnsignedByte(0x1);}
		if(Main.keys.getKeyDown(org.lwjgl.input.Keyboard.KEY_A)){Keyboard[0x7]=new UnsignedByte(0x1);}
		
		if(Main.keys.getKeyDown(org.lwjgl.input.Keyboard.KEY_S)){Keyboard[0x8]=new UnsignedByte(0x1);}
		if(Main.keys.getKeyDown(org.lwjgl.input.Keyboard.KEY_D)){Keyboard[0x9]=new UnsignedByte(0x1);}
		if(Main.keys.getKeyDown(org.lwjgl.input.Keyboard.KEY_Z)){Keyboard[0xa]=new UnsignedByte(0x1);}
		if(Main.keys.getKeyDown(org.lwjgl.input.Keyboard.KEY_C)){Keyboard[0xb]=new UnsignedByte(0x1);}
		
		if(Main.keys.getKeyDown(org.lwjgl.input.Keyboard.KEY_4)){Keyboard[0xc]=new UnsignedByte(0x1);}
		if(Main.keys.getKeyDown(org.lwjgl.input.Keyboard.KEY_R)){Keyboard[0xd]=new UnsignedByte(0x1);}
		if(Main.keys.getKeyDown(org.lwjgl.input.Keyboard.KEY_F)){Keyboard[0xe]=new UnsignedByte(0x1);}
		if(Main.keys.getKeyDown(org.lwjgl.input.Keyboard.KEY_V)){Keyboard[0xf]=new UnsignedByte(0x1);}

	}
	public void setDebug(boolean b) {
	debug=b;
	}
	public String getHex(int i)
	{
		return Integer.toHexString(i);
	}

}
	

