package com.sotapit.videoplay;

import java.io.File;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.LogManager;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnBufferingUpdateListener;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.Toast;
import android.widget.SeekBar.OnSeekBarChangeListener;

import com.sotapit.app.BaseActivity;
import com.sotapit.app.R;
import com.sotapit.app.SotapitApplication;
import com.sotapit.download.DownloadInfo;
import com.sotapit.download.DownloadTask;
import com.sotapit.download.UpdateListener;
import com.sotapit.store.Store;

public class VideoPlayerActivity extends BaseActivity  implements OnClickListener, OnBufferingUpdateListener, OnPreparedListener, OnCompletionListener{

		private static final String VIDEO_ID = "VIDEO_ID";
		private static final String VIDEO_PATH = "VIDEO_PATH";
		ImageView btnplay;
        SurfaceView surfaceView;
        MediaPlayer mediaPlayer;
        int position;
        DownloadInfo info;
        VideoInfo videoInfo;
        private Timer mTimer=new Timer();  
		private SurfaceHolder surfaceHolder;
		private SeekBar seekBar;
		
		
		public static void startAc(Context context,String Id) {
			Intent intent = new Intent(context, VideoPlayerActivity.class);
			intent.putExtra(VIDEO_ID, Id);
			context.startActivity(intent);
		}

        public void onCreate(Bundle savedInstanceState) {
                super.onCreate(savedInstanceState);
                setContentView(R.layout.video_play);
                btnplay=(ImageView) this.findViewById(R.id.video_control);
                seekBar=( SeekBar) this.findViewById(R.id.seekBar);
                surfaceView=(SurfaceView) this.findViewById(R.id.video_surface);
                Intent intent = this.getIntent();
                String infoId = (String) intent.getSerializableExtra(VIDEO_ID);
                info=SotapitApplication.appContext.downloadInfoMap.get(infoId);
                
                if(SotapitApplication.appContext.downloadTaskMap.containsKey(infoId)){               	
                	if(!SotapitApplication.appContext.downloadTaskMap.get(infoId).isRunning){
                		SotapitApplication.appContext.downloadTaskMap.get(infoId).execute("");
                		showLoadingDialog();
                	}
                }else{     	
                	DownloadTask tast = new DownloadTask(info);
                	tast.execute("");
                	showLoadingDialog();
                	SotapitApplication.appContext.downloadTaskMap.put(info.id,tast);
                }
                SotapitApplication.appContext.downloadTaskMap.get(infoId).setUpdateListener(updateListener);
                
                if(info!=null){
                    if(Store.getObject(mContext,info.id)!=null)
                    	info=(DownloadInfo) Store.getObject(mContext,info.id);
                    seekBar.setProgress(info.playProgress);
                    
                }else{
                	finish();
                }
                btnplay.setOnClickListener(this);
                mediaPlayer=new MediaPlayer();
                mTimer.schedule(mTimerTask, 0, 100);  
                surfaceView.setOnClickListener(this);
                surfaceHolder =surfaceView.getHolder();
                surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);                                
                seekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
					
					@Override
					public void onStopTrackingTouch(SeekBar seekBar) {
						// TODO Auto-generated method stub
					}
					
					@Override
					public void onStartTrackingTouch(SeekBar seekBar) {
						// TODO Auto-generated method stub
						
					}
					
