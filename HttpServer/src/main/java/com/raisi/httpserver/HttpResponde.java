package com.raisi.httpserver;

import java.util.Set;
import java.util.Scanner;

public class HttpResponde extends HttpMessage
{
	
	protected StringBuffer source=new StringBuffer();
	private String status;
	
	public HttpResponde(){
		
	}
	
	public void setStatus(String s){
		status=s;
	}
	public String getStatus(){
		return status;
	}

	@Override
	public String getSource()
	{
		make();
		// TODO: Implement this method
		return source.toString();
	}

	
	
	private void make(){
		
		source.setLength(0);
		if(status==null)throw new RuntimeException("status code is null");
		source.append(String.format("%s %s \n",version,status));

		String headerFormat="%s: %s \n";

		Set<String> keys=headers.keySet();

		for(String key:keys){
			String value=headers.get(key);
			String line=String.format(headerFormat,key,value);

			source.append(line);
		}
		
		source.append("\n");
		
	}
	
}
