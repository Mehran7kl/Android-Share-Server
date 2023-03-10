package com.raisi.hostserver.reqnodes;
import com.raisi.httpserver.HttpRequest;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.net.URLDecoder;

public abstract class PathNode
{
	protected Map<String,PathNode> childs=new HashMap<>();
	
	public static int
		OK=0,
		NOT_FOUND=-1,
		ERROR=-2;
	
	public int handle(List<String> path, HttpRequest req, InputStream in, OutputStream out)
	{
		if(path.size()>0){
			
			String next=path.get(0);
			path.remove(0);
			PathNode nextNode = childs.get(next);
			
			if(nextNode==null)return NOT_FOUND;
			
			return nextNode.handle(path,req,in,out);
		}else{
			return handle(req,in,out);
		}
	}
	
	
	public abstract int handle(HttpRequest req, InputStream in, OutputStream out);
	
	
	public void addChild(String p,PathNode n){
		childs.put(p,n);
		
		
	}
	
	
	
	
	public void removeChild(String p){
		childs.remove(p);
	}
	
}
