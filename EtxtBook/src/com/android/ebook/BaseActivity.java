package com.android.ebook;



import android.app.Activity;
import android.os.Bundle;

public class BaseActivity  extends Activity{

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
	}

	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
	}

	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
	}
//	public Tracker getTracker(){
//		//    		GoogleAnalytics analytics = GoogleAnalytics.getInstance(this);
//		//    		analytics.getLogger().setLogLevel(Logger.LogLevel.VERBOSE);
//		//		Tracker mTracker = analytics.newTracker(R.xml.ga);
//		//		return mTracker;
//		return null;
//	}
	public void sendScreenName(String screenName){
//	    //新增GoogleAnalytics物件
//		GoogleAnalytics analytics = GoogleAnalytics.getInstance(this);
//		analytics.getLogger().setLogLevel(Logger.LogLevel.VERBOSE);
//		//取得追蹤物件
//		Tracker mTracker = analytics.newTracker(R.xml.ga);
//		//設定當前畫面名稱
//		mTracker.setScreenName(screenName);
//	    //送出訊息
//		mTracker.send(new HitBuilders.AppViewBuilder().build());
	}

}
