
package com.android.mylibrary.bookturn;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.io.UnsupportedEncodingException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.text.DecimalFormat;
import java.util.Vector;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Paint.FontMetrics;


public class BookPageFactory{
    //private final String TAGLOG = "BOOK";
	private File book_file = null;
	private MappedByteBuffer m_mbBuf = null;
	private int m_mbBufLen = 0;
	private int m_mbBufBegin = 0;
	private int m_mbBufEnd = 0;
	private String m_strCharsetName = "utf8";
	private Bitmap m_book_bg = null;
	private int mWidth;
	private int mHeight;
	private Vector<String> m_lines = new Vector<String>();
	private float m_fontSize = 30;
     float m_fontSize_forMsg = 20;

	private int m_textColor = Color.BLACK;
	private int m_backColor = 0xffffffee; // 背景顏色
	private int marginWidth = 40; // 左右與邊緣的距離
	
	private int marginHeight = 40; // 上下與邊緣的距離
	
	private int mLineCount; // 每頁可以顯示的行數
	private float mVisibleHeight; // 繪制內容的寬
	private float mVisibleWidth; // 繪制內容的寬
	private boolean m_isfirstPage,m_islastPage;
	String strPercent = "";
	// private int m_nLineSpaceing = 5;
    private int delay_lineCount = 1;

	private Paint mPaint;
	private Paint mPaint_formsg;
	private String BookName = "";
	public BookPageFactory(int w, int h,int marginWidth,int marginHeight) {
		// TODO Auto-generated constructor stub
		mWidth = w;
		mHeight = h;
		this.marginWidth = marginWidth;
		this.marginHeight = marginHeight;
		mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		mPaint.setTextAlign(Align.LEFT);//設置繪制文字的對齊方向  	
		mPaint.setColor(m_textColor);
		mPaint_formsg = new Paint(Paint.ANTI_ALIAS_FLAG);
		mPaint_formsg.setColor(m_textColor);
		setM_fontSize_forMsg(30);
		setM_fontSize(30);// 可顯示的行數
	
	
	}
	
	public void openBook(Context context,String fileName){
		try{
			openBook(getRobotCacheFile(context,fileName).getAbsolutePath());
		}catch(IOException e){
			
		}
	}
	  private File getRobotCacheFile(Context context,String fileName) throws IOException {
	        File cacheFile = new File(context.getCacheDir(), fileName);
	        try {
	            InputStream inputStream = context.getAssets().open(fileName);
	            try {
	                FileOutputStream outputStream = new FileOutputStream(cacheFile);
	                try {
	                    byte[] buf = new byte[1024];
	                    int len;
	                    while ((len = inputStream.read(buf)) > 0) {
	                        outputStream.write(buf, 0, len);
	                    }
	                } finally {
	                    outputStream.close();
	                }
	            } finally {
	                inputStream.close();
	            }
	        } catch (IOException e) {
	            throw new IOException("Could not open robot png", e);
	        }
	        return cacheFile;
	    }
	  @SuppressWarnings("resource")
	public void openBook(String strFilePath){
		  try {
			  book_file = new File(strFilePath);
			  long lLen = book_file.length();
			  m_mbBufLen = (int) lLen;

			  /*
			   * 內存映射文件能讓你創建和修改那些因為太大而無法放入內存的文件。有了內存映射文件，你就可以認為文件已經全部讀進了內存，
			   * 然後把它當成一個非常大的數組來訪問。這種解決辦法能大大簡化修改文件的代碼。 
			   * 
			   * fileChannel.map(FileChannel.MapMode mode, long position, long size)將此通道的文件區域直接映射到內存中。但是，你必
			   * 須指明，它是從文件的哪個位置開始映射的，映射的範圍又有多大
			   */
			  FileChannel fc=new RandomAccessFile(book_file, "r").getChannel();

			  //文件通道的可讀可寫要建立在文件流本身可讀寫的基礎之上  
			  m_mbBuf =fc.map(FileChannel.MapMode.READ_ONLY, 0, lLen);
		  }catch(IOException e){

		  }
	  }

	
	protected byte[] readParagraphBack(int nFromPos) {
		int nEnd = nFromPos;
		int i;
		byte b0, b1;
		if (m_strCharsetName.equals("UTF-16LE")) {
			i = nEnd - 2;
			while (i > 0) {
				b0 = m_mbBuf.get(i);
				b1 = m_mbBuf.get(i + 1);
				if (b0 == 0x0a && b1 == 0x00 && i != nEnd - 2) {
					i += 2;
					break;
				}
				i--;
			}

		} else if (m_strCharsetName.equals("UTF-16BE")) {
			i = nEnd - 2;
			while (i > 0) {
				b0 = m_mbBuf.get(i);
				b1 = m_mbBuf.get(i + 1);
				if (b0 == 0x00 && b1 == 0x0a && i != nEnd - 2) {
					i += 2;
					break;
				}
				i--;
			}
		} else {
			i = nEnd - 1;
			while (i > 0) {
				b0 = m_mbBuf.get(i);
				if (b0 == 0x0a && i != nEnd - 1) {
					i++;
					break;
				}
				i--;
			}
		}
		if (i < 0)
			i = 0;
		int nParaSize = nEnd - i;
		int j;
		byte[] buf = new byte[nParaSize];
		for (j = 0; j < nParaSize; j++) {
			buf[j] = m_mbBuf.get(i + j);
		}
		return buf;
	}
	//讀取上一段落
	protected byte[] readParagraphForward(int nFromPos) {
		int nStart = nFromPos;
		int i = nStart;
		byte b0, b1;
		// 根據編碼格式判斷換行
		if (m_strCharsetName.equals("UTF-16LE")) {
			while (i < m_mbBufLen - 1) {
				b0 = m_mbBuf.get(i++);
				b1 = m_mbBuf.get(i++);
				if (b0 == 0x0a && b1 == 0x00) {
					break;
				}
			}
		} else if (m_strCharsetName.equals("UTF-16BE")) {
			while (i < m_mbBufLen - 1) {
				b0 = m_mbBuf.get(i++);
				b1 = m_mbBuf.get(i++);
				if (b0 == 0x00 && b1 == 0x0a) {
					break;
				}
			}
		} else {
			while (i < m_mbBufLen) {
				b0 = m_mbBuf.get(i++);
				if (b0 == 0x0a) {
					break;
				}
			}
		}
		//共讀取了多少字符
		int nParaSize = i - nStart;
		byte[] buf = new byte[nParaSize];
		for (i = 0; i < nParaSize; i++) {
			//將已讀取的字符放入數組
			buf[i] = m_mbBuf.get(nFromPos + i);
		}
		return buf;
	}

