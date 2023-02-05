package com.raisi.hostserver.reqnodes;
import com.raisi.httpserver.HttpRequest;
import java.io.OutputStream;
import java.io.InputStream;
import com.raisi.httpserver.HttpMessage;
import com.raisi.hostserver.R;
import com.raisi.httpserver.HttpResponde;
import android.content.res.AssetFileDescriptor;
import com.raisi.hostserver.MainActivity;
import java.io.IOException;
import com.raisi.httpserver.Server;
import com.raisi.httpserver.Log;

public class IconNode extends PathNode
{
	int id;
	IconNode(int i){
		id=i;
	}

	@Override
	public int handle(HttpRequest req, InputStream in, OutputStream out)
	{
		
		String type;
			switch(id){
				case R.raw.folder_icon:
				case R.raw.img_icon:
					type=HttpMessage.JPEGm;
				break;
				default:
				type=HttpMessage.PNGm;
			}
		
		HttpResponde responde=new HttpResponde();
		
		responde.setStatus(HttpMessage.OK);
		responde.setHeader(HttpMessage.CONTENT_TYPE,type);
		
		AssetFileDescriptor fd= MainActivity.currentContext.getResources().openRawResourceFd(id);
		int length =(int) fd.getLength();
		responde.setHeader(HttpMessage.CONTENT_LENGTH,""+length);
		try{
		out.write(responde.getSourceBytes());
		FileNode.readChunkWriteBuffer(0,length,fd.createInputStream(),out);
		return OK;
		}catch(IOException e){
			
			Log.err(e);
			return ERROR;
		}
		
	}

	
}
