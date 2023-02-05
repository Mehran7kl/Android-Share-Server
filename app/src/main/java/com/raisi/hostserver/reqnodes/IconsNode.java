package com.raisi.hostserver.reqnodes;
import com.raisi.httpserver.HttpRequest;
import java.io.OutputStream;
import java.io.InputStream;
import java.util.List;
import com.raisi.hostserver.MainActivity;
import android.content.res.AssetFileDescriptor;
import java.net.URLDecoder;
import java.util.Locale;

public class IconsNode extends PathNode
{

	@Override
	public int handle(List<String> path, HttpRequest req, InputStream in, OutputStream out)
	{
		if(path.size()>0){
		Locale.setDefault(Locale.US);
		String id= path.get(0);
		
		
		
		addChild(id,new IconNode(Integer.valueOf(id)));
		
		}else return NOT_FOUND;
		return super.handle(path, req, in, out);
	}

	@Override
	public int handle(HttpRequest req, InputStream in, OutputStream out)
	{
		// TODO: Implement this method
		return NOT_FOUND;
	}

	
	
	
}
