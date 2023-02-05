package com.raisi.httpserver;


import java.net.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import android.os.HandlerThread;
import android.os.Handler;
import android.os.Looper;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;

public class Server implements Runnable
{
	
	ServerSocket ss;
	RequestHandler reqHandler;
	
	//Will throw IOException in failure to create ServerSocket
	public Server(int port,RequestHandler handler)throws IOException{
		ss=new ServerSocket(port);
		reqHandler=handler;
		try{
			ss.setReuseAddress(true);
		}catch(SocketException e){
			Log.d(e.toString());
		}
		
	}
	
	@Override
	public void run()
	{
		while(true){
			Socket socket=null;
			try{
			
			socket=ss.accept();
			
			}catch(IOException e){
				Log.info(e.toString());
				continue;
			}
			
			
			Session s=new Session(socket,reqHandler);
			new Thread(s).start();
			
			
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
