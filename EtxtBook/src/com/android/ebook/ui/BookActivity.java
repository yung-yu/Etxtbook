package com.android.ebook.ui;

import java.lang.reflect.Method;
import java.text.DecimalFormat;
import java.util.Calendar;

import org.apache.http.protocol.HTTP;

import net.margaritov.preference.colorpicker.ColorPickerDialog;

import com.android.ebook.R;
import com.android.ebook.bookturn.TurnBook;
import com.android.ebook.data.BookData;
import com.android.ebook.data.BookMark;
import com.android.ebook.data.Unity;
import com.android.ebook.data.sharePerferenceHelper;
import com.android.ebook.unit.CustomToast;
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
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.Window;
import android.view.WindowManager;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;


public class BookActivity extends Activity{
	
	private final String  BOOK_TEXT_SIZE= "BookTextSize";
	private final String  BOOK_TEXT_COLOR= "BookTextCOLOR";
	private final String  BOOK_BG_TYPE= "Bookbgtype";
	private final String  BOOK_BG_PATH= "Bookbgpath";
	private final String  BOOK_BG_COLOR= "Bookbgclor";
	private final String  BOOK_SCREEN= "BookScreen";
	private static int RESULT_LOAD_IMAGE = 99999;
	private Context parent;
	private String filePath;
	private TurnBook mTurnBook;
	private BookData mBookData;
	private int encode ;
	private String[] decode_array ;
	private BookMark mBookMark;
	private DisplayMetrics dm ;
	private Bitmap myBitmap;
	private ImageLoader mImageLoader;
	private boolean isTable=false;
	private String bookName;
	private int screenType;
	private Context context;
	private int bookId;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		isTable = Unity.isTablet(this);
		context = this;
		screenType = sharePerferenceHelper.getIntent(this).getInt(BOOK_SCREEN, 0);
		if(screenType!=0)
		{ 
			getWindow().setFlags( WindowManager.LayoutParams.FLAG_FULLSCREEN, 
					WindowManager.LayoutParams.FLAG_FULLSCREEN );
		}
		boolean ishasNavBar = hasNavBar(this);
        if(isTable||ishasNavBar){
        		requestWindowFeature(Window.FEATURE_ACTION_BAR);
        }else{
        		requestWindowFeature(Window.FEATURE_NO_TITLE);
        }


		parent = this;
		dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		Bundle bd = getIntent().getExtras();
		filePath = bd.getString("filepath");
		bookName = bd.getString("bookname");
		bookId = bd.getInt("bookid");
		initImageLoader();
		mBookData = new BookData(this);
		decode_array = parent.getResources().getStringArray(R.array.decoding_value);	    
		int magin = (int) getResources().getDimension(R.dimen.bookPage_magin); 
	    int width  = dm.widthPixels;
	    int height = dm.heightPixels;
	    height = screenType==0? height - getStatusBarHeight():height;
	    height = ishasNavBar? height - getnavigation_bar_height():height;
	    
