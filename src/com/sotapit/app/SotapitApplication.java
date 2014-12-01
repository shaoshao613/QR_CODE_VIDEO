package com.sotapit.app;

import java.io.File;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Application;
import android.content.Context;
import android.os.Environment;
import android.util.Log;

import com.nostra13.universalimageloader.cache.memory.impl.UsingFreqLimitedMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.utils.StorageUtils;
import com.sotapit.download.DownloadInfo;
import com.sotapit.download.DownloadTask;
import com.sotapit.store.Store;
import com.sotapit.videoplay.VideoInfo;

public class SotapitApplication extends Application{
	public static SotapitApplication appContext;
	public Map<String,DownloadTask> downloadTaskMap;
	public Map<String,DownloadInfo> downloadInfoMap;
	public Map<String,VideoInfo> videoInfoMap;
	public List<String> downloadIdList;
	public List<String> videoList;
	public static String SDCARD_VIDEO_PATH = Environment.getExternalStorageDirectory() + "/sotapit/";
	private static final String HEXES = "0123456789abcdef";
	
	//imageloader
	public static final String IMAGE_ENGIN_PATH = "sotapit/cache";
	public static int IMAGE_ENGINE_CORETHREAD = 5;
	public static int IMAGE_ENGINE_COMPRESS_QUALITY = 100; 
															// Can slow
															// ImageLoader, use
															// it carefully
															// (Better don't use
															// it)
	public static int IMAGE_ENGINE_FREQ_LIMITED_MEMECACHE = 2 * 1024 * 1024; 
	public static int IMAGE_ENGINE_SCALE_FOR_SDCARD = 500; 
	public static int IMAGE_ENGINE_SCALE_FOR_MEMECACHE = 500;
	  @Override 
      public void onCreate() { 
		  appContext = this;
		  downloadTaskMap=new HashMap<String,DownloadTask>();
		  downloadInfoMap=new HashMap<String,DownloadInfo>();
		  videoInfoMap=new HashMap<String,VideoInfo>();
		  downloadIdList = (List<String>) Store.getObject(getApplicationContext(),Store.ID_LIST);
		  if( downloadIdList==null)
			  downloadIdList=new ArrayList<String>();
		  videoList = (List<String>) Store.getObject(getApplicationContext(),Store.VIDEO_LIST);
		  if( videoList==null)
			  videoList=new ArrayList<String>();
		  Log.v("msg", "videoSize"+videoList.size());
		  boolean sdcardExist = Environment.MEDIA_MOUNTED.equals(Environment
					.getExternalStorageState());
		  if (sdcardExist) {
			File p = new File(SDCARD_VIDEO_PATH);
			if(!p.exists()){
				p.mkdirs();
			}
		  }
		  initImageLoader(getApplicationContext());
		  initDownloadList(getApplicationContext());
		  initVideoList(getApplicationContext());
		  
	  }
	  
	 private void initVideoList(Context mContext) {
		// TODO Auto-generated method stub
		 for(String id:appContext.videoList){
			 VideoInfo videoInfo = (VideoInfo) Store.getObject(mContext,id);
			 if(videoInfo!=null){
				 appContext.videoInfoMap.put(videoInfo.vid, videoInfo);
			 } 
		 }	
	}

	public static void addDownloadInfo(Context mContext,DownloadInfo downloadInfo){
		 if(!appContext.downloadIdList.contains(downloadInfo.id)){
			 appContext.downloadIdList.add(downloadInfo.id);			 
		 }else{
			 DownloadInfo downloadInfoStore = (DownloadInfo) Store.getObject(mContext,downloadInfo.id);
			 if(downloadInfoStore!=null){
				 Log.v("msg","store:"+downloadInfoStore.toString());
				 downloadInfoStore.update(downloadInfo);	
				 downloadInfo=downloadInfoStore;
			 } 
		 }
		 appContext.downloadInfoMap.put(downloadInfo.id, downloadInfo);
		 if(downloadInfo.getProgress()!=100&&!appContext.downloadTaskMap.containsKey(downloadInfo.id)){
			 DownloadTask downloadTask=new DownloadTask(downloadInfo);
				appContext.downloadTaskMap.put(downloadTask.downloadInfo.id, downloadTask); 				 
		 }
		 
	 }
	 public static void initDownloadList(Context mContext){		 
		 for(String id:appContext.downloadIdList){
			 DownloadInfo downloadInfo = (DownloadInfo) Store.getObject(mContext,id);
			 if(downloadInfo!=null){
				 appContext.downloadInfoMap.put(downloadInfo.id, downloadInfo);
				 if(downloadInfo.getProgress()!=100){
					 DownloadTask downloadTask=new DownloadTask(downloadInfo);
						appContext.downloadTaskMap.put(downloadTask.downloadInfo.id, downloadTask); 				 
				 }
			 } 
		 }	 
	 }

	 
	 public static boolean isRunningTask(String id){
		 if(appContext.downloadTaskMap.containsKey(id)){
			 
			 
			 return  appContext.downloadTaskMap.get(id).isRunning;
		 }
		 return false;
	 }
	  
	 public static void removeDownloadInfo(Context mContext,String key){  	
		 appContext.downloadIdList.remove(key);
		 appContext.downloadInfoMap.remove(key);
		 appContext.downloadTaskMap.remove(key); 
	 }
	 
	 public static void removeDownloadTask(Context mContext,String key){  	
		 appContext.downloadTaskMap.remove(key); 
	 }
	  
	public static void initImageLoader(Context context) {
			File cacheDir = StorageUtils.getOwnCacheDirectory(context,
					IMAGE_ENGIN_PATH);
			ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
					context)
					.threadPoolSize(IMAGE_ENGINE_CORETHREAD)
					.threadPriority(Thread.NORM_PRIORITY - 1)
					.denyCacheImageMultipleSizesInMemory()
					.memoryCache(
							new UsingFreqLimitedMemoryCache(
									IMAGE_ENGINE_FREQ_LIMITED_MEMECACHE))
					.defaultDisplayImageOptions(DisplayImageOptions.createSimple())
					.build();
			// Initialize ImageLoader with configuration.
			ImageLoader.getInstance().init(config);
		}
	  
	  public static String md5(String data) {
			MessageDigest engine = null;
			try {
				engine = MessageDigest.getInstance("MD5");
			} catch (NoSuchAlgorithmException e) {
				throw new RuntimeException(e);
			}
			byte[] digest = engine.digest(data.getBytes());
			StringBuffer hexBuilder = new StringBuffer(digest.length * 2);
			for (final byte b : digest)
				hexBuilder.append(HEXES.charAt((b & 0xf0) >> 4)).append(
						HEXES.charAt((b & 0x0f)));
			return hexBuilder.toString();
		}
}
