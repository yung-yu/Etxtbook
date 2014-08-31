package com.android.ebook.data;

public class Book {
    int  BookId;
	String BookName;
    String BookPath;
    public Book(){
    	
    }
    public Book(int id,String name ,String path){
    	setBookId(id);
    	setBookName(name);
    	setBookPath(path);
    	
    }
	public String getBookName() {
		return BookName;
	}
	public void setBookName(String bookName) {
		BookName = bookName;
	}
	public String getBookPath() {
		return BookPath;
	}
	public void setBookPath(String bookPath) {
		BookPath = bookPath;
	}
	public int getBookId() {
		return BookId;
	}
	public void setBookId(int bookId) {
		BookId = bookId;
	}
}
