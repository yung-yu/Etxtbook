package com.android.ebook.data;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Calendar;

import android.content.Context;
import android.content.res.Configuration;
import android.os.Environment;
import android.text.format.Time;

public class Unity {
	public static final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";
	private static final int BaseYear = 2014;
	@SuppressWarnings("static-access")
	public static int getDate2Int(int year, int month, int day)
	{
		int date = ((year - BaseYear) << 9) + (month << 5) + day;
		return date;
	}
	public static String dateToString(int date, int baseYear) {
		return date == 0 ?  "":
			String.format("%d/%02d/%02d", (date>>9)+baseYear, (date>>5)&0xF, date&0x1F);
	}
	/**紀錄24時制*/
	public static int  getTime2Int(int h ,int m ,int s){
		int time = h*60*60+m*60+s;
		return  time;
	}
	/**紀錄24時制*/
	public static String  getTime2Str(int time){
		int s = time % 60;
		int h = time / 60%60;
		int m = time / 60/60;
		return  s+":"+h+":"+m;
	}
	public static int getCurDate(Calendar c){
		int year = c.get(Calendar.YEAR) ;
		int month = c.get(Calendar.MONTH)+1 ;
		int  day = c.get(Calendar.DAY_OF_MONTH) ;
		return getDate2Int(year, month, day);
	}
	public static int getCurTime(Calendar c){
		int year = c.get(Calendar.HOUR_OF_DAY) ;
		int month = c.get(Calendar.MINUTE) ;
		int  day = c.get(Calendar.SECOND) ;
		return getTime2Int(year, month, day);
	}
	public static boolean isTablet(Context context) {
		return (context.getResources().getConfiguration().screenLayout
				& Configuration.SCREENLAYOUT_SIZE_MASK)
				>= Configuration.SCREENLAYOUT_SIZE_LARGE;
	}
	/**複製資料庫到ＳＤ卡*/
	public static void copyDatabase(Context context,String dbname)
	{
		for(String database : context.databaseList())
		{
			if(database.equals(dbname))
				try {
					File file = context.getDatabasePath(database);
					InputStream is = new FileInputStream(file);
					copyFile(is, new FileOutputStream(new File(Environment.getExternalStorageDirectory(), dbname)));
					//Log.d("DaraReloadFromAssets","database="+database);
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException io)
				{
					io.printStackTrace();
				}
		}
	}
	/**複製檔案*/
	public static void copyFile(InputStream is, OutputStream os) throws IOException {
		InputStream in = new BufferedInputStream(is);
		OutputStream out = new BufferedOutputStream(os);

		byte[] buf = new byte[4096];
		while (true) {
			int len = in.read(buf);
			if (len <= 0)
				break;
			out.write(buf, 0, len);
		}

		out.flush();
		out.close();
		in.close();
	}


}
