package com.raisi.hostserver;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.raisi.hostserver.reqnodes.NodeTreeFactory;
import com.raisi.httpserver.RequestHandler;
import com.raisi.httpserver.Server;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintStream;
import android.widget.LinearLayout;
import java.net.SocketException;
import java.net.InetAddress;
import java.util.List;
import android.widget.Toast;
import android.view.LayoutInflater;
import android.os.Process;
import android.content.Intent;
import com.raisi.httpserver.Log;
import android.net.Uri;



public class MainActivity extends Activity 
{
	Button searchButton;
	LinearLayout rootLayout;
	View mainlayout;
	Thread st;
	Intent serviceIntent;
	RequestHandler requestHandler;
	private static WebView webview;
	public static volatile MainActivity currentContext;
	EditText searchField;
	static File rootDir;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
		super.onCreate(savedInstanceState);
		initStatics();
		try
		{
			setDebuggerFile("debug/log.txt");


			setLoader();
			int port=6655;
			startServer(port);


			List<InetAddress> addrs=Server.getAddresses();
			StringBuilder sb=new StringBuilder();
			sb.append("port is: 6655\n");
			for (InetAddress a:addrs)
			{
				if (a.isLoopbackAddress())continue;
				sb.append("http://");
				sb.append(a.getHostAddress());
				sb.append(":");
				sb.append(port);
				sb.append("\n");
			}


			update(sb.toString());
		}
		catch (Throwable e)
		{
			showError(e);
		}
	}

	private void update(final String adrs)
	{
		final View  view = mainlayout;





		rootLayout = view.findViewById(R.id.root);

		searchField = view.findViewById(R.id.searchField);
		searchButton = view.findViewById(R.id.search);
		webview = view.findViewById(R.id.webview);

		//configuring webview

		webview.setWebViewClient(new WebViewClient());
		webview.setWebChromeClient(new WebChromeClient()); 
		webview.getSettings().setSupportZoom(true);
		webview.getSettings().setDisplayZoomControls(false);
		webview.getSettings().setBuiltInZoomControls(true);
		WebSettings webs=webview.getSettings();
		webs.setJavaScriptEnabled(true);
		
		view.setAlpha(0);
		setContentView(view);
		view.animate().alpha(1).setDuration(1000).start();

		webview.loadData(adrs.toString(), "text/plain", "utf8");
		

    }

	private void setDebuggerFile(String logfile)throws IOException
	{
		if (BuildConfig.DEBUG)
		{
			File f=new File(rootDir, logfile);
			f.mkdirs();
			f.delete();
			f.createNewFile();
			
			System.setOut(new PrintStream(f));
			System.setErr(System.out);

			Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler(){
					@Override
					public void uncaughtException(Thread th, Throwable err)
					{
						err.printStackTrace(System.out);
					}
				});
		}

		Log.addListener(new Log.Listener(){
				public void onLog(String s)
				{
					showError(s);
				}
			}, Log.ERROR_LEVEL);
	}




	private void initStatics()
	{
		currentContext = this;
		rootDir = Environment.getExternalStorageDirectory();

	}

	private void setLoader()
	{
		//setContentView(R.layout.loader);
		mainlayout = getLayoutInflater().inflate(R.layout.mainlayout, null);
	}


	private void startServer(int port)throws IOException
	{

		serviceIntent = new Intent(this, ServerService.class);
		serviceIntent.putExtra("port", port);
		startService(serviceIntent);
	}


	public void search(View v)
	{
		try
		{
			webview.loadUrl(searchField.getText().toString());
		}
		catch (Throwable e)
		{showError(e);}
	}


	@Override
	public void onBackPressed()
	{
		if (webview.canGoBack())webview.goBack();
		else super.onBackPressed();
	}
	boolean freezeDueEroor;
	public static synchronized void showError(final Throwable e)
	{
		currentContext.runOnUiThread(new Runnable(){
				public void run()
				{
					TextView  text=new TextView(currentContext);
					text.append(e.toString() + System.lineSeparator());
					for (StackTraceElement ele:e.getStackTrace())text.append(ele.toString() + System.lineSeparator());
					currentContext.setContentView(text);
					currentContext.freezeDueEroor = true;
				}
			});
	}
	public static synchronized void showError(final String e)
	{
	
		currentContext.runOnUiThread(new Runnable(){
				public void run()
				{
					TextView  text=currentContext.findViewById(R.id.logView);
					text.append(e);
					//currentContext.setContentView(text);
					//currentContext.freezeDueEroor = true;
				}
			});
		
	}
	@Override
	public void setContentView(View view)
	{
		//if (freezeDueEroor)return;
		super.setContentView(view);
	}

	@Override
	protected void onDestroy()
	{
		stopService(serviceIntent);
		super.onDestroy();
	}


}
