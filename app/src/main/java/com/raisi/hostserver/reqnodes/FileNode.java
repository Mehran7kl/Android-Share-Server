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
import java.nio.file.Path;
import java.util.Date;
import java.time.Instant;
import android.os.AsyncTask;

public class FileNode extends PathNode
{
	
	
	HttpRequest req;
	@Override
	public int handle(HttpRequest req, InputStream in, OutputStream out)
	{
		
		this.req=req;
		File f=getFilePath(req);
		
		if(f.exists())
		if(f.isDirectory()){
			return returnDirectory(f,out);
			
		}
		else{
			
			return returnFile(f,out);
		}
		
		return ERROR;
	}
	
	protected int returnDirectory(File f,OutputStream out){
		String htmlFormat=null;
		try{
		htmlFormat=Utils.readRawString(R.raw.dirlist);
		}catch(IOException e){
			Log.err(e);
			return ERROR;
		}
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
		addTimeHeaders(message,f);
		try{
			out.write(message.getSourceBytes());
			out.write(buffer);
		}catch(IOException e){
			Log.err(e);
			return ERROR;
		}
		return OK;
	}
	
	protected int returnFile(File f,OutputStream out){
		
		if(!req.rangeParts.isEmpty()) return sendAsPartial(f,out);
			
		HttpResponde message=new HttpResponde();
		message.setStatus(HttpResponde.OK);
		int fileSize=(int)f.length();
		message.setHeader(HttpResponde.CONNECTION,HttpResponde.KEEP_ALIVE);
		message.setHeader(HttpResponde.CONTENT_TYPE,HttpMessage.detectMime(f));
		message.setHeader(HttpResponde.CONTENT_LENGTH,""+fileSize);
		message.setHeader(HttpResponde.ACCEPT_RANGE,"bytes");
		//When server say I'm agree with requesting ranges; 
		//Browser may send multiple range request in a multithreaded way
		//So be aware about problems and don't try to look in the log
		addTimeHeaders(message,f);
		Log.d(req.getSource());
		Log.d(message.getSource());
		
		//This works for Chrome v80 and later
		if(isForDocument())
		{
			try{
				out.write(message.getSourceBytes());
			}catch(IOException e){
				Log.err(e);
				return ERROR;
			}
			return OK;
		}
		
		try{
		out.write(message.getSourceBytes());
		}catch(IOException e){
			Log.err(e);
			return ERROR;
		}
		//Client may refuse to download file; so this line sometimes throws exceptions that is usual.
		try{
			Files.copy(f.toPath(),out);
		}catch(IOException e){
			reportErrorOnSendingBody(e);
		}
		return OK;
	}
	//When a request triggered by browser with this headers; 
	//It likely wont download the file with this reponde; it will try senfing a new request.
	//Thus when this returns true don't send file; just send the headers.
	private boolean isForDocument()
	{
		/*String fetchDest=req.getHeader(req.SEC_FETCH_DEST);
		
		return fetchDest!=null&&
			(
			fetchDest.equalsIgnoreCase(req.DOC_DEST)
			);*/
		return false;
	}
	public int sendAsPartial(File f,OutputStream out)
	{
		HttpResponde message=new HttpResponde();
		message.setStatus(HttpResponde.PARTIAL);
		message.setHeader(HttpResponde.CONNECTION,HttpResponde.KEEP_ALIVE);
		
		message.setHeader(HttpResponde.CONTENT_TYPE,HttpMessage.detectMime(f));
		message.setHeader(message.ACCEPT_RANGE,"bytes");
		addTimeHeaders(message,f);
		//Currently only single part response is supported
		int fileSize=(int)f.length();
		int offset=req.rangeParts.get(0)[0];
		int end=req.rangeParts.get(0)[1];
		int lengthToEnd=-1;
		if(offset==-1) lengthToEnd=end;
		if(end==-1) end=fileSize-1;
		int writeOffset= offset==-1? fileSize-lengthToEnd: offset;
		int writeLength= offset==-1? lengthToEnd: end-offset+1;
		message.setHeader(HttpResponde.CONTENT_LENGTH,""+writeLength);
		message.setHeader(message.CONTENT_RANGE,
						  String.format("bytes %d-%d/%d",writeOffset,writeOffset+writeLength-1,fileSize));
		Log.d(req.getSource());
		Log.d(message.getSource());
		
		if(isForDocument())
		{
			try{
				out.write(message.getSourceBytes());
			}catch(IOException e){
				Log.err(e);
				return ERROR;
			}
			return OK;
		}
		
		try{
			out.write(message.getSourceBytes());
		}catch(IOException e){
			Log.err(e);
			return ERROR;
		}
		try{
		//Client may refuse to download file; so this code sometimes throws exceptions that is usual.
		InputStream in= Files.newInputStream( f.toPath());
		readChunkWriteBuffer(writeOffset,writeLength,in,out);
		}catch(Exception e){
			reportErrorOnSendingBody(e);
		}
		return OK;
	}
	private void reportErrorOnSendingBody(Exception e)
	{
		Log.info("\n"+e.getMessage());
		Log.info("\nWhen sending body on requesting this:\n"+req.getSource());
	}
	protected File getFilePath(HttpRequest req)
	{
		String path=URLDecoder.decode(req.getPath().substring(6));
		File f=new File(Environment.getExternalStorageDirectory(),path);
		return f;
	}
	
	public static void readChunkWriteBuffer(int chunkOffset, int chunkLength, InputStream in,OutputStream out)throws IOException{
		int fh=1024*64;//64KB
		in.skip(chunkOffset);
		byte[] b=new byte[fh];
		while(chunkLength!=0){
			if(chunkLength<fh)fh=chunkLength;
			in.read(b,0,fh);
			out.write(b,0,fh);
			chunkLength -=fh;
		}
	}
	
	public static int getIconFor(File f){
		
		if(f.isDirectory()) return R.raw.folder_icon;
		String path=f.getAbsolutePath();
		int lastdot=path.lastIndexOf(".");
		if(lastdot==-1)return R.raw.bin_icon;
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
	
	private void addTimeHeaders(HttpResponde message, File f){
		message.setHeader(HttpResponde.DATE,Date.from( Instant.now()).toGMTString());
		try{
		message.setHeader(HttpResponde.LAST_MODIFIED,Date.from(Files.getLastModifiedTime(f.toPath()).toInstant()).toGMTString());
		}catch(IOException e){
			Log.err(e);
		}
	}
}
