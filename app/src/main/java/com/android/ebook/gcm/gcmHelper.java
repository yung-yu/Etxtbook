package com.android.ebook.gcm;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.android.ebook.config.SystemConfig;
import com.android.ebook.util.AndroidUtil;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by andyli on 2015/6/21.
 */
public class GcmHelper {


    public final static String GCM_SENDER_ID = "876306650181";
    public static enum GCMState{
        PLAY_SERVICES_UNSUPPORT,
        NEED_REGISTER,
        AVAILABLE
    }

    public static interface GCMListener{
        void gcmRegistered(boolean success,String regId);
    }
    Activity activity;
    private final String REGID_KEY = "registerid";
    private final String VERSION_KEY ="version";
    public final static int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    GCMListener mGCMListener;
    public GcmHelper(Activity activity, GCMListener listener) {
        this.activity = activity;
        this.mGCMListener = listener;
    }
    public SharedPreferences getSharedPreferences(){
        return activity.getSharedPreferences(activity.getPackageName(),Context.MODE_PRIVATE);
    }
    public int getAppVersion(){
        PackageManager pm = activity.getPackageManager();
        try {
            PackageInfo info = pm.getPackageInfo(activity.getPackageName(), 0);
            return info.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            throw new RuntimeException("Could not get package name: " + e);
        }
    }
    public String getRegisterId(){
        SharedPreferences sp = getSharedPreferences();
        String registerId = sp.getString(REGID_KEY,"");
        if(registerId.isEmpty()){
            return registerId;
        }
        int curVersion = getAppVersion();
        if(curVersion!=sp.getInt(VERSION_KEY,Integer.MAX_VALUE)){
            return "";
        }
        return registerId;
    }
    public void saveRegisterId(String id){
        SharedPreferences sp = getSharedPreferences();
        SharedPreferences.Editor et = sp.edit();
        et.putString(REGID_KEY, id);
        et.putInt(VERSION_KEY, getAppVersion());
        et.commit();
    }
    /**
     * 檢查Google Play Service可用狀態
     *
     * @return 傳回Google Play Service可用狀態
     */
    public boolean  checkPlayServices() {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(activity);

        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, activity,
                        PLAY_SERVICES_RESOLUTION_REQUEST).show();
            }
            return false;
        }
        return true;
    }

    public GCMState  openGcm(){
        if(checkPlayServices()){
            String regid = getRegisterId();
            if(regid.isEmpty()){
                registerInBackground();
                return GCMState.NEED_REGISTER;
            }else {
                return GCMState.AVAILABLE;
            }
        }else{
            return GCMState.PLAY_SERVICES_UNSUPPORT;
        }
    }

    public void registerInBackground(){
        new RegisterTask().execute();
    }

    private class RegisterTask extends AsyncTask<Void,Void,String>{
        @Override
        protected String doInBackground(Void... voids) {
            GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(activity);
            try {
                String regId = gcm.register(GCM_SENDER_ID);
                if(regId==null||regId.isEmpty())
                    return "";
                saveRegisterId(regId);

                if(mGCMListener!=null){
                    if(!sendRegisterIdToAppServer(regId)){
                        saveRegisterId("");
                        return "";
                    }
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
        public boolean sendRegisterIdToAppServer(String regID){
            HttpClient client = new DefaultHttpClient();
            HttpPost post = new HttpPost(SystemConfig.REGISTER_GEM_URL);
            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
            String email = AndroidUtil.getEmail(activity);
            nameValuePairs.add(new BasicNameValuePair("email", email));
            nameValuePairs.add(new BasicNameValuePair("regid", regID));
            try {
                post.setEntity(new UrlEncodedFormEntity(nameValuePairs));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            try {
                HttpResponse response = client.execute(post);
                int statusCode = response.getStatusLine().getStatusCode();
                Log.d("gcm", "statusCode :" + statusCode);
                if(statusCode==200){
                    final String body = AndroidUtil.fromStream(response.getEntity().getContent());
                    Log.d("gcm", "body :" + body);
                    JSONObject result = new JSONObject(body);
                    final String errmsg = result.getString("errMsg");
                    if(result.getInt("statusCode")==0) {
                        return true;
                    }else if(result.getInt("statusCode")==1) {
                        return false;
                    }
                }
            } catch (final IOException e) {
                e.printStackTrace();
            } catch (final JSONException e) {
                e.printStackTrace();
            }
            return false;
        }
        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if(mGCMListener!=null){
                mGCMListener.gcmRegistered(!s.isEmpty(),s);
            }
        }
    }

}
