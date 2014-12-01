package com.sotapit.download;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import android.os.AsyncTask;
import android.util.Log;

import com.sotapit.app.SotapitApplication;
import com.sotapit.http_request.ResponseListener;
import com.sotapit.store.Store;

public class DownloadTask extends AsyncTask<String, Integer, String> {
			
			private UpdateListener updateListener;
			private String mUrl;
			private String path;
			public DownloadInfo downloadInfo;
			public Boolean isRunning=false;	
			public DownloadTask(DownloadInfo downloadInfo){
				this.mUrl=mUrl;
				this.downloadInfo=downloadInfo;
				this.path=downloadInfo.filePath;
				this.mUrl=downloadInfo.url;
				Log.v("msg", "task:"+downloadInfo.toString());
			}
			
			public void setUpdateListener(UpdateListener updateListener){
				
				this.updateListener=updateListener;
			}
			@Override
			protected String doInBackground(String... params) {
				// TODO Auto-generated method stub
				isRunning=true;
				for(String param:params){
					Log.v("msg",param+" doInBackground");
				}
				HttpURLConnection conn;
				try {
				
					File download_file = new File(path);
					if(download_file.exists()){					
						if(download_file.length()>downloadInfo.fileSize){
							downloadInfo.setProgress(100);
							return "sucess";
						}
					}
					conn = (HttpURLConnection) new URL(mUrl).openConnection();
					if(download_file.exists())
						conn.setRequestProperty("Range", "bytes="+download_file.length()+ "-");
					if(!download_file.getParentFile().exists())
						download_file.getParentFile().mkdirs();
					download_file.createNewFile();
					conn.setConnectTimeout(5000);
					Log.v("msg",download_file.length()+"download_file.length()");
					RandomAccessFile fos= new RandomAccessFile(download_file, "rwd");
					fos.seek(download_file.length());
					InputStream is = conn.getInputStream();
					downloadInfo.fileSize= conn.getContentLength(); 
					if(updateListener!=null)
			    		  updateListener.onStart();
					byte[] buffer = new byte[4096];
					int length = -1;
					while ((length = is.read(buffer)) != -1) {
						fos.write(buffer, 0, length);
						downloadInfo.downloadFileSize += length;
						downloadInfo.setProgress(downloadInfo.downloadFileSize*100/downloadInfo.fileSize);
						if(length!=0)
							onProgressUpdate(downloadInfo.getProgress());
					}
					if (conn != null) {
						conn.disconnect();
					}
					fos.close();
					is.close();
				} catch (MalformedURLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();

					return "error"+e.toString();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					
					if(e instanceof FileNotFoundException){
						if(updateListener!=null)
				    		  updateListener.onError("链接已失效");
						
					}
					
					e.printStackTrace();
					return "error"+e.toString();
				}
				return "success";
			}  
		
		      protected void onProgressUpdate(Integer... progresses) { 
		    	  Log.v("msg",progresses[0]+" tast:"+downloadInfo.fileName);
		    	  if(updateListener!=null)
		    		  updateListener.onProgressus(progresses[0]);
		      }  
		          
		        @Override  
		        protected void onPostExecute(String result) {  
		        	 Log.v("msg",result+"result");
		        	if(downloadInfo.getProgress()==100){
			        	 if(updateListener!=null)
				    		  updateListener.onCompelete();
		        	 }
		        } 
		        @Override  
		        protected void onCancelled() {
		        	super.onCancelled();
		        	isRunning=false;
		        	Log.v("msg","onCancelled");
		        }
		        
}