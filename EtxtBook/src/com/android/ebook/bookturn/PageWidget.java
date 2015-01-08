    package com.android.ebook.bookturn;


import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.Region;
import android.graphics.drawable.GradientDrawable;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Scroller;

public class PageWidget extends View {

	@SuppressWarnings("unused")
	private static final String TAG = "Book_Turn";
	private int mWidth = 0;
	private int mHeight = 0;
	private int mCornerX = 0; // 拖拽點對應的頁腳
	private int mCornerY = 0;
	private Path mPath0;
	private Path mPath1;
	Bitmap mCurPageBitmap = null; // 當前頁
	Bitmap mNextPageBitmap = null;

	//PointF:PointF holds two float coordinates
	PointF mTouch = new PointF(); // // 拖拽點
	public PointF getmTouch() {
		return mTouch;
	}

	PointF mBezierStart1 = new PointF(); // 貝塞爾曲線起始點
	PointF mBezierControl1 = new PointF(); // 貝塞爾曲線控制點
	PointF mBeziervertex1 = new PointF(); // 貝塞爾曲線頂點
	PointF mBezierEnd1 = new PointF(); // 貝塞爾曲線結束點

	PointF mBezierStart2 = new PointF(); // 另一條貝塞爾曲線
	PointF mBezierControl2 = new PointF();
	PointF mBeziervertex2 = new PointF();
	PointF mBezierEnd2 = new PointF();

	float mMiddleX;
	float mMiddleY;
	float mDegrees;
	float mTouchToCornerDis;
	ColorMatrixColorFilter mColorMatrixFilter;
	Matrix mMatrix;
	float[] mMatrixArray = { 0, 0, 0, 0, 0, 0, 0, 0, 1.0f };

	boolean mIsRTandLB; // 是否屬於右上左下
	float mMaxLength = (float) Math.hypot(mWidth, mHeight);
	int[] mBackShadowColors;
	int[] mFrontShadowColors;
	GradientDrawable mBackShadowDrawableLR;
	GradientDrawable mBackShadowDrawableRL;
	GradientDrawable mFolderShadowDrawableLR;
	GradientDrawable mFolderShadowDrawableRL;

	GradientDrawable mFrontShadowDrawableHBT;
	GradientDrawable mFrontShadowDrawableHTB;
	GradientDrawable mFrontShadowDrawableVLR;
	GradientDrawable mFrontShadowDrawableVRL;

	Paint mPaint;

	Scroller mScroller;

