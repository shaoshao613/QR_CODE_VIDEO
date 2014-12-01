package com.sotapit.http_request;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIUtils;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import android.content.Context;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.Log;

public class HttpRequestTask  extends AsyncTask<String, Integer, String> {

	private Context mContext ;
	private ResponseListener mResponseListener;
	private String url;
	public HttpRequestTask(Context context,String url,ResponseListener responseListener){
		   mContext = context;
		   mResponseListener=responseListener;
		   this.url=url;
	}
	
	
	
	
	@Override
	protected String doInBackground(String... params) {
		// TODO Auto-generated method stub
		Integer retCode = null;  
		String content=null; 
	      
	    try  
	    {  
	        DefaultHttpClient client = new DefaultHttpClient();// 自己去初始化，通常会自己写一个DefaultHttpClient  
	          
//	        // 为Post设置一些头信息  
//	        HttpPost post = new HttpPost(url);                  
//	        post.setHeader("Content-type", "application/json");  
//	        post.setHeader("Accept", "application/json");  
//	        Log.v("msg", url);
//	        // 利用JSON，构造需要传入的参数  
//	        JSONObject param_data = new JSONObject();  
////	        param_data.put("param1", params[0]);  
////	        param_data.put("param2", params[1]);  
////	        param_data.put("param3", params[2]);  
//	        post.setEntity(new StringEntity(param_data.toString()));  
//	          
//	        HttpResponse response = client.execute(post);  
//	        
//	        
//	        List<NameValuePair> list = new ArrayList<NameValuePair>();  
//	        list.add(new BasicNameValuePair("param1", params[0]));  
//	        list.add(new BasicNameValuePair("param2", params[1]));  
//	        list.add(new BasicNameValuePair("param3", params[2]));  
	          
//	        URI uriget = URIUtils.createURI("http", url, -1, "", URLEncodedUtils.format(list, "UTF-8"), null);  
	        if(!TextUtils.isEmpty(params[0])){
	        	url=params[0];
	        }
	        Log.v("msg",URI.create(url)+"URI.create(url)");
	        HttpGet get = new HttpGet(URI.create(url));  
	        HttpResponse response = client.execute(get);  
	        
	        
	        retCode = response.getStatusLine().getStatusCode();  
	        content = EntityUtils.toString(response.getEntity(),"utf-8");  

	        Log.v("msg", retCode+"");
	        Log.v("msg", content+"content");
	    } finally{
	    	 return content;
	    	
	    }
	      
	}

	//此方法在主线程执行,当任务执行之前开始调用此方法，可以在这里显示进度对话框
	@Override
	protected void onPreExecute() {
	 super.onPreExecute();
	
	}
  //此方法在主线程执行，用于显示任务执行的进度
	@Override
	protected void onProgressUpdate(Integer... values) {
	 super.onProgressUpdate(values);
	 
	}  


	//此方法在主线程执行，任务执行的结果作为此方法的参数返回。
	@Override
	protected void onPostExecute(String result) {
	
		 mResponseListener.onGetData(result);
	}

}
