package com.sotapit.download;

import java.util.List;

import android.content.Context;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.sotapit.app.R;
import com.sotapit.app.SotapitApplication;
import com.sotapit.videoplay.VideoPlayerActivity;

public class DownloadAdapter extends BaseAdapter{
	private LayoutInflater mInflater;
	private Context mContext;
	private DownloadAdapter mAdapter=this;
	public List<DownloadInfo> mDownloadInfoList;
	public DownloadAdapter(Context context,List<DownloadInfo> downloadInfoList) {
		// TODO Auto-generated constructor stub
		this.mContext = context;
		mInflater = LayoutInflater.from(mContext);
		this.mDownloadInfoList=downloadInfoList;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return mDownloadInfoList.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return  mDownloadInfoList.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}
	
	
	Handler mmHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			
			switch (msg.what) {
			case 1:
				ProgressBar progressBar2=(ProgressBar)msg.obj;
				progressBar2.setProgress(msg.arg1);
				break;
			case 2:
				Toast.makeText(mContext, (CharSequence) msg.obj, Toast.LENGTH_SHORT).show();
				break;
			case 3:
				String id=(String) msg.obj;
				DownloadInfo removeItem=null;
				for(DownloadInfo info:mDownloadInfoList){
					if(info.id.equals(id))
						removeItem=info;
				}
				if(removeItem!=null)
					mDownloadInfoList.remove(removeItem);				
				SotapitApplication.removeDownloadTask(mContext,id);
				mAdapter.notifyDataSetChanged();
				break;
			default:
				break;
			}
			
			
		}
		
	};
	
	OnClickListener startListener = new OnClickListener() {
		
		@Override
		public void onClick(final View v) {
			// TODO Auto-generated method stub
			final String id= (String) v.getTag(R.id.tag_first);
			final ProgressBar progressBar=(ProgressBar) v.getTag(R.id.tag_second);
			UpdateListener updateListener=new UpdateListener(){
				public void onProgressus(int progress) {
					mmHandler.sendMessage(mmHandler.obtainMessage(1,progress,0,progressBar));
				}

				@Override
				public void onCompelete() {
					// TODO Auto-generated method stub				
					mmHandler.sendMessage(mmHandler.obtainMessage(1,100,0,progressBar));
					mmHandler.sendMessage(mmHandler.obtainMessage(3, id));
					//v.setVisibility(View.INVISIBLE);
				}

				@Override
				public void onError(String error) {
					// TODO Auto-generated method stub
					mmHandler.sendMessage(mmHandler.obtainMessage(2, error));
				}

				@Override
				public void onStart() {
					// TODO Auto-generated method stub
					
				}
				
			};
			DownloadTask downloadTask = SotapitApplication.appContext.downloadTaskMap.get(id);
			downloadTask.setUpdateListener(updateListener);
			downloadTask.execute("");

			((TextView)v).setText("stop");
			((TextView)v).setOnClickListener(stopListener);
		}
	};
	OnClickListener stopListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub			
			String id=(String) v.getTag(R.id.tag_first);
			SotapitApplication.appContext.downloadTaskMap.get(id).cancel(true);
			((TextView)v).setText("start");
			((TextView)v).setOnClickListener(startListener);
		}
		
		
		
		
		
	};
	OnClickListener openListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			String infoId=(String) v.getTag();
			VideoPlayerActivity.startAc(mContext, infoId);
		}
	};
	
	OnClickListener deleteListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			DownloadInfo info=(DownloadInfo) v.getTag();
			mDownloadInfoList.remove(info);
			SotapitApplication.removeDownloadInfo(mContext, info.id);
			mAdapter.notifyDataSetChanged();
		}
	};
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		HodeView hodeView;
		if (convertView == null) {
			
			hodeView = new HodeView();	
			convertView = mInflater.inflate(R.layout.download_item, null);
			hodeView.progressBar=(ProgressBar) convertView.findViewById(R.id.progressBar);
			hodeView.buttonOpen=(Button) convertView.findViewById(R.id.button_open);
			hodeView.buttonStopStart=(Button) convertView.findViewById(R.id.button_stop);
			hodeView.buttonDelete=(Button) convertView.findViewById(R.id.button_delete);
			hodeView.titleView= (TextView) convertView.findViewById(R.id.titleView);
			convertView.setTag(hodeView);
		} else {
			hodeView = (HodeView) convertView.getTag();
		}
		DownloadInfo info = mDownloadInfoList.get(position);
//		Log.v("msg","info:"+ info.toString());
//		if(info.getProgress()==100)
//			hodeView.buttonStop.setVisibility(View.INVISIBLE);
		hodeView.buttonOpen.setTag(info.id);

		hodeView.buttonStopStart.setTag(R.id.tag_first,info.id);
		hodeView.buttonStopStart.setTag(R.id.tag_second, hodeView.progressBar);
		if(SotapitApplication.isRunningTask(info.id)){
			hodeView.buttonStopStart.setText("stop");
			hodeView.buttonStopStart.setOnClickListener(stopListener);
			
		}else{
			hodeView.buttonStopStart.setText("start");
			hodeView.buttonStopStart.setOnClickListener(startListener);
		}

		hodeView.buttonOpen.setOnClickListener(openListener);
		hodeView.buttonDelete.setTag(info);
		hodeView.buttonDelete.setOnClickListener(deleteListener);
		
		hodeView.titleView.setText(info.fileName);
		hodeView.progressBar.setProgress(info.getProgress());
		return convertView;
	}
	
	public class HodeView {
		ProgressBar progressBar;
		TextView titleView;
		Button buttonOpen;
		Button buttonStopStart;
		Button buttonDelete;
	}

}