	@SuppressLint("InlinedApi")
	public PageWidget(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		/**   
		 * Paint類介紹   
		 *    
		 * Paint即畫筆，在繪圖過程中起到了極其重要的作用，畫筆主要保存了顏色，   
		 * 樣式等繪制信息，指定了如何繪制文本和圖形，畫筆對像有很多設置方法，   
		 * 大體上可以分為兩類，一類與圖形繪制相關，一類與文本繪制相關。          
		 *    
		 * 1.圖形繪制   
		 * setARGB(int a,int r,int g,int b);   
		 * 設置繪制的顏色，a代表透明度，r，g，b代表顏色值。   
		 *    
		 * setAlpha(int a);   
		 * 設置繪制圖形的透明度。   
		 *    
		 * setColor(int color);   
		 * 設置繪制的顏色，使用顏色值來表示，該顏色值包括透明度和RGB顏色。   
		 *    
		 * setAntiAlias(boolean aa);   
		 * 設置是否使用抗鋸齒功能，會消耗較大資源，繪制圖形速度會變慢。   
		 *    
		 * setDither(boolean dither);   
		 * 設定是否使用圖像抖動處理，會使繪制出來的圖片顏色更加平滑和飽滿，圖像更加清晰   
		 *    
		 * setFilterBitmap(boolean filter);   
		 * 如果該項設置為true，則圖像在動畫進行中會濾掉對Bitmap圖像的優化操作，加快顯示   
		 * 速度，本設置項依賴於dither和xfermode的設置   
		 *    
		 * setMaskFilter(MaskFilter maskfilter);   
		 * 設置MaskFilter，可以用不同的MaskFilter實現濾鏡的效果，如濾化，立體等       *    
		 * setColorFilter(ColorFilter colorfilter);   
		 * 設置顏色過濾器，可以在繪制顏色時實現不用顏色的變換效果   
		 *    
		 * setPathEffect(PathEffect effect);   
		 * 設置繪制路徑的效果，如點畫線等   
		 *    
		 * setShader(Shader shader);   
		 * 設置圖像效果，使用Shader可以繪制出各種漸變效果   
		 *   
		 * setShadowLayer(float radius ,float dx,float dy,int color);   
		 * 在圖形下面設置陰影層，產生陰影效果，radius為陰影的角度，dx和dy為陰影在x軸和y軸上的距離，color為陰影的顏色   
		 *    
		 * setStyle(Paint.Style style);   
		 * 設置畫筆的樣式，為FILL，FILL_OR_STROKE，或STROKE   
		 *    
		 * setStrokeCap(Paint.Cap cap);   
		 * 當畫筆樣式為STROKE或FILL_OR_STROKE時，設置筆刷的圖形樣式，如圓形樣式   
		 * Cap.ROUND,或方形樣式Cap.SQUARE   
		 *    
		 * setSrokeJoin(Paint.Join join);   
		 * 設置繪制時各圖形的結合方式，如平滑效果等   
		 *    
		 * setStrokeWidth(float width);   
		 * 當畫筆樣式為STROKE或FILL_OR_STROKE時，設置筆刷的粗細度   
		 *    
		 * setXfermode(Xfermode xfermode);   
		 * 設置圖形重疊時的處理方式，如合並，取交集或並集，經常用來制作橡皮的擦除效果   
		 *    
		 * 2.文本繪制   
		 * setFakeBoldText(boolean fakeBoldText);   
		 * 模擬實現粗體文字，設置在小字體上效果會非常差   
		 *    
		 * setSubpixelText(boolean subpixelText);   
		 * 設置該項為true，將有助於文本在LCD屏幕上的顯示效果   
		 *       
		 * setTextScaleX(float scaleX);   
		 * 設置繪制文字x軸的縮放比例，可以實現文字的拉伸的效果   
		 *    
		 * setTextSkewX(float skewX);   
		 * 設置斜體文字，skewX為傾斜弧度   
		 *    
		 * setTypeface(Typeface typeface);   
		 * 設置Typeface對像，即字體風格，包括粗體，斜體以及襯線體，非襯線體等   
		 *    
		 * setUnderlineText(boolean underlineText);   
		 * 設置帶有下劃線的文字效果   
		 *    
		 * setStrikeThruText(boolean strikeThruText);   
		 * 設置帶有刪除線的效果   
		 *    
		 */    
		setScreen(mWidth, mHeight);
		setLayerType(View.LAYER_TYPE_SOFTWARE, null);
		mPath0 = new Path();//Path路徑對像 
		mPath1 = new Path();
		createDrawable();

		mPaint = new Paint();
		mPaint.setStyle(Paint.Style.FILL);

		//顏色矩陣（ColorMatrix）和坐標變換矩陣（Matrix），對圖片進行變換，以拉伸，扭曲等
		ColorMatrix cm = new ColorMatrix();

		//顏色矩陣，顏色矩陣是一個5x4 的矩陣，可以用來方便的修改圖片中RGBA各分量的值，顏色矩陣以一維數組的方式存儲如下：
		// [ a, b, c, d, e, f, g, h, i, j, k, l, m, n, o, p, q, r, s, t ]，他通過RGBA四個通道來直接操作對應顏色
		float array[] = { 0.55f, 0, 0, 0, 80.0f, 0,
			         	0.55f, 0, 0, 80.0f, 0, 0, 
				      0.55f, 0, 80.0f, 0, 0, 0, 0.2f, 0 };
		cm.set(array);
		//顏色濾鏡，就像QQ的在線和離線圖片，同一張圖片通過顏色濾鏡處理，顯示不同的效果，可減少圖片資源加入
		mColorMatrixFilter = new ColorMatrixColorFilter(cm);
		mMatrix = new Matrix();
		mScroller = new Scroller(getContext());

		mTouch.x = 0.01f; // 不讓x,y為0,否則在點計算時會有問題
		mTouch.y = 0.01f;
	}

