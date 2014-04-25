package com.android.mylibrary.bookturn;

import java.io.IOException;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;


@SuppressLint("WrongCall")
public class TurnBook extends PageWidget{
	Bitmap mCurPageBitmap, mNextPageBitmap;
	Canvas mCurPageCanvas, mNextPageCanvas;
	BookPageFactory pagefactory;
	Context parent;
	onBookChangeListener sonBookChangeListener;
	public void setOnBookChangeListener(onBookChangeListener sonBookChangeListener) {
		this.sonBookChangeListener = sonBookChangeListener;
	}
	public interface onBookChangeListener{
		   void onBookChange(String Prcent);
		   void onFirstIndex();
		   void onFinalIndex();
	}
	public TurnBook(Context context,int width,int height,int maginW,int maginh) {
		super(context);
		// TODO Auto-generated constructor stub
		parent = context;
		setScreen(width, height);
		mCurPageBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
		mNextPageBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);

		mCurPageCanvas = new Canvas(mCurPageBitmap);
		mNextPageCanvas = new Canvas(mNextPageBitmap);
	   
		pagefactory = new BookPageFactory(width, height, maginW, maginh);
		pagefactory.onDraw(mCurPageCanvas);
		setBitmaps(mCurPageBitmap, mCurPageBitmap);
		setOnTouchListener(new OnTouchListener() {
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
							if(sonBookChangeListener!=null)
								sonBookChangeListener.onBookChange( pagefactory.getStrPercent());
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
							if(sonBookChangeListener!=null)
								sonBookChangeListener.onBookChange( pagefactory.getStrPercent());
						}
						setBitmaps(mCurPageBitmap, mNextPageBitmap);
					}
                 
					 ret = doTouchEvent(e);
					return ret;
				}
				return false;
			}

		});
	}
	public boolean prePage(){	
		if(pagefactory.isfirstPage()){
			if(sonBookChangeListener!=null)
				sonBookChangeListener.onFirstIndex();
			return false;
		}
			//是否從左邊翻向右邊		
			try {
				//true，顯示上一頁					
				pagefactory.prePage();
				
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}						
			pagefactory.onDraw(mCurPageCanvas);
			setBitmaps(mCurPageBitmap, mCurPageBitmap);
			  postInvalidate();
			  if(sonBookChangeListener!=null)
					sonBookChangeListener.onBookChange( pagefactory.getStrPercent());
			  return true;
	}
	public boolean NextPage(){	 		  
		if(pagefactory.islastPage()){
			if(sonBookChangeListener!=null)
				sonBookChangeListener.onFinalIndex();
			return false; 
		}
		try {
			//false，顯示下一頁							
			pagefactory.nextPage();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		pagefactory.onDraw(mCurPageCanvas);
		setBitmaps(mCurPageBitmap, mCurPageBitmap);	
		postInvalidate();
		if(sonBookChangeListener!=null)
			sonBookChangeListener.onBookChange( pagefactory.getStrPercent());
		return true;
	}
	/**
	 * %數轉換成小數再帶入
	 * @param Percent
	 */
	public void setPercentToPage(float Percent){
           Log.d("book","Percent :"+Percent);
  
            float beginPercent  = pagefactory.getM_mbBufBegin()/pagefactory.getM_mbBufLen();
            float endPercent  = pagefactory.getM_mbBufEnd()/pagefactory.getM_mbBufLen();
            Log.d("book","Percent :"+Percent);
            Log.d("book","beginPercent :"+beginPercent);
            Log.d("book","endPercent :"+endPercent);
		
	}
	/**書頁背景*/
   public void setBookBackgroundBitmap(Bitmap bmp){
	   if(pagefactory!=null)
		   pagefactory.setBgBitmap(bmp);
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
           pagefactory.onDraw(mNextPageCanvas);
           postInvalidate();
           if(sonBookChangeListener!=null)
    			sonBookChangeListener.onBookChange( pagefactory.getStrPercent());
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
   }
	
}