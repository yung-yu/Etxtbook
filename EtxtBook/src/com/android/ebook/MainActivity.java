package com.android.ebook;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import com.android.ebook.data.Book;
import com.android.ebook.data.BookData;
import com.android.ebook.data.Unity;
import com.android.ebook.data.sharePerferenceHelper;
import com.android.ebook.ui.BookActivity;
import com.android.mylibrary.filebrowser.FileItem;
import com.android.mylibrary.filebrowser.FileManager;
import com.google.ads.*;
import com.google.ads.AdRequest.ErrorCode;

import net.simonvt.menudrawer.MenuDrawer;
import net.simonvt.menudrawer.Position;
import android.os.Bundle;
import android.os.Environment;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.graphics.Color;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity implements AdListener{
	private MenuDrawer mMenu_left;
	private FileAdapter fadapter;
	private ListView lv;
	private FileManager mfileChooser;
	private List<FileItem> fList =new ArrayList<FileItem>();
	private TextView tv;
	private ImageView bt_back,bt_clearcache;
	private List<Book> booklist = new ArrayList<Book>();
	private GridView gv_desk;
	private BookAdapter mBookAdapter;
	private ImageView iv_addbook;
	private BookData mBookData;
	private Context context;
	private TextView tv_Msg;
	private List<String> removebooklist = new ArrayList<String>();
	private LinearLayout ad_area;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		mBookData = new BookData(this);
		context = this;	
		setContentView( R.layout.activity_main);
		mMenu_left = MenuDrawer.attach(this, MenuDrawer.MENU_DRAG_WINDOW,Position.LEFT);
		mMenu_left.setMenuView(R.layout.filelist);	
		//		mMenu_right = MenuDrawer.attach(this, MenuDrawer.MENU_DRAG_WINDOW,Position.RIGHT);
		//		mMenu_right.setMenuView(R.layout.aboutview);
		gv_desk = (GridView)findViewById(R.id.desk);
		iv_addbook = (ImageView)findViewById(R.id.imageButton1);
		bt_clearcache = (ImageView)findViewById(R.id.imageButton2);
		ad_area = (LinearLayout)findViewById(R.id.ad);
		tv_Msg = (TextView)findViewById(R.id.tv_msg);

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
						bd.putString("filepath",booklist.get(position).getBookPath());
						bd.putString("bookname",booklist.get(position).getBookName());
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
							bd.putString("filepath",booklist.get(position).getBookPath());
							bd.putString("bookname",booklist.get(position).getBookName());
							Intent it=new Intent();
							it.setClass(MainActivity.this, BookActivity.class);
							it.putExtras(bd);
							startActivity(it);

						}else{
							Toast.makeText(context, getString(R.string.file_not_exist), Toast.LENGTH_SHORT).show();
						}
					}
				}else{
					if(removebooklist.contains(booklist.get(position).getBookPath())){
						removebooklist.remove(booklist.get(position).getBookPath());
						Toast.makeText(context, "取消選取 :"+booklist.get(position).getBookName(),Toast.LENGTH_SHORT ).show();
					}else{
						removebooklist.add(booklist.get(position).getBookPath());
						Toast.makeText(context, "選取 :"+booklist.get(position).getBookName(),Toast.LENGTH_SHORT).show();
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
		fadapter = new FileAdapter(this);
		lv = (ListView)mMenu_left.getMenuView().findViewById(R.id.lv);
		bt_back = (ImageView)mMenu_left.getMenuView().findViewById(R.id.bt_back);
		tv = (TextView)mMenu_left.getMenuView().findViewById(R.id.textView1);
		lv.setAdapter(fadapter);
		lv.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position,
					long arg3) {
				// TODO Auto-generated method stub
				mfileChooser.analysisFile(fList.get(position));
			}
		});
		lv.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
					int position, long arg3) {
				// TODO Auto-generated method stub
				
				return false;
			}
		});
		mfileChooser = new FileManager();
		mfileChooser.setOnFileChangedListener(new FileEvent());
		bt_back.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				mfileChooser.Back();
			}
		});
		mfileChooser.init(Environment.getExternalStorageDirectory());
		//initAD();
	}
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		showAppMsg();
		new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub	

				booklist.clear();
				booklist.addAll(mBookData.getBookList(context));
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
	}
	@Override
	public void onDestroy() {
		if (adView != null) {
			adView.destroy();
		}
		super.onDestroy();
	}
	AdView adView;