	/**
	 * 計算拖拽點對應的拖拽角
	 */
	public void calcCornerXY(float x, float y) {
		//將手機屏幕分為四個像限，判斷手指落在哪個像限內
		if (x <= mWidth / 2)
			mCornerX = 0;
		else
			mCornerX = mWidth;
		if (y <= mHeight / 2)
			mCornerY = 0;
		else
			mCornerY = mHeight;

		//如果手指落在第一像限或第三像限，也就是右上角或左下角
		if ((mCornerX == 0 && mCornerY == mHeight)
				|| (mCornerX == mWidth && mCornerY == 0))
			mIsRTandLB = true;
		else
			mIsRTandLB = false;
	}

	public boolean doTouchEvent(MotionEvent event) {
		// TODO Auto-generated method stub
		if (event.getAction() == MotionEvent.ACTION_MOVE) {
			mTouch.x = event.getX();
			mTouch.y = event.getY();
			/* Android提供了Invalidate和postInvalidate方法實現界面刷新，但是Invalidate不能直接在線程中調用，因為他是違背了單線程模型：
			 * Android UI操作並不是線程安全的，並且這些操作必須在UI線程中調用。 
			 * invalidate()的調用是把之前的舊的view從主UI線程隊列中pop掉
			 * 而postInvalidate()在工作者線程中被調用
			 */
			this.postInvalidate();
		}
		if (event.getAction() == MotionEvent.ACTION_DOWN) {
			mTouch.x = event.getX();
			mTouch.y = event.getY();
			// calcCornerXY(mTouch.x, mTouch.y);
			// this.postInvalidate();
		}
		if (event.getAction() == MotionEvent.ACTION_UP) {
			//是否觸發翻頁
			if (canDragOvear()) {
				startAnimation(1200);
				
			} else {
				mTouch.x = mCornerX - 0.09f;//如果不能翻頁就讓mTouch返回沒有靜止時的狀態
				mTouch.y = mCornerY - 0.09f;//- 0.09f是防止mTouch = 800 或mTouch= 0 ,在這些值時會出現BUG
			}

			this.postInvalidate();		
		}
		// return super.onTouchEvent(event);
		return true;
	}

	/**
	 * 求解直線P1P2和直線P3P4的交點坐標
	 */
	public PointF getCross(PointF P1, PointF P2, PointF P3, PointF P4) {
		PointF CrossP = new PointF();
		// 二元函數通式： y=ax+b
		float a1 = (P2.y - P1.y) / (P2.x - P1.x);
		float b1 = ((P1.x * P2.y) - (P2.x * P1.y)) / (P1.x - P2.x);

		float a2 = (P4.y - P3.y) / (P4.x - P3.x);
		float b2 = ((P3.x * P4.y) - (P4.x * P3.y)) / (P3.x - P4.x);
		CrossP.x = (b2 - b1) / (a1 - a2);
		CrossP.y = a1 * CrossP.x + b1;
		return CrossP;
	}

