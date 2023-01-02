package com.raisi.hostserver.reqnodes;
import android.os.Environment;
import com.raisi.hostserver.MainActivity;
import com.raisi.hostserver.R;
import com.raisi.httpserver.HttpMessage;
import com.raisi.httpserver.HttpRequest;
import com.raisi.httpserver.HttpResponde;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLDecoder;
import java.util.Locale;
import java.nio.file.Files;
import java.nio.file.Paths;
import com.raisi.hostserver.Utils;
import com.raisi.httpserver.Log;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FileNode extends PathNode
{
	
	
	HttpRequest req;
	@Override
	public boolean handle(HttpRequest req, InputStream in, OutputStream out)
	{
		try{
		this.req=req;
		File f=getFile(req);
		if(f.exists())
		if(f.isDirectory()){
			returnDirectory(f,req,out);
			return true;
		}
		else{
			
			return returnFile(f,out);
		}
		}catch(Throwable e){
			Log.err(e);
		}
		return false;
	}
	
	private void returnDirectory(File f,HttpRequest req,OutputStream out)throws IOException{
		
		String htmlFormat=Utils.readRawString(R.raw.dirlist);
		String linkFormat="<a href='%s/%s'>%s</a><img src='/icons/%d'/><br/>\n";
		
		StringBuilder links=new StringBuilder();
		String[] files=f.list();
		
		
		for(String file:files){
			
			links.append(String.format(Locale.US,linkFormat,req.getPath(),file,file,getIconFor(new File(f,file))));
			
		}
		String html=String.format(htmlFormat,links);
		byte[] buffer=html.getBytes();
		
		HttpResponde message=new HttpResponde();
		message.setStatus(HttpResponde.OK);
		message.setHeader(HttpResponde.CONNECTION,HttpResponde.KEEP_ALIVE);
		message.setHeader(HttpResponde.CONTENT_TYPE,HttpResponde.HTMLm);
		message.setHeader(HttpResponde.CONTENT_LENGTH,""+buffer.length);
		
		out.write(message.getSourceBytes());
		out.write(buffer);
		
	}
	
	private boolean returnFile(File f,OutputStream out)throws Exception{
		
		HttpResponde message=new HttpResponde();
		message.setStatus(HttpResponde.OK);
		message.setHeader(HttpResponde.CONNECTION,HttpResponde.KEEP_ALIVE);
		message.setHeader(HttpResponde.CONTENT_TYPE,HttpMessage.detectMime(f));
		if(req.getHeader(message.SEC_FETCH_DEST)==req.DOC_DEST)
		{
			out.write(message.getSourceBytes());
			return true;
		}
		
		long longlength=f.length();
		if(longlength>Integer.MAX_VALUE)return false;
		
		int length=(int)longlength;
		
		message.setHeader(HttpResponde.CONTENT_LENGTH,""+length);
		
		
		out.write(message.getSourceBytes());
		Files.copy(f.toPath(),out);
		
		return true;
	}
	
	protected File getFile(HttpRequest req){
		String path=URLDecoder.decode(req.getPath().substring(6));
		File f=new File(Environment.getExternalStorageDirectory(),path);
		return f;
	}
	
	public static void readFileWriteBuffer(InputStream in,OutputStream out,int length)throws IOException{
		int fh=100;
		byte[] b=new byte[fh];
		while(length!=0){
			if(length<fh)fh=length;
			in.read(b,0,fh);
			out.write(b,0,fh);
			length-=fh;
		}
	}
	
	public static int getIconFor(File f){
		
		if(f.isDirectory()) return R.raw.folder_icon;
		String path=f.getAbsolutePath();
		int lastdot=path.lastIndexOf(".");
		int id;
		
		String ext=path.substring(lastdot+1,path.length());
		
		
		
		switch(ext){
			case "html":
				id=R.raw.html_icon;
				break;
			case "css":
			case "js":
			case "xml":
			case "txt":
				id=R.raw.txt_icon;
				break;
			case "png":
			case "jpg":
			case "jpeg":
			case "svg":
				id=R.raw.img_icon;
				break;
			case "apk":
				id=R.raw.apk_icon;
				break;
			case "mp4":
				id=R.raw.video_icon;
				break;
				
			default:
			id=R.raw.bin_icon;
		}

		return id;
	}
	
	private int[] getRange()
	{
		int offset=-1;
		int end=-1;
		String rangeVal=req.getHeader("Range");
		if(rangeVal!=null){
			Matcher matcher= Pattern.compile("bytes=(\\d+)-(\\d*)").matcher(rangeVal);
			if(matcher.groupCount()>1)
				offset=Integer.valueOf(matcher.group(1));
			if(matcher.groupCount()>2)
				offset=Integer.valueOf(matcher.group(2));
			
			
		}
		
		return new int[]{offset,end};
	}
}
