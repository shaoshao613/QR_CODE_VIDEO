package com.sotapit.app;

import android.app.Activity;
import android.os.Handler;
import android.widget.Toast;

import com.sotapit.widgets.LoadingDialog;

public class BaseActivity extends Activity{

	protected static final int ERROR_MESSAGE = 0;
	protected static final int DISMISS_LOADING = 1;
	private LoadingDialog progressDialog;
	protected Activity mContext=this;

	protected void showLoadingDialogAuto(){
		if (progressDialog == null){
			progressDialog = LoadingDialog.createDialog(this);
	    	progressDialog.setMessage("正在加载中");
		}
		if(!progressDialog.isShowing()){
	    	progressDialog.show();
	    	 new Handler().postDelayed(new Runnable() {
					@Override
					public void run() {
						dismissLoadingDialog();
					}
				}, 3000);
    	 }
	}
	
	protected void showLoadingDialog(){
		if (progressDialog == null){
			progressDialog = LoadingDialog.createDialog(this);
	    	progressDialog.setMessage("正在加载中");
		}
		if(!progressDialog.isShowing()){
	    	progressDialog.show();
	    }
	}
	
	protected Handler mHandler=new Handler(){
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			
			case ERROR_MESSAGE:
				Toast.makeText(mContext, (CharSequence) msg.obj, Toast.LENGTH_SHORT).show();
				break;
			case DISMISS_LOADING:
				dismissLoadingDialog();
				break;
				
			
			}
		}
		
		
	};
	
	
	protected void dismissLoadingDialog(){
		if (progressDialog != null&&progressDialog.isShowing()){
			progressDialog.dismiss();
			progressDialog = null;
		}
	}
	
	
}
