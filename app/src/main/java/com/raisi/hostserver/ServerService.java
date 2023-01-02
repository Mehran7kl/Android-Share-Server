package com.raisi.hostserver;
import android.app.IntentService;
import android.content.Intent;
import com.raisi.httpserver.Server;
import com.raisi.hostserver.reqnodes.NodeTreeFactory;
import com.raisi.httpserver.Log;

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
		server=new Server(intent.getIntExtra("port",6655), requestHandler);
		server.run();
		
		}catch(Throwable e){
			Log.err(e);
		}
	}
	
	
}
