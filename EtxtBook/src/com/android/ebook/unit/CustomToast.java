package com.android.ebook.unit;

import android.content.Context;
import android.widget.Toast;

public class CustomToast {
      public static  Toast mToast;
      public static void  CreateToast(Context context, String text,int duration){
    	       if(mToast!=null)
    	    	   		mToast.cancel();
    	       mToast=Toast.makeText(context, text, duration);
    	       mToast.show();
      }
}