	private void calcPoints() {
		mMiddleX = (mTouch.x + mCornerX) / 2;
		mMiddleY = (mTouch.y + mCornerY) / 2;
		mBezierControl1.x = mMiddleX - (mCornerY - mMiddleY)
				* (mCornerY - mMiddleY) / (mCornerX - mMiddleX);
		mBezierControl1.y = mCornerY;
		mBezierControl2.x = mCornerX;
		mBezierControl2.y = mMiddleY - (mCornerX - mMiddleX)
				* (mCornerX - mMiddleX) / (mCornerY - mMiddleY);

		mBezierStart1.x = mBezierControl1.x - (mCornerX - mBezierControl1.x)
				/ 2;
		mBezierStart1.y = mCornerY;

		// 當mBezierStart1.x < 0或者mBezierStart1.x > 480時
		// 如果繼續翻頁，會出現BUG故在此限制
		if (mTouch.x > 0 && mTouch.x < mWidth) {
			if (mBezierStart1.x < 0 || mBezierStart1.x > mWidth) {
				if (mBezierStart1.x < 0)
					mBezierStart1.x = mWidth - mBezierStart1.x;

				float f1 = Math.abs(mCornerX - mTouch.x);
				float f2 = mWidth * f1 / mBezierStart1.x;
				mTouch.x = Math.abs(mCornerX - f2);

				float f3 = Math.abs(mCornerX - mTouch.x)
						* Math.abs(mCornerY - mTouch.y) / f1;
				mTouch.y = Math.abs(mCornerY - f3);

				mMiddleX = (mTouch.x + mCornerX) / 2;
				mMiddleY = (mTouch.y + mCornerY) / 2;

				mBezierControl1.x = mMiddleX - (mCornerY - mMiddleY)
						* (mCornerY - mMiddleY) / (mCornerX - mMiddleX);
				mBezierControl1.y = mCornerY;

				mBezierControl2.x = mCornerX;
				mBezierControl2.y = mMiddleY - (mCornerX - mMiddleX)
						* (mCornerX - mMiddleX) / (mCornerY - mMiddleY);

				mBezierStart1.x = mBezierControl1.x
						- (mCornerX - mBezierControl1.x) / 2;
			}
		}
		mBezierStart2.x = mCornerX;
		mBezierStart2.y = mBezierControl2.y - (mCornerY - mBezierControl2.y)
				/ 2;

		mTouchToCornerDis = (float) Math.hypot((mTouch.x - mCornerX),
				(mTouch.y - mCornerY));

		mBezierEnd1 = getCross(mTouch, mBezierControl1, mBezierStart1,
				mBezierStart2);
		mBezierEnd2 = getCross(mTouch, mBezierControl2, mBezierStart1,
				mBezierStart2);

		/*
		 * mBeziervertex1.x 推導
		 * ((mBezierStart1.x+mBezierEnd1.x)/2+mBezierControl1.x)/2 化簡等價於
		 * (mBezierStart1.x+ 2*mBezierControl1.x+mBezierEnd1.x) / 4
		 */
		mBeziervertex1.x = (mBezierStart1.x + 2 * mBezierControl1.x + mBezierEnd1.x) / 4;
		mBeziervertex1.y = (2 * mBezierControl1.y + mBezierStart1.y + mBezierEnd1.y) / 4;
		mBeziervertex2.x = (mBezierStart2.x + 2 * mBezierControl2.x + mBezierEnd2.x) / 4;
		mBeziervertex2.y = (2 * mBezierControl2.y + mBezierStart2.y + mBezierEnd2.y) / 4;
	}

	private void drawCurrentPageArea(Canvas canvas, Bitmap bitmap, Path path) {
		mPath0.reset();
		mPath0.moveTo(mBezierStart1.x, mBezierStart1.y);
		mPath0.quadTo(mBezierControl1.x, mBezierControl1.y, mBezierEnd1.x,
				mBezierEnd1.y);
		mPath0.lineTo(mTouch.x, mTouch.y);
		mPath0.lineTo(mBezierEnd2.x, mBezierEnd2.y);
		mPath0.quadTo(mBezierControl2.x, mBezierControl2.y, mBezierStart2.x,
				mBezierStart2.y);
		mPath0.lineTo(mCornerX, mCornerY);
		mPath0.close();
		canvas.save();
		canvas.clipPath(path, Region.Op.XOR);
		canvas.drawBitmap(bitmap, 0, 0, null);
		canvas.restore();
	}

