package com.android.ebook.data;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class BookData {
	public final static String DATABASE_NAME = "book.db";
	public final static int  DATABASE_VERSION = 1;
	public static class Table{
		public final static String BOOK = "books";
		public final static String BOOK_MARK = "bookmark";
	}
	public static class Cloums{
		public final static String BOOK_NAME = "bookname";
		public final static String BOOK_PATH = "bookpath";
		public final static String BOOK_ENCODE = "bookeecode";

		public final static String BOOK_TAG = "booktag";
		public final static String BOOK_TAG_TEXT = "booktagtext";
		public final static String BOOK_TAG_PERCENT = "booktagpercent";

		public final static String CREATE_DATE = "createdate";
		public final static String CREATE_TIME = "createtime";

		public final static String UPDATE_DATE = "updatedate";
		public final static String UPDATE_TIME = "updatetime";
	}
	public static class CloumsIndex{		
		public final static int BOOK_PATH = 0;
		public final static int BOOK_NAME = 1;
		public final static int BOOK_DECODE = 2;
		public final static int CREATE_DATE = 3;
		public final static int CREATE_TIME = 4;
		public final static int BOOK_UPDATE_DATE = 5;
		public final static int BOOK_UPDATE_TIME = 6;

		public final static int BOOK_TAG = 1;
		public final static int BOOK_TAG_TEXT = 2;
		public final static int BOOK_TAG_PERCENT = 3;  
		public final static int UPDATE_DATE = 4;
		public final static int UPDATE_TIME = 5;
	}
	private class sqlitHelper extends SQLiteOpenHelper{
		private final String DROP_TABLE = "DROP TABLE IF EXISTS ";
		private final String Create_table_book = 
				"create table "+Table.BOOK+"("+
		                Cloums.BOOK_PATH + " TEXT ,"+
						Cloums.BOOK_NAME + " TEXT ,"+
						Cloums.BOOK_ENCODE + " INTEGER ,"+
						Cloums.CREATE_DATE + " INTEGER ,"+
						Cloums.CREATE_TIME + " INTEGER ,"+
						Cloums.UPDATE_DATE + " INTEGER ,"+
						Cloums.UPDATE_TIME + " INTEGER "
						+" )";
		
		private final String Create_table_bookmark = 
				"create table "+Table.BOOK_MARK+"("+
						Cloums.BOOK_PATH + " TEXT ,"+
						Cloums.BOOK_TAG + " INTEGER ,"+
						Cloums.BOOK_TAG_TEXT + " TEXT ,"+
						Cloums.BOOK_TAG_PERCENT + " FLOAT ,"+
						Cloums.UPDATE_DATE + " INTEGER,"+
						Cloums.UPDATE_TIME + " INTEGER"+
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
    		Book item = new Book();
    		item.setBookName("¤d½[Øpª«»y");
    		item.setBookPath(ASSATS_PATH+"demo1.txt");
    		Calendar c = Calendar.getInstance();
    		int date =Unity.getCurDate(c);
    		int time =Unity.getCurTime(c);
    		value = new ContentValues();
    		value.put(Cloums.BOOK_NAME, item.getBookName());
    		value.put(Cloums.BOOK_PATH, item.getBookPath());
    		value.put(Cloums.CREATE_DATE, date);
    		value.put(Cloums.CREATE_TIME, time);
    		value.put(Cloums.UPDATE_DATE, date);
    		value.put(Cloums.UPDATE_TIME, time);
    		db.insert(Table.BOOK,null, value);
    }
	private Cursor query(String table, String selection,String orderBy){
		return msqlitHelper.getReadableDatabase().query(table, null, selection, null, null, null, orderBy);
	}
	private void update(String table,ContentValues  v,String where){

		msqlitHelper.getWritableDatabase().update(table, v, where, null);
	
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
		value = new ContentValues();
		value.put(Cloums.BOOK_NAME, item.getBookName());
		value.put(Cloums.BOOK_PATH, item.getBookPath());
		value.put(Cloums.CREATE_DATE, date);
		value.put(Cloums.CREATE_TIME, time);
		value.put(Cloums.UPDATE_DATE, date);
		value.put(Cloums.UPDATE_TIME, time);
		insert(Table.BOOK, value);
		close();
	}
	private void updateBookTime(String Path,int date ,int time)
	{
		ContentValues	v = new ContentValues();
		v.put(Cloums.UPDATE_DATE, date);
		v.put(Cloums.UPDATE_TIME, time);
		update(Table.BOOK,v, Cloums.BOOK_PATH+" = \""+Path+"\"");

	}
	public void deleteBook(Context context,String Path){
		open(context);
		delete(Table.BOOK, Cloums.BOOK_PATH+" = \""+Path+"\"");
		delete(Table.BOOK_MARK, Cloums.BOOK_PATH+" = \""+Path+"\"");
		close();
	}
	public int  getBookEncode(Context context,String Path){
		open(context);
		Cursor mCursor = query(Table.BOOK, null, null);
		int value = 0;
		for(mCursor.moveToFirst();!mCursor.isAfterLast();mCursor.moveToNext())
		{  
			if(mCursor.getString(CloumsIndex.BOOK_PATH).equals(Path)){
				value = mCursor.getInt(CloumsIndex.BOOK_DECODE);
				break;
			}
		}
		mCursor.close();
		close();
		return value;
	}
	public void updateBookEncode(Context context,String Path,int encode){
		open(context);
		value = new ContentValues();
		value.put(Cloums.BOOK_ENCODE, encode);
		update(Table.BOOK, value, Cloums.BOOK_PATH+" = \""+Path+"\"");
		close();
	}
	public void addBookTag(Context context,BookMark tag,String Path){
		open(context);
		value = new ContentValues();
		value.put(Cloums.BOOK_PATH, Path);
		value.put(Cloums.BOOK_TAG ,tag.getBegin());
		value.put(Cloums.BOOK_TAG_TEXT ,tag.getContent());
		value.put(Cloums.BOOK_TAG_PERCENT ,tag.getPercent());
		value.put(Cloums.UPDATE_DATE,tag.getUpdate_date());
		value.put(Cloums.UPDATE_TIME,tag.getUpdate_time());
		updateBookTime(Path, tag.getUpdate_date(), tag.getUpdate_time());
		insert(Table.BOOK_MARK, value);
		close();
	}
	public List<Book> getBookList(Context context)
	{	open(context);
		bookList.clear();
		String orderBy = Cloums.UPDATE_DATE+" DESC ,"+Cloums.UPDATE_TIME+" DESC";
		Cursor mCursor = query(Table.BOOK, null, orderBy);
		if(mCursor.getCount()>0)
		{
			for(mCursor.moveToFirst();!mCursor.isAfterLast();mCursor.moveToNext())
			{
				Book mBook = new Book();
				mBook.setBookName(mCursor.getString(CloumsIndex.BOOK_NAME));
				mBook.setBookPath(mCursor.getString(CloumsIndex.BOOK_PATH));
				bookList.add(mBook);
			}
		}
		mCursor.close();
		close();
		return bookList;
	}
	public BookMark getBookMark(Context context,String Path)
	{   
		open(context);
		String Where = Cloums.BOOK_PATH +" = \""+ Path+"\"";
		String orderBy = Cloums.UPDATE_DATE+" DESC ,"+Cloums.UPDATE_TIME+" DESC";
		Cursor mCursor = query(Table.BOOK_MARK, Where, orderBy);
		BookMark mBookMark = null;
		if(mCursor.getCount()>0)
		{
			for(mCursor.moveToFirst();!mCursor.isAfterLast();mCursor.moveToNext())
			{
				if(mCursor.getString(CloumsIndex.BOOK_PATH).equals(Path))
				{
				    mBookMark = new BookMark();
					mBookMark.setBegin(mCursor.getInt(CloumsIndex.BOOK_TAG));
					mBookMark.setContent(mCursor.getString(CloumsIndex.BOOK_TAG_TEXT));
					mBookMark.setPercent(mCursor.getInt(CloumsIndex.BOOK_TAG_PERCENT));
					mBookMark.setUpdate_date(mCursor.getInt(CloumsIndex.UPDATE_DATE));
					break;
				}
			}
		}
		mCursor.close();
		close();
		return mBookMark;
	}
}
