package com.sotapit.download;

import java.io.Serializable;

import android.util.Log;

import com.google.gson.Gson;
import com.sotapit.app.SotapitApplication;
import com.sotapit.http_request.ResponseListener;

public class DownloadInfo implements Serializable {
	public String id;
	public String vid;
	private int progress;
	public int playProgress=0;
	public int fileSize;
	public int downloadFileSize;
	public String fileName;
	public String url;
	public String filePath;
	public String nextVideoId;
	
	public DownloadInfo(String mUrl,String title,int fileOrder,String vid){
		fileName=title+"("+fileOrder+")";
		String path = SotapitApplication.SDCARD_VIDEO_PATH+title+"/"+fileOrder+".mp4";
		id=SotapitApplication.md5(path);				
		filePath=path;
		url=mUrl;
		this.vid=vid;
		
	}
	
	
	public int getProgress() {
		return progress;
	}
	public void update(DownloadInfo d){
		this.url=d.url;
	}
	public void setProgress(int progress) {
		this.progress = progress;
	}
	public String toString(){	
		return (new Gson()).toJson(this);
		
		
	}
}