	private void drawNextPageAreaAndShadow(Canvas canvas, Bitmap bitmap) {
		mPath1.reset();
		mPath1.moveTo(mBezierStart1.x, mBezierStart1.y);
		mPath1.lineTo(mBeziervertex1.x, mBeziervertex1.y);
		mPath1.lineTo(mBeziervertex2.x, mBeziervertex2.y);
		mPath1.lineTo(mBezierStart2.x, mBezierStart2.y);
		mPath1.lineTo(mCornerX, mCornerY);
		mPath1.close();

		mDegrees = (float) Math.toDegrees(Math.atan2(mBezierControl1.x
				- mCornerX, mBezierControl2.y - mCornerY));
		int leftx;
		int rightx;
		GradientDrawable mBackShadowDrawable;
		if (mIsRTandLB) {
			leftx = (int) (mBezierStart1.x);
			rightx = (int) (mBezierStart1.x + mTouchToCornerDis / 4);
			mBackShadowDrawable = mBackShadowDrawableLR;
		} else {
			leftx = (int) (mBezierStart1.x - mTouchToCornerDis / 4);
			rightx = (int) mBezierStart1.x;
			mBackShadowDrawable = mBackShadowDrawableRL;
		}
		canvas.save();
		canvas.clipPath(mPath0);
		canvas.clipPath(mPath1, Region.Op.INTERSECT);
		canvas.drawBitmap(bitmap, 0, 0, null);
		canvas.rotate(mDegrees, mBezierStart1.x, mBezierStart1.y);
		mBackShadowDrawable.setBounds(leftx, (int) mBezierStart1.y, rightx,
				(int) (mMaxLength + mBezierStart1.y));
		mBackShadowDrawable.draw(canvas);
		canvas.restore();
	}

	public void setBitmaps(Bitmap bm1, Bitmap bm2) {
		mCurPageBitmap = bm1;
		mNextPageBitmap = bm2;
	}

	public void setScreen(int w, int h) {
		mWidth = w;
		mHeight = h;
	}

	@Override
	protected void onDraw(Canvas canvas) {
		//deawBackBg(canvas);	
		canvas.drawColor(0xFFAAAAAA);
		calcPoints();
		drawCurrentPageArea(canvas, mCurPageBitmap, mPath0);
		drawNextPageAreaAndShadow(canvas, mNextPageBitmap);
		drawCurrentPageShadow(canvas);
		drawCurrentBackArea(canvas, mCurPageBitmap);
	
	}

