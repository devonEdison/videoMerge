package com.dragonplayer.merge.utils;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.FileEntity;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.AsyncTask;
import android.util.Log;

public class WeTouch_uploadImage extends AsyncTask<String, Void, String>{
	private static final String TAG = "WeTouch_uploadImage";
	WeTouch_network_interface listener = null;
	/** progress dialog to show user that the backup is processing. */
	private Context mContext;
	String email="",username="",tel="";
	
	public WeTouch_uploadImage(Context context, WeTouch_network_interface listener) {
		this.mContext = context;
		this.listener = listener;
		
	    SharedPreferences settings = mContext.getSharedPreferences("iDragon",Context.MODE_PRIVATE);
	    username = settings.getString("username", "");	 
	    email= settings.getString("email", "");	
	    tel= settings.getString("tel", "");	
	}
	
	//pass url first then file location
	@Override
	protected String doInBackground(String... params) {
		
		String upLoadServerUri = params[0];
        String fileName = params[1];

        HttpURLConnection conn = null;
        DataOutputStream dos = null;  
        String lineEnd = "\r\n";
        String twoHyphens = "--";
        String boundary = "*****";
        int bytesRead, bytesAvailable, bufferSize;
        byte[] buffer;
        int maxBufferSize = 1 * 1024 * 1024; 
        File sourceFile = new File(fileName); 
        if (!sourceFile.isFile()) {
         Log.e("uploadFile", "Source File Does not exist");
         return null;
        }
            try { // open a URL connection to the Servlet
             FileInputStream fileInputStream = new FileInputStream(sourceFile);
             URL url = new URL(upLoadServerUri);
             conn = (HttpURLConnection) url.openConnection(); // Open a HTTP  connection to  the URL
             conn.setDoInput(true); // Allow Inputs
             conn.setDoOutput(true); // Allow Outputs
             conn.setUseCaches(false); // Don't use a Cached Copy
             conn.setRequestMethod("POST");
             conn.setRequestProperty("Connection", "Keep-Alive");
             conn.setRequestProperty("ENCTYPE", "multipart/form-data");
             conn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
             conn.setRequestProperty("uploadfile", fileName); 
             dos = new DataOutputStream(conn.getOutputStream());
   
             dos.writeBytes(twoHyphens + boundary + lineEnd); 
             dos.writeBytes("Content-Disposition: form-data; name=\"uploadfile\";filename=\""+ email+fileName.substring(fileName.length()-4, fileName.length()) + "\"" + lineEnd);
             dos.writeBytes(lineEnd);
   
             bytesAvailable = fileInputStream.available(); // create a buffer of  maximum size
   
             bufferSize = Math.min(bytesAvailable, maxBufferSize);
             buffer = new byte[bufferSize];
   
             // read file and write it into form...
             bytesRead = fileInputStream.read(buffer, 0, bufferSize);  
               
             while (bytesRead > 0) {
            	 Log.e("bytesRead", "bytesRead="+bytesRead);
            	 dos.write(buffer, 0, bufferSize);
            	 bytesAvailable = fileInputStream.available();
            	 bufferSize = Math.min(bytesAvailable, maxBufferSize);
            	 bytesRead = fileInputStream.read(buffer, 0, bufferSize);               
              }
   
             // send multipart form data necesssary after file data...
             dos.writeBytes(lineEnd);
             dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);
   
             // Responses from the server (code and message)
             String serverResponseMessage = conn.getResponseMessage();
             Log.e("serverResponseMessage", conn.getResponseCode() + ":" + serverResponseMessage);
             
             //close the streams //
             fileInputStream.close();
             dos.flush();
             dos.close();

             return serverResponseMessage;
              
        } catch (MalformedURLException ex) {  
            ex.printStackTrace();
            Log.e("Upload file to server", "error: " + ex.getMessage(), ex);  
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("Upload file to server Exception", "Exception : " + e.getMessage(), e);  
        }
            return null;
	} 	
	
	@Override
	protected void onPostExecute(String result) {
		super.onPostExecute(result);
		if (result != null){
			listener.onImageUploadComplete(result);
		}
	}
	
}
