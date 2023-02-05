package com.raisi.hostserver;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.webkit.DownloadListener;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.raisi.httpserver.Log;
import com.raisi.httpserver.RequestHandler;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Locale;



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
		initStaticVars();
		try
		{
			
			Locale.setDefault(Locale.US);
			setDebuggerFile("debug/log.txt");
			setLoader();
			int port=6655;
			startServer(port);
		}
		catch (Throwable e)
		{
			Log.err(e);
		}
	}
	public void initView()
	{
		mainlayout=getLayoutInflater().inflate(R.layout.mainlayout,null);

		final View  view = mainlayout;





		rootLayout = view.findViewById(R.id.root);

		searchField = view.findViewById(R.id.searchField);
		searchButton = view.findViewById(R.id.search);
		webview = view.findViewById(R.id.webview);
		
		
		//configuring webview

		webview.setWebViewClient(new WebViewClient());
		webview.setWebChromeClient(new WebChromeClient()); 
		webview.setDownloadListener(new DownloadListener(){
				@Override
				public void onDownloadStart(String uri, String userAgent,
											String contentDisposition,
											String mimeType, long length){
					Intent i = new Intent(Intent.ACTION_VIEW);

					i.setData(Uri.parse(uri));
					startActivity(i);
				}
			});
		webview.getSettings().setSupportZoom(true);
		webview.getSettings().setDisplayZoomControls(false);
		webview.getSettings().setBuiltInZoomControls(true);
		WebSettings webs=webview.getSettings();
		webs.setJavaScriptEnabled(true);

		view.setAlpha(0);
		setContentView(view);
		view.animate().alpha(1).setDuration(1000).start();
		
	}
	
	public void updateWebView(final String adrs)
	{
		webview.post(new Runnable(){
		public void run(){
			webview.loadData(adrs, "text/plain", "utf8");
		}});
		

    }
/*
Exception handling rules:

1. Don't let an exception raise to system otherwise App will crash.
2. Report unusual exceptoins by Log.err
3. Report usual IO exceptions by Log.info
4. Don't let control flow to continue in bad state by ignoring an exception
5. Use Log.debug to report output

*/
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
						System.out.println(err);
					}
				});
		}

		Log.addListener(new Log.Listener(){
				public void onLog(String s)
				{
					synchronized(System.out){
					try{
					showError(s);
					}finally{
					System.out.println(s);
					}
					}
				}
			}, Log.ERROR_LEVEL);
		Log.addListener(new Log.Listener(){
				public void onLog(String s)
				{
					synchronized(System.out){
					System.out.println(s);
					}
				}
			}, Log.INFO_LEVEL);
		Log.addListener(new Log.Listener(){
				public void onLog(String s)
				{
					synchronized(System.out){
					System.out.println(s);
					}
				}
			}, Log.DEBUG_LEVEL);
		
	}




	private void initStaticVars()
	{
		currentContext = this;
		rootDir = Environment.getExternalStorageDirectory();

	}

	private void setLoader()
	{
		setContentView(R.layout.loader);
	}


	private void startServer(int port)
	{

		serviceIntent = new Intent(this, ServerService.class);
		serviceIntent.putExtra("port", port);
		startService(serviceIntent);
	}
	
	//This is a listener
	public void search(View v)
	{
		//I dont let an exception raise here;
		//If so, app will crash
		try
		{
			webview.loadUrl(searchField.getText().toString());
		}
		catch (Throwable e)
		{	
			Log.err(e);
		}
	}


	@Override
	public void onBackPressed()
	{
		if (webview.canGoBack())
			webview.goBack();
		else super.onBackPressed();
	}
	
	public static synchronized void showError(final Throwable e)
	{
		currentContext.runOnUiThread(new Runnable(){
				public void run()
				{
					TextView  text=new TextView(currentContext);
					text.append(e.toString() + System.lineSeparator());
					for (StackTraceElement ele:e.getStackTrace())text.append(ele.toString() + System.lineSeparator());
					currentContext.setContentView(text);
				}
			});
	}
	public static synchronized void showError(final String e)
	{
		if(currentContext.mainlayout!=null)
			currentContext.runOnUiThread(new Runnable(){
				public void run()
				{
					TextView  text=currentContext.findViewById(R.id.logView);
					
					if(text.getMovementMethod()==null)
						text.setMovementMethod((new ScrollingMovementMethod()));
					
					text.append(e);
					
				}
			});
		else System.out.println(e);
	}
	

	@Override
	protected void onDestroy()
	{
		stopService(serviceIntent);
		super.onDestroy();
	}


}
