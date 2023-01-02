package com.raisi.httpserver;

import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.Set;
import org.apache.commons.codec.Charsets;
import java.util.Scanner;
import java.nio.charset.*;
import java.io.*;
import android.util.Log;
import java.util.logging.Logger;
import android.util.LogPrinter;
public abstract class HttpMessage
{
	public static final String
	OK="200 OK", FORB="403 Forbidden",NOTF="404 Not Found",
	
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
	
	CONNECTION="Connection",
	KEEP_ALIVE="Keep-Alive",
	CONTENT_TYPE="Content-Type",
	CONTENT_LENGTH="Content-Length",
	DATE="Date",
	SEC_FETCH_MODE="Sec-Fetch-Mode",
	SEC_FETCH_DEST="Sec-Fetch-Dest",
	VIDEO_DEST="video",
	DOC_DEST="document",
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
