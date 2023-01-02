package com.raisi.httpserver;

import java.io.OutputStream;
import java.io.InputStream;
public interface RequestHandler 
{
	
	void handleRequest(HttpRequest request,InputStream in,OutputStream out);
	
	
}