	/**
	 * 創建陰影的GradientDrawable
	 */
	private void createDrawable() {

		/*
		 * GradientDrawable 支持使用漸變色來繪制圖形，通常可以用作Button或是背景圖形。

		 * GradientDrawable允許指定繪制圖形的種類：LINE，OVAL，RECTANGLE或是RING ，顏色漸變支持LINEAR_GRADIENT，RADIAL_GRADIENT 和 SWEEP_GRADIENT。

		 * 其中在使用RECTANGLE（矩形），還允許設置矩形四個角為圓角，每個圓角的半徑可以分別設置：

		 * public void setCornerRadii(float[] radii)

		 * radii 數組分別指定四個圓角的半徑，每個角可以指定[X_Radius,Y_Radius]，四個圓角的順序為左上，右上，右下，左下。如果X_Radius,Y_Radius為0表示還是直角。

		 * 顏色漸變的方向由GradientDrawable.Orientation定義,共八種

		 * GradientDrawable的構造函數：public GradientDrawable(GradientDrawable.Orientation orientation, int[] colors)

		 * orientation指定了漸變的方向，漸變的顏色由colors數組指定，數組中的每個值為一個顏色。

		 * 本例定義一個漸變方向從組左上到右下，漸變顏色為紅，綠，藍三色：

		 * mDrawable = new GradientDrawable(GradientDrawable.Orientation.TL_BR,new int[] { 0xFFFF0000, 0xFF00FF00,0xFF0000FF });

		 * 分別使用Liner,Radial 和Sweep三種漸變模式，並可配合指定矩形四個角圓角半徑
		 * */

		int[] color = { 0x333333, 0x333333 };

		//從右向左由顏色0x333333漸變為0x333333
		mFolderShadowDrawableRL = new GradientDrawable(
				GradientDrawable.Orientation.RIGHT_LEFT, color);
		mFolderShadowDrawableRL
		.setGradientType(GradientDrawable.LINEAR_GRADIENT);//線性漸變， "radial"：徑向漸變，  "sweep" ：角度漸變

		mFolderShadowDrawableLR = new GradientDrawable(
				GradientDrawable.Orientation.LEFT_RIGHT, color);
		mFolderShadowDrawableLR
		.setGradientType(GradientDrawable.LINEAR_GRADIENT);

		mBackShadowColors = new int[] { 0xff111111, 0x111111 };
		mBackShadowDrawableRL = new GradientDrawable(
				GradientDrawable.Orientation.RIGHT_LEFT, mBackShadowColors);
		mBackShadowDrawableRL.setGradientType(GradientDrawable.LINEAR_GRADIENT);

		mBackShadowDrawableLR = new GradientDrawable(
				GradientDrawable.Orientation.LEFT_RIGHT, mBackShadowColors);
		mBackShadowDrawableLR.setGradientType(GradientDrawable.LINEAR_GRADIENT);

		//mFrontShadowColors = new int[] { 0x80111111, 0x111111 };
		mBackShadowColors = new int[] { 0xff111111, 0x111111 };
		mFrontShadowDrawableVLR = new GradientDrawable(
				GradientDrawable.Orientation.LEFT_RIGHT, mFrontShadowColors);
		mFrontShadowDrawableVLR
		.setGradientType(GradientDrawable.LINEAR_GRADIENT);
		mFrontShadowDrawableVRL = new GradientDrawable(
				GradientDrawable.Orientation.RIGHT_LEFT, mFrontShadowColors);
		mFrontShadowDrawableVRL
		.setGradientType(GradientDrawable.LINEAR_GRADIENT);

		mFrontShadowDrawableHTB = new GradientDrawable(
				GradientDrawable.Orientation.TOP_BOTTOM, mFrontShadowColors);
		mFrontShadowDrawableHTB
		.setGradientType(GradientDrawable.LINEAR_GRADIENT);

		mFrontShadowDrawableHBT = new GradientDrawable(
				GradientDrawable.Orientation.BOTTOM_TOP, mFrontShadowColors);
		mFrontShadowDrawableHBT
		.setGradientType(GradientDrawable.LINEAR_GRADIENT);
	}

