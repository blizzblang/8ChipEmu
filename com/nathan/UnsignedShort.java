package com.nathan;

public class UnsignedShort 
{
	int TwoBytes = 0x0000;
	public UnsignedShort (int i)
	{
		TwoBytes = i & 0xFFFF;
		
	}
	public UnsignedShort(UnsignedShort i) 
	{
		TwoBytes = i.ReturnOpcode() & 0xFFFF;
	}
	public int ReturnOpcode(){return TwoBytes & 0xFFFF;}
	public void minus(int i) {
		TwoBytes = (TwoBytes-i) & 0xFFFF;
		
	}

}
