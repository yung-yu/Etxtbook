package com.android.ebook.data;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.renderscript.Sampler.Value;

public class BookData {
	public final static String DATABASE_NAME = "book.db";
	public final static int  DATABASE_VERSION = 1;
	public static class Table{
		public final static String BOOK = "books";
		public final static String BOOK_MARK = "bookmark";
	}
	public static class column{
		public final static String ID = "id";
		public final static String NAME = "name";
		public final static String PATH = "path";
		public final static String ENCODE = "encode";
        public final static String BACKUP = "backup";
        
		public final static String TAG = "tag";
		public final static String TAG_TEXT = "tagtext";
		public final static String TAG_PERCENT = "tagpercent";

		public final static String CREATE_DATE = "createdate";
		public final static String CREATE_TIME = "createtime";

		public final static String UPDATE_DATE = "updatedate";
		public final static String UPDATE_TIME = "updatetime";
	}
	public static class columnIndex{		
		public final static int BOOK_ID = 0;
		public final static int BOOK_PATH = 1;
		public final static int BOOK_NAME = 2;
		public final static int BOOK_BACKUP= 3;
		public final static int BOOK_DECODE = 4;
		public final static int BOOK_CREATE_DATE = 5;
		public final static int BOOK_CREATE_TIME = 6;
		public final static int BOOK_UPDATE_DATE = 7;
		public final static int BOOK_UPDATE_TIME = 8;
        
		
		public final static int BOOK_TAG_ID = 0;
		public final static int BOOK_TAG = 1;
		public final static int BOOK_TAG_TEXT = 2;
		public final static int BOOK_TAG_PERCENT = 3;  
		public final static int BOOK_TAG_UPDATE_DATE = 4;
		public final static int BOOK_TAG_UPDATE_TIME = 5;
	}
	private class sqlitHelper extends SQLiteOpenHelper{
		private final String DROP_TABLE = "DROP TABLE IF EXISTS ";
		private final String Create_table_book = 
				"create table "+Table.BOOK+"("+
		                column.ID+" INTEGER PRIMARY KEY,"+
		                column.PATH + " TEXT ,"+
						column.NAME + " TEXT ,"+
		                column.BACKUP+" BOOL ,"+
						column.ENCODE + " INTEGER ,"+
						column.CREATE_DATE + " INTEGER ,"+
						column.CREATE_TIME + " INTEGER ,"+
						column.UPDATE_DATE + " INTEGER ,"+
						column.UPDATE_TIME + " INTEGER "
						+" )";
		
		private final String Create_table_bookmark = 
				"create table "+Table.BOOK_MARK+"("+
						column.ID + " INTEGER ,"+
						column.TAG + " INTEGER ,"+
						column.TAG_TEXT + " TEXT ,"+
						column.TAG_PERCENT + " FLOAT ,"+
						column.UPDATE_DATE + " INTEGER,"+
						column.UPDATE_TIME + " INTEGER"+
						" )";
		public sqlitHelper(Context context, String name,
				CursorFactory factory, int version) {
			super(context, name, factory, version);
			// TODO Auto-generated constructor stub
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			// TODO Auto-generated method stub

			db.execSQL(Create_table_book);
			db.execSQL(Create_table_bookmark);
			firstCreateDb(db);

		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion,
				int newVersion) {
			// TODO Auto-generated method stub
			db.execSQL(DROP_TABLE+Table.BOOK);
			db.execSQL(DROP_TABLE+Table.BOOK_MARK);
			onCreate(db);
		}

	}
	sqlitHelper msqlitHelper;
	ContentValues value;
	List<Book> bookList = new ArrayList<Book>();
	List<BookMark> bookMarkList = new ArrayList<BookMark>();
	public static final String ASSATS_PATH = "assets:\\";
	public  BookData(Context context){
		open(context);
	
	}
	private void open(Context context){
		if(msqlitHelper == null)
			msqlitHelper = new sqlitHelper(context, DATABASE_NAME, null, DATABASE_VERSION);
	}
	private void close(){
		if(msqlitHelper!=null)
		{
			msqlitHelper.close();
			msqlitHelper = null;
		}
	}
    private void firstCreateDb(SQLiteDatabase db){
//    		Book item = new Book();
//    		item.setBookName("¤d½[Øpª«»y");
//    		item.setBookPath(ASSATS_PATH+"demo1.txt");
//    		Calendar c = Calendar.getInstance();
//    		int date =Unity.getCurDate(c);
//    		int time =Unity.getCurTime(c);
//    		value = new ContentValues();
//    		value.put(column.BOOK_NAME, item.getBookName());
//    		value.put(column.BOOK_PATH, item.getBookPath());
//    		value.put(column.CREATE_DATE, date);
//    		value.put(column.CREATE_TIME, time);
//    		value.put(column.UPDATE_DATE, date);
//    		value.put(column.UPDATE_TIME, time);
//    		db.insert(Table.BOOK,null, value);
    }
	private Cursor query(String table, String selection,String orderBy){
		return msqlitHelper.getReadableDatabase().query(table, null, selection, null, null, null, orderBy);
	}
	private void update(String table,ContentValues  v,String where){
		msqlitHelper.getWritableDatabase().update(table, v, where, null);
	}
	private void update(String table,String exp,String where){
		msqlitHelper.getWritableDatabase().execSQL(" UPDATE "+table+" SET "+exp +" WHERE "+where);
	}
	private void delete(String table,String where){
		msqlitHelper.getWritableDatabase().delete(table, where, null);
	}
	private void insert(String table,ContentValues values){
		try{
			msqlitHelper.getWritableDatabase().insertOrThrow(table, null, values);
		}catch(SQLException e){
			e.printStackTrace();		
		}
	}
	public void clearAllData(Context context){
		open(context);
		delete(Table.BOOK, null);
		delete(Table.BOOK_MARK, null);
		close();	
	}
	