		mTurnBook = new TurnBook(this,width,height,magin,magin);	
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
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		if(myBitmap != null)
			if(!myBitmap.isRecycled())
				myBitmap.recycle();
		if(mTurnBook != null)
			mTurnBook.recycle();
	}
	private int getnavigation_bar_height(){
		Resources resources = context.getResources();
		int resourceId = resources.getIdentifier("navigation_bar_height", "dimen", "android");
		if (resourceId > 0) {
		    return resources.getDimensionPixelSize(resourceId);
		}
		return 0;

	}
	@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
	private boolean hasNavBar(Context context) {
		Resources res = context.getResources();
		int resourceId = res.getIdentifier("config_showNavigationBar", 
				"bool","android");
		if (resourceId != 0) {
			boolean hasNav = res.getBoolean(resourceId);
			String sNavBarOverride = null;
			if (Build.VERSION.SDK_INT >Build.VERSION_CODES.JELLY_BEAN_MR2) {
				try {
					Class c = Class.forName("android.os.SystemProperties");
					Method m = c.getDeclaredMethod("get", String.class);
					m.setAccessible(true);
					sNavBarOverride = (String) m.invoke(null,"qemu.hw.mainkeys");
				} catch (Throwable e) {
					sNavBarOverride = null;
				}
			}

			// check override flag (see static block)
			if ("1".equals(sNavBarOverride)) {
				hasNav = false;
			} else if ("0".equals(sNavBarOverride)) {
				hasNav = true;
			}
			return hasNav;
		} else { // fallback
			return !ViewConfiguration.get(context).hasPermanentMenuKey();
		}
	}

	/**��o�W�h�T���C����*/
	private  int getStatusBarHeight() { 
		int result = 0;
		int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
		if (resourceId > 0) {
			result = getResources().getDimensionPixelSize(resourceId);
		} 
		return result;
	} 
	/**��l��*/ 
	private  void initBook(){
		setBookBg();
		if(filePath.startsWith(BookData.ASSATS_PATH))
			mTurnBook.setBookFile(filePath.substring(BookData.ASSATS_PATH.length()), true);
		else
			mTurnBook.setBookFile(filePath, false);
		if(isTable){
			//mTurnBook.getBookPageFactory().setDelay_lineCount(3);
		}
		mTurnBook.setOnBookChangeListener(new TurnBook.onBookChangeListener() {

			@Override
			public void onFirstIndex() {
				// TODO Auto-generated method stub			   
				CustomToast.CreateToast(parent, getString(R.string.book_start), Toast.LENGTH_SHORT);

			}

			@Override
			public void onFinalIndex() {
				// TODO Auto-generated method stub
				CustomToast.CreateToast(parent, getString(R.string.book_end), Toast.LENGTH_SHORT);
			}
		});
		mTurnBook.getBookPageFactory().setBookName(bookName);
		mTurnBook.getBookPageFactory().setM_fontSize_forMsg(getResources().getDimension(R.dimen.txt_msg_textsize));
		mTurnBook.setTextSize(sharePerferenceHelper.getIntent(this).getInt(BOOK_TEXT_SIZE, 30));
		mTurnBook.setTextColor(sharePerferenceHelper.getIntent(this).getInt(BOOK_TEXT_COLOR, Color.WHITE));
		encode = mBookData.getBookEncode(parent,bookId);
		mBookMark = mBookData.getBookMark(parent,bookId);
		mTurnBook.setDecoding(decode_array[encode]);
	}
	/**��l�ƹϤ��J�]�w*/
	private  void initImageLoader(){
		ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(this)
		.discCacheFileCount(1)
		.discCacheSize(1024*1024*2)
		.build();
		mImageLoader=ImageLoader.getInstance();
		mImageLoader.init(config);

	}
	/**�]�w�ѥ��I��*/
	private  void setBookBg(){
		if(mTurnBook == null)
			return;
		int type = sharePerferenceHelper.getIntent(this).getInt(BOOK_BG_TYPE, 0);
		switch (type) {
		case 0:
			mTurnBook.setBookBackgroundBitmap(null);
			mTurnBook.setBookBackgroundColor(Color.BLACK);
			break;
		case 1:
			int bg_color = sharePerferenceHelper.getIntent(this).getInt(BOOK_BG_COLOR,color.white);
			mTurnBook.setBookBackgroundBitmap(null);
			mTurnBook.setBookBackgroundColor(bg_color);
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
	/**�s�W����*/
	private  void addBookTag(){
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
			mBookData.addBookTag(parent,bookId,mBookMark);
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
			mBookData.addBookTag(parent,bookId,mBookMark);
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		// ������ɮ�
		if(resultCode == RESULT_OK)
		{
			if ( requestCode == RESULT_LOAD_IMAGE )
			{
				// ��o�ɮת� Uri
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
			showDefineTextSizeDilog();
			break;
		case R.id.item3:
			showTextColorDialog();
			break;		
		case R.id.item4:
			showScreenDilog();
			break;
		case R.id.item5:
			showChangeBgDialog();
			break;
		case R.id.item6:
			showProgressDialog();
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

	private boolean isclick1 = false;
	private boolean isclick2 = false;
	@Override
	public boolean dispatchKeyEvent(KeyEvent event) {
		// TODO Auto-generated method stub
		if(event.getKeyCode() == KeyEvent.KEYCODE_VOLUME_DOWN)
		{  
			if(isclick1)
			{   	 
				isclick1 =false;
				mTurnBook.NextPage(true);
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
				mTurnBook.prePage(true);
			}else{
				isclick2 = true;
			}
			return true;
		}
		return super.dispatchKeyEvent(event);
	}
	/**�ù��]�wDialog*/
	private  void showScreenDilog(){
		int type = sharePerferenceHelper.getIntent(this).getInt(BOOK_SCREEN, 0);
		AlertDialog.Builder ab = new AlertDialog.Builder( this);
		ab.setTitle(R.string.alert_title_pgbg);
		ab.setSingleChoiceItems(getResources().getStringArray(R.array.screen_select), type, new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				if(screenType!=which)
				{
					sharePerferenceHelper.getIntent(parent).setInt(BOOK_SCREEN, which);
					Toast.makeText(parent, R.string.alert_screen_tip,  Toast.LENGTH_SHORT).show();
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
	/**�]�w�I��dialog*/
	private  void showChangeBgDialog(){
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
					Intent destIntent = Intent.createChooser( intent, "����ɮ�" );
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
	/**��ܽs�Xdialog*/
	private  void showProgressDialog(){
		AlertDialog.Builder ab = new AlertDialog.Builder( this);
		ab.setTitle(R.string.alert_title_progress);
		View view =LayoutInflater.from(this).inflate(R.layout.book_progress, null);
		SeekBar mSeekBar = (SeekBar)view.findViewById(R.id.seekBar1);
		final TextView mMsg = (TextView)view.findViewById(R.id.textView1);
		mSeekBar.setMax(10000);
		int progress = (int) (mTurnBook.getProgress()*10000);
		mSeekBar.setProgress(progress);
		float tmp = Float.valueOf(progress)/100f;
		mMsg.setText(getResources().getString(R.string.book_progress)+" : "+new DecimalFormat("00.00").format(tmp));
		ab.setView(view);
		mSeekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

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
				float tmp = Float.valueOf(progress)/100f;
				mTurnBook.setProgress(tmp);
				mMsg.setText(getResources().getString(R.string.book_progress)+" : "+new DecimalFormat("00.00").format(tmp));
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
	/**��ܽs�Xdialog*/
	private  void showDecodeDialog(){
		AlertDialog.Builder ab = new AlertDialog.Builder( this);
		ab.setTitle(R.string.alert_title_encode);
		ab.setSingleChoiceItems(getResources().getStringArray(R.array.decoding), encode, new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				mBookData.updateBookEncode(parent,bookId, which);
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
	/**�]�w�I���C��dialog */
	private  void showbookbgColorDialog(){

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
	/**�]�w��r�C��dialog*/
	private  void showTextColorDialog(){
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
	/**�]�w��r�j�pdialog*/
	private void showDefineTextSizeDilog(){
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
	private void showClearStatus(){
		AlertDialog.Builder ab =new AlertDialog.Builder(this);
		ab.setTitle(R.string.alert_tip_deleteStatus);
		ab.setNegativeButton(R.string.alert_ok, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				sharePerferenceHelper.getIntent(context).clear();
				initBook();
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
}
