package com.sotapit.videoplay;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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

import com.sotapit.app.R;
/**
 * 本实例，我们将通过检索SDCard上的Video信息
 * 在MediaStore中，MediaStore.Video.Media中就有Video相关信息，
 * 同时MediaStore.Video.Thumbnails中含有各个video对应的缩略图信息
 * 
 * @author Administrator
 *
 */
public class VideoManagerTest extends ListActivity {
	
	private Cursor cursor;
	
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.video_list);
		
		init();
	}
	
	
	private void init(){
		String[] thumbColumns = new String[]{
				MediaStore.Video.Thumbnails.DATA,
				MediaStore.Video.Thumbnails.VIDEO_ID
		};
		
		String[] mediaColumns = new String[]{
				MediaStore.Video.Media.DATA,
				MediaStore.Video.Media._ID,
				MediaStore.Video.Media.TITLE,
				MediaStore.Video.Media.MIME_TYPE
		};
		
		//首先检索SDcard上所有的video
		cursor = this.managedQuery(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, mediaColumns, null, null, null);
		
		ArrayList<VideoInfo> videoList = new ArrayList<VideoManagerTest.VideoInfo>();
		
		if(cursor.moveToFirst()){
			do{
				VideoInfo info = new VideoInfo();
				
				info.filePath = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATA));
				info.mimeType = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.MIME_TYPE));
				info.title = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.TITLE));
				Log.v("msg","info.filePath:"+info.filePath );
				Log.v("msg","info.title:"+info.title );
				Log.v("msg","info.mimeType:"+info.mimeType );
				//获取当前Video对应的Id，然后根据该ID获取其Thumb
				int id = cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Video.Media._ID));
				Log.v("msg","info.id:"+info.mimeType );
				String selection = MediaStore.Video.Thumbnails.VIDEO_ID +"=?";
				String[] selectionArgs = new String[]{
						id+""
				};
				Cursor thumbCursor = this.managedQuery(MediaStore.Video.Thumbnails.EXTERNAL_CONTENT_URI, thumbColumns, selection, selectionArgs, null);
				
				if(thumbCursor.moveToFirst()){
					info.thumbPath = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Thumbnails.DATA));
					
				}else{
					
					info.thumbPath= getThumbPath(info.filePath,100);
				}
				
				//然后将其加入到videoList
				videoList.add(info);
				
			}while(cursor.moveToNext());
		}
		
		//然后需要设置ListView的Adapter了，使用我们自定义的Adatper
		VideoAdapter adapter = new VideoAdapter(this, videoList);
		this.getListView().setAdapter(adapter);
	}
	 private String getThumbPath(String videoPath,int quality){
		    String saveFilePath=videoPath+".jpg";
		    File saveFile=new File(saveFilePath);
		    if(saveFile.exists()){
		    	Log.v("msg", "缩略图创建成功");
		    	return saveFilePath;
		    }
		    Bitmap rawBitmap = getVideoThumbnail(videoPath, 720,720, MediaStore.Video.Thumbnails.FULL_SCREEN_KIND);
		    if (!saveFile.exists()) {
		      try {
		        saveFile.createNewFile();
		        FileOutputStream fileOutputStream=new FileOutputStream(saveFile);
		        if (fileOutputStream!=null) {
		          //imageBitmap.compress(format, quality, stream);
		          //把位图的压缩信息写入到一个指定的输出流中
		          //第一个参数format为压缩的格式
		          //第二个参数quality为图像压缩比的值,0-100.0 意味着小尺寸压缩,100意味着高质量压缩
		          //第三个参数stream为输出流
		          rawBitmap.compress(Bitmap.CompressFormat.JPEG, quality, fileOutputStream);
		        }
		        fileOutputStream.flush();
		        fileOutputStream.close();
		      } catch (IOException e) {
		        e.printStackTrace();
		        
		      }
		    }
		    return saveFilePath;
	}
	private Bitmap getVideoThumbnail(String videoPath, int width , int height, int kind){ 
		    Bitmap bitmap = null; 
		    bitmap = ThumbnailUtils.createVideoThumbnail(videoPath, kind); 
		    bitmap = ThumbnailUtils.extractThumbnail(bitmap, width, height, ThumbnailUtils.OPTIONS_RECYCLE_INPUT); 
		    return bitmap; 
	}
	static class VideoInfo{
		String filePath;
		String mimeType;
		String thumbPath;
		String title;
	}
	
	/**
	 * 定义一个Adapter来显示缩略图和视频title信息
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
				String path=(String) v.getTag();
				VideoPlayerActivity.startAc(context, path);
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
			
			//显示信息
			holder.titleText.setText(videoItems.get(position).title);
			holder.thumbImage.setTag(videoItems.get(position).filePath);
			holder.thumbImage.setOnClickListener(videoClickListener);
			if(videoItems.get(position).thumbPath != null){
				holder.thumbImage.setImageURI(Uri.parse(videoItems.get(position).thumbPath));
			}
			
			return convertView;
		}

		class ViewHolder{
			ImageView thumbImage;
			TextView titleText;
		}
		
	}
}

