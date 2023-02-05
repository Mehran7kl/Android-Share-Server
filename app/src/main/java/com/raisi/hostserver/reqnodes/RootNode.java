package com.raisi.hostserver.reqnodes;

import com.raisi.hostserver.MainActivity;
import com.raisi.hostserver.R;
import com.raisi.httpserver.HttpRequest;
import com.raisi.httpserver.HttpResponde;
import java.io.InputStream;
import java.io.OutputStream;
import com.raisi.httpserver.Server;
import com.raisi.hostserver.Utils;
import com.raisi.httpserver.Log;
import java.io.IOException;

public class RootNode extends PathNode
{

	@Override
	public int handle(HttpRequest req, InputStream in, OutputStream out)
	{
		String htmlFormat=null;
		try{
			htmlFormat=Utils.readRawString(R.raw.dirlist);
		}catch(IOException e){
			Log.err(e);
			return ERROR;
		}
		String linkFormat="<a href='%s'>%s</a><br/>\n";

		StringBuilder links=new StringBuilder();
		
		links.append(String.format(linkFormat,"/files","Files"));
		links.append(String.format(linkFormat,"/apps","Apps"));
			
		
		String html=String.format(htmlFormat,links);
		byte[] buffer=html.getBytes();

		HttpResponde message=new HttpResponde();
		message.setStatus(HttpResponde.OK);
		message.setHeader(HttpResponde.CONNECTION,HttpResponde.KEEP_ALIVE);
		message.setHeader(HttpResponde.CONTENT_TYPE,HttpResponde.HTMLm);
		message.setHeader(HttpResponde.CONTENT_LENGTH,""+buffer.length);
		try{
			//I except no error here.
			out.write(message.getSourceBytes());
			out.write(buffer);
		
		}catch(IOException e){
			Log.err("returned error due to:\n"+e.toString());
			return ERROR;
		}
		return OK;
	}

	
}
