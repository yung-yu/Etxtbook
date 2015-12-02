package com.android.ebook;



import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Logger;
import com.google.android.gms.analytics.Tracker;
import com.org.ebook.R;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

public class BaseActivity  extends FragmentActivity{

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
	public Tracker getTracker(){
		    		GoogleAnalytics analytics = GoogleAnalytics.getInstance(this);
		    		analytics.getLogger().setLogLevel(Logger.LogLevel.VERBOSE);
				Tracker mTracker = analytics.newTracker(R.xml.ga);
				return mTracker;
	}
	public void sendScreenName(String screenName){

		GoogleAnalytics analytics = GoogleAnalytics.getInstance(this);
		analytics.getLogger().setLogLevel(Logger.LogLevel.VERBOSE);

		Tracker mTracker = analytics.newTracker(R.xml.ga);

		mTracker.setScreenName(screenName);

		mTracker.send(new HitBuilders.AppViewBuilder().build());
	}

}
