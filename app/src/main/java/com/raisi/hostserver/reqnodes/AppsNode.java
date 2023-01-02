package com.raisi.hostserver.reqnodes;
import java.io.InputStream;
import com.raisi.httpserver.HttpRequest;
import java.io.OutputStream;
import java.util.List;
import com.raisi.hostserver.MainActivity;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;
import com.raisi.httpserver.HttpResponde;
import java.io.IOException;
import java.util.Locale;
import java.io.File;
import com.raisi.hostserver.R;
import android.content.pm.PackageManager;
import com.raisi.httpserver.Server;
import com.raisi.hostserver.Utils;

public class AppsNode extends PathNode
{
	private List<String> apps=new ArrayList<>();
	
	AppsNode(){
		Intent intent=new Intent(Intent.ACTION_MAIN);
		intent.addCategory(Intent.CATEGORY_LAUNCHER);
		PackageManager pm=MainActivity.currentContext.getPackageManager();
		
		List<ResolveInfo> info=pm.queryIntentActivities(intent,0);
		
		
		for(ResolveInfo i:info){
			
			String name=i.activityInfo.loadLabel(pm).toString();
			String apkpath=i.activityInfo.applicationInfo.publicSourceDir;
			
			addChild(name+".apk",new ApkNode(apkpath));
			apps.add(name);
			
		}
		
	}
	

	@Override
	public boolean handle(HttpRequest req, InputStream in, OutputStream out)
	{
		try{
		String htmlFormat=Utils.readRawString(R.raw.dirlist);
		String linkFormat="<a href='/apps/%s.apk'>%s</a><br/>\n";
		
		StringBuilder links=new StringBuilder();
		
		for(String file:apps){
			
			links.append(String.format(linkFormat,file,file));
			

		}
		String html=String.format(htmlFormat,links);
		byte[] buffer=html.getBytes("utf8");

		HttpResponde message=new HttpResponde();
		message.setStatus(HttpResponde.OK);
		message.setHeader(HttpResponde.CONNECTION,HttpResponde.KEEP_ALIVE);
		message.setHeader(HttpResponde.CONTENT_TYPE,HttpResponde.HTMLm);
		message.setHeader(HttpResponde.CONTENT_LENGTH,""+buffer.length);
		
		out.write(message.getSourceBytes());
		
		out.write(buffer);
		return true;
		}catch(IOException e){
			return false;
		}
	}

	
}
