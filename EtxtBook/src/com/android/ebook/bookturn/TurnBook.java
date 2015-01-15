package com.android.ebook.bookturn;

import java.io.IOException;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.view.MotionEvent;
import android.view.View;



@SuppressLint("WrongCall")
public class TurnBook extends PageWidget implements View.OnTouchListener {
	Bitmap mCurPageBitmap, mNextPageBitmap;
	Canvas mCurPageCanvas, mNextPageCanvas;
	BookPageFactory pagefactory;
	Context parent;
	onBookChangeListener sonBookChangeListener;
	public void setOnBookChangeListener(onBookChangeListener sonBookChangeListener) {
		this.sonBookChangeListener = sonBookChangeListener;
	}
	public interface onBookChangeListener{
		   void onFirstIndex();
		   void onFinalIndex();
	}
	public TurnBook(Context context,int width,int height,int maginW,int maginh) {
		super(context);
		// TODO Auto-generated constructor stub
		parent = context;
		setInit(width, height, maginW, maginh);
	}
	public void setInit(int width,int height,int maginW,int maginh){
		if(width==0||height==0)
			return;
		setScreen(width, height);
		if(mCurPageBitmap!=null)
			if(!mCurPageBitmap.isRecycled())
				mCurPageBitmap.recycle();
		if(mNextPageBitmap!=null)
			if(!mNextPageBitmap.isRecycled())
				mNextPageBitmap.recycle();
		mCurPageBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
		mNextPageBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);

		mCurPageCanvas = new Canvas(mCurPageBitmap);
		mNextPageCanvas = new Canvas(mNextPageBitmap);
	   
		pagefactory = new BookPageFactory(width, height, maginW, maginh);
		
