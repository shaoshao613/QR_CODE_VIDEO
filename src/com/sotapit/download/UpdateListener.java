package com.sotapit.download;

public interface UpdateListener {
		public void onProgressus(int progress);
		public void onCompelete();
		public void onError(String error);
		public void onStart();
}
