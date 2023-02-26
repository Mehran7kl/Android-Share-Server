package com.raisi.httpserver;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class HttpRequest extends HttpMessage
{
	private String source,method,path;
	private StringBuilder sourceCon=new StringBuilder();
	
	private static Pattern
					rangePattern= Pattern.compile("(\\d*)-(\\d*)"),
					headerPattern= Pattern.compile("^(.+?)\\s*:\\s*(.+)");
	
	
	
	public List<int[]> rangeParts=new ArrayList<>();
	
	public HttpRequest(CharSequence src)
	{
		source=src.toString();
		compileSource();
		
	}
	
	public HttpRequest(InputStream in)throws IOException
	{
		DataInputStream din=new DataInputStream(in);
		
		StringBuilder sb=sourceCon;
		String line;
		do{
			line=din.readLine();
			if(line==null){
				try
				{
					Thread.sleep(100);
				}
				catch (InterruptedException e)
				{}
				continue;
			}
			if(!line.isEmpty()){ 
				sb.append(line);
				sb.append(System.lineSeparator());
				if(sb.length()>100000) throw new IllegalStateException("didnt get end of request yet: last input-> "+sb.substring(sb.length()-64));
				
			}
		}while(!line.isEmpty());
		
		source=sb.toString();
		compileSource();
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
		Matcher m=headerPattern.matcher("");
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
		computeRange();
	}
	
	private void computeRange()
	{
		
		
		String val= getHeader(RANGE);
		
		if(val!=null){
			String[] ranges= val.split(",");
			for(String range: ranges){
				int offset=-1,end;
				Matcher matcher=rangePattern.matcher(range);
				
				String result;
				if(!matcher.find())continue;
				result=matcher.group(1);
				if(result.isEmpty())
					offset=-1;
				else
					offset=Integer.valueOf(result);
					
				result=matcher.group(2);
				if(result.isEmpty())
					end=-1;
				else
					end=Integer.valueOf(result);
				
				rangeParts.add(new int[]{offset,end});
				
				
			}
		}
		
	}
	@Override
	public String getSource()
	{
		return source;
	}

	
	
}
