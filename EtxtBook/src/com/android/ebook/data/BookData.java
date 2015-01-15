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
		public final static String BACKUP = "backup";
		public final static String ENCODE = "encode";
        public final static String BOOKCOVER = "bookcover";
        
        public final static String TAG_ID = "tagid";
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
		public final static int BOOK_COVER = 4;
		public final static int BOOK_DECODE = 5;
		
		public final static int BOOK_CREATE_DATE = 6;
		public final static int BOOK_CREATE_TIME = 7;
		public final static int BOOK_UPDATE_DATE = 8;
		public final static int BOOK_UPDATE_TIME = 9;
        
		public final static int BOOK_TAG_ID = 0;
		public final static int BOOK_TAG_BOOKID = 1;
		public final static int BOOK_TAG = 2;
		public final static int BOOK_TAG_TEXT = 3;
		public final static int BOOK_TAG_PERCENT = 4;  
		public final static int BOOK_TAG_UPDATE_DATE = 5;
		public final static int BOOK_TAG_UPDATE_TIME = 6;
	}
	private class sqlitHelper extends SQLiteOpenHelper{
		private final String DROP_TABLE = "DROP TABLE IF EXISTS ";
		private final String Create_table_book = 
				"create table "+Table.BOOK+"("+
		                column.ID+" INTEGER primary key autoincrement not null,"+
		                column.PATH + " TEXT ,"+
						column.NAME + " TEXT ,"+
		                column.BACKUP+" BOOL ,"+
		                column.BOOKCOVER+" TEXT ,"+
						column.ENCODE + " INTEGER ,"+
						column.CREATE_DATE + " INTEGER ,"+
						column.CREATE_TIME + " INTEGER ,"+
						column.UPDATE_DATE + " INTEGER ,"+
						column.UPDATE_TIME + " INTEGER "
						+" )";
		
		private final String Create_table_bookmark = 
				"create table "+Table.BOOK_MARK+"("+
						column.TAG_ID+" INTEGER primary key autoincrement not null,"+
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
	public void open(Context context){
		if(msqlitHelper==null)
			msqlitHelper = new sqlitHelper(context, DATABASE_NAME, null, DATABASE_VERSION);
	}
	public void close(){
		if(msqlitHelper!=null)
			msqlitHelper.close();
		msqlitHelper = null;
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
	public void clearAllData(){
		delete(Table.BOOK, null);
		delete(Table.BOOK_MARK, null);
	}
	
	public boolean addBook(Book item ,int date ,int time){
		
		String where =column.PATH +" = \""+item.getBookPath()+"\"";
		Cursor mcursor =query(Table.BOOK, where, null);
		int count = mcursor.getCount();
		mcursor.close();
		if(count>0){
			return false;
		}
		value = new ContentValues();
		value.put(column.NAME, item.getBookName());
		value.put(column.PATH, item.getBookPath());
		value.put(column.BOOKCOVER, item.getBookCover());
		value.put(column.BACKUP, false);
		value.put(column.CREATE_DATE, date);
		value.put(column.CREATE_TIME, time);
		value.put(column.UPDATE_DATE, date);
		value.put(column.UPDATE_TIME, time);
		insert(Table.BOOK, value);
		return true;
	}
	private void updateBookTime(int id,int date ,int time){
		ContentValues	v = new ContentValues();
		v.put(column.UPDATE_DATE, date);
		v.put(column.UPDATE_TIME, time);
		update(Table.BOOK,v, column.ID+" = "+ id);

	}
	public void deleteBook(int  id){
		delete(Table.BOOK, column.ID+" = "+ id);
		delete(Table.BOOK_MARK, column.ID+" = "+ id);
	}
	public int  getBookEncode(int  id){
		String where = column.ID+" = "+ id;
		Cursor mCursor = query(Table.BOOK, where, null);
		int value = 0;
		if(mCursor.getCount()>0)
		{
			if(mCursor.moveToFirst())
			{
				value = mCursor.getInt(columnIndex.BOOK_DECODE);
			}
		}
		mCursor.close();
		return value;
	}
	public Book getBook(int  id){
		Book mBook = null;
		String where = column.ID+" = "+ id;
		Cursor mCursor = query(Table.BOOK, where, null);
		if(mCursor.getCount()>0){
			if(mCursor.moveToFirst()){
				mBook = new Book();
				mBook.setBookName(mCursor.getString(columnIndex.BOOK_NAME));
				mBook.setBookPath(mCursor.getString(columnIndex.BOOK_PATH));
				mBook.setBookId(mCursor.getInt(columnIndex.BOOK_ID));
				mBook.setBookCover(mCursor.getString(columnIndex.BOOK_COVER));
				mBook.setBookMark(getLastBookMark(mCursor.getInt(columnIndex.BOOK_ID)));
			}
		}
		mCursor.close();
		return mBook;
	}
	public String  getBookName(int  id){
		String where = column.ID+" = "+ id;
		Cursor mCursor = query(Table.BOOK, where, null);
		String value = "";
		if(mCursor.getCount()>0)
		{
			if(mCursor.moveToFirst())
			{
				value = mCursor.getString(columnIndex.BOOK_NAME);
			}
		}
		mCursor.close();
		
		return value;
	}
	public void updateBookEncode(int id,int encode){
		ContentValues	v = new ContentValues();
		v.put(column.ENCODE, encode);
		update(Table.BOOK, v, column.ID+" = "+id);
	}
	public void addBookTag(int id,BookMark tag){
		value = new ContentValues();
		value.put(column.ID, id);
		value.put(column.TAG ,tag.getBegin());
		value.put(column.TAG_TEXT ,tag.getContent());
		value.put(column.TAG_PERCENT ,tag.getPercent());
		value.put(column.UPDATE_DATE,tag.getUpdate_date());
		value.put(column.UPDATE_TIME,tag.getUpdate_time());
		updateBookTime(id, tag.getUpdate_date(), tag.getUpdate_time());
		delete(Table.BOOK_MARK, column.ID+"="+id);
		insert(Table.BOOK_MARK, value);
	}
	public List<Book> getBookList(){	
		bookList.clear();
		String orderBy = column.UPDATE_DATE+" DESC ,"+column.UPDATE_TIME+" DESC";
		Cursor mCursor = query(Table.BOOK, null, orderBy);
		Book mBook;
		if(mCursor.getCount()>0){
			for(mCursor.moveToFirst();!mCursor.isAfterLast();mCursor.moveToNext()){
				mBook = new Book();
				mBook.setBookName(mCursor.getString(columnIndex.BOOK_NAME));
				mBook.setBookPath(mCursor.getString(columnIndex.BOOK_PATH));
				mBook.setBookId(mCursor.getInt(columnIndex.BOOK_ID));
				mBook.setBookCover(mCursor.getString(columnIndex.BOOK_COVER));
				mBook.setBookMark(getLastBookMark(mCursor.getInt(columnIndex.BOOK_ID)));
				bookList.add(mBook);
			}
		}
		mCursor.close();
		return bookList;
	}
	/**
	 * @param id
	 */
	public BookMark getLastBookMark(int bookId){   
		String Where = column.ID +" = "+ bookId;
		Cursor mCursor = query(Table.BOOK_MARK, Where, null);
		BookMark mBookMark = null;
		if(mCursor.getCount()>0){
			
				if(mCursor.moveToLast()){
				    mBookMark = new BookMark();
				    mBookMark.setId(mCursor.getInt(columnIndex.BOOK_TAG_ID));
				    mBookMark.setBookId(mCursor.getInt(columnIndex.BOOK_ID));
					mBookMark.setBegin(mCursor.getInt(columnIndex.BOOK_TAG));
					mBookMark.setContent(mCursor.getString(columnIndex.BOOK_TAG_TEXT));
					mBookMark.setPercent(mCursor.getFloat(columnIndex.BOOK_TAG_PERCENT));
					mBookMark.setUpdate_date(mCursor.getInt(columnIndex.BOOK_TAG_UPDATE_DATE));
					mBookMark.setUpdate_time(mCursor.getInt(columnIndex.BOOK_TAG_UPDATE_TIME));
				}
			
		}
		mCursor.close();
		return mBookMark;
	}
	public void deleteBookMark(int id){
		String Where = column.TAG_ID +" = "+ id;
		delete(Table.BOOK_MARK, Where);
	}
	public BookMark getBookMakrk(int id){
		String Where = column.TAG_ID +" = "+ id;
		Cursor mCursor = query(Table.BOOK_MARK, Where, null);
		BookMark mBookMark = null;
		if(mCursor.getCount()>0){
			if(mCursor.moveToFirst()){
				mBookMark = new BookMark();
				mBookMark.setId(mCursor.getInt(columnIndex.BOOK_TAG_ID));
				mBookMark.setBookId(mCursor.getInt(columnIndex.BOOK_TAG_BOOKID));
				mBookMark.setBegin(mCursor.getInt(columnIndex.BOOK_TAG));
				mBookMark.setContent(mCursor.getString(columnIndex.BOOK_TAG_TEXT));
				mBookMark.setPercent(mCursor.getFloat(columnIndex.BOOK_TAG_PERCENT));
				mBookMark.setUpdate_date(mCursor.getInt(columnIndex.BOOK_TAG_UPDATE_DATE));
				mBookMark.setUpdate_time(mCursor.getInt(columnIndex.BOOK_TAG_UPDATE_TIME));
				mBookMark.setName(getBookName(mCursor.getInt(columnIndex.BOOK_TAG_BOOKID)));
			}
		}
		return mBookMark;
		
	}
	public List<BookMark> getBookMarkList(){
		List<BookMark> recordlist = new ArrayList<BookMark>();
		String orderBy = column.UPDATE_DATE+" DESC ,"+column.UPDATE_TIME+" DESC,"+column.ID+" DESC" ;
		Cursor mCursor = query(Table.BOOK_MARK, null, orderBy);
		BookMark mBookMark;
		if(mCursor.getCount()>0){
			for(mCursor.moveToFirst();!mCursor.isAfterLast();mCursor.moveToNext()){
				mBookMark = new BookMark();
				mBookMark.setId(mCursor.getInt(columnIndex.BOOK_TAG_ID));
				int bookId = mCursor.getInt(columnIndex.BOOK_TAG_BOOKID);
				mBookMark.setBookId(bookId);
				mBookMark.setBegin(mCursor.getInt(columnIndex.BOOK_TAG));
				mBookMark.setContent(mCursor.getString(columnIndex.BOOK_TAG_TEXT));
				mBookMark.setPercent(mCursor.getFloat(columnIndex.BOOK_TAG_PERCENT));
				mBookMark.setUpdate_date(mCursor.getInt(columnIndex.BOOK_TAG_UPDATE_DATE));
				mBookMark.setUpdate_time(mCursor.getInt(columnIndex.BOOK_TAG_UPDATE_TIME));
				mBookMark.setName(getBookName(bookId));
				recordlist.add(mBookMark);
			}
		}
		mCursor.close();
		return recordlist;
	}
	public void updateBookCover(String CoverName){
		
	}
}