	public void addBook(Context context,Book item ,int date ,int time)
	{
		open(context);
		Cursor mcursor =query(Table.BOOK, null, null);
		int count = mcursor.getCount();
		mcursor.close();
		value = new ContentValues();
		value.put(column.ID,Integer.valueOf(date+""+time+""+count));
		value.put(column.NAME, item.getBookName());
		value.put(column.PATH, item.getBookPath());
		value.put(column.BACKUP, false);
		value.put(column.CREATE_DATE, date);
		value.put(column.CREATE_TIME, time);
		value.put(column.UPDATE_DATE, date);
		value.put(column.UPDATE_TIME, time);
		insert(Table.BOOK, value);
		close();
	}
	private void updateBookTime(int id,int date ,int time)
	{
		ContentValues	v = new ContentValues();
		v.put(column.UPDATE_DATE, date);
		v.put(column.UPDATE_TIME, time);
		update(Table.BOOK,v, column.ID+" = "+ id);

	}
	public void deleteBook(Context context,int  id){
		open(context);
		delete(Table.BOOK, column.ID+" = "+ id);
		delete(Table.BOOK_MARK, column.ID+" = "+ id);
		close();
	}
	public int  getBookEncode(Context context,int  id){
		open(context);
		Cursor mCursor = query(Table.BOOK, null, null);
		int value = 0;
		for(mCursor.moveToFirst();!mCursor.isAfterLast();mCursor.moveToNext())
		{  
			if(mCursor.getInt(columnIndex.BOOK_ID) == id){
				value = mCursor.getInt(columnIndex.BOOK_DECODE);
				break;
			}
		}
		mCursor.close();
		close();
		return value;
	}
	public void updateBookEncode(Context context,int id,int encode){
		open(context);
		update(Table.BOOK, column.ENCODE+" = "+ encode, column.ID+" = "+id);
		close();
	}
	public void addBookTag(Context context,int id,BookMark tag){
		open(context);
		value = new ContentValues();
		value.put(column.ID, id);
		value.put(column.TAG ,tag.getBegin());
		value.put(column.TAG_TEXT ,tag.getContent());
		value.put(column.TAG_PERCENT ,tag.getPercent());
		value.put(column.UPDATE_DATE,tag.getUpdate_date());
		value.put(column.UPDATE_TIME,tag.getUpdate_time());
		updateBookTime(id, tag.getUpdate_date(), tag.getUpdate_time());
		insert(Table.BOOK_MARK, value);
		close();
	}
	public List<Book> getBookList(Context context)
	{	open(context);
		bookList.clear();
		String orderBy = column.UPDATE_DATE+" DESC ,"+column.UPDATE_TIME+" DESC";
		Cursor mCursor = query(Table.BOOK, null, orderBy);
		if(mCursor.getCount()>0)
		{
			for(mCursor.moveToFirst();!mCursor.isAfterLast();mCursor.moveToNext())
			{
				Book mBook = new Book();
				mBook.setBookName(mCursor.getString(columnIndex.BOOK_NAME));
				mBook.setBookPath(mCursor.getString(columnIndex.BOOK_PATH));
				mBook.setBookId(mCursor.getInt(columnIndex.BOOK_ID));
				bookList.add(mBook);
			}
		}
		mCursor.close();
		close();
		return bookList;
	}
	public BookMark getBookMark(Context context,int id)
	{   
		open(context);
		String Where = column.ID +" = "+ id;
		String orderBy = column.UPDATE_DATE+" DESC ,"+column.UPDATE_TIME+" DESC";
		Cursor mCursor = query(Table.BOOK_MARK, Where, orderBy);
		BookMark mBookMark = null;
		if(mCursor.getCount()>0)
		{
			for(mCursor.moveToFirst();!mCursor.isAfterLast();mCursor.moveToNext())
			{
				if(mCursor.getInt(columnIndex.BOOK_ID)==id)
				{
				    mBookMark = new BookMark();
				    mBookMark.setBookId(mCursor.getInt(columnIndex.BOOK_ID));
					mBookMark.setBegin(mCursor.getInt(columnIndex.BOOK_TAG));
					mBookMark.setContent(mCursor.getString(columnIndex.BOOK_TAG_TEXT));
					mBookMark.setPercent(mCursor.getInt(columnIndex.BOOK_TAG_PERCENT));
					mBookMark.setUpdate_date(mCursor.getInt(columnIndex.BOOK_TAG_UPDATE_DATE));
					mBookMark.setUpdate_time(mCursor.getInt(columnIndex.BOOK_TAG_UPDATE_TIME));
					break;
				}
			}
		}
		mCursor.close();
		close();
		return mBookMark;
	}
}
