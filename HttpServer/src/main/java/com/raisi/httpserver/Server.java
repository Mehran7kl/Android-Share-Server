package com.raisi.httpserver;


import java.net.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import android.os.HandlerThread;
import android.os.Handler;
import android.os.Looper;

public class Server implements Runnable
{
	final public boolean AsyncServer=false;
	ServerSocket ss;
	RequestHandler reqHandler;

	public Server(int port,RequestHandler handler)throws IOException{
		ss=new ServerSocket(port);
		reqHandler=handler;
		ss.setReuseAddress(true);
		
	}
	
	@Override
	public void run()
	{
		while(true){
			Socket socket=null;
			try{
			
			socket=ss.accept();
			
			}catch(Throwable e){
				Log.err(e);
				continue;
			}
			try{
			
			
			Session s=new Session(socket,reqHandler);
			if(AsyncServer) new Thread(s).start();
			else s.run();
			}catch(Throwable e){
				Log.err(e);
			}
		}
		
	}
	
	public static List<InetAddress> getAddresses()throws SocketException{
		List<InetAddress> inets=new ArrayList<>();
		Enumeration<NetworkInterface> all= NetworkInterface.getNetworkInterfaces();
		while(all.hasMoreElements()){
			NetworkInterface net=all.nextElement();
			List<InterfaceAddress> addrs=net.getInterfaceAddresses();
			
			for(InterfaceAddress add: addrs){
				if(add.getNetworkPrefixLength()==128)continue;
				inets.add(add.getAddress());


			}
		}
		return inets;
	}
}
