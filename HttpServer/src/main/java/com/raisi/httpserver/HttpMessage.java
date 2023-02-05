package com.raisi.httpserver;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;
import java.nio.file.FileSystem;

public abstract class HttpMessage
{
	public static final String
	OK="200 OK", FORB="403 Forbidden",NOTF="404 Not Found",
	PARTIAL="206 Partial Content",
	
	GIFm="image/gif",
	PLAINm="text/plain",
	HTMLm="text/html",
	CSSm="text/css",
	JSm="text/javascript",
	JPEGm="image/jpeg",
	PNGm="image/png",
	SVGm="image/svg+xml",
	MP4m="video/mp4",
	BINm="application/octet-stream",
	MULTIPART_BYTERANGEm="multipart/byteranges",
	CONNECTION="Connection",
	KEEP_ALIVE="Keep-Alive",
	CONTENT_TYPE="Content-Type",
	CONTENT_RANGE="Content-Range",
	LAST_MODIFIED="Last-Modified",
	CONTENT_LENGTH="Content-Length",
	RANGE="Range",
	ACCEPT_RANGE="Accept-Range",
	DATE="Date",
	SEC_FETCH_MODE="Sec-Fetch-Mode",
	
	SEC_FETCH_DEST="Sec-Fetch-Dest",
	VIDEO_DEST="video",
	DOC_DEST="document",
	EMPTY_DEST="empty",
	IMG_DEST="image";
	
	protected Map<String,String> headers=new HashMap<>();
	
	protected String version="HTTP/1.1";
	
	
	
	public void setHeader(String key,String value){
		headers.put(key,value);
	}
	
	public String getHeader(String key){
		return headers.get(key);
	}
	
	public String getVersion(){
		
		return version;
	}
	
	public abstract String getSource();
	
	
	public byte[] getSourceBytes(){
		try{
			
		return getSource().getBytes("utf8");
		}catch(UnsupportedEncodingException e){}
		return null;
	}
	
	public static String detectMime(File f){
		
		String path=f.getAbsolutePath();
		int lastdot=path.lastIndexOf(".");
		if(lastdot==-1)return HttpResponde.BINm;
		String ext=path.substring(lastdot+1,path.length());
		
		String mime;
		switch(ext){
			case "html":
				mime=HttpResponde.HTMLm;
				break;
			case "css":
				mime=HttpResponde.CSSm;
				break;
			case "js":
				mime=HttpResponde.JSm;
				break;
			case "png":
				mime=HttpResponde.PNGm;
				break;
			case "jpg":
				mime=HttpResponde.JPEGm;
				break;
			case "jpeg":
				mime=HttpResponde.JPEGm;
				break;
			case "svg":
				mime=HttpResponde.SVGm;
				break;
			case "mp4":
				mime=HttpResponde.MP4m;
				break;
			case "xml":
				mime=HttpResponde.PLAINm;
				break;
			case "txt":
				mime=HttpResponde.PLAINm;
				break;
			
			default:
				mime=HttpResponde.BINm;
		}
		
		return mime;
	}
}
