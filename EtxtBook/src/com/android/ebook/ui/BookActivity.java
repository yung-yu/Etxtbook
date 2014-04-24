package com.android.ebook.ui;

import java.text.DecimalFormat;
import java.util.Calendar;

import net.margaritov.preference.colorpicker.ColorPickerDialog;
import net.margaritov.preference.colorpicker.ColorPickerView;
import yuku.ambilwarna.AmbilWarnaDialog;

import com.android.ebook.R;
import com.android.ebook.R.array;
import com.android.ebook.R.dimen;
import com.android.ebook.R.id;
import com.android.ebook.R.layout;
import com.android.ebook.R.menu;
import com.android.ebook.R.string;
import com.android.ebook.data.BookData;
import com.android.ebook.data.BookMark;
import com.android.ebook.data.Unity;
import com.android.ebook.data.sharePerferenceHelper;
import com.android.mylibrary.bookturn.TurnBook;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageSize;


import android.R.color;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

public class BookActivity extends Activity{
    final String  BOOK_TEXT_SIZE= "BookTextSize";
    final String  BOOK_TEXT_COLOR= "BookTextCOLOR";
    final String  BOOK_BG_TYPE= "Bookbgtype";
    final String  BOOK_BG_PATH= "Bookbgpath";
    final String  BOOK_BG_COLOR= "Bookbgclor";
    private static int RESULT_LOAD_IMAGE = 99999;
    LinearLayout Book;
	Context parent;
	String filePath;
	TurnBook mTurnBook;
	DecimalFormat df;
	BookData mBookData;
    int encode ;
	String[] decode_array ;
	BookMark mBookMark;
	float percent = 0f;
	DisplayMetrics dm ;
	Bitmap myBitmap;
	ImageLoader mImageLoader;
	boolean isTable=false;
    String bookname;
    Toast mToast;
    View DecorView;
 	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
	    isTable = Unity.isTablet(this);
		if(isTable){
			requestWindowFeature(Window.FEATURE_ACTION_BAR);
		}else{
			requestWindowFeature(Window.FEATURE_NO_TITLE);
		}
		parent = this;
	    dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        Bundle bd = getIntent().getExtras();
	    filePath = bd.getString("filepath");
	    bookname = bd.getString("bookname");
        initImageLoader();
        mBookData = new BookData(this);
        decode_array = parent.getResources().getStringArray(R.array.decoding_value);	    
	    int magin = (int) getResources().getDimension(R.dimen.bookPage_magin);
	    mTurnBook = new TurnBook(this, dm.widthPixels, dm.heightPixels,magin,magin);	
	    setContentView(mTurnBook);
	    mTurnBook.setVisibility(View.INVISIBLE);
	    new Thread(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				initBook();
				if(mBookMark!=null)
					mTurnBook.ToBookMarkPage(mBookMark.getBegin());	
				else
					mTurnBook.refreach();
				runOnUiThread(new Runnable() {
					
					@Override
					public void run() {
						// TODO Auto-generated method stub
						mTurnBook.setVisibility(View.VISIBLE);

					}
				});  
			}
		}).start();
	
	}
    @Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
	
	}
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		addBookTag();
	}
	
	
	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
	}
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		if(myBitmap != null)
			if(!myBitmap.isRecycled())
				myBitmap.recycle();
		if(mTurnBook != null)
			mTurnBook.recycle();
	}
	
	public void initBook(){
		setBookBg();
		mTurnBook.setBookFile(filePath, false);
		if(isTable){
			mTurnBook.getBookPageFactory().setDelay_lineCount(3);
		}
		mTurnBook.setOnBookChangeListener(new TurnBook.onBookChangeListener() {
			
			@Override
			public void onFirstIndex() {
				// TODO Auto-generated method stub			   
			   Toast.makeText(parent, getString(R.string.book_start), Toast.LENGTH_SHORT).show();

			}
			
			@Override
			public void onFinalIndex() {
				// TODO Auto-generated method stub
				Toast.makeText(parent, getString(R.string.book_end), Toast.LENGTH_SHORT).show();
			}
			
			@Override
			public void onBookChange(final String Prcent) {
				// TODO Auto-generated method stub
					runOnUiThread(new Runnable() {
						
						@Override
						public void run() {
							// TODO Auto-generated method stub
							setTitle(Prcent +"  "+bookname);
						}
					});
			}
		});
		mTurnBook.getBookPageFactory().setIsShowMsg(true);
		mTurnBook.getBookPageFactory().setBookName(bookname);
		mTurnBook.getBookPageFactory().setM_fontSize_forMsg(getResources().getDimension(R.dimen.txt_msg_textsize));
		mTurnBook.setTextSize(sharePerferenceHelper.getIntent(this).getInt(BOOK_TEXT_SIZE, 30));
		mTurnBook.setTextColor(sharePerferenceHelper.getIntent(this).getInt(BOOK_TEXT_COLOR, Color.BLACK));
		encode = mBookData.getBookEncode(parent,filePath);
		mBookMark = mBookData.getBookMark(parent,filePath);
		if(mBookMark != null)
			setBookPercent(mBookMark.getPercent());
		mTurnBook.setDecoding(decode_array[encode]);
	}
	public void initImageLoader(){
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(this)
        .discCacheFileCount(1)
        .discCacheSize(1024*1024*2)
        .build();
        mImageLoader=ImageLoader.getInstance();
        mImageLoader.init(config);
       
	}
	public void setBookPercent(float percent){
		this.percent = percent;
	}
	public float getBookPercent(){
		return percent;
	}
	public void setBookBg(){
		if(mTurnBook == null)
			return;
		int type = sharePerferenceHelper.getIntent(this).getInt(BOOK_BG_TYPE, 0);
		switch (type) {
		case 0:
			mTurnBook.setBookBackgroundBitmap(null);
			mTurnBook.getBookPageFactory().setM_backColor(getResources().getColor(R.color.bg));
			break;
		case 1:
			int bg_color = sharePerferenceHelper.getIntent(this).getInt(BOOK_BG_COLOR,color.white);
			mTurnBook.setBookBackgroundBitmap(null);
			mTurnBook.getBookPageFactory().setM_backColor(bg_color);
			break;
		case 2:
			Uri myUri = Uri.parse( sharePerferenceHelper.getIntent(this).getString(BOOK_BG_PATH, ""));
			try{
				Bitmap tmpBitmap = mImageLoader.loadImageSync(myUri.toString(),new ImageSize( dm.widthPixels, dm.heightPixels));
				myBitmap = Bitmap.createScaledBitmap(tmpBitmap,  dm.widthPixels, dm.heightPixels, true);  
				mTurnBook.setBookBackgroundBitmap(myBitmap);
				tmpBitmap.recycle();
				tmpBitmap = null;
			}catch(Exception e){
				e.printStackTrace();
				mTurnBook.setBookBackgroundBitmap(null);
			}
			break;
		default:
			break;
		}

	}
	public void addBookTag(){
		int newbegin = mTurnBook.getBookPageFactory().getM_mbBufBegin();
		if(mBookMark == null)
		{
			mBookMark = new BookMark();
			mBookMark.setBegin(newbegin);
			String content = "";
			for(String s:mTurnBook.getBookPageFactory().getM_lines())
				content += s;
			mBookMark.setContent(content);
			mBookMark.setPercent(mTurnBook.getBookPageFactory().getfPercent());
			Calendar c = Calendar.getInstance();
			mBookMark.setUpdate_date(Unity.getCurDate(c));
			mBookMark.setUpdate_time(Unity.getCurTime(c));
			mBookData.addBookTag(parent,mBookMark,filePath);
			return;
		}
		else if(mBookMark.getBegin() != newbegin)
		{
		    mBookMark = new BookMark();
			mBookMark.setBegin(newbegin);
			String content = "";
			for(String s:mTurnBook.getBookPageFactory().getM_lines())
				content += s;
			mBookMark.setContent(content);
			mBookMark.setPercent(mTurnBook.getBookPageFactory().getfPercent());
			Calendar c = Calendar.getInstance();
			mBookMark.setUpdate_date(Unity.getCurDate(c));
			mBookMark.setUpdate_time(Unity.getCurTime(c));
			mBookData.addBookTag(parent,mBookMark,filePath);
		}
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		 // 有選擇檔案
		if(resultCode == RESULT_OK)
		{
		    if ( requestCode == RESULT_LOAD_IMAGE )
		    {
		        // 取得檔案的 Uri
		        Uri uri = data.getData();
		       
		        if( uri != null )
		        { 
		        	sharePerferenceHelper.getIntent(parent).setInt(BOOK_BG_TYPE,2);
		        	sharePerferenceHelper.getIntent(parent).setString(BOOK_BG_PATH,uri.toString());
					setBookBg();
					mTurnBook.refreach();
		        }
		        else
		        {
		           Toast.makeText(parent, getString(R.string.file_not_exist), Toast.LENGTH_SHORT).show();
		        }
		    }
		}
	}
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		switch (item.getItemId()) {
		case R.id.item1:
			showDecodeDialog();
			break;
		case R.id.item2:
			showBookPercentDilog();
			break;
		case R.id.item3:
			showDefineTextSizeDilog();
			break;
		case R.id.item4:
			showTextColorDialog();
			break;
		case R.id.item5:
			showChangeBgDialog();
			break;
		default:
			break;
		}
		return super.onOptionsItemSelected(item);
	}
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		return super.onContextItemSelected(item);
	}
	@Override
	public void onContextMenuClosed(Menu menu) {
		// TODO Auto-generated method stub
		super.onContextMenuClosed(menu);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
	    MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.book, menu);
	    return true;
	}
	
	boolean isclick1 = false;
	boolean isclick2 = false;
  @Override
	public boolean dispatchKeyEvent(KeyEvent event) {
		// TODO Auto-generated method stub
	  if(event.getKeyCode() == KeyEvent.KEYCODE_VOLUME_DOWN)
	  {  
		  if(isclick1)
		  {   	 
			  isclick1 =false;
			  mTurnBook.NextPage();
		  }else{
			  isclick1 = true;
		  }
		  return true;
	  }
	  else if(event.getKeyCode() == KeyEvent.KEYCODE_VOLUME_UP)
	  { 
		  if(isclick2)
		  {   
			  isclick2 =false;
			  mTurnBook.prePage();
		  }else{
			  isclick2 = true;
		  }
		  return true;
	  }
		return super.dispatchKeyEvent(event);
	}

   public void showChangeBgDialog(){
		int type = sharePerferenceHelper.getIntent(this).getInt(BOOK_BG_TYPE, 0);
	   AlertDialog.Builder ab = new AlertDialog.Builder( this);
	   ab.setTitle(R.string.alert_title_pgbg);
		  ab.setSingleChoiceItems(getResources().getStringArray(R.array.book_bg_select), type, new DialogInterface.OnClickListener() {

			  @Override
			  public void onClick(DialogInterface dialog, int which) {
				  // TODO Auto-generated method stub
				  switch (which) {
				  case 0:
					  sharePerferenceHelper.getIntent(parent).setInt(BOOK_BG_TYPE,0);
					  setBookBg();
					  mTurnBook.refreach();
					  break;
				  case 1:
					  showbookbgColorDialog();
					  break;
				  case 2:
					  Intent intent = new Intent( Intent.ACTION_PICK );
			          intent.setType( "image/*" );
			          Intent destIntent = Intent.createChooser( intent, "選擇檔案" );
			          startActivityForResult( destIntent,RESULT_LOAD_IMAGE );
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
  public void showDecodeDialog(){
	  AlertDialog.Builder ab = new AlertDialog.Builder( this);
	  ab.setTitle(R.string.alert_title_encode);
	  ab.setSingleChoiceItems(getResources().getStringArray(R.array.decoding), encode, new DialogInterface.OnClickListener() {

		  @Override
		  public void onClick(DialogInterface dialog, int which) {
			  // TODO Auto-generated method stub
              mBookData.updateBookEncode(parent,filePath, which);
              mTurnBook.setDecoding(decode_array[which]);
              encode = which;
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
  public void showbookbgColorDialog(){
	  
	  int color = sharePerferenceHelper.getIntent(parent).getInt(BOOK_BG_COLOR,Color.WHITE);
	  ColorPickerDialog mColorPickerDialog = new ColorPickerDialog(this,color);
	  mColorPickerDialog.setTitle(R.string.alert_title_pgbgcolor);
	  mColorPickerDialog.setOnColorChangedListener(new ColorPickerDialog.OnColorChangedListener() {

		  @Override
		  public void onColorChanged(int color) {
			  // TODO Auto-generated method stub
				 sharePerferenceHelper.getIntent(parent).setInt(BOOK_BG_TYPE,1);
				 sharePerferenceHelper.getIntent(parent).setInt(BOOK_BG_COLOR,color);
				  setBookBg();
				  mTurnBook.refreach();
		  }
	  });
	  mColorPickerDialog.setAlphaSliderVisible(true);
	  mColorPickerDialog.show();
  }
  public void showTextColorDialog(){
	  ColorPickerDialog mColorPickerDialog = new ColorPickerDialog(this,mTurnBook.getTextColor());
	  mColorPickerDialog.setTitle(R.string.alert_title_txtcolor);
	  mColorPickerDialog.setOnColorChangedListener(new ColorPickerDialog.OnColorChangedListener() {

		  @Override
		  public void onColorChanged(int color) {
			  // TODO Auto-generated method stub
				mTurnBook.setTextColor(color);
				mTurnBook.refreach();
				sharePerferenceHelper.getIntent(parent).setInt(BOOK_TEXT_COLOR, color);
		  }
	  });
	  mColorPickerDialog.setAlphaSliderVisible(true);
	  mColorPickerDialog.show();
  }
  public void showBookPercentDilog(){
	  LayoutInflater inflater=(LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	  View vi=inflater.inflate(R.layout.booktextsizecontrol, null);
	  final SeekBar mSeekBar =(SeekBar)vi.findViewById(R.id.sb);
	  final TextView et1 = (TextView)vi.findViewById(R.id.et1);
	  AlertDialog.Builder ab = new AlertDialog.Builder(this);
	  ab.setTitle(R.string.alert_title_txtsize);
	  mSeekBar.setMax(9999);
	  final DecimalFormat df = new DecimalFormat("00.00"); 
	  float orignize_Percent = getBookPercent();
	  String   valStr = df.format(getBookPercent());
	  et1.setText(valStr);
	  mSeekBar.setProgress((int) (getBookPercent()*100));
	  mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

		  @Override
		  public void onStopTrackingTouch(SeekBar seekBar) {
			  // TODO Auto-generated method stub

		  }

		  @Override
		  public void onStartTrackingTouch(SeekBar seekBar) {
			  // TODO Auto-generated method stub

		  }

		  @Override
		  public void onProgressChanged(SeekBar seekBar, int progress,
				  boolean fromUser) {
			  // TODO Auto-generated method stub
			  setBookPercent(Float.valueOf(progress)/100f);
			  String valStr = df.format(getBookPercent());
			  et1.setText(valStr);
		

		  }
	  });

	  ab.setView(vi);
	  ab.setNegativeButton(R.string.alert_ok, new DialogInterface.OnClickListener() {

		  @Override
		  public void onClick(DialogInterface dialog,int which) {
			  // TODO Auto-generated method stub	  
			  mTurnBook.setPercentToPage(getBookPercent());
			  dialog.cancel();
		  }
	  });
	  ab.setPositiveButton(R.string.alert_cancel, new DialogInterface.OnClickListener(){

		  @Override
		  public void onClick(DialogInterface dialog, int which) {
			  // TODO Auto-generated method stub
			  dialog.cancel();
		  }

	  });
	  AlertDialog ad = ab.create();
	  ad.show();
  }
  public void showDefineTextSizeDilog(){
		LayoutInflater inflater=(LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View vi=inflater.inflate(R.layout.booktextsizecontrol, null);
        final SeekBar mSeekBar =(SeekBar)vi.findViewById(R.id.sb);
        final TextView et1 = (TextView)vi.findViewById(R.id.et1);
        AlertDialog.Builder ab = new AlertDialog.Builder(this);
        ab.setTitle(R.string.alert_title_txtsize);
		mSeekBar.setMax(7999);
		int  textsize = (int) (mTurnBook.getTextSize()*100);
		final int init_value = 2000;
		final DecimalFormat df = new DecimalFormat("00.00"); 
		String valStr = df.format(textsize/100f);
		et1.setText(valStr);
		mSeekBar.setProgress(textsize-init_value);
		mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
			
			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
				// TODO Auto-generated method stub
				
				String valStr = df.format((progress+init_value)/100f);
				et1.setText(valStr);

			}
		});
	
		ab.setView(vi);
		ab.setNegativeButton(R.string.alert_ok, new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog,int which) {
				// TODO Auto-generated method stub
				int textsize = (int) (Float.valueOf(mSeekBar.getProgress()+init_value)/100f);
				mTurnBook.setTextSize(textsize);
				sharePerferenceHelper.getIntent(parent).setInt(BOOK_TEXT_SIZE, textsize);
				dialog.cancel();
			}
		});
		ab.setPositiveButton(R.string.alert_cancel, new DialogInterface.OnClickListener(){

			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				dialog.cancel();
			}
			
		});
		AlertDialog ad = ab.create();
		ad.show();
	}
}
