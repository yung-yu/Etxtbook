package com.android.ebook;

import java.io.File;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import com.android.ebook.data.Book;
import com.android.ebook.data.BookData;
import com.android.ebook.data.Unity;
import com.android.ebook.filebrowser.FileItem;
import com.android.ebook.ui.BookActivity;
import com.android.ebook.ui.FileView;
import com.android.ebook.unit.CustomToast;
import com.org.ebook.R;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;

import net.simonvt.menudrawer.MenuDrawer;
import net.simonvt.menudrawer.Position;
import android.os.Bundle;
import android.provider.Settings;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.graphics.Color;
import android.telephony.TelephonyManager;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends BaseActivity {
	private MenuDrawer mMenu_left;
	private ImageView bt_clearcache;
	private List<Book> booklist = new ArrayList<Book>();
	private GridView gv_desk;
	private BookAdapter mBookAdapter;
	private ImageView iv_addbook;
	private BookData mBookData;
	private Context context;
	private TextView tv_Msg;
	private List<Integer> removebooklist = new ArrayList<Integer>();
	FileView mFileView;
	LinearLayout mAd_container;
	private AdView adView;
	private final String MY_AD_UNIT_ID = "ca-app-pub-8866644298400812/1566070888";
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		sendScreenName("­º­¶");
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		mBookData = new BookData(this);
		context = this;	
		setContentView( R.layout.activity_main);
		mMenu_left = MenuDrawer.attach(this, MenuDrawer.MENU_DRAG_WINDOW,Position.LEFT);
		mFileView = new FileView(this);
		mMenu_left.setMenuView(mFileView.getView());	
		gv_desk = (GridView)findViewById(R.id.desk);
		iv_addbook = (ImageView)findViewById(R.id.imageButton1);
		bt_clearcache = (ImageView)findViewById(R.id.imageButton2);
		tv_Msg = (TextView)findViewById(R.id.tv_msg);
		mAd_container = (LinearLayout)findViewById(R.id.ad);
		mBookAdapter = new BookAdapter(this);
		gv_desk.setAdapter(mBookAdapter);
		gv_desk.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, final int position,
					long arg3) {
				// TODO Auto-generated method stub
				if(!mBookAdapter.isRemoveMode())
				{
					String filePath =booklist.get(position).getBookPath();
					if(filePath.startsWith(BookData.ASSATS_PATH))
					{
						Bundle bd = new Bundle();
						bd.putInt("bookid",booklist.get(position).getBookId());
						Intent it=new Intent();
						it.setClass(MainActivity.this, BookActivity.class);
						it.putExtras(bd);
						startActivity(it);
					}
					else
					{
						File mf = new File(filePath);
						if(mf.exists())
						{   
							Bundle bd = new Bundle();
							bd.putInt("bookid",booklist.get(position).getBookId());
							Intent it=new Intent();
							it.setClass(MainActivity.this, BookActivity.class);
							it.putExtras(bd);
							startActivity(it);

						}else{
							CustomToast.CreateToast(context, getString(R.string.file_not_exist), Toast.LENGTH_SHORT);
						}
					}
				}else{
					if(removebooklist.contains(booklist.get(position).getBookId())){
						removebooklist.remove(Integer.valueOf(booklist.get(position).getBookId()));
						CustomToast.CreateToast(context, booklist.get(position).getBookName(),Toast.LENGTH_SHORT );
					}else{
						removebooklist.add(booklist.get(position).getBookId());
						CustomToast.CreateToast(context, booklist.get(position).getBookName(),Toast.LENGTH_SHORT);
					}
					mBookAdapter.notifyDataSetChanged();
				}
			}

		}
				);
		iv_addbook.setOnClickListener(new View.OnClickListener(){
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(!mBookAdapter.isRemoveMode())
					mMenu_left.openMenu();
				else{
					mBookAdapter.setRemoveMode(false);
					mMenu_left.setTouchMode(MenuDrawer.TOUCH_MODE_FULLSCREEN);
					showAppMsg();
					removebooklist.clear();
					mBookAdapter.notifyDataSetChanged();
					iv_addbook.setImageResource(R.drawable.storage);
					bt_clearcache.setImageResource(R.drawable.garbage);
				}
			}

		});
		bt_clearcache.setOnClickListener(new View.OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(!mBookAdapter.isRemoveMode())
					showclearDialog();
				else{
					ShowTipDeleteBook();
				}
			}

		});
		mFileView.setOnFileEventListener(new FileView.OnFileEventListener() {
			
			@Override
			public void selectedFile(FileItem item) {
				// TODO Auto-generated method stub
				if(item.getName().endsWith(".txt")){
					Book mBook = new Book();
					mBook.setBookName(item.getName().substring(0,item.getName().lastIndexOf(".")));
					mBook.setBookPath(item.getPath());
					if(!checkbookIsExists(mBook)){
						Calendar c = Calendar.getInstance();
						mBookData.addBook(mBook,Unity.getCurDate(c),Unity.getCurTime(c));
						notifyDataSetChanged();
						CustomToast.CreateToast(MainActivity.this, getString(R.string.addbook).replace("&s", item.getName()), Toast.LENGTH_SHORT);
					}
					else
						CustomToast.CreateToast(MainActivity.this,getString(R.string.added).replace("&s", item.getName()), Toast.LENGTH_SHORT);
				}else{
					CustomToast.CreateToast(MainActivity.this, getString(R.string.file_not_exist), Toast.LENGTH_SHORT);
				}
			}
		});
		initAds();
	}
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		if(adView!=null)
			adView.resume();
		showAppMsg();
		new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub	

				booklist.clear();
				booklist.addAll(mBookData.getBookList());
				runOnUiThread( new Runnable() {
					public void run() {
						mBookAdapter.notifyDataSetChanged();
					}
				});

			}
		}).start();
	}
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();		
		if(adView!=null)
			adView.pause();
	}
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		if(adView!=null)
			adView.destroy();
		super.onDestroy();
		
	}
	 
    public void initAds(){
    		adView = new AdView(this);
    		adView.setAdUnitId(MY_AD_UNIT_ID);
    	    adView.setAdSize(AdSize.SMART_BANNER);
        mAd_container.addView(adView);
        AdRequest adRequest = new AdRequest.Builder()
//            .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
//            .addTestDevice(getDeviceId())
            .build();
        adView.loadAd(adRequest);
        adView.setAdListener(new AdListener() {

			@Override
			public void onAdOpened() {
				// TODO Auto-generated method stub
				super.onAdOpened();
			}
        	 
		});
    }
	public String getDeviceId(){

        String android_id = Settings.Secure.getString(this.getContentResolver(), Settings.Secure.ANDROID_ID);
        String deviceId = md5(android_id).toUpperCase();
        return deviceId;
	}
	public static final String md5(final String s) {
	    try {
	        // Create MD5 Hash
	        MessageDigest digest = java.security.MessageDigest
	                .getInstance("MD5");
	        digest.update(s.getBytes());
	        byte messageDigest[] = digest.digest();

	        // Create Hex String
	        StringBuffer hexString = new StringBuffer();
	        for (int i = 0; i < messageDigest.length; i++) {
	            String h = Integer.toHexString(0xFF & messageDigest[i]);
	            while (h.length() < 2)
	                h = "0" + h;
	            hexString.append(h);
	        }
	        return hexString.toString();

	    } catch (NoSuchAlgorithmException e) {
	        //Logger.logStackTrace(TAG,e);
	    }
	    return "";
	}
	public void notifyDataSetChanged(){
		booklist.clear();
		booklist.addAll(mBookData.getBookList());
		mBookAdapter.notifyDataSetChanged();
	}
	private void showAppMsg(){
		try{
			PackageInfo pkgInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
			String verName = pkgInfo.versionName;
			int pointIndex = verName.lastIndexOf(".");
			tv_Msg.setText(getString(R.string.easy_read)+"\n Ver"+verName.substring(0, pointIndex)
					+" Build"+verName.substring(pointIndex+1));
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	private boolean checkbookIsExists(Book item){
		for(int i=0;i<booklist.size();i++)
		{
			if(item.getBookPath().equals(booklist.get(i).getBookPath())&&item.getBookName().equals(booklist.get(i).getBookName()))
			{
				return true;
			}
		}
		return false;
	}
	private void showclearDialog(){
		AlertDialog.Builder ab = new AlertDialog.Builder(this);
		ab.setTitle(R.string.alert_title_clear);
		ArrayAdapter<String> tmpadapter = new ArrayAdapter<String>(this, 
				android.R.layout.simple_expandable_list_item_1,android.R.id.text1){
			@Override
			public View getView(int position, View convertView,
					ViewGroup parent) {
				// TODO Auto-generated method stub
				View view = super.getView(position, convertView, parent);
				TextView tv = (TextView) view.findViewById(android.R.id.text1);
				tv.setTextColor(Color.BLACK);
				return view;
			}

				};
				tmpadapter.addAll(getResources().getStringArray(R.array.clear_select));
				ab.setAdapter(tmpadapter, new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						switch (which) {
						case 0:
							mBookAdapter.setRemoveMode(true);
							mMenu_left.setTouchMode(MenuDrawer.TOUCH_MODE_NONE);
							tv_Msg.setText(R.string.setOnremoveMode);
							iv_addbook.setImageResource(R.drawable.cancel);
							bt_clearcache.setImageResource(R.drawable.ok);
							break;
						case 1:
							ShowClearAllBookData();
							break;

						default:
							break;
						}
						dialog.cancel();
					}
				});
				ab.setPositiveButton(R.string.alert_cancel, new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						dialog.cancel();
					}
				});
				ab.show();
	}
	
	private void ShowClearAllBookData(){
		AlertDialog.Builder ab =new AlertDialog.Builder(this);
		ab.setTitle(R.string.alert_tip_Alldelete);
		ab.setNegativeButton(R.string.alert_ok, new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				mBookData.clearAllData();
				booklist.clear();
				mBookAdapter.notifyDataSetChanged();
				CustomToast.CreateToast(context, getString(R.string.delete_success), Toast.LENGTH_SHORT);
				dialog.cancel();
			}
		});
		ab.setPositiveButton(R.string.alert_cancel, new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				dialog.cancel();
			}
		});
		ab.create().show();
	}
    
	private void ShowTipDeleteBook(){
		AlertDialog.Builder ab =new AlertDialog.Builder(this);
		ab.setTitle(R.string.alert_tip_deleteSelect);
		ab.setNegativeButton(R.string.alert_ok, new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				mBookAdapter.setRemoveMode(false);
				mMenu_left.setTouchMode(MenuDrawer.TOUCH_MODE_FULLSCREEN);
				showAppMsg();
				iv_addbook.setImageResource(R.drawable.storage);
				bt_clearcache.setImageResource(R.drawable.garbage); 
				if(removebooklist.size()>0)
				{
					for(int i=0;i<removebooklist.size();i++)
						mBookData.deleteBook(removebooklist.get(i));	
					notifyDataSetChanged();
					CustomToast.CreateToast(context, getString(R.string.delete_success), Toast.LENGTH_SHORT);
				}	
				else{
					mBookAdapter.notifyDataSetChanged();
				}
				dialog.cancel();
			}
		});
		ab.setPositiveButton(R.string.alert_cancel, new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				dialog.cancel();
			}
		});
		ab.create().show();
	}

	private class BookAdapter extends BaseAdapter{
		LayoutInflater inflater;  
		class viewholder{
			TextView tv;
			ImageView select;
		}
		boolean isRemoveMode  = false;
		public boolean isRemoveMode() {
			return isRemoveMode;
		}
		public void setRemoveMode(boolean isRemoveMode) {
			this.isRemoveMode = isRemoveMode;
		}
		public BookAdapter(Context context){
			inflater = LayoutInflater.from(context);
		}
		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return booklist.size();
		}

		@Override
		public Object getItem(int arg0) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public long getItemId(int arg0) {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public View getView(final int position, View v, ViewGroup arg2) {
			// TODO Auto-generated method stub
			viewholder vh;
			if(v == null){
				vh = new viewholder();
				v = inflater.inflate(R.layout.bookitem, null);
				vh.tv = (TextView)v.findViewById(R.id.textView1);
				vh.select = (ImageView)v.findViewById(R.id.imageView1);
				v.setTag(vh);
			}else{
				vh = (viewholder) v.getTag();
			}
			if(position<booklist.size())
				vh.tv.setText(booklist.get(position).getBookName());
			if(!isRemoveMode){
				v.setOnTouchListener(new View.OnTouchListener() {

					@Override
					public boolean onTouch(View v, MotionEvent event) {
						// TODO Auto-generated method stub
						runOnUiThread( new Runnable() {
							public void run() {
								if(position<booklist.size()&&!mBookAdapter.isRemoveMode())
									tv_Msg.setText(booklist.get(position).getBookName());
							}
						});
						return false;
					}
				});
				vh.select.setVisibility(View.GONE);
			}else{
				v.setOnTouchListener(null);
				vh.select.setVisibility(removebooklist.contains(booklist.get(position).getBookId())?View.VISIBLE:View.GONE);
			}

			return v;
		}

	}
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if(event.getKeyCode() == KeyEvent.KEYCODE_BACK){
			if(mMenu_left.isMenuVisible()){
				mMenu_left.closeMenu();
				return false;
			}
		}
		return super.onKeyDown(keyCode, event);
	}

}
