package com.raisi.hostserver;
import android.content.Context;
import com.raisi.hostserver.reqnodes.PathNode;
import com.raisi.httpserver.HttpRequest;
import com.raisi.httpserver.HttpResponde;
import com.raisi.httpserver.RequestHandler;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.net.URLDecoder;
import com.raisi.httpserver.Server;
import com.raisi.httpserver.Log;

public class ServerRequestHandler implements RequestHandler
{
	private MainActivity context;
	private HttpRequest request;
	private InputStream in ;
	private OutputStream out;
	private PathNode rootNode;
	
	public ServerRequestHandler(PathNode n){
		if(n==null)throw new IllegalArgumentException("null argument in ServerRequestHandler<init>");
		rootNode=n;
		context=MainActivity.currentContext;
	}
	
	@Override
	public void handleRequest(HttpRequest r,InputStream i ,OutputStream o)
	{
		try{
		request=r;
		in=i;
		out=o;
		if(BuildConfig.DEBUG)System.out.println(r.getSource());
		observer();
		}catch(Throwable e){
			Log.err(e);
		}
	}
	
	
	private void observer()throws IOException{
		String[] arr=request.getPath().split("/");
		
		List<String> list=new ArrayList<>();
		for(int i=1;i<arr.length;i++){
			list.add(URLDecoder.decode(arr[i]));
		}
		boolean handled=rootNode.handle(list,request,in,out);
		if(!handled)notFound();
	}
	
	private void notFound()throws IOException{
		HttpResponde p=new HttpResponde();
		p.setStatus(p.NOTF);
		out.write(p.getSourceBytes());
	}
	
	
	
	
}
