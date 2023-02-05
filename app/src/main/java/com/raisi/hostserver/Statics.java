package com.raisi.hostserver;
import java.util.List;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import com.raisi.httpserver.HttpRequest;
import com.raisi.httpserver.HttpResponde;
import android.util.Pair;

public class Statics
{
	private static volatile List<IOException> ioExceptions=new ArrayList<>();
	private static volatile List<Pair<HttpRequest,HttpResponde>> httpSessions=new ArrayList<>();
	
	
	public static void addIOException(IOException e)
	
	{
		synchronized(ioExceptions){
			ioExceptions.add(e);
		}
	}
	public static IOException getIOException(int i)
	{
		synchronized(ioExceptions)
		{
			return ioExceptions.get(i);
		}
	}
	public static IOException getIOException()
	{
		synchronized(ioExceptions)
		{
			return ioExceptions.get(ioExceptions.size()-1);
		}
	}
	public static Iterator<IOException> getIOExcpetions()
	{
		synchronized(ioExceptions)
		{
			return ioExceptions.iterator();
		}
	}
	
	public static void addHttpSession(HttpRequest req, HttpResponde res)
	{
		synchronized(httpSessions)
		{
			httpSessions.add(Pair.create(req,res));
		}
	}
	
	public static Pair<HttpRequest,HttpResponde> getHttpSession(int i)
	{
		synchronized(httpSessions)
		{
			return httpSessions.get(i);
		}
	}
	public static Pair<HttpRequest,HttpResponde> getHttpSession()
	{
		synchronized(httpSessions)
		{
			return httpSessions.get(httpSessions.size()-1);
		}
	}
	public static Iterator<Pair<HttpRequest,HttpResponde>> getHttpSessionIt()
	{
		synchronized(httpSessions)
		{
			return httpSessions.iterator();
		}
	}
	
	
}
