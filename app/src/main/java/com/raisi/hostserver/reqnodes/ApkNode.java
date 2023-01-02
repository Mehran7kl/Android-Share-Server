package com.raisi.hostserver.reqnodes;
import java.io.InputStream;
import com.raisi.httpserver.HttpRequest;
import java.io.OutputStream;
import java.io.File;
import com.raisi.httpserver.HttpResponde;
import java.io.IOException;
import java.io.FileInputStream;
import com.raisi.httpserver.Server;
import java.nio.file.Files;

public class ApkNode extends PathNode
{
	File apk;
	ApkNode(String p){
		apk=new File(p);
		
	}
	@Override
	public boolean handle(HttpRequest req, InputStream in, OutputStream out)
	{
		try{
		HttpResponde res=new HttpResponde();
		
		res.setStatus(HttpResponde.OK);
		res.setHeader(HttpResponde.CONNECTION,HttpResponde.KEEP_ALIVE);
		int length=(int)apk.length();
		res.setHeader(res.CONTENT_TYPE,res.BINm);
		res.setHeader(res.CONTENT_LENGTH,""+length);
		out.write(res.getSourceBytes());
		Files.copy(apk.toPath(),out);
		
		return true;
		}catch(IOException e){
		return false;
		}
	}

	
}
