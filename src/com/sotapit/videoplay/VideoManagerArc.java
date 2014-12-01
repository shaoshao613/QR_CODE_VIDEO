package com.sotapit.videoplay;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.ListActivity;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.sotapit.app.R;
import com.sotapit.app.SotapitApplication;
import com.sotapit.download.DownloadInfo;
import com.sotapit.store.Store;


public class VideoManagerArc extends ListActivity{

	private ArrayList<VideoInfo> videoList;

	private Activity mContext=this;
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.video_list);
		
		init();
	}
	
	
	private void init(){

		videoList=new ArrayList<VideoInfo>();
		for(String id:SotapitApplication.appContext.videoList){
			VideoInfo vi=(VideoInfo) Store.getObject(mContext, id);
			if(vi!=null){
				if(SotapitApplication.appContext.downloadIdList.contains(vi.lastPlay()))
					videoList.add(vi);
			}
		}
		
		VideoAdapter adapter = new VideoAdapter(this, videoList);
		this.getListView().setAdapter(adapter);
	}

	
	/**
	 * @author Administrator
	 *
	 */
	static class VideoAdapter extends BaseAdapter{
		
		private Context context;
		private List<VideoInfo> videoItems;
		
		public VideoAdapter(Context context, List<VideoInfo> data){
			this.context = context;
			this.videoItems = data;
		}
		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return videoItems.size();
		}
		@Override
		public Object getItem(int p) {
			// TODO Auto-generated method stub
			return videoItems.get(p);
		}
		@Override
		public long getItemId(int p) {
			// TODO Auto-generated method stub
			return p;
		}
		public OnClickListener videoClickListener=new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				String id=(String) v.getTag();
				VideoPlayerActivity.startAc(context, id);
			}
		};
		
		
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder = null;
			if(convertView == null){
				holder = new ViewHolder();
				convertView = LayoutInflater.from(context).inflate(R.layout.video_item, null);
				holder.thumbImage = (ImageView)convertView.findViewById(R.id.thumb_image);
				holder.titleText = (TextView)convertView.findViewById(R.id.video_title);
				convertView.setTag(holder);
			}else{
				holder = (ViewHolder)convertView.getTag();
			}

			holder.titleText.setText(videoItems.get(position).title);
			holder.thumbImage.setTag(videoItems.get(position).lastPlay());
			holder.thumbImage.setOnClickListener(videoClickListener);
			ImageLoader.getInstance().displayImage(videoItems.get(position).logo, holder.thumbImage);
			
			return convertView;
		}

		class ViewHolder{
			ImageView thumbImage;
			TextView titleText;
		}
		
	}
}

