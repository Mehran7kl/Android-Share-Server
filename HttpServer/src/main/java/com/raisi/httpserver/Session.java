package com.raisi.httpserver;

import java.net.Socket;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.SocketException;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;

public class Session implements Runnable, AutoCloseable
{
	private Socket socket;
	private InputStream in;
	private OutputStream out;
	private HttpRequest request;
	private RequestHandler hanler;
	
	public Session(Socket s,RequestHandler h){
		socket=s;
		hanler=h;
		try{
		s.setKeepAlive(true);
		}catch(SocketException e){
			Log.info(s.toString());
		}
	}
	
	private void init()throws IOException{
		in=socket.getInputStream();
		out=socket.getOutputStream();
		in=new BufferedInputStream(in);
		out=new BufferedOutputStream(out);
		
	}
	
	private void read()throws IOException{
		
		request=new HttpRequest(in);
		
	}
	
	@Override
	synchronized public void run()
	{
		//No exception may raise to thread
		try{
			
			init();
			read();
			
			hanler.handleRequest(request,in,out);
			
			
		}catch(Throwable e){
			Log.err(e);
		}
		try{
			close();
		}catch(Throwable t)
		{
			Log.info(t.toString());
		}
	}

	@Override
	synchronized public void close() throws IOException
	{
		in.close();
		out.close();
		socket.close();
	}

	
	
	
	


	
	
}
