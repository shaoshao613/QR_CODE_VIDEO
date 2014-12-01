package com.sotapit.app;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.sotapit.download.DownloadInfo;
import com.sotapit.download.DownloadManagerArc;
import com.sotapit.http_request.HttpRequestTask;
import com.sotapit.http_request.ResponseListener;
import com.sotapit.qr_codescan.MipcaActivityCapture;
import com.sotapit.store.Store;
import com.sotapit.videoplay.VideoInfo;
import com.sotapit.videoplay.VideoManagerArc;

public class MainActivity extends BaseActivity {
	private final static int SCANNIN_GREQUEST_CODE = 1;
	private TextView mTextView ;
	private ImageView mImageView;	
	private VideoInfo info;
	private Context mContext=this;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		
		
		
		mTextView = (TextView) findViewById(R.id.result); 
		mImageView = (ImageView) findViewById(R.id.qrcode_bitmap);		
		Button mButton = (Button) findViewById(R.id.button1);
		mButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.setClass(MainActivity.this, MipcaActivityCapture.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivityForResult(intent, SCANNIN_GREQUEST_CODE);
			}
		});
		Button mButton2 = (Button) findViewById(R.id.button2);
		mButton2.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				 
				Intent intent = new Intent(mContext,
						DownloadManagerArc.class);
				startActivity(intent);
			}
		});
		
		Button mButton3 = (Button) findViewById(R.id.button3);
		mButton3.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(mContext,
						VideoManagerArc.class);
				startActivity(intent);
			}
		});
		
	}
	private long lastClickBack=0;
	@Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
		case SCANNIN_GREQUEST_CODE:
			if(resultCode == RESULT_OK){
				Bundle bundle = data.getExtras();
				String result=bundle.getString("result");
				if(result.contains("http")){
				
					HttpRequestTask httpRequest=new HttpRequestTask(mContext,result,new ResponseListener() {

						@Override
						public void onGetData(String json) {
							// TODO Auto-generated method stub
							 Gson gson = new Gson();
							 info = gson.fromJson(json,VideoInfo.class);
							 mHandler.sendEmptyMessage(DISMISS_LOADING);
							 mTextView.setText(info.title);
							 doDownload(info);
							 ImageLoader.getInstance().displayImage(info.logo, mImageView);
						}
						
						@Override
						public void onErrorCode(int errorCode, int count) {
							// TODO Auto-generated method stub
							
						}
						
						@Override
						public void onErrorCode(int errorCode) {
							// TODO Auto-generated method stub
							
						}
						
						@Override
						public void onError(String reason) {
							// TODO Auto-generated method stub
							
						}
					});

					showLoadingDialog();
					httpRequest.execute("");
					
					
				}
				mTextView.setText(bundle.getString("result"));
				mImageView.setImageBitmap((Bitmap) data.getParcelableExtra("bitmap"));
			}
			break;
		}
    }	

	
	public void doDownload(VideoInfo videoInfo){
		String title = videoInfo.title;
		int order = 0;
		DownloadInfo preInfo = null;
		for(String url:videoInfo.path){
			order++;
			DownloadInfo downloadInfo=new DownloadInfo(url,title, order,videoInfo.vid);
			if(preInfo!=null)
				preInfo.nextVideoId=downloadInfo.id;
			preInfo=downloadInfo;
			videoInfo.ListDownloadVideo.add(downloadInfo);
			SotapitApplication.addDownloadInfo(mContext,downloadInfo);
		}
		Toast.makeText(mContext, "已添加"+order+"个任务", Toast.LENGTH_SHORT).show();
		Store.saveObject(mContext, videoInfo.vid, videoInfo);
		if(!SotapitApplication.appContext.videoList.contains(videoInfo.vid))
			SotapitApplication.appContext.videoList.add(videoInfo.vid);
		Store.saveObject(mContext, Store.VIDEO_LIST,SotapitApplication.appContext.videoList);
		
	}
	public void onBackPressed() {
	long nowtime = System.currentTimeMillis();
	if (nowtime - lastClickBack <= 2000l) {
		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				System.exit(0);
			}
		}, 50);
	} else {
		lastClickBack = nowtime;
		Toast.makeText(mContext,"再按一次退出",
				Toast.LENGTH_SHORT).show();
	}
	}
}