		pagefactory.onDraw(mCurPageCanvas);
		setBitmaps(mCurPageBitmap, mCurPageBitmap);
		setOnTouchListener(this);
	}
	public boolean prePage(boolean isShowAnim){	
		if(pagefactory.isfirstPage()){
			if(sonBookChangeListener!=null)
				sonBookChangeListener.onFirstIndex();
			return false;
		}
			abortAnimation();
			//是否從左邊翻向右邊
			try {
				//true，顯示上一頁					
				pagefactory.prePage();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}						
			if(pagefactory.isfirstPage()){
				if(sonBookChangeListener!=null)
					sonBookChangeListener.onFirstIndex();
				return false;
			}
			pagefactory.onDraw(mCurPageCanvas);
			setBitmaps(mCurPageBitmap, mCurPageBitmap);
			postInvalidate();
		
			  return true;
	}
	public boolean NextPage(boolean isShowAnim){	 		  
		if(pagefactory.islastPage()){
			if(sonBookChangeListener!=null)
				sonBookChangeListener.onFinalIndex();
			return false; 
		}
	
			abortAnimation();

			//是否從左邊翻向右邊
			try {
				//true，顯示上一頁					
				pagefactory.nextPage();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}						
			if(pagefactory.isfirstPage()){
				if(sonBookChangeListener!=null)
					sonBookChangeListener.onFirstIndex();
				return false;
			}
			pagefactory.onDraw(mCurPageCanvas);
			setBitmaps(mCurPageBitmap, mCurPageBitmap);
			postInvalidate();
		
		return true;
	}
   public float getProgress(){
	   if(pagefactory!=null)
		   return pagefactory.getfPercent();
	   return 0;
   }
   public void setProgress(float progress){
	   if(pagefactory!=null){
		   pagefactory.setPoregress(progress);
           pagefactory.getM_lines().clear(); 
           pagefactory.onDraw(mCurPageCanvas);
           setBitmaps(mCurPageBitmap, mCurPageBitmap);	
           postInvalidate();
	   }
   }
	/**書頁背景*/
   public void setBookBackgroundBitmap(Bitmap bmp){
	   if(pagefactory!=null){
		   pagefactory.setBgBitmap(bmp);
	   }
   }
   /**書頁背景*/
   public void setBookBackgroundColor(int color){
	   if(pagefactory!=null){
		   pagefactory.setM_backColor(color);
	   }
   }
   /**書本檔案*/
   public void setBookFile(String PathOrBookName,boolean isFromAsset){
	   if(pagefactory!=null)
	   {
		   if(isFromAsset)
		     pagefactory.openBook(parent, PathOrBookName);
		   else
			 pagefactory.openBook(PathOrBookName);	
	   }
   }
   /**插入書籤*/
   public void ToBookMarkPage(int beginIndex){
	   if(pagefactory!=null)
	   {
		   pagefactory.setM_mbBufEnd(beginIndex);  
           pagefactory.setM_mbBufBegin(beginIndex);  
           pagefactory.getM_lines().clear(); 
           pagefactory.onDraw(mCurPageCanvas);  
           setBitmaps(mCurPageBitmap, mCurPageBitmap);	
           postInvalidate();
	   }
   }
   /**設定自體大小*/
   public void setTextSize(int m_fontSize){
	   if(pagefactory!=null)
	   {
		   pagefactory.setM_fontSize(m_fontSize); 
           refreach();
	   }
   }
   /**取得當前字體size*/
   public float getTextSize(){
	   if(pagefactory!=null)
	   {
		   return   pagefactory.getM_fontSize();  
	   }
	   return 0;
   }
   /**設定自體顏色*/
   public void setTextColor(int m_textColor){
	   if(pagefactory!=null)
	   {
		   pagefactory.setM_textColor(m_textColor); 
          
	   }
   }
   public int getTextColor(){
	   if(pagefactory!=null)
	   {
		 return  pagefactory.getM_textColor();     
	   }
	   return 0;
   }
   public void setDecoding(String m_strCharsetName){
	   if(pagefactory!=null)
	   {
		   pagefactory.setM_strCharsetName(m_strCharsetName); 
		   refreach();
	   }
   }
   public BookPageFactory getBookPageFactory(){
	   return pagefactory;
   }
   /**重新整理*/
   public void refreach(){
	   if(pagefactory!=null)
	   {
	      int begin1 = pagefactory.getM_mbBufBegin();  
          pagefactory.setM_mbBufEnd(begin1);  
          pagefactory.getM_lines().clear();  
          pagefactory.onDraw(mCurPageCanvas); 
          pagefactory.onDraw(mNextPageCanvas);  
          postInvalidate();
	   }
   }
   public void recycle(){
	 if(mCurPageBitmap!=null)
		 if(!mCurPageBitmap.isRecycled())
		    mCurPageBitmap.recycle();
	 if(mNextPageBitmap!=null)
		 if(!mNextPageBitmap.isRecycled())
			 mNextPageBitmap.recycle();
		mCurPageCanvas = null;
		mNextPageCanvas = null;
		pagefactory  = null;
		System.gc();
   }

	@Override
	public boolean onTouch(View v, MotionEvent e) {
		// TODO Auto-generated method stub
		
		boolean ret=false;
		if (v == TurnBook.this) {
			if (e.getAction() == MotionEvent.ACTION_DOWN) {
				//停止動畫。與forceFinished(boolean)相反，Scroller滾動到最終x與y位置時中止動畫。			
				abortAnimation();
				//計算拖拽點對應的拖拽角
				calcCornerXY(e.getX(), e.getY());
                //將文字繪於當前頁
				pagefactory.onDraw(mCurPageCanvas);
				if (DragToRight()) {
					//是否從左邊翻向右邊
					try {
						//true，顯示上一頁					
						pagefactory.prePage();
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}						
					if(pagefactory.isfirstPage()){
						if(sonBookChangeListener!=null)
							sonBookChangeListener.onFirstIndex();
						return false;
					}
					pagefactory.onDraw(mNextPageCanvas);
					setBitmaps(mCurPageBitmap, mNextPageBitmap);

				} else {
					try {
						//false，顯示下一頁							
						pagefactory.nextPage();
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					if(pagefactory.islastPage()){
						if(sonBookChangeListener!=null)
							sonBookChangeListener.onFinalIndex();
						return false;
					}
					pagefactory.onDraw(mNextPageCanvas);
					setBitmaps(mCurPageBitmap, mNextPageBitmap);
				}
				
			}
         
			 ret = doTouchEvent(e);
			return ret;
		}
		return false;
	}


	
}