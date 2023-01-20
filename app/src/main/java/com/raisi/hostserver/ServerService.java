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
		try{
		ServerRequestHandler requestHandler=new ServerRequestHandler(NodeTreeFactory.create());
		int port=intent.getIntExtra("port",6655);
		server=new Server(port, requestHandler);
		
		final MainActivity cx=MainActivity.currentContext;
		
		List<InetAddress> addrs=Server.getAddresses();
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
		
		cx.runOnUiThread(new Runnable(){
			public void run()
			{
				
				System.out.println(Thread.currentThread().getName());
				cx.update(sb.toString());
				cx.setContentView(cx.mainlayout);
				
			}
		});
		server.run();
		}catch(Throwable e){
			Log.err(e);
		}
	}
	
	
	
}
