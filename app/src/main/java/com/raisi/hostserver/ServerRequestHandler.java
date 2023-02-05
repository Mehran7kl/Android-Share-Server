package com.raisi.hostserver;
import com.raisi.hostserver.reqnodes.PathNode;
import com.raisi.httpserver.HttpRequest;
import com.raisi.httpserver.HttpResponde;
import com.raisi.httpserver.Log;
import com.raisi.httpserver.RequestHandler;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;

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
		
		request=r;
		in=i;
		out=o;
		
		observer();
		
		
	}
	
	
	private void observer()
	{
		String[] arr=request.getPath().split("/");
		
		List<String> list=new ArrayList<>();
		for(int i=1;i<arr.length;i++){
			list.add(URLDecoder.decode(arr[i]));
		}
		
		int handled=rootNode.handle(list,request,in,out);
		if(handled==PathNode.OK){
			try{
				out.flush();
			}catch(IOException e){
				Log.err(e.toString());
			}
		}
		if(handled==PathNode.NOT_FOUND){
			notFound();
			try{
				out.flush();
			}catch(IOException e){
				Log.err(e.toString());
			}
		}
		else if(handled==PathNode.ERROR){
			internalError();
			try{
				//Do I need this when an error happens in the handler?
				//Maybe better to even not flush it. Because probably socket maybe closed already
				out.flush();
			}catch(IOException e){
				Log.info(e.toString());
			}
		}
		
	}
	
	private void notFound(){
		HttpResponde p=new HttpResponde();
		p.setStatus(p.NOTF);
		try{
		out.write(p.getSourceBytes());
		}catch(IOException e){
			Log.err(e);
		}
	}
	
	private void internalError(){
		HttpResponde p=new HttpResponde();
		p.setStatus(p.NOTF);
		try{
			out.write(p.getSourceBytes());
		}catch(IOException e){
			Log.info(e.toString());
		}
	}
	
	
}
