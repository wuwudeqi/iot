package com.ekoplat.iot.util;

public class CrcUtil {

	public static int CRC16(byte[] Buf, int len) {
		int CRC;
		int i, j;
		CRC = 0xffff;
		for (i = 0; i < len; i++) {
			CRC = CRC ^ (Buf[i] & 0xff);
			for (j = 0; j < 8; j++) {
				if ((CRC & 0x01) == 1)
					CRC = (CRC >> 1) ^ 0xA001;
				else
					CRC = CRC >> 1;
			}
		}
		return CRC;
	}

	public static byte[] intToBytes2(int n){
	       byte[] b = new byte[4];
	      
	       for(int i = 0;i < 4;i++)
	       {
	        b[i]=(byte)(n>>(24-i*8));
	     
	   } 
	   return b;
	 }
}
