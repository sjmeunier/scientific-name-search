package com.smeunier.scientificnamesearch;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;

import android.util.Log;

public class WebAccess {
	public static String queryRESTurl(String url) {
		String result = null;
    	Log.println(Log.INFO, "a", url);
    	HttpClient httpclient = new DefaultHttpClient(new BasicHttpParams());
    	HttpPost httppost = new HttpPost(url);
    		
    	// Depends on your web service
    	//httppost.setHeader("Content-type", "application/json");
    	try{
        	InputStream inputStream = null;
        	
        	HttpResponse response = httpclient.execute(httppost);           
        	HttpEntity entity = response.getEntity();

        	inputStream = entity.getContent();
        	BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"), 8);
        	StringBuilder sb = new StringBuilder();

        	String line = null;
        	while ((line = reader.readLine()) != null)
        	{
        	    sb.append(line + "\n");
        	}
        	result = sb.toString();
		} catch (ClientProtocolException e) {
		    //TODO Handle problems..
			return "ERROR-" + e.getMessage();
		} catch (IOException e) {
		    //TODO Handle problems..
			return "ERROR-" + e.getMessage();
		} catch (Exception e) {
			return "ERROR-" + e.getMessage();
		}
		
		return result;
		
	}
}
