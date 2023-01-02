package com.raisi.httpserver;

import java.util.Scanner;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.io.InputStream;
import java.io.IOException;
import java.io.StringReader;


public class HttpRequest extends HttpMessage
{
	private String source,method,path;
	public HttpRequest(CharSequence src)
	{
		source=src.toString();
		compileSource();
	}
	
	public HttpRequest(InputStream in)throws IOException
	{
		StringBuffer sb=new StringBuffer();
		String line;
		do{
			line=readLine(in);
			if(!line.isEmpty()){ 
				sb.append(line);
				sb.append('\n');
			}
		}while(!line.isEmpty());
		
		source=sb.toString();
		compileSource();
	}
	
	private String readLine(InputStream in)throws IOException{
		StringBuilder sb=new StringBuilder();
		
		int c;
		while(true){
			c=in.read();
			if(c=='\r')continue;
			if(c=='\n')break;
			
			sb.append((char)c);
		}
		
		return sb.toString();
	}
	public String getPath(){
		return path;
	}
	public String getMethod(){
		
		return method;
	}
	
	private void compileSource(){

		headers.clear();
		
		Scanner sc=new Scanner(source);
		method=sc.next();
		
		path=sc.next();
		Matcher m=Pattern.compile("^(.+?)\\s?:\\s*(.+)").matcher("");
		
		version=sc.next();
		sc.nextLine();
		while(sc.hasNextLine()){
			String line=sc.nextLine();
			m.reset(line);
			m.find();
			
			String key=m.group(1).trim();
			String val=m.group(2).trim();
			
			headers.put(key,val);
		}
		
	}
	
	
	@Override
	public String getSource()
	{
		// TODO: Implement this method
		return source;
	}

	
	
}
