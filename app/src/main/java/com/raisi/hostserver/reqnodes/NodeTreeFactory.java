package com.raisi.hostserver.reqnodes;
import android.os.Environment;
import com.raisi.httpserver.HttpRequest;
import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.net.URLDecoder;

public class NodeTreeFactory
{
	public static PathNode create(){
		RootNode root=new RootNode();
		PathNode fileTree=new FileNode(){
			@Override
			public boolean handle(List<String> path,HttpRequest req,InputStream in ,OutputStream out){
				
				File f=Environment.getExternalStorageDirectory();
				PathNode node=this;
				for(String i:path){
					i=URLDecoder.decode(i);
					f=new File(f,i);
					if(!f.exists())return false;
					PathNode newNode=new FileNode();
					node.addChild(i,newNode);
					node=newNode;
				}
				
				return super.handle(path,req,in,out);
			}
		};
		root.addChild("files",fileTree);
		root.addChild("icons",new IconsNode());
		root.addChild("apps",new AppsNode());
	
		return root;
	}
}