//	private void initAD(){
//		adView = new AdView(this, AdSize.SMART_BANNER,"" ); 
//		adView.setAdListener(this);
//		adView.setGravity(Gravity.CENTER);
//		ad_area.addView(adView,new LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.WRAP_CONTENT));
//		AdRequest adrequest = new AdRequest();
//		adrequest.addTestDevice("3D2D6870257805D3");    
//		adView.loadAd(adrequest);
//	}
	private class FileEvent implements FileManager.FileChangedListener{

		@Override
		public void toDir(List<FileItem> dirList, List<FileItem> filelist,
				final String Path) {
			// TODO Auto-generated method stub
			fList.clear();
			fList.addAll(dirList);
			fadapter.notifyDataSetChanged();
			runOnUiThread(new Runnable() {

				@Override
				public void run() {
					// TODO Auto-generated method stub
					tv.setText(Path);
				}
			});
		}

		@Override
		public void selectFile(FileItem item) {
			// TODO Auto-generated method stub
			if(item.getName().endsWith(".txt")){
				Book mBook = new Book();
				mBook.setBookName(item.getName().substring(0,item.getName().lastIndexOf(".")));
				mBook.setBookPath(item.getPath());
				if(!checkbookIsExists(mBook)){
					Calendar c = Calendar.getInstance();
					mBookData.addBook(context,mBook,Unity.getCurDate(c),Unity.getCurTime(c));
					booklist.clear();
					booklist.addAll(mBookData.getBookList(context));
					mBookAdapter.notifyDataSetChanged();
					Toast.makeText(MainActivity.this, getString(R.string.addbook).replace("&s", item.getName()), Toast.LENGTH_SHORT).show();
				}
				else
					Toast.makeText(MainActivity.this,getString(R.string.added).replace("&s", item.getName()), Toast.LENGTH_SHORT).show();
			}else{
				Toast.makeText(MainActivity.this, getString(R.string.file_not_exist), Toast.LENGTH_SHORT).show();
			}
		}
		
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
			if(item.getBookPath().equals(booklist.get(i).getBookPath()))
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
	//刪除所有書本
	private void ShowClearAllBookData(){
		AlertDialog.Builder ab =new AlertDialog.Builder(this);
		ab.setTitle(R.string.alert_tip_Alldelete);
		ab.setNegativeButton(R.string.alert_ok, new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				mBookData.clearAllData(context);
				booklist.clear();
				mBookAdapter.notifyDataSetChanged();
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
    //選取刪除書本資料
	private void ShowTipDeleteBook(){
		AlertDialog.Builder ab =new AlertDialog.Builder(this);
		ab.setTitle(R.string.alert_tip_deleteSelect);
		ab.setNegativeButton(R.string.alert_ok, new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub

				if(removebooklist.size()>0)
				{
					for(int i=0;i<removebooklist.size();i++)
						mBookData.deleteBook(context,removebooklist.get(i));
					booklist.clear();
					booklist.addAll(mBookData.getBookList(context));	
					Toast.makeText(context, "刪除成功!", Toast.LENGTH_SHORT).show();
				}	
				mBookAdapter.setRemoveMode(false);
				mMenu_left.setTouchMode(MenuDrawer.TOUCH_MODE_FULLSCREEN);
				showAppMsg();
				iv_addbook.setImageResource(R.drawable.storage);
				bt_clearcache.setImageResource(R.drawable.garbage);
				mBookAdapter.notifyDataSetChanged();

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
	private class FileAdapter extends BaseAdapter{
		LayoutInflater inflater;  
		class viewholder{
			TextView tv;
			ImageView iv;
		}
		public FileAdapter(Context context){
			inflater = LayoutInflater.from(context);
		}
		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return fList.size();
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
		public View getView(int position, View v, ViewGroup arg2) {
			// TODO Auto-generated method stub
			viewholder vh;
			if(v == null){
				vh = new viewholder();
				v = inflater.inflate(R.layout.fileitem, null);
				vh.tv = (TextView)v.findViewById(R.id.textView1);
				vh.iv = (ImageView)v.findViewById(R.id.imageView1);
				v.setTag(vh);
			}else{
				vh = (viewholder) v.getTag();
			}
			vh.tv.setText(fList.get(position).getName());
			if(fList.get(position).getFileType().equals(FileManager.DIRECTORY)){
				vh.iv.setVisibility(View.VISIBLE);
			}else{
				vh.iv.setVisibility(View.GONE);
			}
			return v;
		}

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
				vh.select.setVisibility(removebooklist.contains(booklist.get(position).getBookPath())?View.VISIBLE:View.GONE);
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
	@Override
	public void onDismissScreen(Ad arg0) {
		// TODO Auto-generated method stub

	}
	@Override
	public void onFailedToReceiveAd(Ad arg0, ErrorCode arg1) {
		// TODO Auto-generated method stub
		Log.d("book", "failed to receive ad (" + arg1 + ")");
	}
	@Override
	public void onLeaveApplication(Ad arg0) {
		// TODO Auto-generated method stub

	}
	@Override
	public void onPresentScreen(Ad arg0) {
		// TODO Auto-generated method stub

	}
	@Override
	public void onReceiveAd(Ad arg0) {
		// TODO Auto-generated method stub

	}

}
