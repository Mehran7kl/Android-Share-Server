package com.raisi.hostserver;
import android.content.res.AssetFileDescriptor;
import java.io.IOException;
import java.io.InputStream;
import com.raisi.httpserver.Log;
import android.content.res.AssetManager.AssetInputStream;
import android.content.res.AssetManager;
import java.nio.ByteBuffer;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.util.Scanner;
import java.util.Map;
import java.util.TreeMap;

public class Utils
{
	private static Map<Integer,String> cache=new TreeMap<>();
	
	public static String readRawString(int id)throws IOException
	{
		String cached=cache.get(id);
		if(cached!=null) return cached;
		
		InputStream in= MainActivity.currentContext.getResources(). openRawResource(id);
		StringBuilder sb=new StringBuilder();
		Scanner sc=new Scanner(in);
		
		while(sc.hasNextLine()){
			sb.append(sc.nextLine());
		}
		
		String content=sb.toString();
		
		cache.put(id,content);
		in.close();
		return content;
	}
}
