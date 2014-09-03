package com.android.ebook.data;

public class BookMark {
    private  int begin;
    private  String content;
    private  float Percent;
    private  int update_date;
    private  int update_time;
	public int getUpdate_time() {
		return update_time;
	}
	public void setUpdate_time(int update_time) {
		this.update_time = update_time;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public String getContent() {
		return content;
	}
	public float getPercent() {
		return Percent;
	}
	public void setPercent(float percent) {
		Percent = percent;
	}
	public int getBegin() {
		return begin;
	}
	public void setBegin(int begin) {
		this.begin = begin;
	}
	public int getUpdate_date() {
		return update_date;
	}
	public void setUpdate_date(int update_date) {
		this.update_date = update_date;
	}  
	
} 
