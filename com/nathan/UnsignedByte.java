package com.nathan;

public class UnsignedByte 
{
	short TwoBytes = 0x00;
	public UnsignedByte (int i)
	{
		TwoBytes = (short) (i & 0xFF);
		
	}
	public short ReturnOpcode(){return (short) (TwoBytes & 0xFF);}
	
	
	public int getBit(int x) {
		if(x==0){return (TwoBytes & 0x80) >> 7;}
		if(x==1){return (TwoBytes & 0x40) >> 6;}
		if(x==2){return (TwoBytes & 0x20) >> 5;}
		if(x==3){return (TwoBytes & 0x10) >> 4;}
		if(x==4){return (TwoBytes & 0x08) >> 3;}
		if(x==5){return (TwoBytes & 0x04) >> 2;}
		if(x==6){return (TwoBytes & 0x02) >> 1;}
		if(x==7){return (TwoBytes & 0x01) >> 0;}
		else
			return 0;
	}
}
