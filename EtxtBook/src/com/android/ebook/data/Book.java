package com.android.ebook.data;

import android.graphics.Bitmap;

public class Book {
    int  bookId;
	String bookName;
    String bookPath;
    String bookCover = "";
    BookMark bookMark;
    public BookMark getBookMark() {
		return bookMark;
	}
	public void setBookMark(BookMark bookMark) {
		this.bookMark = bookMark;
	}
	Bitmap  bmp;
    public Bitmap getBmp() {
		return bmp;
	}
	public void setBmp(Bitmap bmp) {
		this.bmp = bmp;
	}
	public String getBookCover() {
		return bookCover;
	}
	public void setBookCover(String bookCover) {
		this.bookCover = bookCover;
	}
	public Book(){
    	
    }
    public Book(int id,String name ,String path){
    	setBookId(id);
    	setBookName(name);
    	setBookPath(path);
    	
    }
	public String getBookName() {
		return bookName;
	}
	public void setBookName(String bookName) {
		this.bookName = bookName;
	}
	public String getBookPath() {
		return bookPath;
	}
	public void setBookPath(String bookPath) {
		this.bookPath = bookPath;
	}
	public int getBookId() {
		return bookId;
	}
	public void setBookId(int bookId) {
		this.bookId = bookId;
	}
}