					@Override
					public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
						// TODO Auto-generated method stub
						if(seekBar.isPressed()){
							if(mediaPlayer.isPlaying()){
								mediaPlayer.seekTo(progress* mediaPlayer.getDuration()/ seekBar.getMax());  
							}else{
								play();
							}
						}
					}
				});
        }
        
    	

		private UpdateListener updateListener=new UpdateListener(){
			public void onProgressus(int progress) {
			

                seekBar.setSecondaryProgress((int)(progress));  
			}

			@Override
			public void onCompelete() {
				// TODO Auto-generated method stub				
				//v.setVisibility(View.INVISIBLE);
			}

			@Override
			public void onError(String error) {
				// TODO Auto-generated method stub

				mHandler.sendEmptyMessage(DISMISS_LOADING);
				mHandler.sendMessage(mHandler.obtainMessage(ERROR_MESSAGE, error));
				//Toast.makeText(mContext,error, Toast.LENGTH_SHORT).show();
			}

			@Override
			public void onStart() {
				// TODO Auto-generated method stub
				mHandler.sendEmptyMessage(DISMISS_LOADING);
			}
			
		};
		
        TimerTask mTimerTask = new TimerTask() {  
            @Override  
            public void run() {  
                if(mediaPlayer==null)  
                    return;  
                if (mediaPlayer.isPlaying() && !seekBar.isPressed()) {  
                    handleProgress.sendEmptyMessage(0);  
                }  
            }  
        };  
          
        Handler handleProgress = new Handler() {  
        	int count=0;
            public void handleMessage(Message msg) {  
      
                int position = mediaPlayer.getCurrentPosition();  
                int duration = mediaPlayer.getDuration(); 
                count++;
                if(count>20){
                	seekBar.setVisibility(View.GONE);  
                	count=0;
                }
                if (duration > 0) {  
                    long pos = seekBar.getMax() * position / duration;  
                    seekBar.setProgress((int) pos);   
                }  
            };  
        };  
        
        @Override
        public void onClick(View v) {        
                switch (v.getId()) {
                case R.id.video_control:
                	 //	this.findViewById(R.id.video_masking).setVisibility(View.INVISIBLE);
                	 	play();
                       // vv.start();
                        break;
                case R.id.video_surface:
                		stop();
                		this.findViewById(R.id.video_control).setVisibility(View.VISIBLE);
                		this.findViewById(R.id.seekBar).setVisibility(View.VISIBLE);
                		break;
                default:
                        break;
                }

        }
        @Override
        protected void onDestroy() {        
        	if(info!=null){
	        	info.playProgress=seekBar.getProgress();
	        	Store.saveObject(mContext,info.id,info);
        	}
            super.onDestroy();
        }
        
        
        @Override
        protected void onPause() {        
        	stop();
            super.onPause();
        }
        private void stop(){
        	if (mediaPlayer!=null&&mediaPlayer.isPlaying()) {
                position=mediaPlayer.getCurrentPosition();
                mediaPlayer.stop();
        }
        	
        	
        }
        private void play() {
                try {
                	

            	 		this.findViewById(R.id.video_control).setVisibility(View.INVISIBLE);
                        mediaPlayer.reset();
                        mediaPlayer
                        .setAudioStreamType(AudioManager.STREAM_MUSIC);
//                        Uri url = Uri.parse(path);
//                        mediaPlayer.setDataSource(this,url);
                        mediaPlayer.setDataSource(info.filePath);
                        mediaPlayer.setDisplay(surfaceView.getHolder());
                        mediaPlayer.setOnBufferingUpdateListener(this); 
                        mediaPlayer.setOnCompletionListener(this);
                        mediaPlayer.setOnPreparedListener(this); 
                        mediaPlayer.prepareAsync();       
                } catch (Exception e) {
                        // TODO: handle exception
                }
        }
		@Override
		public void onPrepared(MediaPlayer mp) {
			// TODO Auto-generated method stub
			
            mediaPlayer.start(); 
            mediaPlayer.seekTo(seekBar.getProgress()* mediaPlayer.getDuration()/ seekBar.getMax());  
		}
		@Override
		public void onBufferingUpdate(MediaPlayer mp, int percent) {
			// TODO Auto-generated method stub
			
		}
		@Override
		public void onCompletion(MediaPlayer mp) {
			// TODO Auto-generated method stub
			 //mediaPlayer.stop();
			 mediaPlayer.reset();
			 
			 if(info!=null&&info.nextVideoId!=null){
				 DownloadInfo nextVideo=SotapitApplication.appContext.downloadInfoMap.get(info.nextVideoId);
				 if(nextVideo!=null){
					 File file=new File(nextVideo.filePath);
					 if(!file.exists()){
						 DownloadTask task = SotapitApplication.appContext.downloadTaskMap.get(nextVideo.id);
						 if(task!=null&&!task.isRunning)
							 task.execute("");
						 
					 }
	                    info=nextVideo;
	                    seekBar.setProgress(info.playProgress);
						try {
							mediaPlayer.setDataSource(info.filePath);
							mediaPlayer.prepareAsync();
						} catch (IllegalArgumentException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (SecurityException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (IllegalStateException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
				 }
			 }
//			 
//			 
//			  String path="http://staticnova.ruoogle.com/video/2644197_avatar_201410171200001";
//              Uri url = Uri.parse(path);
//				mediaPlayer.setDataSource(this,url);

              
		}
		

		
		
}
