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
import com.android.mylibrary.filebrowser.fileChooser;
import net.simonvt.menudrawer.MenuDrawer;
import net.simonvt.menudrawer.Position;
import android.os.Bundle;
import android.os.Environment;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {
    MenuDrawer mMenuDrawer;
    FileAdapter fadapter;
    ListView lv;
    fileChooser mfileChooser;
    List<FileItem> fList =new ArrayList<FileItem>();
    TextView tv;
    Button button1;
    ImageView bt_back,bt_clearcache;
    LinearLayout book_area;
    List<Book> booklist = new ArrayList<Book>();
    View desk_view;
    GridView gv_desk;
    BookAdapter mBookAdapter;
    ImageView iv_addbook;
    BookData mBookData;
    Context context;
    TextView tv_Msg;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		mBookData = new BookData(this);
		context = this;
		mMenuDrawer = MenuDrawer.attach(this, MenuDrawer.MENU_DRAG_WINDOW,Position.LEFT);
		mMenuDrawer.setContentView( R.layout.activity_main);
		mMenuDrawer.setMenuView(R.layout.filelist);	
		gv_desk = (GridView)findViewById(R.id.desk);
		iv_addbook = (ImageView)findViewById(R.id.imageButton1);
		bt_clearcache = (ImageView)findViewById(R.id.imageButton2);
		tv_Msg = (TextView)findViewById(R.id.tv_msg);
	    tv_Msg.setText(getString(R.string.easy_read));
		mBookAdapter = new BookAdapter(this);
		gv_desk.setAdapter(mBookAdapter);
		gv_desk.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, final int position,
					long arg3) {
				// TODO Auto-generated method stub
				String filePath =booklist.get(position).getBookPath();
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
			
		}
        );
		gv_desk.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
					int position, long arg3) {
				// TODO Auto-generated method stub
				if(position<booklist.size()){
				   ShowTipDeleteBook(booklist.get(position).getBookPath());
				}
				return false;
			}
		});
		iv_addbook.setOnClickListener(new View.OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
			
				mMenuDrawer.openMenu();
			}
			
		});
		bt_clearcache.setOnClickListener(new View.OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
			
				showclearDialog();
			}
			
		});
		fadapter = new FileAdapter(this);
		lv = (ListView)mMenuDrawer.getMenuView().findViewById(R.id.lv);
		bt_back = (ImageView)mMenuDrawer.getMenuView().findViewById(R.id.bt_back);
		tv = (TextView)mMenuDrawer.getMenuView().findViewById(R.id.textView1);
		lv.setAdapter(fadapter);
		lv.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position,
					long arg3) {
				// TODO Auto-generated method stub
				mfileChooser.AnalysisFile(fList.get(position));
			}
		});
		mfileChooser = new fileChooser();
		mfileChooser.setOnFileChangedListener(new fileChooser.FileChangedListener() {
			
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
		});
	
		bt_back.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				mfileChooser.Back();
			}
		});
		  mfileChooser.init(Environment.getExternalStorageDirectory());
	}
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
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
   public boolean checkbookIsExists(Book item){
	for(int i=0;i<booklist.size();i++)
	{
		if(item.getBookPath().equals(booklist.get(i).getBookPath())){
			return true;
		}
	}
	return false;
}
   public void showclearDialog(){
		  AlertDialog.Builder ab = new AlertDialog.Builder( this);
		  ab.setTitle(R.string.alert_title_clear);
		  ab.setSingleChoiceItems(getResources().getStringArray(R.array.clear_select), 0, new DialogInterface.OnClickListener() {

			  @Override
			  public void onClick(DialogInterface dialog, int which) {
				  // TODO Auto-generated method stub
				  switch (which) {
				  case 0:
					  ShowClearStatus();
					  break;
				  case 1:
					  ShowCleatBookData();
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
   private void ShowCleatBookData(){
		  AlertDialog.Builder ab =new AlertDialog.Builder(this);
		  ab.setTitle(R.string.alert_tip_delete);
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
   private void ShowClearStatus(){
		  AlertDialog.Builder ab =new AlertDialog.Builder(this);
		  ab.setTitle(R.string.alert_tip_delete);
		  ab.setNegativeButton(R.string.alert_ok, new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
			    sharePerferenceHelper.getIntent(context).clear();
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
  private void ShowTipDeleteBook(final String Path){
	  AlertDialog.Builder ab =new AlertDialog.Builder(this);
	  ab.setTitle(R.string.alert_tip_delete);
	  ab.setNegativeButton(R.string.alert_ok, new DialogInterface.OnClickListener() {
		
		@Override
		public void onClick(DialogInterface dialog, int which) {
			// TODO Auto-generated method stub
			  mBookData.deleteBook(context,Path);
			  booklist.clear();
			  booklist.addAll(mBookData.getBookList(context));
	
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
		if(fList.get(position).getFileType().equals(fileChooser.DIRECTORY)){
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
			v.setTag(vh);
		}else{
			vh = (viewholder) v.getTag();
		}
		if(position<booklist.size())
			vh.tv.setText(booklist.get(position).getBookName());
		v.setOnTouchListener(new View.OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				runOnUiThread( new Runnable() {
					public void run() {
						if(position<booklist.size())
							tv_Msg.setText(booklist.get(position).getBookName());
					}
				});
				return false;
			}
		});
		return v;
	}
	   
   }
@Override
public boolean onKeyDown(int keyCode, KeyEvent event) {
	// TODO Auto-generated method stub
	if(event.getKeyCode() == KeyEvent.KEYCODE_BACK){
		if(mMenuDrawer.isMenuVisible()){
			mMenuDrawer.closeMenu();
			return false;
		}
	}
	return super.onKeyDown(keyCode, event);
}

}
