package com.sotapit.http_request;

public interface ResponseListener {

	void onGetData(String json);

	void onError(String reason);
	
	void onErrorCode(int errorCode);
	
	void onErrorCode(int errorCode, int count);
}
