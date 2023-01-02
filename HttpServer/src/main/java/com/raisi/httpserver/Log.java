package com.raisi.httpserver;
import java.util.List;
import java.util.ArrayList;

public class Log
{
	public static final int 
				ERROR_LEVEL=1,
				ASSERT_LEVEL=2,
				DEBUG_LEVEL=4,
				INFO_LEVEL=8;
	
	private static List<Listener> 
	Elisteners=new ArrayList<>(),
	Alisteners=new ArrayList<>(),
	Dlisteners=new ArrayList<>(),
	Ilisteners=new ArrayList<>();
	public static void addListener(Listener l, int filter)
	{
		if((ERROR_LEVEL&filter)==1)Elisteners.add(l);
		if((ASSERT_LEVEL&filter)==1)Alisteners.add(l);
		if((DEBUG_LEVEL&filter)==1)Dlisteners.add(l);
		if((INFO_LEVEL&filter)==1)Ilisteners.add(l);
		
	}
	public static void info(String s){
		for(Listener l:Ilisteners)
			l.onLog(s);
	}
	public static void d(String s){
		for(Listener l:Dlisteners)
			l.onLog(s);
	}
	public static void ass(String s){
		for(Listener l:Alisteners)
			l.onLog(s);
	}
	public static void err(String s){
		for(Listener l:Elisteners)
			l.onLog(s);
	}
	public static void err(String fmt, String... args){
		err(String.format(fmt,args));
	}
	public static void ass(String fmt, String... args){
		ass(String.format(fmt,args));
	}
	public static void d(String fmt, String... args){
		d(String.format(fmt,args));
	}
	public static void info(String fmt, String... args){
		info(String.format(fmt,args));
	}
	public static void err(Throwable e){
		StringBuilder sb=new StringBuilder();
		sb.append(e.toString()+System.lineSeparator());
		for(StackTraceElement ele:e.getStackTrace())sb.append(ele.toString()+System.lineSeparator());
		err(sb.toString());
	}
	public static abstract class Listener{
		
		public abstract void onLog(String s);
		
	}
}
