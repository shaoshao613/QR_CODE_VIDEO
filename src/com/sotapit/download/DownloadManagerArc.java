package com.sotapit.download;

import java.util.ArrayList;
import java.util.Iterator;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;

import com.sotapit.app.BaseActivity;
import com.sotapit.app.R;
import com.sotapit.app.SotapitApplication;
import com.sotapit.store.Store;

public class DownloadManagerArc extends BaseActivity{
	public String tag="DownloadManagerArc";
	private ListView mListView;
	private Context mContext=this;
	private DownloadAdapter mAdapter;
	private ArrayList<DownloadInfo> mDownloadList;
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.download_list);
		mListView = (ListView) findViewById(R.id.listView_download);
		mDownloadList=new ArrayList<DownloadInfo>();
		Iterator<String> it=SotapitApplication.appContext.downloadTaskMap.keySet().iterator();		
		String key;
		while(it.hasNext()){
			key=it.next();
			DownloadTask task = SotapitApplication.appContext.downloadTaskMap.get(key);
			if(task.downloadInfo.getProgress()!=100)
				mDownloadList.add(task.downloadInfo);
		}
		
		if(mDownloadList.size()>0){
			mAdapter = new DownloadAdapter(mContext,mDownloadList);
			mListView.setAdapter(mAdapter);
			Log.v(tag, mDownloadList.size()+"size");
		}
		
    }
	protected void onResume() {
		super.onResume();
		Log.v(tag, mDownloadList.size()+"size");
	}
	
	protected void onDestroy() {
		ArrayList<String> downloadIdList = new ArrayList<String>();
		for(DownloadInfo di:mDownloadList){
			downloadIdList.add(di.id);
		}
		Store.saveObject(mContext,Store.ID_LIST,downloadIdList);
		
		
		
		Iterator it=SotapitApplication.appContext.downloadInfoMap.keySet().iterator();
		Object key;
		while(it.hasNext()){
			key=it.next();
			DownloadInfo info = SotapitApplication.appContext.downloadInfoMap.get(key);
			Store.saveObject(mContext,info.id,info);
		}
		super.onDestroy();
		
	}

}
