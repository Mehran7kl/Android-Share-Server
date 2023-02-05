package com.raisi.hostserver;
import android.app.IntentService;
import android.content.Intent;
import com.raisi.httpserver.Server;
import com.raisi.hostserver.reqnodes.NodeTreeFactory;
import com.raisi.httpserver.Log;
import com.raisi.httpserver.R;
import android.view.View;
import java.util.List;
import java.net.InetAddress;
import java.util.Locale;
import java.io.IOException;
import java.net.SocketException;

public class ServerService extends IntentService
{
	private Server server;
	public ServerService()
	{
		super("Share Server");
	}
	@Override
	protected void onHandleIntent(Intent intent)
	{
		
		ServerRequestHandler requestHandler=new ServerRequestHandler(NodeTreeFactory.create());
		int port=intent.getIntExtra("port",6655);
		try{
		server=new Server(port, requestHandler);
		}catch(IOException e){
			Log.err(e);
			stopSelf();
			return;
		}
		final MainActivity cx=MainActivity.currentContext;
		
		final String urls=getAddreses(port);
		
		cx.runOnUiThread(new Runnable(){
			public void run()
			{
				cx.initView();
				if(urls!=null) cx.updateWebView(urls);
				
			}
		});
		try{
		server.run();
		}catch(Throwable e){
			Log.err(e);
		}
	}
	
	private String getAddreses(int port){
		List<InetAddress> addrs=null;

		
		try{
			addrs=Server.getAddresses();
		}catch(SocketException e){

		}
		String urls=null;
		if(addrs!=null){
			final StringBuilder sb=new StringBuilder();

			for (InetAddress a:addrs)
			{
				if (a.isLoopbackAddress())continue;
				sb.append("http://");
				sb.append(a.getHostAddress());
				sb.append(":");
				sb.append(port);
				sb.append("\n");

			}
			urls=sb.toString();
		}
		return urls;
	}
	
}
