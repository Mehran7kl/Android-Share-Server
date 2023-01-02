package com.raisi.hostserver;
import java.io.InputStream;
import java.io.FileReader;
import java.io.Reader;
import java.io.StringReader;
import java.io.IOException;
import android.content.res.AssetFileDescriptor;

public class Utils
{
	public static String readRawString(int id)throws IOException{
		String str;

		
		InputStream in=MainActivity.currentContext.getResources().openRawResource(id);
		
		int length= in.available();
		if(length>1024*10)throw new IllegalArgumentException("large file size");
		
		
		byte[] buf=new byte[length];
		
		in.read(buf);
		
		str=new String(buf);
		
		return str;
	}
}