	/**
	 * 繪制翻起頁的陰影
	 */
	public void drawCurrentPageShadow(Canvas canvas) {
		double degree;
		//計算兩點間連線的傾斜角.
		//還可旋轉餅圖
		if (mIsRTandLB) {
			degree = Math.PI
					/ 4
					- Math.atan2(mBezierControl1.y - mTouch.y, mTouch.x
							- mBezierControl1.x);
		} else {
			degree = Math.PI
					/ 4
					- Math.atan2(mTouch.y - mBezierControl1.y, mTouch.x
							- mBezierControl1.x);
		}
		// 翻起頁陰影頂點與touch點的距離
		double d1 = (float) 25 * 1.414 * Math.cos(degree);
		double d2 = (float) 25 * 1.414 * Math.sin(degree);
		float x = (float) (mTouch.x + d1);
		float y;
		if (mIsRTandLB) {
			y = (float) (mTouch.y + d2);
		} else {
			y = (float) (mTouch.y - d2);
		}
		mPath1.reset();
		mPath1.moveTo(x, y);
		mPath1.lineTo(mTouch.x, mTouch.y);
		mPath1.lineTo(mBezierControl1.x, mBezierControl1.y);
		mPath1.lineTo(mBezierStart1.x, mBezierStart1.y);
		mPath1.close();
		float rotateDegrees;
		canvas.save();
		canvas.clipPath(mPath0, Region.Op.XOR);
		canvas.clipPath(mPath1, Region.Op.INTERSECT);
		int leftx;
		int rightx;
		GradientDrawable mCurrentPageShadow;
		if (mIsRTandLB) {
			leftx = (int) (mBezierControl1.x);
			rightx = (int) mBezierControl1.x + 25;
			mCurrentPageShadow = mFrontShadowDrawableVLR;
		} else {
			leftx = (int) (mBezierControl1.x - 25);
			rightx = (int) mBezierControl1.x + 1;
			mCurrentPageShadow = mFrontShadowDrawableVRL;
		}

		rotateDegrees = (float) Math.toDegrees(Math.atan2(mTouch.x
				- mBezierControl1.x, mBezierControl1.y - mTouch.y));
		canvas.rotate(rotateDegrees, mBezierControl1.x, mBezierControl1.y);
		mCurrentPageShadow.setBounds(leftx,
				(int) (mBezierControl1.y - mMaxLength), rightx,
				(int) (mBezierControl1.y));
		mCurrentPageShadow.draw(canvas);
		canvas.restore();
        try{
		mPath1.reset();
		mPath1.moveTo(x, y);
		mPath1.lineTo(mTouch.x, mTouch.y);
		mPath1.lineTo(mBezierControl2.x, mBezierControl2.y);
		mPath1.lineTo(mBezierStart2.x, mBezierStart2.y);	
		canvas.clipPath(mPath0, Region.Op.XOR);
		canvas.clipPath(mPath1, Region.Op.INTERSECT);
		} catch (UnsupportedOperationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        canvas.save();
		if (mIsRTandLB) {
			leftx = (int) (mBezierControl2.y);
			rightx = (int) (mBezierControl2.y + 25);
			mCurrentPageShadow = mFrontShadowDrawableHTB;
		} else {
			leftx = (int) (mBezierControl2.y - 25);
			rightx = (int) (mBezierControl2.y + 1);
			mCurrentPageShadow = mFrontShadowDrawableHBT;
		}
		rotateDegrees = (float) Math.toDegrees(Math.atan2(mBezierControl2.y
				- mTouch.y, mBezierControl2.x - mTouch.x));
		canvas.rotate(rotateDegrees, mBezierControl2.x, mBezierControl2.y);
		float temp;
		if (mBezierControl2.y < 0)
			temp = mBezierControl2.y - mHeight;
		else
			temp = mBezierControl2.y;

		int hmg = (int) Math.hypot(mBezierControl2.x, temp);
		if (hmg > mMaxLength)
			mCurrentPageShadow
			.setBounds((int) (mBezierControl2.x - 25) - hmg, leftx,
					(int) (mBezierControl2.x + mMaxLength) - hmg,
					rightx);
		else
			mCurrentPageShadow.setBounds(
					(int) (mBezierControl2.x - mMaxLength), leftx,
					(int) (mBezierControl2.x), rightx);

		mCurrentPageShadow.draw(canvas);
		canvas.restore();
	}

	/**
	 * 繪制翻起頁背面
	 */
	private void drawCurrentBackArea(Canvas canvas, Bitmap bitmap) {
		int i = (int) (mBezierStart1.x + mBezierControl1.x) / 2;
		float f1 = Math.abs(i - mBezierControl1.x);
		int i1 = (int) (mBezierStart2.y + mBezierControl2.y) / 2;
		float f2 = Math.abs(i1 - mBezierControl2.y);
		float f3 = Math.min(f1, f2);
		mPath1.reset();
		mPath1.moveTo(mBeziervertex2.x, mBeziervertex2.y);
		mPath1.lineTo(mBeziervertex1.x, mBeziervertex1.y);
		mPath1.lineTo(mBezierEnd1.x, mBezierEnd1.y);
		mPath1.lineTo(mTouch.x, mTouch.y);
		mPath1.lineTo(mBezierEnd2.x, mBezierEnd2.y);
		mPath1.close();
		GradientDrawable mFolderShadowDrawable;
		int left;
		int right;
		if (mIsRTandLB) {
			left = (int) (mBezierStart1.x - 1);
			right = (int) (mBezierStart1.x + f3 + 1);
			mFolderShadowDrawable = mFolderShadowDrawableLR;
		} else {
			left = (int) (mBezierStart1.x - f3 - 1);
			right = (int) (mBezierStart1.x + 1);
			mFolderShadowDrawable = mFolderShadowDrawableRL;
		}
		canvas.save();
		canvas.clipPath(mPath0);
		canvas.clipPath(mPath1, Region.Op.INTERSECT);
		mPaint.setColorFilter(mColorMatrixFilter);

		float dis = (float) Math.hypot(mCornerX - mBezierControl1.x,
				mBezierControl2.y - mCornerY);
		float f8 = (mCornerX - mBezierControl1.x) / dis;
		float f9 = (mBezierControl2.y - mCornerY) / dis;
		mMatrixArray[0] = 1 - 2 * f9 * f9;
		mMatrixArray[1] = 2 * f8 * f9;
		mMatrixArray[3] = mMatrixArray[1];
		mMatrixArray[4] = 1 - 2 * f8 * f8;
		mMatrix.reset();
		mMatrix.setValues(mMatrixArray);
		mMatrix.preTranslate(-mBezierControl1.x, -mBezierControl1.y);
		mMatrix.postTranslate(mBezierControl1.x, mBezierControl1.y);
		canvas.drawBitmap(bitmap, mMatrix, mPaint);
		mPaint.setColorFilter(null);
		canvas.rotate(mDegrees, mBezierStart1.x, mBezierStart1.y);
		mFolderShadowDrawable.setBounds(left, (int) mBezierStart1.y, right,
				(int) (mBezierStart1.y + mMaxLength));
		mFolderShadowDrawable.draw(canvas);
		canvas.restore();
	}

	public void computeScroll() {
		super.computeScroll();
		if (mScroller.computeScrollOffset()) {
			float x = mScroller.getCurrX();
			float y = mScroller.getCurrY();
			mTouch.x = x;
			mTouch.y = y;
			postInvalidate();
		}
	}

	public void startAnimation(int delayMillis) {
		int dx, dy;
		// dx 水平方向滑動的距離，負值會使滾動向左滾動
		// dy 垂直方向滑動的距離，負值會使滾動向上滾動
		if (mCornerX > 0) {
			dx = -(int) (mWidth + mTouch.x);
		} else {
			dx = (int) (mWidth - mTouch.x + mWidth);
		}
		if (mCornerY > 0) {
			dy = (int) (mHeight - mTouch.y);
		} else {
			dy = (int) (1 - mTouch.y); // // 防止mTouch.y最終變為0
		}
		//Start scrolling by providing a starting point and the distance to travel.
		mScroller.startScroll((int) mTouch.x, (int) mTouch.y, dx, dy,
				delayMillis);
	}

	public void abortAnimation() {
		if (!mScroller.isFinished()) {
			//停止動畫，與forceFinished(boolean)相反，Scroller滾動到最終x與y位置時中止動畫。
			mScroller.abortAnimation();
		}
	}

	public boolean canDragOvear() {
		//設置開始翻頁的條件
		//		if (mTouchToCornerDis > mWidth / 10)
		if (mTouchToCornerDis>mWidth/5)
			return true;
		return false;
	}

	/**
	 * 是否從左邊翻向右邊
	 */
	public boolean DragToRight() {
		if (mCornerX > 0)
			return false;
		return true;
	}

}
