package com.sotapit.videoplay;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.sotapit.download.DownloadInfo;

public class VideoInfo implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1372188205819948539L;
	public String title;
	public List<String> path;
	private int lastPlay=0;
	public String vid;
	public String logo;
	public String seconds;
	public List<DownloadInfo> ListDownloadVideo=new ArrayList<DownloadInfo>();
	
	
	
	public String lastPlay(){
		if(ListDownloadVideo.size()==0)
			return null;
		return ListDownloadVideo.get(lastPlay).id;
		
	}
}