	protected Vector<String> pageDown() {
		String strParagraph = "";
		Vector<String> lines = new Vector<String>();
		while (lines.size() < mLineCount && m_mbBufEnd < m_mbBufLen) {
			byte[] paraBuf = readParagraphForward(m_mbBufEnd); // 讀取一個段落
			m_mbBufEnd += paraBuf.length;//結束位置後移paraBuf.length
			try {
				strParagraph = new String(paraBuf, m_strCharsetName);//通過decode指定的編碼格式將byte[]轉換為字符串			
				} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			String strReturn = "";
			
			//去除將字符串中的特殊字符
			if (strParagraph.indexOf("\r\n") != -1) {
				strReturn = "\r\n";
				strParagraph = strParagraph.replaceAll("\r\n", "");
			} else if (strParagraph.indexOf("\n") != -1) {
				strReturn = "\n";
				strParagraph = strParagraph.replaceAll("\n", "");
			}

			if (strParagraph.length() == 0) {
				lines.add(strParagraph);
			}
			while (strParagraph.length() > 0) {
				//計算每行可以顯示多少個字符
				//獲益匪淺
				int nSize = mPaint.breakText(strParagraph, true, mVisibleWidth,null);
				nSize = nSize<=strParagraph.length()?nSize:strParagraph.length();
				lines.add(strParagraph.substring(0, nSize));
				strParagraph = strParagraph.substring(nSize);//截取從nSize開始的字符串
				if (lines.size() >= mLineCount) {
					break;
				}
			}
			//當前頁沒顯示完
			if (strParagraph.length() != 0) {
				try {
					m_mbBufEnd -= (strParagraph + strReturn)
							.getBytes(m_strCharsetName).length;
				} catch (UnsupportedEncodingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		return lines;
	}

	protected void pageUp() {
		if (m_mbBufBegin < 0)
			m_mbBufBegin = 0;
		Vector<String> lines = new Vector<String>();
		String strParagraph = "";
		while (lines.size() < mLineCount && m_mbBufBegin > 0) {
			Vector<String> paraLines = new Vector<String>();
			byte[] paraBuf = readParagraphBack(m_mbBufBegin);
			m_mbBufBegin -= paraBuf.length;
			try {
				strParagraph = new String(paraBuf, m_strCharsetName);
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			strParagraph = strParagraph.replaceAll("\r\n", "");
			strParagraph = strParagraph.replaceAll("\n", "");

			if (strParagraph.length() == 0) {
				paraLines.add(strParagraph);
			}
			while (strParagraph.length() > 0) {
				int nSize = mPaint.breakText(strParagraph, true, mVisibleWidth,
						null);
				paraLines.add(strParagraph.substring(0, nSize));
				strParagraph = strParagraph.substring(nSize);
			}
			lines.addAll(0, paraLines);
		}
		while (lines.size() > mLineCount) {
			try {
				m_mbBufBegin += lines.get(0).getBytes(m_strCharsetName).length;
				lines.remove(0);
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		m_mbBufEnd = m_mbBufBegin;
		return;
	}

	public void prePage() throws IOException {
		if (m_mbBufBegin <= 0) {
			//第一頁
			m_mbBufBegin = 0;
			m_isfirstPage=true;
			return;
		}else m_isfirstPage=false;
		m_lines.clear();//Removes all elements from this vector, leaving it empty.
		pageUp();
		m_lines = pageDown();
	}
	
	public void nextPage() throws IOException {
		if (m_mbBufEnd >= m_mbBufLen) {
			m_islastPage=true;
			return;
		}else m_islastPage=false;
		m_lines.clear();
		m_mbBufBegin = m_mbBufEnd;
		m_lines = pageDown();
	}

	@SuppressLint("DrawAllocation")
	public void onDraw(Canvas c) {
		if (m_lines.size() == 0)
			m_lines = pageDown();	
	    int textheight = (int) (mPaint.descent() - mPaint.ascent())+1;
		if (m_lines.size() > 0) {	
			c.drawColor(0xffffffee);
			if (m_book_bg != null){
				c.drawBitmap(m_book_bg, 0, 0, null);
			}else{
						c.drawColor(m_backColor);
			}
			int y = 0;
			for (String strLine : m_lines) {
				y += textheight;
				//從（x,y）坐標將文字繪於手機屏幕		
				c.drawText(strLine, marginWidth, y, mPaint);
			}
		}
		//計算百分比（不包括當前頁）並格式化
		float fPercent = (float) (m_mbBufBegin * 1.0 / m_mbBufLen);
		DecimalFormat df = new DecimalFormat("#0.0");
	    strPercent = df.format(fPercent * 100) + "%";
	    mPaint_formsg.setTextSize(m_fontSize_forMsg);
		//計算999.9%所占的像素寬度	
		int nPercentWidth = (int) mPaint.measureText(strPercent) + 1;
		mPaint_formsg.setTextAlign(Align.RIGHT);
		 int th = (int) (mPaint_formsg.descent() - mPaint_formsg.ascent());
		c.drawText(strPercent, mWidth, mHeight-marginHeight-th , mPaint_formsg);
		mPaint_formsg.setTextAlign(Align.LEFT);
		int size=mPaint_formsg.breakText(BookName, true, mWidth-nPercentWidth,null);
		if(size<BookName.length())
			BookName = BookName.substring(0,size);
		c.drawText(BookName, 0,mHeight-marginHeight-th, mPaint_formsg);
	}

	public void setBookName(String bookName) {
		BookName = bookName;
	}

	public void setBgBitmap(Bitmap BG) {
		m_book_bg = BG;
	}
	 
	public boolean isfirstPage() {
		return m_isfirstPage;
	}
	public boolean islastPage() {
		return m_islastPage;
	}
    
	/**設定編碼*/
	public void setM_strCharsetName(String m_strCharsetName) {
		this.m_strCharsetName = m_strCharsetName;
	}
	public String getM_strCharsetName() {
		return m_strCharsetName;
	}

	/**設定文字size*/
	public void setM_fontSize(float m_fontSize) {
		this.m_fontSize = m_fontSize;
		this.mPaint.setTextSize(m_fontSize);
		FontMetrics fontMetrics = mPaint.getFontMetrics();
	    int textheight = (int) (fontMetrics.descent-fontMetrics.ascent+fontMetrics.leading)+1;
		this.mVisibleWidth = this.mWidth - this.marginWidth * 2;
		this.mVisibleHeight = this.mHeight - this.marginHeight * 2;
		this.mLineCount = (int) (this.mVisibleHeight / textheight)-delay_lineCount;
	}
	public void setDelay_lineCount(int delay_lineCount) {
		this.delay_lineCount = delay_lineCount;
	}
	public int getM_backColor() {
		return m_backColor;
	}
	/**設定文字Color*/
	public void setM_textColor(int m_textColor) {
		this.m_textColor = m_textColor;
		mPaint.setColor(m_textColor);
	}	
	public int  getM_textColor() {
		return m_textColor;
	}
	public void setM_fontSize_forMsg(float m_fontSize_forMsg) {
		this.m_fontSize_forMsg = m_fontSize_forMsg;
	}
	/**設定書頁背景顏色*/
	public void setM_backColor(int m_backColor) {
		this.m_backColor = m_backColor;
	}
	/**設定書頁左右距*/
	public void setMarginWidth(int marginWidth) {
		this.marginWidth = marginWidth;
	}
	/**設定書頁上下間距*/
	public void setMarginHeight(int marginHeight) {
		this.marginHeight = marginHeight;
	}
	/**起始位置*/
	public int getM_mbBufBegin() {
		return m_mbBufBegin;
	}
	/**起始位置*/
	public void setM_mbBufBegin(int m_mbBufBegin) {
		this.m_mbBufBegin = m_mbBufBegin;
	}
	/**當前內文*/
	public Vector<String> getM_lines() {
		return m_lines;
	}
    /**當前內文*/
	public void setM_lines(Vector<String> m_lines) {
		this.m_lines = m_lines;
	}
	/**當前進度*/
	public String getStrPercent() {
		return strPercent;
	}
	/**當前進度*/
	public void setStrPercent(String strPercent) {
		this.strPercent = strPercent;
	}
	/**當前結束位置*/
	public int getM_mbBufEnd() {
		return m_mbBufEnd;
	}
	/**當前結束位置*/
	public void setM_mbBufEnd(int m_mbBufEnd) {
		this.m_mbBufEnd = m_mbBufEnd;
	}
	public float getM_fontSize() {
		return m_fontSize;
	}
}
